package com.secal.juraid.Views.Admin.SuitViews

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.CitasViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.secal.juraid.TitlesView


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EspaciosView(
    navController: NavController,
    citasViewModel: CitasViewModel = viewModel()
) {
    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CitasConfirmadasView(viewModel = citasViewModel)
        }
    }
}


@Composable
fun CitasConfirmadasView(viewModel: CitasViewModel) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var selectedCita by remember { mutableStateOf<CitasViewModel.Cita?>(null) }
    var cancelReason by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TitlesView(title = "Citas")

        when (val state = uiState) {
            is CitasViewModel.UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is CitasViewModel.UiState.Success -> {
                if (state.citas.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay citas confirmadas")
                    }
                }
                LazyColumn {
                    items(state.citas) { cita ->
                        CitaCard(
                            cita = cita,
                            onCancelCita = {
                                selectedCita = cita
                                showCancelDialog = true
                            }
                        )
                    }
                }
            }
            is CitasViewModel.UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: ${state.message}")
                }
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = {
                showCancelDialog = false
                cancelReason = ""
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            textContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            title = { Text("Cancelar Cita") },
            text = {
                Column {
                    Text("¿Estás seguro de que deseas cancelar esta cita?")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cancelReason,
                        onValueChange = { cancelReason = it },
                        label = { Text("Motivo de cancelación") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedCita?.let { cita ->
                            viewModel.cancelarCita(cita, cancelReason)
                        }
                        showCancelDialog = false
                        cancelReason = ""
                    },
                    enabled = cancelReason.isNotBlank()
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        cancelReason = ""
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CitaCard(
    cita: CitasViewModel.Cita,
    onCancelCita: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${cita.nombre} ${cita.apellido}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Fecha: ${cita.fecha}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Hora: ${cita.hora}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Región: ${CitasViewModel.Cita.getNombreRegion(cita.id_region)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Situación: ${CitasViewModel.Cita.getNombreSituacion(cita.id_situacion)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                IconButton(onClick = onCancelCita) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Cancelar cita"
                    )
                }
            }
        }
    }
}
