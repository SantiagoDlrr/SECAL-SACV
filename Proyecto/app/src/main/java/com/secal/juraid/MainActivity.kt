package com.secal.juraid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.secal.juraid.Views.BienvenidaView
import com.secal.juraid.Views.HelpView
import com.secal.juraid.Views.HomeView
import com.secal.juraid.Views.LoginView
import com.secal.juraid.Views.MeetingView
import com.secal.juraid.Views.ServiciosView
import com.secal.juraid.Views.SignUpView
import com.secal.juraid.Views.SuitViews.AlumnosView
import com.secal.juraid.Views.SuitViews.CasosView
import com.secal.juraid.Views.SuitViews.DetalleView
import com.secal.juraid.Views.SuitViews.EspaciosView
import com.secal.juraid.Views.SuitViews.SuitHomeView
import com.secal.juraid.Views.UserView
import com.secal.juraid.ui.theme.JurAidTheme

class MainActivity : ComponentActivity() {
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



@Preview(showBackground = true)
@Composable
fun UserScreen(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.homeVw){
        composable(Routes.bienvenidaVw){
            BienvenidaView(navController = navController)
        }
        composable(Routes.homeVw){
            HomeView(navController = navController)
        }
        composable(Routes.serviciosVw){
            ServiciosView(navController = navController)
        }
        composable(Routes.userVw){
            UserView(navController = navController)
        }
        composable(Routes.loginVw){
            LoginView(navController = navController)
        }
        composable(Routes.signUpVw){
            SignUpView(navController = navController)
        }
        composable(Routes.helpVw){
            HelpView(navController = navController)
        }
        composable(Routes.meetingVw){
            MeetingView(navController = navController)
        }

        composable(Routes.suitVw){
            SuitHomeView(navController = navController)
        }

        composable(Routes.casosVw){
            CasosView(navController = navController)
        }

        composable(Routes.espaciosVw){
            EspaciosView(navController = navController)
        }

        composable(Routes.alumnosVw){
            AlumnosView(navController = navController)
        }


        composable(Routes.detalleVw){
            DetalleView(navController = navController)
        }


    }

}

