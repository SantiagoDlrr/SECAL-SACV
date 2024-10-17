// aquí se muestra la lista de casos y las citas pasadas
import android.content.ContentValues.TAG
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.LocalUserViewModel
import com.secal.juraid.Model.UserRepository
import com.secal.juraid.Routes
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.Case
import com.secal.juraid.ViewModel.CasesViewModel
import com.secal.juraid.ViewModel.CitasViewModel
import com.secal.juraid.ViewModel.HomeViewModel
import com.secal.juraid.ViewModel.UserViewModel
import com.secal.juraid.supabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasosView(navController: NavController, viewModel: CasesViewModel, citasViewModel: CitasViewModel) {

    BackHandler {
        navController.navigate(Routes.suitVw) {
            // Limpia el back stack hasta HomeView
            popUpTo(Routes.casosVw) {
                inclusive = true
            }
            // Evita múltiples copias de HomeView en el stack
            launchSingleTop = true
        }
    }

    Log.d(TAG, "CasosView() called")

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Casos", "Asesorías")


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
                        Button(
                            onClick = { navController.navigate(Routes.invUnitVw) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("Unidades de investigación")
                        }

                        CasosCardView(navController = navController)


                }
                1 -> CitasPasadasView(viewModel = citasViewModel)
            }
        }
    }
}


//es el display de casos
@Composable
fun CasosCardView(navController: NavController) {
    val viewModel: CasesViewModel = viewModel()
    var expandedMenuIndex by remember { mutableStateOf<Int?>(null) }
    var showDeleteCaseDialog by remember { mutableStateOf(false) }
    var deletingCaseId by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()
    val cases by viewModel.activeCases.collectAsState()
    val userRole by LocalUserViewModel.current.userRole.collectAsState()
    val cases2 by viewModel.activeCases.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }



    if (cases.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No se encontraron casos")
        }
    }

    LazyColumn {
        items(cases2) { case ->
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
                            viewModel.deleteCase(it, )
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
    var showRechazarDialog by remember { mutableStateOf(false) }
    var selectedCita by remember { mutableStateOf<CitasViewModel.Cita?>(null) }


    val userViewModel = LocalUserViewModel.current
    val abogado by userViewModel.userName.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.loadCitasPasadas()
    }

    if (citasPasadas.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No se encontraron asesorías por confirmar")
        }
    }

    LazyColumn {
        items(citasPasadas) { cita ->
            CitaCard(
                cita = cita,
                onRepresentar = {
                    selectedCita = cita
                    showRepresentarDialog = true
                },
                onRechazar = {
                    selectedCita = cita
                    showRechazarDialog = true
                }
            )
        }
    }

    if (showRepresentarDialog) {
        AlertDialog(
            onDismissRequest = { showRepresentarDialog = false },
            title = { Text("Representar") },
            text = { Text("¿Deseas aceptar este caso?", color = MaterialTheme.colorScheme.onSecondaryContainer) },
            confirmButton = {
                Button(onClick = {
                    selectedCita?.let { viewModel.representarCita(it, abogado) }
                    showRepresentarDialog = false

                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(onClick = { showRepresentarDialog = false }) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    }

    if (showRechazarDialog) {
        AlertDialog(
            onDismissRequest = { showRechazarDialog = false },
            title = { Text("Rechazar") },
            text = { Text("¿Deseas rechazar este caso?",  color = MaterialTheme.colorScheme.onSecondaryContainer) },
            confirmButton = {
                Button(onClick = {
                    selectedCita?.let { viewModel.rechazarCita(it.id, it.id_usuario.toString()) }
                    showRechazarDialog = false
                }) {
                    Text("Rechazar")
                }
            },
            dismissButton = {
                Button(onClick = { showRechazarDialog = false }) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        )
    }
}

@Composable
fun CitaCard(cita: CitasViewModel.Cita, onRepresentar: () -> Unit, onRechazar: () -> Unit) {
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${cita.nombre ?: ""} ${cita.apellido ?: ""}",
                    maxLines = 2,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Fecha: ${cita.fecha ?: "No disponible"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hora: ${cita.hora ?: "No disponible"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Situación: ${CitasViewModel.Cita.getNombreSituacion(cita.id_situacion)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.width(140.dp)
            ) {
                Button(
                    onClick = { onRepresentar() },
                    modifier = Modifier.width(140.dp)
                ) {
                    Text("Representar")
                }
                Button(
                    onClick = { onRechazar() },
                    modifier = Modifier.width(140.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Rechazar")
                }
            }
        }
    }
}

