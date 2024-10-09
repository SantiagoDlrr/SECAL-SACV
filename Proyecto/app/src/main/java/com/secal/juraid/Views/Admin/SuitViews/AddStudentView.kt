import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AddStudentView(
    viewModel: AlumnosViewModel = viewModel(),
    onClose: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val addStudentResult by viewModel.addStudentResult.collectAsState()

    LaunchedEffect(addStudentResult) {
        if (addStudentResult is AddStudentResult.Success) {
            email = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico del alumno") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.addStudentByEmail(email)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir Alumno")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val result = addStudentResult) {
            is AddStudentResult.Loading -> CircularProgressIndicator()
            is AddStudentResult.Success -> {
                Text(result.message, color = MaterialTheme.colorScheme.primary)
            }
            is AddStudentResult.Error -> {
                Text(result.message, color = MaterialTheme.colorScheme.error)
            }
            null -> {}
            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onClose) {
            Text("Cerrar")
        }
    }
}