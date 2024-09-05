package com.secal.juraid.Views.Generals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.secal.juraid.Routes

@Composable
fun CircleShapeDemo(){
    ExampleBox(shape = CircleShape)
}

// Funcion para crear figuras
@Composable
fun ExampleBox(shape: Shape){
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentSize(Alignment.Center)) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(shape)
                .background(androidx.compose.ui.graphics.Color.Cyan)
        )
    }
}

@Composable
fun BienvenidaView(navController: NavController){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircleShapeDemo()

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Bienvenid@ de vuelta ", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "JurAid a tu disposición")

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            navController.navigate(Routes.homeVw)
        }) {
            Text(text = "Navegar al menú")
        }

        Button(onClick = {
            navController.navigate(Routes.suitVw)
        }) {
            Text(text = "Acceso abogado")
        }

    }
}

