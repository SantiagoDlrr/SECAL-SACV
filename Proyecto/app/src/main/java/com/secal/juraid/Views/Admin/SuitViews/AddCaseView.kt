//vista para añadir un caso nuevo

package com.secal.juraid.Views.Admin.SuitViews

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.secal.juraid.Routes
import com.secal.juraid.ViewModel.CasesViewModel
import com.secal.juraid.ViewModel.HomeViewModel
import com.secal.juraid.ViewModel.unitInvestigation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCaseView(navController: NavController, viewModel: CasesViewModel ) {
    //para ver qué función llamamos
    Log.d(TAG, "AddCaseView() called")

    var nombreAbogado by remember { mutableStateOf("") }
    var nombreCliente by remember { mutableStateOf("") }
    var nuc by remember { mutableStateOf("") }
    var carpetaJudicial by remember { mutableStateOf("") }
    var carpetaInvestigacion by remember { mutableStateOf("") }
    var accesoFv by remember { mutableStateOf("") }
    var passFv by remember { mutableStateOf("") }
    var fiscalTitular by remember { mutableStateOf("") }
    var drive by remember { mutableStateOf("") }

    var selectedUnidadInvestigacion by remember { mutableStateOf<unitInvestigation?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val unitInvestigations by viewModel.unitInvestigations.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Nuevo Caso") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = nombreAbogado,
                    onValueChange = { nombreAbogado = it },
                    label = { Text("Nombre del Abogado") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = nombreCliente,
                    onValueChange = { nombreCliente = it },
                    label = { Text("Nombre del Cliente") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = nuc,
                    onValueChange = { nuc = it },
                    label = { Text("NUC") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = carpetaJudicial,
                    onValueChange = { carpetaJudicial = it },
                    label = { Text("Carpeta Judicial") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = carpetaInvestigacion,
                    onValueChange = { carpetaInvestigacion = it },
                    label = { Text("Carpeta de Investigación") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedUnidadInvestigacion?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unidad de Investigación") },
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
                        unitInvestigations.forEach { unidad ->
                            DropdownMenuItem(
                                text = { Text(unidad.nombre) },
                                onClick = {
                                    selectedUnidadInvestigacion = unidad
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = accesoFv,
                    onValueChange = { accesoFv = it },
                    label = { Text("Acceso FV") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = passFv,
                    onValueChange = { passFv = it },
                    label = { Text("Password FV") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = fiscalTitular,
                    onValueChange = { fiscalTitular = it },
                    label = { Text("Fiscal Titular") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = drive,
                    onValueChange = { drive = it },
                    label = { Text("Drive") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Button(
                    onClick = {
                        scope.launch {
                            isSubmitting = true
                            try {
                                viewModel.addCase(
                                    nombreAbogado = nombreAbogado,
                                    nombreCliente = nombreCliente,
                                    nuc = nuc,
                                    carpetaJudicial = carpetaJudicial,
                                    carpetaInvestigacion = carpetaInvestigacion,
                                    accesoFv = accesoFv,
                                    passFv = passFv,
                                    fiscalTitular = fiscalTitular,
                                    idUnidadInvestigacion = selectedUnidadInvestigacion?.id,
                                    drive = drive
                                )
                                navController.navigate(Routes.casosVw)
                            } finally {
                                isSubmitting = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Añadir Caso")
                    }
                }
            }
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}