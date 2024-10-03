package com.secal.juraid.Views.Generals.Bookings

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.secal.juraid.BottomBar
import com.secal.juraid.Routes
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar
import com.secal.juraid.ui.theme.Purple40
import kotlinx.atomicfu.TraceBase.None.append


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HelpView(navController: NavController) {
    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) {
            Column (
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CasoFormView(navController)
            }
        }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasoFormView(navController: NavController) {

    var selectedOption by remember { mutableStateOf("") }
    val options = listOf("Víctima", "Investigado")
    var termsAccepted by remember { mutableStateOf(false) }


    var showDialog by remember { mutableStateOf(false) } // Estado para mostrar el diálogo

    Scaffold(
    ) {

        TitlesView(title = "Escribe la información de tu caso")

        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Agendar Asesoría Legal",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    ScheduledCard(
                        deliveryTime = "Mañana, 07:00AM - 09:00AM",
                        status = "Agendado"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    RescheduleButton()
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Selecciona tu región:", style = MaterialTheme.typography.labelLarge)

                        Spacer(modifier = Modifier.height(16.dp))

                        var expanded by remember { mutableStateOf(false) }
                        var selectedOption by remember { mutableStateOf("Selecciona tu opción") }
                        val options = listOf("Apodaca", "Escobedo", "Guadalupe", "Monterrey", "San Nicolás", "San Pedro")

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedOption,
                                onValueChange = {},
                                label = { Text("Selecciona tu región") },
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                options.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedOption = option
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Selecciona tu situación:", style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        options.forEach { option ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (option == selectedOption),
                                        onClick = { selectedOption = option }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (option == selectedOption),
                                    onClick = { selectedOption = option },
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = option)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // AVISO PRIVACIDAD
                        Text("Términos y Condiciones", style = MaterialTheme.typography.labelLarge)

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = termsAccepted,
                                onCheckedChange = { termsAccepted = it },
                                modifier = Modifier
                                    .padding(0.dp)
                                    .size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            val annotatedText = buildAnnotatedString {
                                append("Acepto que un ")
                                pushStringAnnotation(tag = "alumno", annotation = "")
                                withStyle(style = SpanStyle(color = Purple40, textDecoration = TextDecoration.Underline)) {
                                    append("alumno")
                                }
                                pop()
                                append(" esté presente durante la reunión")
                            }

                            ClickableText(
                                text = annotatedText,
                                onClick = {showDialog = true}
                            )
                            if (showDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDialog = false },
                                    confirmButton = {},
                                    dismissButton = {},
                                    title = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Aviso Importante",
                                                style = MaterialTheme.typography.titleLarge,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = { showDialog = false },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Cerrar",
                                                    tint = Color.Gray
                                                )
                                            }
                                        }
                                    },
                                    text = {
                                        Text("Dado que la Clínica Penal del Instituto Tecnológico y de Estudios Superiores de Monterrey es un organismo interno, los casos atendidos por esta serán de conocimiento exclusivo de los alumnos involucrados. Esto tiene como objetivo mejorar la calidad de aprendizaje de los estudiantes. En ningún caso la persona representada será defendida por un alumno, sino por el o los abogados en turno. Al aceptar este aviso, usted acepta las condiciones para agendar una cita con nosotros, así como las estipulaciones en caso de que decidamos tomar su caso.")
                                    }
                                )
                            }

                        }

                        // AVISO PRIVACIDAD

                        Button(
                            onClick = { navController.navigate(Routes.meetingVw) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = termsAccepted
                        ) {
                            Text("Siguiente")
                        }
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun PreviewHelpView() {
    val navController = rememberNavController()

    var selectedOption by remember { mutableStateOf("") }
    val options = listOf("Víctima", "Investigado")
    var termsAccepted by remember { mutableStateOf(false) }



    var showDialog by remember { mutableStateOf(false) } // Estado para mostrar el diálogo

    Scaffold(
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Selecciona tu región:", style = MaterialTheme.typography.labelLarge)

                        Spacer(modifier = Modifier.height(16.dp))

                        var expanded by remember { mutableStateOf(false) }
                        var selectedOption by remember { mutableStateOf("Selecciona tu opción") }
                        val options = listOf("Apodaca", "Escobedo", "Guadalupe", "Monterrey", "San Nicolás", "San Pedro")

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedOption,
                                onValueChange = {},
                                label = { Text("Selecciona tu región") },
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                options.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedOption = option
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Selecciona tu situación:", style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        options.forEach { option ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (option == selectedOption),
                                        onClick = { selectedOption = option }
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (option == selectedOption),
                                    onClick = { selectedOption = option },
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = option)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // AVISO PRIVACIDAD
                        Text("Términos y Condiciones", style = MaterialTheme.typography.labelLarge)

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = termsAccepted,
                                onCheckedChange = { termsAccepted = it },
                                modifier = Modifier
                                    .padding(0.dp)
                                    .size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            val annotatedText = buildAnnotatedString (){
                                append("Acepto que un ")
                                pushStringAnnotation(tag = "alumno", annotation = "")
                                withStyle(style = SpanStyle(color = Purple40, textDecoration = TextDecoration.Underline)) {
                                    append("alumno")
                                }
                                pop()
                                append(" esté presente durante la reunión")
                            }

                            ClickableText(
                                text = annotatedText,
                                onClick = {showDialog = true}
                            )
                            if (showDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDialog = false },
                                    confirmButton = {},
                                    dismissButton = {},
                                    title = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Aviso Importante",
                                                style = MaterialTheme.typography.titleLarge,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = { showDialog = false },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Cerrar",
                                                    tint = Color.Gray
                                                )
                                            }
                                        }
                                    },
                                    text = {
                                        Text("Dado que la Clínica Penal del Instituto Tecnológico y de Estudios Superiores de Monterrey es un organismo interno, los casos atendidos por esta serán de conocimiento exclusivo de los alumnos involucrados. Esto tiene como objetivo mejorar la calidad de aprendizaje de los estudiantes. En ningún caso la persona representada será defendida por un alumno, sino por el o los abogados en turno. Al aceptar este aviso, usted acepta las condiciones para agendar una cita con nosotros, así como las estipulaciones en caso de que decidamos tomar su caso.")
                                    }
                                )
                            }

                        }

                        // AVISO PRIVACIDAD

                        Button(
                            onClick = { navController.navigate(Routes.meetingVw) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = termsAccepted
                        ) {
                            Text("Siguiente")
                        }
                    }
                }
            }
        }
    }

}