import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.secal.juraid.BottomBar
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditView(navController: NavController, viewModel: ProfileViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val profileData by viewModel.profileData.collectAsState()
    val imageUploadStatus by viewModel.imageUploadStatus.collectAsState()
    val updateResult by viewModel.updateResult.collectAsState()

    var desc by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let { viewModel.uploadProfileImage(it, context) }
    }

    LaunchedEffect(Unit) {
        viewModel.loadProfileData()
    }

    LaunchedEffect(profileData) {
        desc = profileData.desc
    }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            TitlesView(title = "Editar Perfil")

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
                    .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Image
                    Card(
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.size(160.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Box {
                            AsyncImage(
                                model = selectedImageUri ?: profileData.url_image,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            Button(
                                onClick = { imagePicker.launch("image/*") },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (selectedImageUri != null) Icons.Default.Edit else Icons.Default.Add,
                                    contentDescription = "Cambiar foto",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Image upload status
                    when (val status = imageUploadStatus) {
                        is ImageUploadStatus.Uploading -> CircularProgressIndicator()
                        is ImageUploadStatus.Success -> Text("Imagen subida con éxito", color = MaterialTheme.colorScheme.primary)
                        is ImageUploadStatus.Error -> Text(status.message, color = MaterialTheme.colorScheme.error)
                        else -> {}
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Biografía") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar Cambios")
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmación") },
            text = { Text("¿Estás seguro de que deseas guardar los cambios?") },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            textContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.updateProfile(desc)
                        }
                        showDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Handle update result
    LaunchedEffect(updateResult) {
        when (updateResult) {
            is UpdateProfileResult.Success -> {
                Toast.makeText(context, "Perfil actualizado con éxito", Toast.LENGTH_LONG).show()
                navController.navigateUp()
            }
            is UpdateProfileResult.Error -> {
                Toast.makeText(context, (updateResult as UpdateProfileResult.Error).message, Toast.LENGTH_LONG).show()
            }
            null -> {}
        }
        // Reset the update result after handling
        viewModel.resetUpdateResult()
    }
}