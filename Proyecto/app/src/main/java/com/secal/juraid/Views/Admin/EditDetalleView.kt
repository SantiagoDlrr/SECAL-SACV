package com.secal.juraid.Views.Admin

import CaseDetailViewModel
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.R
import com.secal.juraid.Routes
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.HomeViewModel
import kotlinx.coroutines.launch

//editar un caso
@Composable
fun EditDetalleView(navController: NavController, viewModel: CaseDetailViewModel, caseId: Int) {
    //para ver qué función llamamos
    Log.d(TAG, "EditDetalleView() called")
    
    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TitlesView(title = "Edita la Información de Caso")
            Spacer(modifier = Modifier.height(16.dp))
            EditCard(navController, viewModel, caseId)
        }

    }
}

@Composable
fun EditCard(navController: NavController, viewModel: CaseDetailViewModel, caseId: Int) {
    //val viewModel: CaseDetailViewModel = viewModel()
    val caseDetail by viewModel.caseDetail.collectAsState()
    val hyperlinks by viewModel.hyperlinks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()

    var nombreCliente by remember { mutableStateOf("") }
    var nuc by remember { mutableStateOf("") }
    var carpetaJudicial by remember { mutableStateOf("") }
    var carpetaInvestigacion by remember { mutableStateOf("") }
    var fiscalTitular by remember { mutableStateOf("") }
    var drive by remember { mutableStateOf("") }

    // Para el diálogo de agregar nuevo hipervínculo
    var showAddHyperlinkDialog by remember { mutableStateOf(false) }
    var newHyperlinkText by remember { mutableStateOf("") }
    var newHyperlinkLink by remember { mutableStateOf("") }

    // Para el diálogo de editar hipervínculo existente
    var showEditHyperlinkDialog by remember { mutableStateOf(false) }
    var editingHyperlink by remember { mutableStateOf<CaseDetailViewModel.Hiperlink?>(null) }
    var editHyperlinkText by remember { mutableStateOf("") }
    var editHyperlinkLink by remember { mutableStateOf("") }

    //Para diálogo eliminar hipervínculo
    var showDeleteHyperlinkDialog by remember { mutableStateOf(false) }
    var deletingHyperlink by remember { mutableStateOf<CaseDetailViewModel.Hiperlink?>(null) }

    LaunchedEffect(caseId) {
        viewModel.loadCaseDetail(caseId)
    }

    LaunchedEffect(caseDetail) {
        caseDetail?.let { case ->
            nombreCliente = case.nombre_cliente
            nuc = case.NUC
            carpetaJudicial = case.carpeta_judicial
            carpetaInvestigacion = case.carpeta_investigacion
            fiscalTitular = case.fiscal_titular
            drive = case.drive
        }
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = nombreCliente,
                onValueChange = { nombreCliente = it },
                label = { Text("Nombre del Cliente") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nuc,
                onValueChange = { nuc = it },
                label = { Text("NUC") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = carpetaJudicial,
                onValueChange = { carpetaJudicial = it },
                label = { Text("Carpeta Judicial") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = carpetaInvestigacion,
                onValueChange = { carpetaInvestigacion = it },
                label = { Text("Carpeta de Investigación") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = fiscalTitular,
                onValueChange = { fiscalTitular = it },
                label = { Text("Fiscal Titular") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = drive,
                onValueChange = { drive = it },
                label = { Text("Drive URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Hipervínculos",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            hyperlinks.forEach { hyperlink ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(hyperlink.texto, modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        editingHyperlink = hyperlink
                        editHyperlinkText = hyperlink.texto
                        editHyperlinkLink = hyperlink.link
                        showEditHyperlinkDialog = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar hipervínculo")
                    }
                    IconButton(onClick = {
                        deletingHyperlink = hyperlink
                        showDeleteHyperlinkDialog = true
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar hipervínculo")
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Button(
                onClick = { showAddHyperlinkDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Añadir Hipervínculo")
            }

            Spacer(modifier = Modifier.height(24.dp))

            //BOTÓN GUARDAR CAMBIOS
            Button(
                onClick = {
                    scope.launch{
                        viewModel.updateCase(
                            caseId,
                            nombreCliente,
                            nuc,
                            carpetaJudicial,
                            carpetaInvestigacion,
                            fiscalTitular,
                            drive
                        )
                        navController.navigate("${Routes.detalleVw}/$caseId") {
                            popUpTo("${Routes.editDetalleVw}/$caseId") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                //enabled = selectedCategory != null
            ) {
                Text("Guardar cambios")
            }
        }

    }

    if (showAddHyperlinkDialog) {
        HyperlinkDialog(
            title = "Añadir Hipervínculo",
            text = newHyperlinkText,
            link = newHyperlinkLink,
            onTextChange = { newHyperlinkText = it },
            onLinkChange = { newHyperlinkLink = it },
            isLoading = isLoading,
            onDismiss = {
                showAddHyperlinkDialog = false
                newHyperlinkText = ""
                newHyperlinkLink = ""
            },
            onConfirm = {
                scope.launch {
                    viewModel.addHyperlink(caseId, newHyperlinkText, newHyperlinkLink)
                    showAddHyperlinkDialog = false
                    newHyperlinkText = ""
                    newHyperlinkLink = ""
                }
            }
        )
    }

    // Diálogo para editar hipervínculo existente
    if (showEditHyperlinkDialog) {
        HyperlinkDialog(
            title = "Editar Hipervínculo",
            text = editHyperlinkText,
            link = editHyperlinkLink,
            onTextChange = { editHyperlinkText = it },
            onLinkChange = { editHyperlinkLink = it },
            isLoading = isLoading,
            onDismiss = {
                showEditHyperlinkDialog = false
                editingHyperlink = null
            },
            onConfirm = {
                scope.launch {
                    editingHyperlink?.let { hyperlink ->
                        viewModel.updateHyperlink(
                            hyperlink.id,
                            editHyperlinkText,
                            editHyperlinkLink
                        )
                    }
                    showEditHyperlinkDialog = false
                    editingHyperlink = null
                }
            }
        )
    }

    // Diálogo para editar hipervínculo existente
    if (showDeleteHyperlinkDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteHyperlinkDialog = false },
            title = { Text("Eliminar hipervínculo") },
            text = { Text("¿Estás seguro de que deseas eliminar este hipervínculo?") },
            confirmButton = {
                Button(onClick = {
                    deletingHyperlink?.let { hyperlink ->
                        viewModel.deleteHyperlink(
                            hyperlink.id
                        )
                    }
                    showDeleteHyperlinkDialog = false
                    deletingHyperlink = null
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteHyperlinkDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun HyperlinkDialog(
    title: String,
    text: String,
    link: String,
    onTextChange: (String) -> Unit,
    onLinkChange: (String) -> Unit,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    label = { Text("Texto") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = link,
                    onValueChange = onLinkChange,
                    label = { Text("URL") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
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
