package com.secal.juraid.Views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.HelpButton
import com.secal.juraid.TopBar

@Composable
fun HomeView(navController: NavController) {
    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {


            // Llama a HelpButton y alinea en la esquina inferior derecha
            HelpButton(modifier = Modifier.align(Alignment.BottomEnd), navController = navController)
        }
    }
}

@Composable
fun searchBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Buscar")
    }
}
