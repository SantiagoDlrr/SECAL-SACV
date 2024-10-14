//Es la vista de cada post de información
//Los abogados verán un botón extra de editar

package com.secal.juraid.Views.Generals.BaseViews

import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.secal.juraid.BottomBar
import com.secal.juraid.Model.UserRepository
import com.secal.juraid.Routes
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.HomeViewModel
import com.secal.juraid.ViewModel.UserViewModel
//import com.secal.juraid.ViewModel.getImageUrl
import com.secal.juraid.supabase
import io.github.jan.supabase.auth.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ArticuloDetailView(navController: NavController, viewModel: HomeViewModel, postId: Int) {
    Log.d(TAG, "ArticuloDetailView() called")
    Log.d(TAG, "POST ID $postId")

    val coroutineScope = rememberCoroutineScope()
    var contentItem by remember { mutableStateOf<HomeViewModel.ContentItem?>(null) }
    //var imageUrl by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var deleteDialog by remember { mutableStateOf(false) }

    val userRole by UserViewModel(
        UserRepository(
            supabase,
            CoroutineScope(Dispatchers.IO)
        )
    ).userRole.collectAsState()


    LaunchedEffect(postId) {
        coroutineScope.launch {
            isLoading = true
            contentItem = viewModel.getFullContentItem(postId)
            isLoading = false
        }
    }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(navController = navController) },
        floatingActionButton = {
            if (userRole == 1) {
                Column {
                    FloatingActionButton(
                        onClick = {
                            deleteDialog = true
                        },
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar artículo")

                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    FloatingActionButton(
                        onClick = {
                            Log.d(TAG, "POST ID $postId")
                            navController.navigate("${Routes.editArticuloVw}/$postId")
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar artículo")
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (postId == -1) {
                Text("Error: Post ID no es válido", modifier = Modifier.padding(16.dp))
                return@Box
            }
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                contentItem?.let { item ->
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        ArticuloDetailItem(item)
                    }
                } ?: run {
                    Text(
                        "Error: No se pudo cargar el contenido",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        if (deleteDialog) {
            AlertDialog(
                onDismissRequest = { deleteDialog = false },
                title = { Text("Eliminar post") },
                text = { Text("Estás a punto de eliminar el post") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteContentItem(postId)
                        deleteDialog = false
                        navController.popBackStack()
                    }) {
                        Text("Confirmar", color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                },
                dismissButton = {
                    Button(onClick = { deleteDialog = false }) {
                        Text("Cancelar")
                    }
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                textContentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.formatDate(): String {
    return try {
        val offsetDateTime = OffsetDateTime.parse(this)
        val zoneId = ZoneId.systemDefault()
        val zonedDateTime = offsetDateTime.atZoneSameInstant(zoneId)
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy | h:mm a", Locale.ENGLISH)
        zonedDateTime.format(formatter).lowercase(Locale.ROOT)
    } catch (e: Exception) {
        "Fecha no disponible"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ArticuloDetailItem(item: HomeViewModel.ContentItem) {

    Card(
        modifier = Modifier.fillMaxWidth()
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header Card (Image, Title, Date)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    AsyncImage(
                        model = item.url_header,
                        contentDescription = item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop,
                        onError = {
                            Log.e(
                                "ArticuloDetailView",
                                "Error loading image: ${it.result.throwable}"
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Fecha de publicación")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.created_at.formatDate(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content Card (Text)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                item.text?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium.copy(lineBreak = LineBreak.Paragraph),
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}