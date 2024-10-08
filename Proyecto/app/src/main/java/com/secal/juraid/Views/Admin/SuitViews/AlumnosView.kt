import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.R
import com.secal.juraid.Routes
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.AlumnosViewModel
import com.secal.juraid.ViewModel.Student


@Composable
fun AlumnosView(
    navController: NavController,
    viewModel: AlumnosViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val students by viewModel.students.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
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
}

@Composable
fun AlumnosCardView(
    student: Student,
    navController: NavController
) {
    var expandedMenuIndex by remember { mutableStateOf<Int?>(null) }
    var showDeleteCaseDialog by remember { mutableStateOf(false) }
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
                IconButton(onClick = {expandedMenuIndex = 1}) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                }
                DropdownMenu(
                    expanded = expandedMenuIndex == 1,
                    onDismissRequest = { expandedMenuIndex = null },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    DropdownMenuItem(
                        text = { Text("Eliminar") },
                        onClick = {

                        }
                    )
                }
            }

        }
    }
}
