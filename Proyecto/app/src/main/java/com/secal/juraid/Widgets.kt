package com.secal.juraid

import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.secal.juraid.Model.UserRepository
import com.secal.juraid.ViewModel.HomeViewModel
import com.secal.juraid.ViewModel.UserViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.SessionStatus
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val supabase = createSupabaseClient(
    supabaseUrl = Credentials.supabaseUrl,
    supabaseKey = Credentials.supabaseKey

) {
    install(Postgrest)
    install(Auth)
    install(Storage)
}

@Composable
fun HelpButton(modifier: Modifier, navController: NavController) {
    val sessionState by UserViewModel(UserRepository(supabase, CoroutineScope(Dispatchers.IO))).sessionState.collectAsState()
    val isLogged = sessionState is SessionStatus.Authenticated
    // Botón flotante en la esquina inferior derecha
    FloatingActionButton(
        onClick = {
            if (isLogged) {
                navController.navigate(Routes.helpVw)
            } else {
                navController.navigate(Routes.userVw)
            }},
        modifier = modifier.padding(16.dp),
        containerColor = MaterialTheme.colorScheme.primary,
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(8.dp)


        ) {
            Icon(imageVector = Icons.Outlined.Info, contentDescription = "Necesito Ayuda", tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
fun AnimatedHelpButton(
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel // Ahora aceptamos el UserViewModel como parámetro
) {
    val sessionState by userViewModel.sessionState.collectAsState()
    val isLogged = sessionState is SessionStatus.Authenticated
    val isExpanded = remember { mutableStateOf(false) }
    val animatedWidth by animateDpAsState(
        targetValue = if (isExpanded.value) 160.dp else 56.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (isExpanded.value) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    LaunchedEffect(Unit) {
        delay(500) // Pequeña pausa antes de iniciar la animación
        isExpanded.value = true
        delay(2000)
        isExpanded.value = false
    }

    FloatingActionButton(
        onClick = {
            if (isLogged) {
                navController.navigate(Routes.helpVw)
            } else {
                navController.navigate(Routes.userVw)
            }
        },
        modifier = modifier
            .padding(16.dp)
            .width(animatedWidth)
            .height(56.dp),
        containerColor = MaterialTheme.colorScheme.primary,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "Necesito Ayuda",
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Solicitar cita",
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.alpha(textAlpha),
                maxLines = 1
            )
        }
    }
}



@Composable
fun BottomBar(navController: NavController) {
    val userViewModel = LocalUserViewModel.current // Obtenemos el UserViewModel desde el CompositionLocal
    val sessionState by userViewModel.sessionState.collectAsState()
    val isLogged = sessionState is SessionStatus.Authenticated
    val userRole by userViewModel.userRole.collectAsState()

    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.secondaryContainer),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón de inicio
        IconButton(
            onClick = { navController.navigate(Routes.homeVw) },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(40.dp)
                    .background(
                        if (currentRoute == Routes.homeVw) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Home,
                    contentDescription = "Inicio",
                    Modifier.size(25.dp),
                    tint = if (currentRoute == Routes.homeVw) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Botón de servicios
        IconButton(
            onClick = { navController.navigate(Routes.serviciosVw) },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(40.dp)
                    .background(
                        if (currentRoute == Routes.serviciosVw) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = "Servicios",
                    Modifier.size(25.dp),
                    tint = if (currentRoute == Routes.serviciosVw) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Botón de perfil (lógica de redirección según el estado de sesión y rol)
        IconButton(
            onClick = {
                if (isLogged) {
                    when (userRole) {
                        0 -> navController.navigate(Routes.userHomeVw)
                        1 -> navController.navigate(Routes.suitVw)
                        2 -> navController.navigate(Routes.studentHomeVw)
                        else -> navController.navigate(Routes.userVw)
                    }
                } else {
                    navController.navigate(Routes.userVw)
                }
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(40.dp)
                    .background(
                        if (currentRoute == Routes.userVw ||
                            currentRoute == Routes.userHomeVw ||
                            currentRoute == Routes.suitVw ||
                            currentRoute == Routes.studentHomeVw)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Transparent,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.AccountCircle,
                    contentDescription = "Perfil",
                    Modifier.size(25.dp),
                    tint = if (currentRoute == Routes.userVw ||
                        currentRoute == Routes.userHomeVw ||
                        currentRoute == Routes.suitVw ||
                        currentRoute == Routes.studentHomeVw)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}






@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .height(100.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        /*Text(
            text = "Juraid",
            maxLines = 1,
            fontSize = 25.sp,
            overflow = TextOverflow.Ellipsis
        )*/
        if (isSystemInDarkTheme()) {
            Image(
                painter = painterResource(id = R.drawable.martillo_blanco),
                contentDescription = "Juraid",
                modifier = Modifier.size(100.dp)
            )

        } else {
            Image(
                painter = painterResource(id = R.drawable.martillo),
                contentDescription = "Juraid",
                modifier = Modifier.size(100.dp)
            )
        }

    }
}


@Composable
fun CategorySection(title: String, items: List<HomeViewModel.ContentItemPreview>, navController: NavController) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                CategoryItem(item = item, navController = navController)
            }
        }
    }
}

//Son las tarjetitas de la pagina principal, las de categorias
@Composable
fun CategoryItem(item: HomeViewModel.ContentItemPreview, navController: NavController) {
    Card(
        onClick = {
            val itemJson = Uri.encode(Json.encodeToString(item))
            navController.navigate("${Routes.articuloDetailVw}/$itemJson")
        },
        modifier = Modifier
            .width(200.dp)
            .height(270.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = item.url_header,
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = item.title,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
fun TitlesView(title: String, size: Int = 35) {
    Spacer(modifier = Modifier.height(16.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Text(
            text = title,
            fontSize = size.sp,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge.copy(
                lineHeight = 44.sp  // Aumentado el espaciado entre líneas
            )
        )
    }
}

@Composable
fun ButtonUserCard(navController: NavController, title: String = "", icon: ImageVector, route: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            onClick = { navController.navigate(route) },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(70.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .weight(0.5f), // Fija un espacio constante para el ícono
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Spacer(modifier = Modifier.width(16.dp)) // Espacio constante entre el ícono y el texto

                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .weight(0.8f) // El texto ocupa el resto del espacio
                )
            }
        }
    }
}

@Composable
fun NameUserCard(name: String, desc: String) {
    val viewModel = LocalUserViewModel.current
    val isTec by viewModel.isTec.collectAsState()

    val shouldShowTecLogo by remember {
        derivedStateOf { isTec }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .height(100.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (shouldShowTecLogo) {
                    Image(
                        painter = painterResource(id = R.drawable.logotec),
                        contentDescription = "Tec",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                }
                Text(
                    text = desc,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}



