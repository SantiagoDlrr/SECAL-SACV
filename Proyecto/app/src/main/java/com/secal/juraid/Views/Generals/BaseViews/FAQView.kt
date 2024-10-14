package com.secal.juraid.Views.Generals.BaseViews

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar

data class FAQItem(val question: String, val answer: String)

@Composable
fun FAQView(navController: NavController) {
    val faqList = listOf(
        FAQItem("¿Cómo puedo agendar una cita?", "En la página principal hay un botón de necesito ayuda, ese botón te llevará a agendar una cita"),
        FAQItem("¿Cuánto tiempo dura una asesoría?", "Las consultas generalmente duran entre 30 y 60 minutos, dependiendo de la complejidad del caso."),
        FAQItem("¿Puedo cancelar mi cita?", "Sí, puedes cancelar tu cita hasta 24 horas antes sin costo alguno."),
        FAQItem("¿Qué tipos de casos manejan los abogados?", "Nuestros abogados cubren diversas áreas como derecho civil, penal, laboral, familiar, entre otras."),
        FAQItem("¿Cómo me preparo para la consulta?", "Te recomendamos tener a mano todos los documentos relevantes a tu caso y una lista de preguntas que quieras hacer."),
        FAQItem("¿Las consultas son confidenciales?", "Sí, todas las consultas son estrictamente confidenciales."),
        FAQItem("¿Ofrecen consultas virtuales?", "Sí, ofrecemos tanto consultas presenciales como virtuales."),
        FAQItem("¿Cuál es el costo de una consulta?", "Las consultas son gratuitas."),
        FAQItem("¿Qué pasa si necesito más tiempo de consulta?", "Si necesitas más tiempo, puedes acordar con el abogado extender la consulta o agendar una nueva cita.")
    )

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                TitlesView(title = "Preguntas Frecuentes")
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(faqList) { faq ->
                FAQItem(faq)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}




@Composable
fun FAQItem(faq: FAQItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    if (!expanded) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                    contentDescription = "Expandir"
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ){Column(
                    modifier = Modifier.padding(16.dp)
                ){
                        Text(
                            text = faq.answer,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }
        }
    }
}