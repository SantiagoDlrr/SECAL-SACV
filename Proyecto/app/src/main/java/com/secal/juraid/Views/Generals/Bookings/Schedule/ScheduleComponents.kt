package com.secal.juraid.Views.Generals.Bookings.Schedule
import TimeSlot
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import com.secal.juraid.ui.theme.Primary


// Schedule Components
@Composable
fun DateCard(
    date: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val (dayName, dateText) = date.split("\n")

    Card(
        modifier = Modifier
            .width(80.dp)
            .height(60.dp)
            .clickable(onClick = onSelect),
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
                text = dayName,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Primary else Color.Black
            )
            Text(
                text = dateText,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Primary else Color.Gray
            )
        }
    }
}


@Composable
fun TimeSlotItem(
    timeSlot: TimeSlot,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                enabled = timeSlot.isAvailable,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Primary,
                    disabledSelectedColor = Color.Gray,
                    disabledUnselectedColor = Color.Gray
                )
            )
            Text(
                text = timeSlot.displayTime,
                modifier = Modifier.padding(start = 8.dp),
                color = if (timeSlot.isAvailable) Color.Black else Color.Gray
            )
        }

        // Availability indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = if (timeSlot.isAvailable) Color.Green else Color.Red,
                    shape = CircleShape
                )
        )
    }
}
