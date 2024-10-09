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
import com.secal.juraid.ViewModel.UserViewModel
import com.secal.juraid.Views.Admin.SuitViews.StatusChip
import com.secal.juraid.supabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasosView(navController: NavController, viewModel: CasesViewModel) {
    //para ver qué función llamamos
    Log.d(TAG, "CasosView() called")

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Casos", "Asesorías")

    val cases by viewModel.activeCases.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.addCaseVw) }  //Se va a AddPostView.kt
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
                1 -> CitasPasadasView() // Implementa esta vista según sea necesario
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
    var selectedCitaIndex by remember { mutableStateOf<Int?>(null) }
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
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
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
                        if (userRole == 1) { // 1 = Abogado
                            Box {
                                IconButton(onClick = { expandedMenuIndex = case.id }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                                }
                                DropdownMenu(
                                    expanded = expandedMenuIndex == case.id,
                                    onDismissRequest = { expandedMenuIndex = null }
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

                }
            }
        }
    }

    if (showDeleteCaseDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteCaseDialog = false },
            title = { Text("Eliminar caso") },
            text = { Text("¿Estás seguro de que deseas eliminar este caso?") },
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
fun CitasPasadasView() {
    var showAcceptDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }


    var currentCitaIndex by remember { mutableStateOf(-1) }

    LazyColumn {
        items(7) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Cita $index", fontWeight = FontWeight.Bold)
                        Text("Nombre")
                        Text("Fecha: 02/10/2024")
                    }
                    Row (
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Button(onClick = {
                            currentCitaIndex = index
                            showAcceptDialog = true
                        }) {
                            Text("Representar")
                            //Icon(imageVector = Icons.Default.Check, contentDescription = "Aceptar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            currentCitaIndex = index
                            showRejectDialog = true
                        }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Rechazar")
                        }
                    }
                }
            }
        }
    }

    if (showAcceptDialog) {
        AlertDialog(
            onDismissRequest = { showAcceptDialog = false },
            title = { Text("Representar") },
            text = { Text("¿Deseas aceptar esta caso?") },
            confirmButton = {
                Button(onClick = {
                    // Lógica para aceptar la cita
                    println("Caso $currentCitaIndex aceptado")
                    showAcceptDialog = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(onClick = { showAcceptDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("No representar") },
            text = { Text("¿Deseas no representar este caso?") },
            confirmButton = {
                Button(onClick = {
                    // Lógica para rechazar la cita
                    println("Caso $currentCitaIndex rechazado")
                    showRejectDialog = false
                }) {
                    Text("Rechazar")
                }
            },
            dismissButton = {
                Button(onClick = { showRejectDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

