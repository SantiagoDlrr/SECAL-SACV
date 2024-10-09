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

    val availableDates = listOf(
        "Hoy\n2 oct",
        "Mañana\n3 oct",
        "Miércoles\n4 oct",
        "Jueves\n5 oct",
        "Viernes\n6 oct"
    )

    val availableTimeSlots = listOf(
        TimeSlot("10:00AM - 11:00AM", true),
        TimeSlot("11:00AM - 12:00PM", true),
        TimeSlot("12:00PM - 01:00PM", true),
        TimeSlot("01:00PM - 02:00PM", false),
        TimeSlot("02:00PM - 03:00PM", true),
        TimeSlot("03:00PM - 04:00PM", false)
    )

    fun scheduleAppointment(date: String, timeSlot: TimeSlot) {
        if (timeSlot.isAvailable) {
            _uiState.update {
                it.copy(
                    selectedDate = date.split("\n")[0],
                    selectedTime = timeSlot.time,
                    isDialogOpen = false
                )
            }
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

data class TimeSlot(
    val time: String,
    val isAvailable: Boolean
)
