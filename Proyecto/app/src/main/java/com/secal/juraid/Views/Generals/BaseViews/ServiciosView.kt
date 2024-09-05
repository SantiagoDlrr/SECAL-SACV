package com.secal.juraid.Views.Generals.BaseViews

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.HelpButton
import com.secal.juraid.TopBar
import androidx.compose.ui.platform.LocalContext
import com.secal.juraid.Routes


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ServiciosView(navController : NavController) {
    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Habilitar scroll vertical
            ) {
                MapCardView()
                ServiceDistribution(item1 = "Preguntas Frecuentes", route1 = "", item2 = "Vista Abogado", route2 = Routes.suitVw, navController = navController)
                ServiceDistribution(item1 = "Vista Estudiante", route1 = Routes.studentHomeVw, item2 = "Vista Usuario", route2 = Routes.userHomeVw, navController = navController)


            }

            HelpButton(modifier = Modifier.align(Alignment.BottomEnd), navController = navController)
        }
    }
}

@Composable
fun MapCardView() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        Card(
            onClick = {
                val gmmIntentUri = Uri.parse("https://maps.app.goo.gl/rdYMmwrTHGwQ7Png7")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)


                if (mapIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapIntent)
                }
            },
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .width(350.dp)
                .height(200.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Ubicación de la Clínica",
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.padding(10.dp))

                    Text(
                        text = "Ubicación de la Clínica",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}



@Composable
fun ServiceDistribution(item1: String, route1: String, item2: String, route2: String, navController : NavController) {
    Spacer(modifier = Modifier.height(16.dp))
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)

    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
                ServiceCardView(item1, navController, route = route1)
                Spacer(modifier = Modifier.width(16.dp))
                ServiceCardView(item2, navController, route = route2)

        }
    }
}

@Composable
fun ServiceCardView(item: String, navController: NavController, route: String) {
    Card(
        onClick = {navController.navigate(route)},
        modifier = Modifier
            .width(160.dp)  // Ancho de la tarjeta
            .height(100.dp) // Altura de la tarjeta
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(), // Ocupa todo el tamaño disponible de la tarjeta
            contentAlignment = Alignment.Center // Centra el contenido dentro del Box
        ) {
            Text(
                text = item,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(), // Ocupa todo el ancho disponible
                maxLines = 2, // Limita las líneas a 2
                overflow = TextOverflow.Ellipsis, // Si es muy largo, añade "..."
                textAlign = TextAlign.Center,
                fontSize = 17.sp
            )
        }
    }
}





