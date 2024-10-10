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

@Composable
fun BiometricAuthView(
    biometricViewModel: BiometricViewModel,
    onAuthSuccess: (AuthenticationType) -> Unit,
    onAuthError: (String) -> Unit
) {
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
