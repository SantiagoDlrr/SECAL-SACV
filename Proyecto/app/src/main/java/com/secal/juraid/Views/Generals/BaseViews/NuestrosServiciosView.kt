package com.secal.juraid.Views.Generals.BaseViews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar

data class Servicio(val titulo: String, val descripcion: String)

@Composable
fun NuestrosServiciosView(navController: NavController) {
    val servicios = listOf(
        Servicio(
            "Asesoría Legal Gratuita",
            "Ofrecemos consultas iniciales gratuitas para ayudarte a entender tu situación legal y las posibles opciones disponibles."
        ),
        Servicio(
            "Representación Legal Profesional",
            "Contamos con un equipo de abogados altamente calificados y experimentados en diversas áreas del derecho para representarte de manera efectiva."
        ),
        Servicio(
            "Secciones Informativas",
            "Accede a una amplia variedad de artículos y posts informativos sobre temas legales relevantes, actualizados regularmente en nuestra aplicación."
        )
    )

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Text(
                    text = "Descubre cómo podemos ayudarte",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
            items(servicios) { servicio ->
                ServicioCard(servicio)
            }
        }
    }
}

@Composable
fun ServicioCard(servicio: Servicio) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = servicio.titulo,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = servicio.descripcion,
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 24.sp
                )
            )
        }
    }
}