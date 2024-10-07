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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(8.dp)),
        onClick = { navController.navigate("${Routes.alumnoDetailVw}/${student.id}") },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Placeholder",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
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
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "More options",
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}