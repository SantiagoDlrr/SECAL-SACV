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
                        }
                    }
                    .decodeList<Cita>()

                _citasPasadas.value = citasPasadas.sortedByDescending { it.fecha }
            } catch (e: Exception) {
                println("Error al cargar citas pasadas: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun representarCita(id: Int) {
        viewModelScope.launch {
            try {
                supabase.from("Citas")
                    .update(mapOf("representada" to true)) {
                        filter { eq("id", id) }
                    }
                loadCitasPasadas() // Recargar las citas después de la actualización
            } catch (e: Exception) {
                println("Error al representar cita: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun noRepresentarCita(id: Int) {
        viewModelScope.launch {
            try {
                supabase.from("Citas")
                    .update(mapOf("representada" to false)) {
                        filter { eq("id", id) }
                    }
                loadCitasPasadas() // Recargar las citas después de la actualización
            } catch (e: Exception) {
                println("Error al no representar cita: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private suspend fun getCitasFromDatabase(): List<Cita> {
        return withContext(Dispatchers.IO) {
            try {
                supabase.from("Citas")
                    .select() {
                        filter {
                            this.eq("estado_cita", true)
                        }
                    }
                    .decodeList<Cita>()
            } catch (e: Exception) {
                println("Error detallado en getCitasFromDatabase: ${e.message}")
                e.printStackTrace()
                throw Exception("Error al obtener citas de la base de datos: ${e.message}")
            }
        }
    }

    fun cancelarCita(cita: Cita, motivo: String) {
        viewModelScope.launch {
            try {
                println("Iniciando cancelación de cita: ${cita.id}")
                withContext(Dispatchers.IO) {
                    val updates = JsonObject(mapOf(
                        "estado_cita" to JsonPrimitive(false),
                        "motivo_cancelacion" to JsonPrimitive(motivo)
                    ))

                    val response = supabase.from("Citas")
                        .update(updates) {
                            filter {
                                this.eq("id", cita.id)
                            }
                        }

                    println("Respuesta de Supabase: $response")
                    println("Cancelación completada para la cita: ${cita.id}")
                }
                // Recargar las citas después de la cancelación
                loadCitas()
            } catch (e: Exception) {
                println("Error detallado en cancelarCita: ${e.message}")
                e.printStackTrace()
                _uiState.value = UiState.Error("Error al cancelar la cita: ${e.message}")
            }
        }
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
        val motivo_cancelacion: String? = null
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