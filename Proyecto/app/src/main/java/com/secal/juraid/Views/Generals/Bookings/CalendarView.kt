package com.secal.juraid.Views.Generals.Bookings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.secal.juraid.ui.theme.Primary

@Composable
fun ScheduledCard(deliveryTime: String, status: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = deliveryTime,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun RescheduleButton(onOpenDialog: () -> Unit) {
    OutlinedButton(
        onClick = onOpenDialog,
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
}

@Composable
fun TimeSelectionDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (String, String) -> Unit
) {
    // Initialize with the first date (Hoy)
    var selectedDate by remember { mutableStateOf("Hoy") }
    var selectedTime by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .width(360.dp)
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
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                DateSelector(
                    initialSelectedDate = 0,  // Select first date by default
                    onDateSelected = { date ->
                        selectedDate = date
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    TimeSlotList { time ->
                        selectedTime = time
                    }
                }

                Button(
                    onClick = {
                        if (selectedTime != null) {
                            onTimeSelected(selectedDate, selectedTime!!)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    enabled = selectedTime != null  // Only check for selectedTime since date is always selected
                ) {
                    Text("Seleccionar", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun DateSelector(
    initialSelectedDate: Int = 0,
    onDateSelected: (String) -> Unit
) {
    val dates = remember {
        listOf(
            "Hoy\n2 oct",
            "Mañana\n3 oct",
            "Miércoles\n4 oct",
            "Jueves\n5 oct",
            "Viernes\n6 oct"
        )
    }
    var selectedDateIndex by remember { mutableStateOf(initialSelectedDate) }

    LaunchedEffect(Unit) {
        onDateSelected(dates[initialSelectedDate].split("\n")[0])
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dates) { date ->
            val index = dates.indexOf(date)
            DateCard(
                date = date,
                isSelected = index == selectedDateIndex,
                onSelect = {
                    selectedDateIndex = index
                    onDateSelected(date.split("\n")[0])
                }
            )
        }
    }
}

@Composable
fun TimeSlotList(onTimeSelected: (String) -> Unit) {
    val timeSlots = remember {
        listOf(
            "10:00AM - 11:00AM",
            "11:00AM - 12:00PM",
            "12:00PM - 01:00PM",
            "01:00PM - 02:00PM",
            "02:00PM - 03:00PM",
            "03:00PM - 04:00PM"
        )
    }

    var selectedTimeSlot by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(timeSlots) { time ->
            TimeSlotItem(
                time = time,
                isAvailable = true,
                isSelected = time == selectedTimeSlot,
                onSelect = {
                    selectedTimeSlot = time
                    onTimeSelected(time)
                }
            )
        }
    }
}
@Composable
fun TimeSlotItem(time: String, isAvailable: Boolean, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                enabled = isAvailable
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            enabled = isAvailable,
            colors = RadioButtonDefaults.colors(selectedColor = Primary)
        )
        Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(if (isAvailable) Color.Green else Color.Red)
        )
    }
}

@Composable
fun DateCard(date: String, isSelected: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(60.dp)
            .selectable(selected = isSelected, onClick = onSelect),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Primary.copy(alpha = 0.1f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.split("\n")[0],
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Primary else Color.Black
            )
            Text(
                text = date.split("\n")[1],
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Primary else Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleUI() {
    var selectedDateTime by remember { mutableStateOf<Pair<String, String>?>(null) }
    var isDialogOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (selectedDateTime != null) {
            val (date, time) = selectedDateTime!!
            ScheduledCard(
                deliveryTime = "$date, $time",
                status = "Agendado"
            )
        } else {
            ScheduledCard(
                deliveryTime = "Aún no cuentas con una cita",
                status = "No Agendado"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        RescheduleButton(
            onOpenDialog = { isDialogOpen = true }
        )

        if (isDialogOpen) {
            TimeSelectionDialog(
                onDismissRequest = { isDialogOpen = false },
                onTimeSelected = { date, time ->
                    selectedDateTime = Pair(date, time)
                    isDialogOpen = false
                }
            )
        }
    }
}