package com.secal.juraid.Views.Generals.BaseViews

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.InsertEmoticon
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.HelpButton
import com.secal.juraid.TopBar
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.secal.juraid.ButtonUserCard
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
                ButtonUserCard(navController = navController, "Servicios", Icons.Outlined.Search, route = Routes.nuestrosServiciosVw)
                ButtonUserCard(navController = navController, "     FAQ", Icons.Outlined.QuestionAnswer, route = Routes.faqVw)
                ButtonUserCard(navController = navController, "Acerca de", Icons.Outlined.Info, route = Routes.acercaDeVw)

            }

            HelpButton(modifier = Modifier.align(Alignment.BottomEnd), navController = navController)
        }
    }
}

@Composable
fun MapCardView() {
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isDarkTheme = isSystemInDarkTheme()

    // Reemplaza estas coordenadas con las de la ubicación de tu clínica
    val latitude = "25.648307"
    val longitude = "-100.289482"

    val lightModeStyle = listOf(
        "style=feature:poi|element:all|visibility:off",
        "style=feature:all|element:labels.text.fill|color:0x000000",
        "style=feature:all|element:labels.text.stroke|color:0xffffff|weight:3",
        "style=feature:landscape|element:all|color:0xf2f2f2",
        "style=feature:road|element:all|color:0xffffff",
        "style=feature:road|element:geometry.stroke|color:0xdddddd",
        "style=feature:road|element:labels.text.fill|color:0x000000",
        "style=feature:water|element:all|color:0x46bcec"
    ).joinToString("&")

    // Estilos mejorados para mejor contraste en modo oscuro
    val darkModeStyle = listOf(
        "style=feature:poi|element:all|visibility:off",
        "style=feature:all|element:labels.text.fill|color:0xffffff",
        "style=feature:all|element:labels.text.stroke|color:0x000000|weight:3",
        "style=feature:landscape|element:all|color:0x1c1c1c",
        "style=feature:road|element:all|color:0x3c3c3c",
        "style=feature:road|element:geometry.stroke|color:0x585858",
        "style=feature:road|element:labels.text.fill|color:0xffffff",
        "style=feature:water|element:all|color:0x0e4a77"

    ).joinToString("&")

    val mapStyle = if (isDarkTheme) darkModeStyle else lightModeStyle

    val mapUrl = "https://maps.googleapis.com/maps/api/staticmap?" +
            "center=$latitude,$longitude" +
            "&zoom=16" +
            "&size=600x300" +
            "&maptype=roadmap" +
            "&markers=color:red%7C$latitude,$longitude" +
            "&$mapStyle" +
            "&key=AIzaSyB1GvXqwgi1iaZvAtuaSckad58KzyyAE9w"

    Card(
        onClick = {
            val gmmIntentUri = Uri.parse("https://maps.app.goo.gl/rdYMmwrTHGwQ7Png7")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(mapUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Mapa de la ubicación de la clínica",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    CircularProgressIndicator()
                },
                error = {
                    errorMessage = "Error al cargar el mapa: ${it.result.throwable.message}"
                    Text(
                        text = errorMessage ?: "Ubicación de la Clínica",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )
        }
    }

    errorMessage?.let {
        Log.e("MapCardView", "Error: $it")
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
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),

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





