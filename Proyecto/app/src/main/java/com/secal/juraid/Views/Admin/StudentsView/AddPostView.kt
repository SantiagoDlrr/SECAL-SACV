package com.secal.juraid.Views.Admin.StudentsView

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.secal.juraid.BottomBar
import com.secal.juraid.TopBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.secal.juraid.ViewModel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPostView(navController: NavController, viewModel: HomeViewModel) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<HomeViewModel.Category?>(null) }
    var urlHeader by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    // State to hold the list of categories
    var categories by remember { mutableStateOf<List<HomeViewModel.Category>>(emptyList()) }

    // Fetch categories when the composable is first created
    LaunchedEffect(Unit) {
        categories = viewModel.getCategoriesfromDatabase()
    }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(navController = navController) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            // Dropdown for category selection
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = {},
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name_category ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = false,
                    onDismissRequest = {}
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name_category) },
                            onClick = {
                                selectedCategory = category
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = urlHeader,
                onValueChange = { urlHeader = it },
                label = { Text("URL de la imagen") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth().weight(1f).padding(bottom = 8.dp)
            )

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Añadir Post")
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    selectedCategory?.let { category ->
                        viewModel.addContentItem(
                            title = title,
                            category = category.ID_Category,
                            urlHeader = urlHeader,
                            text = text
                        )
                    }
                    navController.popBackStack()
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
            text = { Text("Estás a punto de publicar un post.") }
        )
    }
}

