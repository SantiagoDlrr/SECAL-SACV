package com.secal.juraid.Views.Admin.StudentsView

import AlumnosViewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.secal.juraid.BottomBar
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.UserViewModel
import com.secal.juraid.Views.Admin.SuitViews.FullScreenHorario

@Composable
fun HorarioStudentView(
    navController: NavController,
    userViewModel: UserViewModel,
    alumnosViewModel: AlumnosViewModel = viewModel()
) {
    val context = LocalContext.current
    // Collect user ID from UserViewModel
    val id = userViewModel.userId.collectAsState().value
    val isLoading = alumnosViewModel.isLoading.collectAsState().value

    val horarioUrl by alumnosViewModel.horarioUrl.collectAsState()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isFullScreen by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(id) {
        alumnosViewModel.getHorarioUrlByStudentId(id)
    }


    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Tu Horario",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (horarioUrl != null || selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri ?: horarioUrl,
                            contentDescription = "Horario del estudiante",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .clickable { isFullScreen = true },
                            contentScale = ContentScale.Fit
                        )
                        Button(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Cambiar Horario")
                        }

                        if(selectedImageUri != null){
                            Button(
                                onClick = {
                                    alumnosViewModel.insertHorario(id,selectedImageUri!!,context)
                                    navController.popBackStack()
                                          },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text("Confirmar")
                            }
                        }

                    } else {
                        Text(
                            "Aún no has subido tu horario",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Subir Horario")
                        }
                    }
                }
            }
        }
        if (isFullScreen) {
            FullScreenHorario(
                horarioUrl = selectedImageUri?.toString() ?: horarioUrl,
                onDismiss = { isFullScreen = false }
            )
        }
    }
}

