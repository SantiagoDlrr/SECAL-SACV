package com.secal.juraid.Views.Sesion

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat
import com.secal.juraid.ViewModel.BiometricViewModel
import java.util.concurrent.Executor

@Composable
fun BiometricAuthView(
    biometricViewModel: BiometricViewModel,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit
) {
    val context = LocalContext.current
    val executor: Executor = ContextCompat.getMainExecutor(context)

    // Verifica si el contexto es FragmentActivity
    if (context !is FragmentActivity) {
        Text("Error: No es una instancia de FragmentActivity")
        return
    }

    // Inicializa el BiometricPrompt
    val biometricPrompt = BiometricPrompt(context, executor, object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            biometricViewModel.setSuccess()  // Actualizar el estado en el ViewModel
            onAuthSuccess()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            biometricViewModel.setError(errString.toString())  // Actualizar el estado en el ViewModel
            onAuthError(errString.toString())
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            biometricViewModel.setError("Autenticación fallida")  // Actualizar el estado en el ViewModel
            onAuthError("Autenticación fallida")
        }
    })

    // Configura la información del prompt biométrico
    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación Biométrica")
            .setSubtitle("Usa tu huella digital para autenticarte")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setNegativeButtonText("Cancelar")
            .build()
    }

    // Verifica si el dispositivo soporta la autenticación biométrica
    val biometricManager = BiometricManager.from(context)
    val canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)

    // Muestra el estado actual del proceso biométrico
    if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
        // Mostrar botón si el dispositivo soporta autenticación biométrica
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                biometricPrompt.authenticate(promptInfo)  // Iniciar autenticación
            }) {
                Text("Autenticarse con Huella Digital")
            }
        }
    } else {
        // Si el dispositivo no soporta biometría, muestra un mensaje
        Text("Este dispositivo no soporta la autenticación biométrica", color = MaterialTheme.colorScheme.error)
    }
}
