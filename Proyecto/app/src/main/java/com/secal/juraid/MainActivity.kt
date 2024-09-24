package com.secal.juraid

import AlumnosView
import ArticulosView
import CasosView
import DetalleView
import MeetingView
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.secal.juraid.Views.Generals.BaseViews.BienvenidaView
import com.secal.juraid.Views.Generals.Bookings.HelpView
import com.secal.juraid.Views.Generals.BaseViews.HomeView
import com.secal.juraid.Views.Sesion.LoginView
import com.secal.juraid.Views.Generals.BaseViews.ServiciosView
import com.secal.juraid.Views.Sesion.SignUpView
import com.secal.juraid.Views.Admin.SuitViews.EspaciosView
import com.secal.juraid.Views.Admin.SuitViews.SuitHomeView
import com.secal.juraid.Views.Generals.BaseViews.UserView
import com.secal.juraid.ui.theme.JurAidTheme
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.secal.juraid.Model.UserRepository
import com.secal.juraid.ViewModel.HomeViewModel
import com.secal.juraid.ViewModel.UserViewModel
import com.secal.juraid.Views.Admin.EditArticuloView
import com.secal.juraid.Views.Admin.EditDetalleView
import com.secal.juraid.Views.Admin.StudentsView.CasosStudentView
import com.secal.juraid.Views.Admin.StudentsView.StudentHomeView
import com.secal.juraid.Views.Admin.SuitViews.AlumnoDetailView
import com.secal.juraid.Views.Generals.BaseViews.ArticuloDetailView
import com.secal.juraid.Views.Generals.Users.UserHomeView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JurAidTheme {
                UserScreen()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@Preview(showBackground = true)
@Composable
fun UserScreen() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.homeVw,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(Routes.bienvenidaVw) {
            BienvenidaView(navController = navController)
        }
        composable(Routes.homeVw) {
            val homeViewModel = viewModel<HomeViewModel>()
            val contentItems by homeViewModel.contentItems.collectAsState()
            HomeView(navController = navController, contentItems = contentItems)
        }
        composable(Routes.serviciosVw) {
            ServiciosView(navController = navController)
        }

        composable(Routes.userVw) {
            UserView(navController = navController)
        }
        composable(Routes.loginVw) {
            LoginView(navController = navController, UserViewModel(
                UserRepository(supabase, CoroutineScope(
                    Dispatchers.IO)
                )
            )
            )
        }

        composable(Routes.signUpVw) {
            SignUpView(navController = navController, UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO))))
        }
        composable(Routes.helpVw) {
            HelpView(navController = navController)
        }
        composable(Routes.meetingVw) {
            MeetingView(navController = navController)
        }
        composable(Routes.suitVw) {
            SuitHomeView(navController = navController)
        }
        composable(Routes.casosVw) {
            CasosView(navController = navController)
        }
        composable(Routes.espaciosVw) {
            EspaciosView(navController = navController)
        }
        composable(Routes.studentHomeVw) {
            StudentHomeView(navController = navController)
        }
        composable(Routes.userHomeVw) {
            UserHomeView(navController = navController, UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO))))
        }
        composable(Routes.detalleVw) {
            DetalleView(navController = navController)
        }
        composable(Routes.studentHomeVw) {
            StudentHomeView(navController = navController)
        }
        composable(Routes.casosStVw) {
            CasosStudentView(navController = navController)
        }
        composable(Routes.editDetalleVw) {
            EditDetalleView(navController = navController)
        }
        composable(Routes.alumnoDetailVw) {
            AlumnoDetailView(navController = navController)
        }
        composable(Routes.articulosVw) {
            ArticulosView(navController = navController)
        }
        composable(Routes.articuloDetailVw) {
            ArticuloDetailView(navController = navController)
        }
        composable(Routes.editArticuloVw) {
            EditArticuloView(navController = navController)
        }
    }
}