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
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico del alumno") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
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
                Text(result.message, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            is AddStudentResult.Error -> {
                Text(result.message, color = MaterialTheme.colorScheme.error)
            }
            null -> {}
            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onClose,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            ) {
            Text("Cerrar")
        }
    }
}