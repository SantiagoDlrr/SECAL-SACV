import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.secal.juraid.Routes
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.Case
import com.secal.juraid.ViewModel.CasesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasosView(navController: NavController, viewModel: CasesViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Casos", "Asesorías")

    val cases by viewModel.cases.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
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


@Composable
fun CasosCardView(navController: NavController, cases: List<Case>) {
    LazyColumn {
        items(cases) { case ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                onClick = { navController.navigate("${Routes.detalleVw}/${case.id}") },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(case.nombre_cliente, fontWeight = FontWeight.Bold)
                        Text("NUC: ${case.NUC}", maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Abogado: ${case.nombre_abogado}", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }
            }
        }
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

