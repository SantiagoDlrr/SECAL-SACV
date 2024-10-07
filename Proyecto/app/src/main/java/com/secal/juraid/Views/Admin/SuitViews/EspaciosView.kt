package com.secal.juraid.Views.Admin.SuitViews

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.TopBar


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EspaciosView(navController: NavController) {
    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CitasConfirmadasView()
        }
    }
}

@Composable
fun CitasConfirmadasView() {
    var expandedMenuIndex by remember { mutableStateOf<Int?>(null) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var cancelReason by remember { mutableStateOf("") }
    var selectedCitaIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        CitasHeader()
        CitasList(
            expandedMenuIndex = expandedMenuIndex,
            onExpandMenu = { expandedMenuIndex = it },
            onShowDetails = { index ->
                selectedCitaIndex = index
                showDetailsDialog = true
            },
            onCancelCita = { index ->
                selectedCitaIndex = index
                showCancelDialog = true
            }
        )
    }

    if (showDetailsDialog) {
        CitaDetailsDialog(
            onDismiss = { showDetailsDialog = false }
        )
    }

    if (showCancelDialog) {
        CancelCitaDialog(
            cancelReason = cancelReason,
            onCancelReasonChange = { cancelReason = it },
            onConfirm = {
                println("Cita $selectedCitaIndex cancelada. Motivo: $cancelReason")
                showCancelDialog = false
                cancelReason = ""
                selectedCitaIndex = null
            },
            onDismiss = {
                showCancelDialog = false
                cancelReason = ""
                selectedCitaIndex = null
            }
        )
    }
}

@Composable
fun CitasHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Citas", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun CitasList(
    expandedMenuIndex: Int?,
    onExpandMenu: (Int) -> Unit,
    onShowDetails: (Int) -> Unit,
    onCancelCita: (Int) -> Unit
) {
    LazyColumn {
        items(7) { index ->
            CitaCard(
                index = index,
                isExpanded = expandedMenuIndex == index,
                onExpandMenu = { onExpandMenu(index) },
                onShowDetails = { onShowDetails(index) },
                onCancelCita = { onCancelCita(index) }
            )
        }
    }
}

@Composable
fun CitaCard(
    index: Int,
    isExpanded: Boolean,
    onExpandMenu: () -> Unit,
    onShowDetails: () -> Unit,
    onCancelCita: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(100.dp)
            .clip(MaterialTheme.shapes.medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text("Cita $index", fontWeight = FontWeight.Bold)
                Text("Descripción de la cita $index", maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Box {
                IconButton(onClick = onExpandMenu) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                }
                DropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { onExpandMenu() }
                ) {
                    DropdownMenuItem(
                        text = { Text("Detalles") },
                        onClick = {
                            onShowDetails()
                            onExpandMenu()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Cancelar cita") },
                        onClick = {
                            onCancelCita()
                            onExpandMenu()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CitaDetailsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalles de la Cita") },
        text = {
            Column {
                Text("Nombre: Emiliano Luna George")
                Text("Región: Región Monterrey")
                Text("Situación: En espera")
                Text("Fecha: 01/10/2024")
                Text("Hora: 13:00hrs")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun CancelCitaDialog(
    cancelReason: String,
    onCancelReasonChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cancelar cita") },
        text = {
            Column {
                Text("Por favor, indique el motivo de la cancelación:")
                TextField(
                    value = cancelReason,
                    onValueChange = onCancelReasonChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}