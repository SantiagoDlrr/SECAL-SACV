import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun AlumnosView(navController: NavController) {

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TitlesView(title = "Alumnos")
            LazyColumn {
                items(5) {
                    AlumnosCardView(navController = navController)
                }
            }
        }


    }
}

@Composable
fun AlumnosCardView(navController: NavController) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                onClick = { navController.navigate(Routes.alumnoDetailVw) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxSize()
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Placeholder",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                        ,
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Alumno", fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }
            }

}




