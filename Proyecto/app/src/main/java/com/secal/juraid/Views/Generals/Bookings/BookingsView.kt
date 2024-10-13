package com.secal.juraid.Views.Generals.Bookings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.Booking
import com.secal.juraid.ViewModel.BookingsViewModel
import com.secal.juraid.ViewModel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookingsView(
    navController: NavController,
    bookingsViewModel : BookingsViewModel
) {
    val bookings by bookingsViewModel.bookings.collectAsState()
    val isLoading by bookingsViewModel.isLoading.collectAsState()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            AppointmentList(
                bookings = bookings,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}


@Composable
fun AppointmentList(
    bookings: List<Booking>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(bookings) { booking ->
            AppointmentCard(booking = booking)
        }
    }
}

@Composable
fun AppointmentCard(
    booking: Booking,
    modifier: Modifier = Modifier
) {
    val isExpired = isAppointmentExpired(booking.fecha)

    val (backgroundColor, statusText, statusColor) = when {
        isExpired -> Triple(
            Color(0xFFE8E8E8),
            "Expirada",
            Color(0xFF6E6E6E)
        )
        booking.estado_cita == true -> Triple(
            Color(0xFFF5F7FA),
            "Confirmado",
            Color(0xFF4CAF50)
        )
        booking.estado_cita == false -> Triple(
            Color(0xFFF5F7FA),
            "Cancelado",
            Color(0xFFE53935)
        )
        else -> Triple(
            Color(0xFFF5F7FA),
            "Pendiente",
            Color(0xFF42A5F5)
        )
    }

    // Format the date to Spanish format
    val formattedDate = formatToSpanishDate(booking.fecha)
    // Format the time to show duration
    val timeRange = formatTimeRange(booking.hora)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row with ID and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detalles de la Cita #${booking.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Client information row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${booking.nombre} ${booking.apellido}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Situation type
            val situacionText = when (booking.id_situacion) {
                1 -> "Víctima"
                2 -> "Investigado"
                else -> "No especificado"
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = situacionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time range row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBox, // You might want to use a different icon for time
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = timeRange,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Add these utility functions outside the composable
private fun formatToSpanishDate(dateStr: String): String {
    val inputFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = inputFormatter.parse(dateStr) ?: return dateStr

    val dayOfWeekFormatter = SimpleDateFormat("EEEE", Locale("es", "ES"))
    val dayFormatter = SimpleDateFormat("d", Locale.getDefault())
    val monthFormatter = SimpleDateFormat("MMMM", Locale("es", "ES"))

    val dayOfWeek = dayOfWeekFormatter.format(date).capitalize()
    val day = dayFormatter.format(date)
    val month = monthFormatter.format(date).capitalize()

    return "$dayOfWeek $day de $month"
}

private fun formatTimeRange(startTime: String): String {
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val startDate = timeFormatter.parse(startTime) ?: return startTime

    // Add one hour to get end time
    val calendar = Calendar.getInstance()
    calendar.time = startDate
    calendar.add(Calendar.HOUR_OF_DAY, 1)
    val endDate = calendar.time

    val startTimeStr = timeFormatter.format(startDate)
    val endTimeStr = timeFormatter.format(endDate)

    return "$startTimeStr - $endTimeStr"
}

// Extension function to capitalize first letter
private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

private fun isAppointmentExpired(fecha: String): Boolean {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val appointmentDate = formatter.parse(fecha)
    val currentDate = Date()
    return appointmentDate?.before(currentDate) ?: false
}