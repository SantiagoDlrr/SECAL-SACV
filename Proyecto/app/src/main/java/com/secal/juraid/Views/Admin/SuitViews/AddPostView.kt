//En la lista de articulos, hay un botón de añadir, ese botón te lleva aquí

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.secal.juraid.BottomBar
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostView(navController: NavController, viewModel: HomeViewModel) {
    //para ver qué función llamamos
    Log.d(TAG, "AddPostView() called")


    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<HomeViewModel.Category?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var urlHeader by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val categories by viewModel.categories.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(navController = navController) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                TitlesView(title = "Añadir Post")

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                    ) {
                        OutlinedTextField(
                            value = selectedCategory?.name_category ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(16.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name_category) },
                                    onClick = {
                                        selectedCategory = category
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    /*
                    OutlinedTextField(
                        value = urlHeader,
                        onValueChange = { urlHeader = it },
                        label = { Text("URL de la imagen") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                     */

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Contenido") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    //BOTON PARA IMAGEN
                    Button(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (selectedImageUri != null) "Cambiar imagen" else "Seleccionar imagen")
                    }

                    selectedImageUri?.let { uri ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(16.dp))
                        )
                        Text("Imagen seleccionada", style = MaterialTheme.typography.bodyMedium)
                    }

                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Añadir Post")
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        selectedCategory?.let { category ->
                            try {
                                /*if(selectedImageUri != null){
                                    val imageByteArray = selectedImageUri?.uriToByteArray(context)
                                    imageByteArray?.let {
                                        viewModel.uploadFile("postImage", imageByteArray)
                                    }
                                }*/
                                viewModel.addContentItem(
                                    title = title,
                                    category = category.ID_Category,
                                    imageUri = selectedImageUri,
                                    text = text,
                                    context = context
                                )
                                // Mostrar Toast cuando se añade el post exitosamente
                                Toast.makeText(context, "Post añadido con éxito", Toast.LENGTH_LONG).show()
                                navController.popBackStack()
                            } catch (e: Exception) {
                                // Mostrar Toast cuando hay un error al añadir el post
                                Toast.makeText(context, "Error al añadir el post", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            },
            title = { Text("Confirmación") },
            text = { Text("Estás a punto de publicar un post.") },
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    }
}