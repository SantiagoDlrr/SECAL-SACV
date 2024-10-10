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
import kotlinx.serialization.json.Json

class CitasViewModel : ViewModel() {
    sealed class UiState {
        object Loading : UiState()
        data class Success(val citas: List<Cita>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

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
                val result = supabase.from("Citas").select().decodeList<Cita>()
                println("Citas recuperadas: ${result.size}")
                result.forEach { cita ->
                    println("Cita: $cita")
                }
                result
            } catch (e: Exception) {
                println("Error al obtener citas: ${e.message}")
                e.printStackTrace()
                throw Exception("Error al obtener citas de la base de datos: ${e.message}")
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
        val id_usuario: String? = null
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