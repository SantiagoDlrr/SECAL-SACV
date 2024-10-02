package com.secal.juraid.Views.Admin

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.R
import com.secal.juraid.Routes
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar

@Composable
fun EditDetalleView(navController: NavController) {
    //para ver qué función llamamos
    Log.d(TAG, "EditDetalleView() called")

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.detalleVw) },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Guardar"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TitlesView(title = "Edita la Información de Caso")
            Spacer(modifier = Modifier.height(16.dp))
            EditCard()
        }

    }
}



@Composable
fun EditCard() {

    var desc by remember { mutableStateOf("") }
    var nameAct by remember { mutableStateOf("") }
    var descAct by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Placeholder",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Headline", fontWeight = FontWeight.Bold)
                    Text("supporting text", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Published date", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(value = desc, onValueChange = {desc = it},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label = { Text("Descripción") },
                placeholder = { Text(text = "", fontSize = 16.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Acciones",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Drive")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir acción")
                
            }

            Spacer(modifier = Modifier.height(16.dp))


        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                    }) {
                        Text("Aceptar")
                    }
                },
                title = { Text("Añadir nueva acción") },
                text = {

                    Column {
                        OutlinedTextField(value = nameAct, onValueChange = {nameAct = it},
                            modifier = Modifier
                                .fillMaxWidth(),
                            label = { Text("Nombre de la acción") },
                            placeholder = { Text(text = "", fontSize = 16.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(value = descAct, onValueChange = {descAct = it},
                            modifier = Modifier
                                .fillMaxWidth(),
                            label = { Text("Hipervínculo") },
                            placeholder = { Text(text = "", fontSize = 16.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }

                }

            )
        }

    }
}


