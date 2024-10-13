package com.secal.juraid.Views.Admin.SuitViews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.CasesViewModel
import com.secal.juraid.ViewModel.unitInvestigation
import kotlinx.coroutines.launch

@Composable
fun InvUnitView(navController: NavController, viewModel: CasesViewModel = CasesViewModel()) {
    val unitInvestigations by viewModel.unitInvestigations.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editingUnit by remember { mutableStateOf<unitInvestigation?>(null) }
    var deletingUnit by remember { mutableStateOf<unitInvestigation?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir unidad")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TitlesView(title = "Edita las unidades de investigación")
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)

            ) {
                items(unitInvestigations) { unit ->
                    UnitInvestigationItem(
                        unit = unit,
                        onEditClick = {
                            editingUnit = unit
                            showEditDialog = true
                        },
                        onDeleteClick = {
                            deletingUnit = unit
                            showDeleteDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            if (showAddDialog) {
                UnitInvestigationDialog(
                    title = "Añadir Unidad de Investigación",
                    onDismiss = { showAddDialog = false },
                    onConfirm = { nombre, direccion ->
                        scope.launch {
                            viewModel.addUnitInvestigation(nombre, direccion)
                            showAddDialog = false
                        }
                    }
                )
            }
            if (showEditDialog && editingUnit != null) {
                UnitInvestigationDialog(
                    title = "Editar Unidad de Investigación",
                    initialNombre = editingUnit!!.nombre,
                    initialDireccion = editingUnit!!.direccion,
                    onDismiss = {
                        showEditDialog = false
                        editingUnit = null
                    },
                    onConfirm = { nombre, direccion ->
                        scope.launch {
                            viewModel.updateUnitInvestigation(editingUnit!!.id, nombre, direccion)
                            showEditDialog = false
                            editingUnit = null
                        }
                    }
                )
            }
            if (showDeleteDialog && deletingUnit != null) {
                DeleteConfirmationDialog(
                    unitName = deletingUnit!!.nombre,
                    onConfirm = {
                        scope.launch {
                            viewModel.deleteUnitInvestigation(deletingUnit!!.id)
                            showDeleteDialog = false
                            deletingUnit = null
                        }
                    },
                    onDismiss = {
                        showDeleteDialog = false
                        deletingUnit = null
                    }
                )
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    unitName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar eliminación") },
        text = { Text("¿Estás seguro de que quieres eliminar la unidad '$unitName'?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun UnitInvestigationItem(
    unit: unitInvestigation,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = unit.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = unit.direccion, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

@Composable
fun UnitInvestigationDialog(
    title: String,
    initialNombre: String = "",
    initialDireccion: String = "",
    onDismiss: () -> Unit,
    onConfirm: (nombre: String, direccion: String) -> Unit
) {
    var nombre by remember { mutableStateOf(initialNombre) }
    var direccion by remember { mutableStateOf(initialDireccion) }
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isLoading = true
                    onConfirm(nombre, direccion)
                },
                enabled = !isLoading
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}