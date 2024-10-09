package com.secal.juraid.ViewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Schedule View Model
class ScheduleViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    // Updated dates with day names
    val availableDates = listOf(
        "Hoy\n2 oct",
        "Mañana\n3 oct",
        "Miércoles\n4 oct",
        "Jueves\n5 oct",
        "Viernes\n6 oct"
    )

    val availableTimeSlots = listOf(
        "10:00AM - 11:00AM",
        "11:00AM - 12:00PM",
        "12:00PM - 01:00PM",
        "01:00PM - 02:00PM",
        "02:00PM - 03:00PM",
        "03:00PM - 04:00PM"
    )

    fun scheduleAppointment(date: String, time: String) {
        _uiState.update {
            it.copy(
                selectedDate = date.split("\n")[0], // Only use the day name
                selectedTime = time,
                isDialogOpen = false
            )
        }
    }

    fun openDialog() = _uiState.update { it.copy(isDialogOpen = true) }
    fun closeDialog() = _uiState.update { it.copy(isDialogOpen = false) }
}
data class ScheduleUiState(
    val selectedDate: String? = null,
    val selectedTime: String? = null,
    val isDialogOpen: Boolean = false
)