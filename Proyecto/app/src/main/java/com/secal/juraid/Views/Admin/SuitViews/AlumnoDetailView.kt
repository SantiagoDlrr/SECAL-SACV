package com.secal.juraid.Views.Admin.SuitViews

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.R
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.AlumnosViewModel
import com.secal.juraid.ViewModel.Case
import com.secal.juraid.ViewModel.CasesViewModel
import com.secal.juraid.ViewModel.Student
import kotlinx.coroutines.launch

@Composable
fun AlumnoDetailView(
    navController: NavController,
    studentId: String,
    alumnosViewModel: AlumnosViewModel = viewModel(),
    casesViewModel: CasesViewModel = viewModel()
) {
    val student by alumnosViewModel.getStudentById(studentId).collectAsState(initial = null)
    val assignedCases by casesViewModel.getAssignedCasesForStudent(studentId).collectAsState(initial = emptyList())
    val allCases by casesViewModel.cases.collectAsState()

    LaunchedEffect(Unit) {
        casesViewModel.loadAllData()
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { innerPadding ->
        student?.let { studentData ->
            AlumnoDetailContent(
                navController = navController,
                student = studentData,
                assignedCases = assignedCases,
                allCases = allCases,
                casesViewModel = casesViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        } ?: Text(
            "Cargando detalles del estudiante...",
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun AlumnoDetailContent(
    navController: NavController,
    student: Student,
    assignedCases: List<Case>,
    allCases: List<Case>,
    casesViewModel: CasesViewModel,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var isAssigning by remember { mutableStateOf(false) }
    var showUnassignConfirmation by remember { mutableStateOf(false) }
    var caseToUnassign by remember { mutableStateOf<Case?>(null) }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    StudentInfoSection(student)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Asignar nuevo caso")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Casos Asignados",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (assignedCases.isEmpty()) {
                        Text(
                            "No hay casos asignados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        assignedCases.forEach { case ->
                            AssignedCaseCard(
                                case = case,
                                onUnassign = {
                                    caseToUnassign = case
                                    showUnassignConfirmation = true
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        val unassignedCases = allCases.filter { case -> case.id !in assignedCases.map { it.id } }
        AssignCaseDialog(
            nucList = unassignedCases.map { it.NUC },
            isAssigning = isAssigning,
            onDismiss = { showDialog = false },
            onAssign = { selectedNuc ->
                isAssigning = true
                scope.launch {
                    try {
                        casesViewModel.assignCaseToStudent(student.id, selectedNuc)
                        showDialog = false
                    } catch (e: Exception) {
                        // Handle error
                    } finally {
                        isAssigning = false
                    }
                }
            }
        )
    }

    if (showUnassignConfirmation) {
        AlertDialog(
            onDismissRequest = { showUnassignConfirmation = false },
            title = { Text("Confirmar desasignación") },
            text = { Text("¿Estás seguro de que quieres desasignar este caso del alumno?") },
            confirmButton = {
                Button(
                    onClick = {
                        caseToUnassign?.let { case ->
                            scope.launch {
                                casesViewModel.unassignCaseFromStudent(student.id, case.id)
                                showUnassignConfirmation = false
                            }
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showUnassignConfirmation = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun StudentInfoSection(student: Student) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Foto del estudiante",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                "${student.name} ${student.first_last_name} ${student.second_last_name}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(student.email, style = MaterialTheme.typography.bodyMedium)
            Text("Teléfono: ${student.phone}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignedCaseCard(
    case: Case,
    onUnassign: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f),
        ),
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
                Text(
                    text = "NUC: ${case.NUC}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                StatusChip(status = case.status ?: 0)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Cliente",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = case.nombre_cliente,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Abogado",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = case.nombre_abogado,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Carpeta Judicial",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = case.carpeta_judicial,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Fiscal Titular",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = case.fiscal_titular,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onUnassign,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Desasignar caso")
            }
        }
    }
}

@Composable
fun StatusChip(status: Int) {
    val (backgroundColor, textColor, text) = when (status) {
        1 -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            "Activo"
        )
        0 -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "Inactivo"
        )
        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            "Desconocido"
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignCaseDialog(
    nucList: List<String>,
    isAssigning: Boolean,
    onDismiss: () -> Unit,
    onAssign: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedNuc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        title = { Text("Asignar caso a alumno") },
        text = {
            Column {
                Text("¿Qué caso quieres asignar?")
                Spacer(modifier = Modifier.height(16.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedNuc,
                        onValueChange = {},
                        label = { Text("NUC") },
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
                        nucList.forEach { nuc ->
                            DropdownMenuItem(
                                text = { Text(nuc) },
                                onClick = {
                                    selectedNuc = nuc
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAssign(selectedNuc) },
                enabled = selectedNuc.isNotEmpty() && !isAssigning
            ) {
                if (isAssigning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Aceptar")
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}