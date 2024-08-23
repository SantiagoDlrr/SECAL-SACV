package com.secal.juraid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.secal.juraid.Views.BienvenidaView
import com.secal.juraid.Views.HomeView
import com.secal.juraid.Views.LoginView
import com.secal.juraid.Views.ServiciosView
import com.secal.juraid.Views.SignUpView
import com.secal.juraid.Views.UserView
import com.secal.juraid.ui.theme.JurAidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JurAidTheme {
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun UserScreen(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.bienvenidaVw){
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
    }

}

