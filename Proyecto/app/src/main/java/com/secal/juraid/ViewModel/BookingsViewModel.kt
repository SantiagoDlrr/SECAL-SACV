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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingsViewModel(private val userViewModel: UserViewModel) : ViewModel() {
    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    private val _filteredBookings = MutableStateFlow<List<NumberedBooking>>(emptyList())
    val filteredBookings: StateFlow<List<NumberedBooking>> = _filteredBookings.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadAllData()
        setupFilteredBookings()
    }

    private fun setupFilteredBookings() {
        viewModelScope.launch {
            combine(_bookings, userViewModel.userId) { bookings, userId ->
                bookings.filter { it.id_usuario == userId }
                    .sortedByDescending { parseDate(it.fecha) }
                    .mapIndexed { index, booking ->
                        NumberedBooking(
                            booking = booking,
                            number = bookings.size - index
                        )
                    }
            }.collect { filteredSortedBookings ->
                _filteredBookings.value = filteredSortedBookings
            }
        }
    }

    fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                loadBookings()
            } catch (e: Exception) {
                Log.e("BookingsViewModel", "Error loading data: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadBookings() {
        try {
            val fetchedBookings = getBookingsFromDatabase()
            _bookings.value = fetchedBookings
        } catch (e: Exception) {
            Log.e("BookingsViewModel", "Error loading bookings: ${e.message}", e)
        }
    }

    private fun parseDate(dateString: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.parse(dateString) ?: Date()
    }

    private suspend fun getBookingsFromDatabase(): List<Booking> {
        return withContext(Dispatchers.IO) {
            try {
                val bookingsList = supabase
                    .from("Citas")
                    .select()
                    .decodeList<Booking>()
                bookingsList
            } catch (e: Exception) {
                Log.e("BookingsViewModel", "Error getting bookings from database", e)
                when (e) {
                    is kotlinx.serialization.SerializationException -> {
                        Log.e("BookingsViewModel", "Serialization error. Check if the Booking data class matches the database schema", e)
                    }
                    is io.github.jan.supabase.exceptions.RestException -> {
                        Log.e("BookingsViewModel", "Supabase REST API error", e)
                    }
                    else -> {
                        Log.e("BookingsViewModel", "Unknown error occurred", e)
                    }
                }
                emptyList()
            }
        }
    }

    suspend fun addBooking(
        nombre: String,
        apellido: String,
        fecha: String,
        hora: String,
        idRegion: Int,
        idSituacion: Int,
        id_usuario: String
    ) {
        viewModelScope.launch {
            try {
                val newBooking = BookingInsert(
                    nombre = nombre,
                    apellido = apellido,
                    fecha = fecha,
                    hora = hora,
                    id_region = idRegion,
                    id_situacion = idSituacion,
                    id_usuario = id_usuario
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

    suspend fun updateBookingStatus(bookingId: Int, newStatus: Boolean) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    supabase.from("Citas")
                        .update(
                            { set("estado_cita", newStatus) }
                        ) {
                            filter { eq("id", bookingId) }
                        }
                }

                // Update the local state
                _bookings.value = _bookings.value.map { booking ->
                    if (booking.id == bookingId) {
                        booking.copy(estado_cita = newStatus)
                    } else {
                        booking
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating booking status", e)
            }
        }
    }
}

@Serializable
data class BookingInsert(
    val nombre: String,
    val apellido: String,
    val fecha: String,
    val hora: String,
    val id_region: Int,
    val estado_cita: Boolean = true,
    val id_situacion: Int,
    val id_usuario: String
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
    val id_usuario: String,
    val motivo_cancelacion: String? = null
)

data class NumberedBooking(
    val booking: Booking,
    val number: Int
)
