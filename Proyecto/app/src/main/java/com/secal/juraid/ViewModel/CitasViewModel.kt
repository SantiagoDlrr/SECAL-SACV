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

class CitasViewModel : ViewModel() {
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
        val nombre: String,
        val apellido: String,
        val fecha: String,
        val hora: String,
        val id_region: Int,
        val estado_cita: Boolean,
        val id_situacion: Int,
        val id_usuario: String,
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