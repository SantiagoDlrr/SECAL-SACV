package com.secal.juraid.Views.Sesion

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.secal.juraid.ViewModel.AuthenticationType
import com.secal.juraid.ViewModel.BiometricState
import com.secal.juraid.ViewModel.BiometricViewModel
import java.util.concurrent.Executor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import com.secal.juraid.R
import kotlin.math.max

@Composable
fun BiometricAuthView(
    biometricViewModel: BiometricViewModel,
    onAuthSuccess: (AuthenticationType) -> Unit,
    onAuthError: (String) -> Unit
) {
    Box (
        modifier = Modifier.fillMaxSize()
    ){
        BG()
    }
    val context = LocalContext.current
    val executor: Executor = ContextCompat.getMainExecutor(context)

    if (context !is FragmentActivity) {
        Text("Error: No es una instancia de FragmentActivity")
        return
    }

    val biometricPrompt = BiometricPrompt(context, executor, object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            val authenticationType = when (result.authenticationType) {
                BiometricPrompt.AUTHENTICATION_RESULT_TYPE_BIOMETRIC -> AuthenticationType.BIOMETRIC
                BiometricPrompt.AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL -> AuthenticationType.PIN
                else -> AuthenticationType.UNKNOWN
            }
            biometricViewModel.setSuccess(authenticationType)  // Actualizar estado del ViewModel
            onAuthSuccess(authenticationType)  // Notificar éxito
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            biometricViewModel.setError(errString.toString())
            onAuthError(errString.toString())  // Notificar error
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            biometricViewModel.setError("Autenticación fallida")
            onAuthError("Autenticación fallida")
        }
    })

    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación")
            .setSubtitle("Usa tu rostro, huella digital o PIN para autenticarte")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .setConfirmationRequired(false)
            .build()
    }

    val biometricManager = BiometricManager.from(context)
    val canAuthenticate = biometricManager.canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (canAuthenticate) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Text(
                    "Seleccione un método de autenticación",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Button(
                    onClick = { biometricPrompt.authenticate(promptInfo) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text("Autenticarse con Rostro, Huella o PIN")
                }

                when (val state = biometricViewModel.biometricState.collectAsState().value) {
                    is BiometricState.Success -> {
                        Text(
                            "Autenticación exitosa con ${state.authenticationType}",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    is BiometricState.Error -> {
                        Text(
                            "Error: ${state.message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {
                    }
                }
            }
            else -> {
                Text(
                    "Este dispositivo no soporta autenticación biométrica o PIN",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BG() {
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
            animation = tween(10000, easing = LinearEasing), // Reduced from 30000 to 10000
            repeatMode = RepeatMode.Restart // Changed from Reverse to Restart for a more noticeable effect
        ),
        label = "gradientProgress"
    )

    val overlayOpacity by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f, // Increased range for more noticeable change
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing), // Reduced from 15000 to 5000
            repeatMode = RepeatMode.Reverse
        ),
        label = "overlayOpacity"
    )

    Scaffold(
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .onSizeChanged { size = it }
        ) {
            // Animated background
            if (size != IntSize.Zero) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = colors,
                                start = Offset(size.width.toFloat() * animatedProgress, 0f),
                                end = Offset(size.width.toFloat() * (1 + animatedProgress), size.height.toFloat())
                            )
                        )
                )

                // Overlay gradients
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.1f * overlayOpacity), Color.Transparent),
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
                                colors = listOf(Color(0xFF020734).copy(alpha = 0.1f * overlayOpacity), Color.Transparent),
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // App logo
                        Image(
                            painter = painterResource(R.drawable.martillo),
                            contentDescription = "Logo de la app",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(16.dp),
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // App name
                        Text(
                            text = "JurAid",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // App version
                        Text(
                            text = "Versión 1.0.0",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Copyright information
                        Text(
                            text = "© 2024 SECAL Tech.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }
        }
    }
}