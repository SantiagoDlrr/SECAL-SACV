/*package com.secal.juraid.Views.Generals.Bookings

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
}*/