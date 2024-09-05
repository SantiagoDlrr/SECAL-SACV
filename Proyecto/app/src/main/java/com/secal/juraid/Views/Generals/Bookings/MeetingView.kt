import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.Routes
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingView(navController: NavController) {
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column {
                TitlesView(title = "Selecciona un horario para la cita")
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                ) {

                    CalendarView()
                    Spacer(modifier = Modifier.height(16.dp))
                    TimeSlotsList(navController = navController)
                }


            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView() {
    val currentDate = remember { LocalDate.now() }
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))

    Column (
    ){
        Text(
            text = currentDate.format(dateFormatter),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(20.dp)
        )

        LazyRow (){
            items(7) { dayOffset ->
                val date = currentDate.plusDays(dayOffset.toLong())
                DayItem(date, isSelected = dayOffset == 0)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayItem(date: LocalDate, isSelected: Boolean) {
    val dayOfWeekFormatter = DateTimeFormatter.ofPattern("E", Locale("es", "ES"))
    val dayOfMonthFormatter = DateTimeFormatter.ofPattern("d", Locale("es", "ES"))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .clickable(onClick = { })
    ) {
        Text(
            text = date.format(dayOfWeekFormatter),
            fontSize = 14.sp,
            color = Color.Gray
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent)
        ) {
            Text(
                text = date.format(dayOfMonthFormatter),
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun TimeSlotsList(navController: NavController) {
    val timeSlots = listOf("09:00am", "10:00am", "11:00am", "12:00pm", "01:00pm", "02:00pm", "03:00pm", "04:00pm")

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(timeSlots) { timeSlot ->
            TimeSlotItem(timeSlot, isSelected = timeSlot == "09:00am", navController = navController)
        }
    }
}

@Composable
fun TimeSlotItem(timeSlot: String, isSelected: Boolean, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) } // Estado para mostrar el diálogo

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { showDialog = true } // Mostrar diálogo al hacer clic
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = timeSlot,
                fontSize = 18.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.Black else Color.Gray
            )
        }
    }

    // Diálogo de confirmación
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false }, // Cerrar diálogo al hacer clic fuera
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    navController.navigate(Routes.homeVw)
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Rechazar")
                }
            },
            title = { Text("Aviso Importante") },
            text = { Text("Dado que la Clínica Penal del Instituto Tecnológico y de Estudios Superiores de Monterrey es un organismo interno, los casos atendidos por esta serán de conocimiento exclusivo de los alumnos involucrados. Esto tiene como objetivo mejorar la calidad de aprendizaje de los estudiantes. En ningún caso la persona representada será defendida por un alumno, sino por el o los abogados en turno. Al aceptar este aviso, usted acepta las condiciones para agendar una cita con nosotros, así como las estipulaciones en caso de que decidamos tomar su caso.") }
        )
    }
}

