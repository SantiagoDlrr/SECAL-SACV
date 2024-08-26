package com.secal.juraid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController



@Composable
fun BottomBar(navController : NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.secondaryContainer),

        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón de inicio
        IconButton(
            onClick = { navController.navigate(Routes.homeVw) },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Icon(
                Icons.Outlined.Home,
                contentDescription = "Inicio",
                Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.primary

            )
        }
        // Botón de servicios
        IconButton(
            onClick = { navController.navigate(Routes.serviciosVw) },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight() // Asegurarse de que el botón ocupe toda la altura
        ) {
            Icon(
                Icons.Outlined.LocationOn,
                contentDescription = "Servicios",
                Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        // Botón de Perfil
        IconButton(
            onClick = { navController.navigate(Routes.userVw) },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Icon(
                Icons.Outlined.AccountCircle,
                contentDescription = "Perfil",
                Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .height(100.dp)
            .statusBarsPadding(),

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,

    ) {
        Text(
            text = "Juraid",
            maxLines = 1,
            fontSize = 25.sp,
            overflow = TextOverflow.Ellipsis
        )
    }
}

