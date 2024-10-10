package com.secal.juraid.ViewModel

import android.content.ContentValues.TAG
import android.util.Log
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

class BookingsViewModel : ViewModel() {
    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    suspend fun addBooking(
        fecha: String,
        hora: String,
        idRegion: Int,
        idSituacion: Int
    ) {
        viewModelScope.launch {
            try {
                val newBooking = BookingInsert(
                    fecha = fecha,
                    hora = hora,
                    id_region = idRegion,
                    id_situacion = idSituacion
                )

                val insertedBooking = withContext(Dispatchers.IO) {
                    supabase.from("Citas")
                        .insert(newBooking)
                        .decodeSingle<Booking>()
                }

                // Update the bookings list with the new booking
                _bookings.value = _bookings.value + insertedBooking

            } catch (e: Exception) {
                Log.e(TAG, "Error adding booking", e)
            }
        }
    }
}

@Serializable
data class BookingInsert(
    val nombre: String = "S",
    val apellido: String = "Prueba",
    val fecha: String,
    val hora: String,
    val id_region: Int,
    val estado_cita: Boolean? = null,
    val id_situacion: Int,
    val id_usuario: String? = null
)

@Serializable
data class Booking(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val fecha: String,
    val hora: String,
    val id_region: Int,
    val estado_cita: Boolean,
    val id_situacion: Int,
    val id_usuario: String?
)