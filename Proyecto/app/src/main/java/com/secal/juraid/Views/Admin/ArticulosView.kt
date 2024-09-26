/*
Es la vista donde esta la lista de posts en la vista de alumnos abogados
 */


import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.secal.juraid.BottomBar
import com.secal.juraid.CategoryItem
import com.secal.juraid.R
import com.secal.juraid.Routes
import com.secal.juraid.TitlesView
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.HomeViewModel
import com.secal.juraid.Views.Generals.BaseViews.formatDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun ArticulosView(navController: NavController, items: List<HomeViewModel.ContentItem>) {
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.addPostVw) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar artículo")
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
            ArticulosLista(navController = navController, items = items)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ArticulosLista(navController: NavController, items: List<HomeViewModel.ContentItem>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            ArticuloItem(item = item, navController = navController)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ArticuloItem(item: HomeViewModel.ContentItem, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = {
            val itemJson = Uri.encode(Json.encodeToString(item))
            navController.navigate("${Routes.articuloDetailVw}/$itemJson")
        }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.url_header,
                contentDescription = item.title,
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = item.category?.name_category ?: "Sin categoría",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = "Fecha de publicación")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.created_at.formatDate(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}