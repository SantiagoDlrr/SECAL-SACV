package com.secal.juraid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HelpButton(modifier: Modifier, navController: NavController) {
    // Botón flotante en la esquina inferior derecha
    FloatingActionButton(
        onClick = { navController.navigate(Routes.helpVw) },
        modifier = modifier.padding(16.dp),
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(8.dp)


        ) {
            Icon(imageVector = Icons.Outlined.Info, contentDescription = "Necesito Ayuda")
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(3.dp)
            ){
                Text("Necesito")
                Text("ayuda")

            }
        }
    }
}


@Composable
fun BottomBar(navController: NavController) {
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
                .fillMaxHeight()
        ) {
            Icon(
                Icons.Outlined.LocationOn,
                contentDescription = "Servicios",
                Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Botón de perfil
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
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
