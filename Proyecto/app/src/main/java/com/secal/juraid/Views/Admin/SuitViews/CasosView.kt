// aquí se muestra la lista de casos y las citas pasadas
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.Model.UserRepository
import com.secal.juraid.Routes
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.Case
import com.secal.juraid.ViewModel.CasesViewModel
import com.secal.juraid.ViewModel.CitasViewModel
import com.secal.juraid.ViewModel.UserViewModel
import com.secal.juraid.Views.Admin.SuitViews.StatusChip
import com.secal.juraid.supabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasosView(navController: NavController, viewModel: CasesViewModel, citasViewModel: CitasViewModel) {
    Log.d(TAG, "CasosView() called")

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Casos", "Asesorías")

    val cases by viewModel.activeCases.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllData()
        citasViewModel.loadCitasPasadas()  // Cargar citas pasadas
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.addCaseVw) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar caso")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        CasosCardView(navController = navController, cases = cases)
                    }
                }
                1 -> CitasPasadasView(viewModel = citasViewModel)
            }
        }
    }
}


//es el display de casos
@Composable
fun CasosCardView(navController: NavController, cases: List<Case>) {
    val viewModel: CasesViewModel = viewModel()
    var expandedMenuIndex by remember { mutableStateOf<Int?>(null) }
    var showDeleteCaseDialog by remember { mutableStateOf(false) }
    var deletingCaseId by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    val userRole by UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO))).userRole.collectAsState()

    LazyColumn {
        items(cases) { case ->
            val caseId = case.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                onClick = { navController.navigate("${Routes.detalleVw}/${case.id}") },
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
                        Text(
                            text = "NUC: ${case.NUC}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (userRole == 1) { // 1 = Abogado
                            Box {
                                IconButton(onClick = { expandedMenuIndex = case.id }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                                }
                                DropdownMenu(
                                    expanded = expandedMenuIndex == case.id,
                                    onDismissRequest = { expandedMenuIndex = null },
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Editar") },
                                        onClick = {
                                            // Lógica para editar el caso
                                            Log.d(TAG, "CASE ID $caseId")
                                            navController.navigate("${Routes.editDetalleVw}/$caseId")
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Eliminar") },
                                        onClick = {
                                            showDeleteCaseDialog = true
                                            deletingCaseId = case.id
                                            expandedMenuIndex = null
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Cliente",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cliente: " + case.nombre_cliente,
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
                            text = "Abogado: " + case.nombre_abogado,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    if (showDeleteCaseDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteCaseDialog = false },
            title = { Text("Eliminar caso") },
            text = { Text("¿Estás seguro de que deseas eliminar este caso?") },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            textContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        deletingCaseId?.let {
                            viewModel.deleteCase(
                                it
                            )
                        }
                        showDeleteCaseDialog = false
                        deletingCaseId = null
                    }
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteCaseDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CitasPasadasView(viewModel: CitasViewModel) {
    val citasPasadas by viewModel.citasPasadas.collectAsState()
    var showRepresentarDialog by remember { mutableStateOf(false) }
    var showNoRepresentarDialog by remember { mutableStateOf(false) }
    var selectedCita by remember { mutableStateOf<CitasViewModel.Cita?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadCitasPasadas()
    }

    LazyColumn {
        items(citasPasadas) { cita ->
            CitaCard(
                cita = cita,
                onRepresentar = {
                    selectedCita = cita
                    showRepresentarDialog = true
                },
                onNoRepresentar = {
                    selectedCita = cita
                    showNoRepresentarDialog = true
                }
            )
        }
    }

    if (showRepresentarDialog) {
        AlertDialog(
            onDismissRequest = { showRepresentarDialog = false },
            title = { Text("Representar") },
            text = { Text("¿Deseas aceptar este caso?") },
            confirmButton = {
                Button(onClick = {
                    // Lógica para aceptar la cita
                    selectedCita?.let { viewModel.representarCita(it.id) }
                    showRepresentarDialog = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(onClick = { showRepresentarDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showNoRepresentarDialog) {
        AlertDialog(
            onDismissRequest = { showNoRepresentarDialog = false },
            title = { Text("No representar") },
            text = { Text("¿Deseas no representar este caso?") },
            confirmButton = {
                Button(onClick = {
                    // Lógica para rechazar la cita
                    selectedCita?.let { viewModel.noRepresentarCita(it.id) }
                    showNoRepresentarDialog = false
                }) {
                    Text("Rechazar")
                }
            },
            dismissButton = {
                Button(onClick = { showNoRepresentarDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CitaCard(cita: CitasViewModel.Cita, onRepresentar: () -> Unit, onNoRepresentar: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp)),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Column {
                Text("${cita.nombre} ${cita.apellido}", fontWeight = FontWeight.Bold)
                Text("Fecha: ${cita.fecha ?: "No disponible"}")
                Text("Situación: ${CitasViewModel.Cita.getNombreSituacion(cita.id_situacion)}")
            }
            Row {
                Button(onClick = onRepresentar) {
                    Text("Representar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onNoRepresentar) {
                    Icon(Icons.Default.Close, contentDescription = "No representar")
                }
            }
        }
    }
}

