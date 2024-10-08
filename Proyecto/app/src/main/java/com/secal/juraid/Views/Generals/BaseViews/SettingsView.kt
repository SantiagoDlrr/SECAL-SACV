package com.secal.juraid.Views.Generals.BaseViews

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.Routes
import com.secal.juraid.TitlesView
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.UserViewModel

@Composable
fun SettingsView(navController: NavController, viewModel: UserViewModel) {
    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            Column {
                SettingsHomeCardView(navController = navController, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SettingsHomeCardView(navController: NavController, viewModel: UserViewModel) {

    // Recoger el estado de autenticación biométrica desde el ViewModel usando collectAsState
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        TitlesView(title = "Configuración")

        Spacer(modifier = Modifier.padding(16.dp))

        // Card para activar/desactivar la autenticación biométrica
        Card(
            onClick = {},
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(70.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Autenticación Biométrica",
                    fontSize = 20.sp,
                    modifier = Modifier.weight(0.8f)
                )

                Switch(
                    checked = isBiometricEnabled,
                    onCheckedChange = { isChecked ->
                        // Actualizar la preferencia del usuario en la base de datos
                        viewModel.updateBiometricSetting(isChecked)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.padding(16.dp))

        // Card para cerrar sesión
        Card(
            onClick = {
                viewModel.signOut()
                navController.navigate(Routes.userVw)
            },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(70.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Outlined.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .weight(0.5f),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Cerrar Sesión",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    fontSize = 25.sp,
                    modifier = Modifier.weight(0.8f)
                )
            }
        }
    }
}