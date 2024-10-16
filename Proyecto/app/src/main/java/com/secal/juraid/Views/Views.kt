package com.secal.juraid.Views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.secal.juraid.ViewModel.HomeViewModel
import com.secal.juraid.ViewModel.UserViewModel
import com.secal.juraid.Views.Generals.BaseViews.HomeView
import io.github.jan.supabase.auth.SessionStatus

@Composable
fun UserScreenManager(viewModel: UserViewModel) {
    val navController = rememberNavController()  // Controlador para navegación
    val sessionState by viewModel.sessionState.collectAsState()  // Obtener el estado de la sesión de forma reactiva

    when (sessionState) {
        is SessionStatus.Authenticated -> {
            val homeViewModel = viewModel<HomeViewModel>()
            HomeView(navController = navController, viewModel = homeViewModel, userViewModel = viewModel)
        }
        SessionStatus.LoadingFromStorage -> {
            // Pantalla de carga mientras se comprueba la sesión
            LoadingScreen()
        }
        SessionStatus.NetworkError -> {
            // Mostrar un error de red
            ErrorScreen("Network error")
        }
        is SessionStatus.NotAuthenticated -> {
            // Pantalla para iniciar sesión, pasando el viewModel para manejo de login
            NotAuthenticatedScreen(viewModel = viewModel)
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()  // Indicador de carga simple
    }
}

@Composable
fun ErrorScreen(errorMessage: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = errorMessage,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NotAuthenticatedScreen(viewModel: UserViewModel) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Please log in to continue",
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        // Aquí podrías agregar un botón para iniciar sesión
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingScreen() {
    LoadingScreen()
}

@Preview(showBackground = true)
@Composable
fun PreviewErrorScreen() {
    ErrorScreen("An error occurred")
}


