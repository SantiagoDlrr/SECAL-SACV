package com.secal.juraid.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secal.juraid.supabase
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CitasViewModel : ViewModel() {
    private val _citasPasadas = MutableStateFlow<List<Cita>>(emptyList())
    val citasPasadas: StateFlow<List<Cita>> = _citasPasadas.asStateFlow()

    sealed class UiState {
        object Loading : UiState()
        data class Success(val citas: List<Cita>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadCitas()
        loadCitasPasadas()
    }

    fun loadCitas() {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val fetchedCitas = getCitasFromDatabase()
                _uiState.value = UiState.Success(fetchedCitas)
                println("Citas cargadas exitosamente: ${fetchedCitas.size}")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al cargar las citas: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun loadCitasPasadas() {
        viewModelScope.launch {
            try {
                val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val citasPasadas = supabase.from("Citas")
                    .select() {
                        filter {
                            lt("fecha", currentDate)
                            and {
                                eq("estado_representacion", 0) // Solo citas pendientes
                            }
                        }
                    }
                    .decodeList<Cita>()

                _citasPasadas.value = citasPasadas
            } catch (e: Exception) {
                println("Error al cargar citas pasadas: ${e.message}")
            }
        }
    }

    fun representarCita(cita: Cita) {
        viewModelScope.launch {
            try {
                // Crear nuevo caso en la tabla Cases
                val newCase = mapOf(
                    "nombre_cliente" to "${cita.nombre} ${cita.apellido}",
                    "fecha" to (cita.fecha ?: "Sin información"),
                    "hora" to (cita.hora ?: "Sin información"),
                    "id_region" to (cita.id_region ?: 1),
                    "id_situacion" to (cita.id_situacion ?: 1),
                    "id_unidad_investigacion" to 1,
                    "status" to 1,
                    "drive" to "https://drive.google.com",
                    "NUC" to "Sin información",
                    "carpeta_judicial" to "Sin información",
                    "carpeta_investigacion" to "Sin información",
                    "acceso_fv" to "Sin información",
                    "pass_fv" to "Sin información",
                    "fiscal_titular" to "Sin información"
                )

                val response = supabase.from("Cases").insert(newCase)

                if (response != null) {
                    updateCitaEstado(cita.id, 1) // 1 para representada
                    println("Caso creado y cita actualizada exitosamente")
                } else {
                    throw Exception("Error al crear el caso: respuesta nula")
                }
            } catch (e: Exception) {
                println("Error al representar cita: ${e.message}")
            }
        }
    }

    fun rechazarCita(citaId: Int) {
        viewModelScope.launch {
            try {
                updateCitaEstado(citaId, 2) // 2 para rechazada
            } catch (e: Exception) {
                println("Error al rechazar cita: ${e.message}")
            }
        }
    }

    fun cancelarCita(cita: Cita, motivo: String) {
        viewModelScope.launch {
            try {
                val updates = mapOf(
                    "estado_cita" to false,
                    "motivo_cancelacion" to motivo
                )

                val response = supabase.from("Citas")
                    .update(updates) {
                        filter { eq("id", cita.id) }
                    }

                if (response != null) {
                    println("Cita cancelada exitosamente")
                    loadCitas() // Recargar las citas después de la cancelación
                } else {
                    throw Exception("Error al cancelar la cita: respuesta nula")
                }
            } catch (e: Exception) {
                println("Error al cancelar cita: ${e.message}")
                _uiState.value = UiState.Error("Error al cancelar la cita: ${e.message}")
            }
        }
    }

    private suspend fun updateCitaEstado(citaId: Int, estado: Int) {
        try {
            val response = supabase.from("Citas")
                .update(mapOf("estado_representacion" to estado)) {
                    filter { eq("id", citaId) }
                }

            if (response != null) {
                println("Estado de cita actualizado exitosamente")
                loadCitasPasadas() // Recargar las citas pasadas para reflejar el cambio
            } else {
                println("No se pudo actualizar el estado de la cita")
            }
        } catch (e: Exception) {
            println("Error al actualizar estado de cita: ${e.message}")
        }
    }

    private suspend fun getCitasFromDatabase(): List<Cita> = withContext(Dispatchers.IO) {
        supabase.from("Citas")
            .select() {
                filter { eq("estado_cita", true) }
            }
            .decodeList<Cita>()
    }

    @Serializable
    data class Cita(
        val id: Int,
        val nombre: String? = null,
        val apellido: String? = null,
        val fecha: String? = null,
        val hora: String? = null,
        val id_region: Int? = null,
        val estado_cita: Boolean? = null,
        val id_situacion: Int? = null,
        val id_usuario: String? = null,
        val motivo_cancelacion: String? = null,
        var estado_representacion: Int? = null
    ) {
        companion object {
            private val regionesMap = mapOf(
                1 to "Apodaca",
                2 to "Escobedo",
                3 to "Guadalupe",
                4 to "Monterrey",
                5 to "San Nicolás de los Garza",
                6 to "San Pedro Garza García",
                7 to "Otro"
            )

            private val situacionesMap = mapOf(
                1 to "Víctima",
                2 to "Investigado"
            )

            fun getNombreRegion(id: Int?): String = regionesMap[id] ?: "Desconocido"
            fun getNombreSituacion(id: Int?): String = situacionesMap[id] ?: "Desconocido"
        }
    }
}