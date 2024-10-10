import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class ScheduleViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    private val displayDateFormatter = DateTimeFormatter.ofPattern("d MMM")
    @RequiresApi(Build.VERSION_CODES.O)
    private val databaseDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    @RequiresApi(Build.VERSION_CODES.O)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    @RequiresApi(Build.VERSION_CODES.O)
    val availableDates: List<String> = calculateNextBusinessDays()

    val availableTimeSlots = generateTimeSlots()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextBusinessDays(): List<String> {
        val dates = mutableListOf<String>()
        var currentDate = LocalDate.now()
        var daysAdded = 0

        while (daysAdded < 7) {
            if (isBusinessDay(currentDate)) {
                val dayName = currentDate
                    .dayOfWeek.getDisplayName(TextStyle.FULL_STANDALONE, Locale("es", "ES"))
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

                val formattedDate = currentDate.format(displayDateFormatter)
                dates.add("$dayName\n$formattedDate")
                daysAdded++
            }
            currentDate = currentDate.plusDays(1)
        }
        return dates
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isBusinessDay(date: LocalDate): Boolean {
        return date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY
    }

    private fun generateTimeSlots(): List<TimeSlot> {
        return (9..15).map { hour ->
            val startHour = String.format("%02d:00", hour)
            val endHour = String.format("%02d:00", hour + 1)
            TimeSlot(
                displayTime = "$startHour - $endHour",
                startTime = startHour,
                isAvailable = true // You can implement availability logic here
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleAppointment(date: String, timeSlot: TimeSlot) {
        if (timeSlot.isAvailable) {
            // Parse the selected date
            val selectedDateStr = date.split("\n")[0]
            val selectedDate = parseDisplayDateToLocalDate(date)

            // Format date and time for database (using only start time)
            val databaseDate = selectedDate.format(databaseDateFormatter)
            val databaseTime = LocalTime.parse(timeSlot.startTime + ":00").format(timeFormatter)

            _uiState.update {
                it.copy(
                    selectedDate = selectedDateStr,
                    selectedTime = timeSlot.displayTime,
                    databaseDateTime = "$databaseDate $databaseTime",
                    isDialogOpen = false
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDisplayDateToLocalDate(displayDate: String): LocalDate {
        val dateStr = displayDate.split("\n")[1]
        val currentYear = LocalDate.now().year
        return LocalDate.parse("$dateStr $currentYear", DateTimeFormatter.ofPattern("d MMM yyyy"))
    }

    fun openDialog() = _uiState.update { it.copy(isDialogOpen = true) }
    fun closeDialog() = _uiState.update { it.copy(isDialogOpen = false) }
}

data class ScheduleUiState(
    val selectedDate: String? = null,
    val selectedTime: String? = null,
    val databaseDateTime: String? = null,
    val isDialogOpen: Boolean = false
)

data class TimeSlot(
    val displayTime: String,    // For display: "09:00 - 10:00"
    val startTime: String,      // For database: "09:00"
    val isAvailable: Boolean
)