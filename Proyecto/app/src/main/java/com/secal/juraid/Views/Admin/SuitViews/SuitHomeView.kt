package com.secal.juraid.Views.Admin.SuitViews

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.ButtonUserCard
import com.secal.juraid.NameUserCard
import com.secal.juraid.Routes
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.UserViewModel
import io.github.jan.supabase.auth.SessionStatus


@Composable
fun SuitHomeView(navController: NavController, viewModel: UserViewModel) {
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
                SuitHomeCardView(navController = navController, viewModel = viewModel)
            }
        }
    }
}


@Composable
fun SuitHomeCardView(navController: NavController, viewModel: UserViewModel) {
    val userName by viewModel.userName.collectAsState()
    val sessionState by viewModel.sessionState.collectAsState()
    val errorMessage by viewModel.errorMessage

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        NameUserCard(userName, "Abogado")

        Spacer(modifier = Modifier.padding(16.dp))

        Column {
            ButtonUserCard(navController = navController, "Casos", Icons.Outlined.Menu, route = Routes.casosVw)
            ButtonUserCard(navController = navController, "Espacios", Icons.Outlined.DateRange, route = Routes.meetingVw)
            ButtonUserCard(navController = navController, "Alumnos", Icons.Outlined.AccountCircle, route = Routes.alumnosVw)
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




