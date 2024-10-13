import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.Model.UserRepository
import com.secal.juraid.R
import com.secal.juraid.Routes
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar
import com.secal.juraid.supabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


@Composable
fun AlumnosView(
    navController: NavController,
    viewModel: AlumnosViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val students by viewModel.students.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showAddStudentDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val application = context.applicationContext as Application
    val userRepository = UserRepository(supabase, CoroutineScope(Dispatchers.IO))

    val profViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(application, userRepository)
    )


    val profileData by profViewModel.profileData.collectAsState()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddStudentDialog = true }, contentColor = MaterialTheme.colorScheme.onPrimary, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Alumno")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TitlesView(title = "Alumnos")

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                students.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No se encontraron estudiantes")
                    }
                }
                else -> {
                    LazyColumn {
                        items(students) { student ->
                            AlumnosCardView(student = student, navController = navController)
                        }
                    }
                }
            }
        }
    }

    if (showAddStudentDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddStudentDialog = false
                viewModel.resetAddStudentResult()
            },
            title = { Text("Añadir Alumno") },
            text = {
                AddStudentView(
                    viewModel = viewModel,
                    onClose = {
                        showAddStudentDialog = false
                        viewModel.resetAddStudentResult()
                    }
                )
            },
            confirmButton = { },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            textContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun AlumnosCardView(
    student: Student,
    navController: NavController,
    viewModel: AlumnosViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var expandedMenuIndex by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp)),
        onClick = { navController.navigate("${Routes.alumnoDetailVw}/${student.id}") },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Placeholder",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    "${student.name} ${student.first_last_name}",
                    fontWeight = FontWeight.Bold
                )
                Text(student.email, style = MaterialTheme.typography.bodyMedium)
            }
            Box {
                IconButton(onClick = { expandedMenuIndex = 1 }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                }
                DropdownMenu(
                    expanded = expandedMenuIndex == 1,
                    onDismissRequest = { expandedMenuIndex = null },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    DropdownMenuItem(
                        text = { Text("Desactivar alumno") },
                        onClick = {
                            expandedMenuIndex = null
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar desactivación") },
            text = { Text("¿Estás seguro que deseas desactivar a este alumno?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deactivateStudent(
                            studentId = student.id,
                            onSuccess = {
                                showDeleteDialog = false
                                snackbarMessage = "Alumno desactivado exitosamente"
                                showSnackbar = true
                            },
                            onError = { error ->
                                showDeleteDialog = false
                                snackbarMessage = error
                                showSnackbar = true
                            }
                        )
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            textContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }

    if (showSnackbar) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { showSnackbar = false }) {
                    Text("Cerrar")
                }
            }
        ) {
            Text(snackbarMessage)
        }
    }
}

