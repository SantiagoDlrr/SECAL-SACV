package com.secal.juraid.Views.Admin.SuitViews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.HomeViewModel

@Composable
fun CategoriasView(navController: NavController, viewModel: HomeViewModel = HomeViewModel()) {
    val categories by viewModel.categories.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<HomeViewModel.Category?>(null) }
    var deletingCategory by remember { mutableStateOf<HomeViewModel.Category?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir categoría")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TitlesView(title = "Gestión de Categorías")
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                items(categories) { category ->
                    CategoryItem(
                        category = category,
                        onEditClick = {
                            editingCategory = category
                            showEditDialog = true
                        },
                        onDeleteClick = {
                            deletingCategory = category
                            showDeleteDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            if (showAddDialog) {
                CategoryDialog(
                    title = "Añadir Categoría",
                    onDismiss = { showAddDialog = false },
                    onConfirm = { name ->
                        viewModel.addCategory(name)
                        showAddDialog = false
                    }
                )
            }
            if (showEditDialog && editingCategory != null) {
                CategoryDialog(
                    title = "Editar Categoría",
                    initialName = editingCategory!!.name_category,
                    onDismiss = {
                        showEditDialog = false
                        editingCategory = null
                    },
                    onConfirm = { name ->
                        viewModel.updateCategory(editingCategory!!.ID_Category, name)
                        showEditDialog = false
                        editingCategory = null
                    }
                )
            }
            if (showDeleteDialog && deletingCategory != null) {
                DeleteConDialog(
                    categoryName = deletingCategory!!.name_category,
                    onConfirm = {
                        viewModel.deleteCategory(deletingCategory!!.ID_Category)
                        showDeleteDialog = false
                        deletingCategory = null
                    },
                    onDismiss = {
                        showDeleteDialog = false
                        deletingCategory = null
                    }
                )
            }

            errorMessage?.let { message ->
                AlertDialog(
                    onDismissRequest = { viewModel.clearErrorMessage() },
                    title = { Text("Error") },
                    text = { Text(message) },
                    confirmButton = {
                        Button(onClick = { viewModel.clearErrorMessage() }) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: HomeViewModel.Category,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.name_category,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

@Composable
fun CategoryDialog(
    title: String,
    initialName: String = "",
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la categoría") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DeleteConDialog(
    categoryName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar eliminación") },
        text = { Text("¿Estás seguro de que quieres eliminar la categoría '$categoryName'?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}