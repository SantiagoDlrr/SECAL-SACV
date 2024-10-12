import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.secal.juraid.R
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcercaDeView(navController: NavController) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    val colors = listOf(
        Color(0xFF1B2735),
        Color(0xFF162029),
        Color(0xFF11191E),
        Color(0xFF0C1214),
        Color(0xFF090A0F),
        Color(0xFF0C1214),
        Color(0xFF11191E),
        Color(0xFF162029),
        Color(0xFF1B2735)
    )

    val infiniteTransition = rememberInfiniteTransition(label = "backgroundAnimation")

    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientProgress"
    )

    val overlayOpacity by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "overlayOpacity"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acerca de") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size = it }
        ) {
            // Fondo animado
            if (size != IntSize.Zero) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = colors,
                                start = Offset(
                                    size.width.toFloat() * animatedProgress,
                                    0f
                                ),
                                end = Offset(
                                    size.width.toFloat() * (1 + animatedProgress),
                                    size.height.toFloat()
                                )
                            )
                        )
                )

                // Overlay gradients
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.05f * overlayOpacity), Color.Transparent),
                                center = Offset(size.width * 0.8f, size.height * 0.2f),
                                radius = max(size.width, size.height).toFloat() * 0.5f
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF020734).copy(alpha = 0.05f * overlayOpacity), Color.Transparent),
                                center = Offset(size.width * 0.2f, size.height * 0.8f),
                                radius = max(size.width, size.height).toFloat() * 0.5f
                            )
                        )
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo de la aplicación
                Image(
                    painter = painterResource(R.drawable.martillo),
                    contentDescription = "Logo de la app",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nombre de la aplicación
                Text(
                    text = "JurAid",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Versión de la aplicación
                Text(
                    text = "Versión 1.0.0",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Información de copyright
                Text(
                    text = "© 2024 SECAL Tech.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }
    }
}