package com.secal.juraid.Views.Generals.Bookings

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccessAlarms
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.BookingsViewModel
import com.secal.juraid.ViewModel.NumberedBooking
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun BookingsView(
    navController: NavController,
    bookingsViewModel: BookingsViewModel
) {
    val filteredBookings by bookingsViewModel.filteredBookings.collectAsState()
    val isLoading by bookingsViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        bookingsViewModel.loadAllData()
    }

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
            if (filteredBookings.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes citas programadas.")
                }
            } else {
                AppointmentList(
                    numberedBookings = filteredBookings,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun AppointmentList(
    numberedBookings: List<NumberedBooking>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(numberedBookings) { numberedBooking ->
            AppointmentCard(numberedBooking = numberedBooking)
        }
    }
}

@Composable
fun AppointmentCard(
    numberedBooking: NumberedBooking,
    modifier: Modifier = Modifier
) {
    val booking = numberedBooking.booking
    val isExpired = isAppointmentExpired(booking.fecha)

    val (statusText, statusColor) = when {
        isExpired -> "Expirada" to MaterialTheme.colorScheme.error
        booking.estado_cita == true -> "Confirmado" to MaterialTheme.colorScheme.primary
        booking.estado_cita == false -> "Cancelado" to MaterialTheme.colorScheme.error
        else -> "Pendiente" to MaterialTheme.colorScheme.secondary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header Card (Booking number and Status)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cita #${numberedBooking.number}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Surface(
                        color = statusColor.copy(alpha = 0.2f),
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content Card (Appointment Details)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Client information row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${booking.nombre} ${booking.apellido}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (booking.estado_cita == false) {
                        // Display cancellation reason
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Motivo de cancelación: ${booking.motivo_cancelacion ?: "No especificado"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    } else {
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
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = situacionText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
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
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formatToSpanishDate(booking.fecha),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Time range row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccessAlarms,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formatTimeRange(booking.hora),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

// Utility functions (unchanged)
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