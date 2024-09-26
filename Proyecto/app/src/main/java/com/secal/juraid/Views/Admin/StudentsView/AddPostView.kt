package com.secal.juraid.Views.Admin.StudentsView

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.Routes
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.HomeViewModel
import com.secal.juraid.Views.Admin.EditArticuloDetailItem

@Composable
fun AddPostView(navController: NavController, viewModel: HomeViewModel) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var urlHeader by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }

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

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Categoría (ID)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

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
                onClick = {
                    viewModel.addContentItem(
                        title = title,
                        category = category.toIntOrNull() ?: 1,
                        urlHeader = urlHeader,
                        text = text
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Añadir Post")
            }
        }
    }
}

