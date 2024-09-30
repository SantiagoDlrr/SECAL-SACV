package com.secal.juraid.Views.Generals.Users

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.ButtonUserCard
import com.secal.juraid.NameUserCard
import com.secal.juraid.Routes
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.UserViewModel
import io.github.jan.supabase.auth.SessionStatus

@Composable
fun UserHomeView(navController: NavController, viewModel: UserViewModel) {
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
                UserHomeCardView(navController = navController, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun UserHomeCardView(navController: NavController, viewModel: UserViewModel) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val userName by viewModel.userName.collectAsState()
        val sessionState by viewModel.sessionState.collectAsState()
        val errorMessage by viewModel.errorMessage

        NameUserCard(userName, "Usuario")

        Spacer(modifier = Modifier.padding(16.dp))

        Column {
            ButtonUserCard(navController = navController, "Mi Caso", Icons.Outlined.Menu, route = Routes.detalleVw)

            Card(
                onClick = { viewModel.signOut() },
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
                            .weight(0.5f), // Fija un espacio constante para el ícono
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Spacer(modifier = Modifier.width(16.dp)) // Espacio constante entre el ícono y el texto

                    Text(
                        text = "Cerrar Sesión",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        fontSize = 25.sp,
                        modifier = Modifier
                            .weight(0.8f) // El texto ocupa el resto del espacio
                    )
                }
            }
        }

        // Handle session state
        LaunchedEffect(sessionState) {
            when (sessionState) {
                is SessionStatus.NotAuthenticated -> navController.navigate(Routes.homeVw)
                else -> {} // Handle other states if needed
            }
        }

        // Show error message if any
        if (errorMessage.isNotEmpty()) {
            Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

}
