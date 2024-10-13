//Para editar los posts

package com.secal.juraid.Views.Admin

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.secal.juraid.BottomBar
import com.secal.juraid.Routes
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.HomeViewModel
import com.secal.juraid.Views.Generals.BaseViews.ArticuloDetailItem
import com.secal.juraid.Views.Generals.BaseViews.LoadingScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditArticuloView(navController: NavController, viewModel: HomeViewModel, postId: Int) {
    Log.d(TAG, "EditArticuloView() called")

    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var urlHeader by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<HomeViewModel.Category?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

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

    LaunchedEffect(postId) {
        viewModel.getFullContentItem(postId)?.let { item ->
            title = item.title
            content = item.text ?: ""
            urlHeader = item.url_header
            selectedCategory = categories.find { it.ID_Category == item.ID_Category }
            isLoading = false
        }
    }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        if (isLoading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                TitlesView(title = "Editar Post")

                Card(
                    modifier = Modifier.fillMaxWidth()
                        .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Header Card (Image Upload and Title)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                // Image Preview and Change Button
                                if (selectedImageUri != null || urlHeader.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    ) {
                                        Image(
                                            painter = rememberAsyncImagePainter(selectedImageUri ?: urlHeader),
                                            contentDescription = "Vista previa de imagen",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )

                                        Button(
                                            onClick = { imagePicker.launch("image/*") },
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Cambiar imagen"
                                                )
                                                Text("Cambiar imagen")
                                            }
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { imagePicker.launch("image/*") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Añadir imagen",
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Text("Seleccionar imagen de portada")
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                OutlinedTextField(
                                    value = title,
                                    onValueChange = { title = it },
                                    label = { Text("Título") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Category Selection Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "Categoría",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded }
                                ) {
                                    OutlinedTextField(
                                        value = selectedCategory?.name_category ?: "",
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
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
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Content Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "Contenido",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                OutlinedTextField(
                                    value = content,
                                    onValueChange = { content = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        focusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        cursorColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                )
                            }
                        }

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
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmación") },
            text = { Text("¿Estás seguro de que deseas guardar los cambios?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            selectedCategory?.let { category ->
                                try {
                                    viewModel.updateContentItem(
                                        postId = postId,
                                        title = title,
                                        category = category.ID_Category,
                                        urlHeader = urlHeader,
                                        text = content,
                                        imageUri = selectedImageUri,
                                        context = context
                                    )
                                    Toast.makeText(context, "Cambios guardados con éxito", Toast.LENGTH_LONG).show()
                                    navController.navigateUp()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error al guardar los cambios", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        showDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false },

                ) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            textContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}