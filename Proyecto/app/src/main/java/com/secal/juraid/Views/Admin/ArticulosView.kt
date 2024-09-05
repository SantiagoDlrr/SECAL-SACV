import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.R
import com.secal.juraid.Routes
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar

@Composable
fun ArticulosView(navController: NavController) {
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.editArticuloVw) }
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Agregar artículo")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TitlesView(title = "Artículos Publicados")
            Spacer(modifier = Modifier.height(16.dp))
            ArticulosLista(navController = navController)
        }
    }
}

@Composable
fun ArticulosLista(navController: NavController) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(3) { // Asumimos que hay 3 artículos como en la imagen
            ArticuloItem(navController = navController)
        }
    }
}

@Composable
fun ArticuloItem(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { navController.navigate(Routes.articuloDetailVw) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder para la imagen del artículo
            Image(
                Icons.Default.ExitToApp,
                contentDescription = "Placeholder",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Título",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Description duis aute irure dolor in reprehenderit in vol...",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = "Fecha de publicación")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Today • Nombre",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}