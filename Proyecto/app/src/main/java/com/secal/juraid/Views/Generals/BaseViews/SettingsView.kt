package com.secal.juraid.Views.Generals.BaseViews

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.secal.juraid.BottomBar
import com.secal.juraid.Routes
import com.secal.juraid.TitlesView
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.secal.juraid.TopBar
import com.secal.juraid.ViewModel.UserViewModel

@Composable
fun SettingsView(navController: NavController, viewModel: UserViewModel) {
    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { TopBar() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            Column {
                SettingsHomeCardView(navController = navController, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SettingsHomeCardView(navController: NavController, viewModel: UserViewModel) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPreferences", android.content.Context.MODE_PRIVATE)

    var isBiometricEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("biometric_enabled", false))
    }

    val biometricManager = BiometricManager.from(context)

    fun showBiometricPrompt(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val canAuthenticate = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        )

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            val executor = ContextCompat.getMainExecutor(context)
            val biometricPrompt = BiometricPrompt(context as FragmentActivity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        onSuccess()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        onError(errString.toString())
                    }

                    override fun onAuthenticationFailed() {
                        onError("Authentication failed")
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Confirmar autenticación")
                .setSubtitle("Confirma tu identidad para cambiar la configuración biométrica")
                .setNegativeButtonText("Cancelar")
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
                )
                .build()

            biometricPrompt.authenticate(promptInfo)
        } else {
            onError("No se puede usar autenticación biométrica en este dispositivo")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        TitlesView(title = "Configuración")

        Spacer(modifier = Modifier.padding(16.dp))

        Card(
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
                Text(
                    text = "Autenticación Biométrica",
                    fontSize = 20.sp,
                    modifier = Modifier.weight(0.8f)
                )

                Switch(
                    checked = isBiometricEnabled,
                    onCheckedChange = { isChecked ->
                        // Mostrar prompt de autenticación biométrica
                        showBiometricPrompt(
                            onSuccess = {
                                // Si la autenticación es exitosa, permitir el cambio
                                isBiometricEnabled = isChecked
                                with(sharedPreferences.edit()) {
                                    putBoolean("biometric_enabled", isChecked)
                                    apply()
                                }
                            },
                            onError = { errorMessage ->
                                // Mostrar error si la autenticación falla
                                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                    ),
                )
            }
        }
    }

    Card(
        onClick = {
            viewModel.signOut()
            navController.navigate(Routes.userVw)
        },
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
                imageVector = Icons.Outlined.ExitToApp,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .weight(0.5f),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Cerrar Sesión",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                fontSize = 25.sp,
                modifier = Modifier.weight(0.8f)
            )
        }
    }
}