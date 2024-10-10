package com.secal.juraid.Views.Generals.Bookings.Schedule

import ScheduleViewModel
import TimeSlot
import TimeSlotItem
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// Schedule View
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (uiState.selectedDate != null)
                        "${uiState.selectedDate}, ${uiState.selectedTime}"
                    else "Sin cita programada",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (uiState.selectedDate != null) "Agendado" else "No Agendado",
                    style = MaterialTheme.typography.bodyMedium
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleDialog(viewModel: ScheduleViewModel) {
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTimeSlot by remember { mutableStateOf<TimeSlot?>(null) }

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
                    items(
                        count = viewModel.availableDates.size,
                        itemContent = { index ->
                            val date = viewModel.availableDates[index]
                            DateCard(
                                date = date,
                                isSelected = date == selectedDate,
                                onSelect = { selectedDate = date }
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Time Selection
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        count = viewModel.availableTimeSlots.size,
                        itemContent = { index ->
                            val timeSlot = viewModel.availableTimeSlots[index]
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
                    )
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
