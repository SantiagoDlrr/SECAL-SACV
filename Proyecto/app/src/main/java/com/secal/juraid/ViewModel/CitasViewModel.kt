package com.secal.juraid.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.secal.juraid.Model.UserRepository
import com.secal.juraid.supabase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
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
import java.time.LocalTime

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
                val futureCitas = filterFutureCitas(fetchedCitas)
                _uiState.value = UiState.Success(futureCitas)
                println("Citas cargadas exitosamente: ${fetchedCitas.size}")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al cargar las citas: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun filterFutureCitas(citas: List<Cita>): List<Cita> {
        val currentDate = LocalDate.now()
        return citas.filter { cita ->
            val citaDate = LocalDate.parse(cita.fecha, DateTimeFormatter.ISO_LOCAL_DATE)
            citaDate.isAfter(currentDate) || citaDate.isEqual(currentDate)
        }
    }

    fun loadCitasPasadas() {
        viewModelScope.launch {
            try {
                val currentDateTime = LocalDateTime.now()
                //val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                /*val citasPasadas = supabase.from("Citas")
                    .select() {
                        filter {
                            lt("fecha", currentDate)
                            and {
                                eq("estado_representacion", 0) // Solo citas pendientes
                            }
                        }
                    }
                    .decodeList<Cita>()*/

                val citasPasadas = withContext(Dispatchers.IO) {
                    supabase.from("Citas")
                        .select() {
                            filter {
                                lte("fecha", currentDateTime.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                and {
                                    eq("estado_representacion", 0) // Solo citas pendientes
                                }
                            }
                        }
                        .decodeList<Cita>()
                }

                val citasFiltradas = citasPasadas.filter { cita ->
                    val citaDateTime = LocalDateTime.of(
                        LocalDate.parse(cita.fecha, DateTimeFormatter.ISO_LOCAL_DATE),
                        LocalTime.parse(cita.hora, DateTimeFormatter.ofPattern("HH:mm:ss"))
                    )
                    citaDateTime.isBefore(currentDateTime) || citaDateTime.isEqual(currentDateTime)
                }

                _citasPasadas.value = citasFiltradas
            } catch (e: Exception) {
                println("Error al cargar citas pasadas: ${e.message}")
            }
        }
    }

    fun representarCita(cita: Cita, abogado: String?) {
        viewModelScope.launch {
            try {

                val nombreAbogado = abogado ?: "Sin asignar"

                // Crear nuevo caso en la tabla Cases
                val newCase = CasesViewModel.CaseInsert(
                    nombre_abogado = nombreAbogado,
                    nombre_cliente = "${cita.nombre} ${cita.apellido}",
                    NUC = "Sin información",
                    carpeta_judicial = "Sin información",
                    carpeta_investigacion = "Sin información",
                    acceso_fv = "Sin información",
                    pass_fv = "Sin información",
                    fiscal_titular = "Sin información",
                    id_unidad_investigacion = null,
                    drive = "https://drive.google.com",
                    status = 1,

                )

                supabase.from("Cases")
                    .insert(newCase)
                    .decodeSingle<Case>()

                updateCitaEstado(cita.id, 1) // 1 para representada

            } catch (e: Exception) {
                println("Error al representar cita: ${e.message}")
                updateCitaEstado(cita.id, 1)
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
                supabase.from("Citas").update(
                    {
                        set("motivo_cancelacion", motivo ?: "Sin motivo")
                        set("estado_cita", false)
                    }
                ) {
                    filter {
                        eq("id", cita.id)
                    }
                }

                loadCitas()
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
        var estado_representacion: Int? = 0
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

object ServiceLocator {
    private var supabaseClient: SupabaseClient? = null
    private var userRepository: UserRepository? = null

    fun provideSupabaseClient(): SupabaseClient {
        return supabaseClient ?: throw IllegalStateException("SupabaseClient not initialized")
    }

    fun provideUserRepository(scope: CoroutineScope): UserRepository {
        return userRepository ?: UserRepository(provideSupabaseClient(), scope).also {
            userRepository = it
        }
    }

    fun initialize(supabaseClient: SupabaseClient) {
        this.supabaseClient = supabaseClient
    }
}

@Serializable
data class CitaCancel(
    val motivo_cancelacion: String? = null,
    val estado_cita: Boolean? = false
)