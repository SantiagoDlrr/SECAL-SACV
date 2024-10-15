package com.secal.juraid.Views.Generals.Bookings.Schedule

import ScheduleViewModel
import TimeSlot
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.secal.juraid.TitlesView

// Schedule View
@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel) {
    val uiState by viewModel.uiState.collectAsState()

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Current Appointment Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Cita Actual",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (uiState.selectedDate != null)
                                    "${uiState.selectedDate}, ${uiState.selectedTime}"
                                else "Sin cita programada",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Schedule Button
                    OutlinedButton(
                        onClick = viewModel::openDialog,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agendar en otro horario")
                    }

                    // Schedule Dialog
                    if (uiState.isDialogOpen) {
                        ScheduleDialog(viewModel)
                    }
                }
            }
        }
    }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleDialog(viewModel: ScheduleViewModel) {
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTimeSlot by remember { mutableStateOf<TimeSlot?>(null) }
    val availableTimeSlots by viewModel.availableTimeSlots.collectAsState()

    LaunchedEffect(selectedDate) {
        selectedDate?.let { viewModel.updateAvailableTimeSlots(it) }
    }

    Dialog(
        onDismissRequest = viewModel::closeDialog,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .width(380.dp)
                .height(640.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Horarios Disponibles",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date Selection
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(viewModel.availableDates) { date ->
                        DateCard(
                            date = date,
                            isSelected = date == selectedDate,
                            onSelect = {
                                selectedDate = date
                                selectedTimeSlot = null
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Time Selection
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(availableTimeSlots) { timeSlot ->
                        TimeSlotItem(
                            timeSlot = timeSlot,
                            isSelected = timeSlot == selectedTimeSlot,
                            onSelect = {
                                if (timeSlot.isAvailable) {
                                    selectedTimeSlot = timeSlot
                                }
                            }
                        )
                    }
                }

                // Confirm Button
                Button(
                    onClick = {
                        if (selectedDate != null && selectedTimeSlot != null) {
                            viewModel.scheduleAppointment(selectedDate!!, selectedTimeSlot!!)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedDate != null && selectedTimeSlot != null && selectedTimeSlot?.isAvailable == true
                ) {
                    Text("Confirmar")
                }
            }
        }
    }
}


/*Button(
            onClick = viewModel::openDialog,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Agendar Cita")
        }*/