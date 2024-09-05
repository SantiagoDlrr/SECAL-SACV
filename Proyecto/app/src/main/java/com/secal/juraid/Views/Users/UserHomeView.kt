package com.secal.juraid.Views.Users

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.ButtonUserCard
import com.secal.juraid.NameUserCard
import com.secal.juraid.Routes
import com.secal.juraid.TopBar

@Composable
fun UserHomeView(navController: NavController) {
    Scaffold (
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    )
    { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),

            ) {
            Column {
                UserHomeCardView(navController = navController)
            }
        }
    }
}

@Composable
fun UserHomeCardView(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        NameUserCard("John Jane Doe", "Usuario")

        Spacer(modifier = Modifier.padding(16.dp))

        Column {
            ButtonUserCard(navController = navController, "Mi Caso", Icons.Outlined.Menu, route = Routes.casosVw)
        }

    }
}