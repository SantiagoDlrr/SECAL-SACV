package com.secal.juraid.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secal.juraid.Model.UserRepository
import io.github.jan.supabase.auth.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Estado de sesión observable por la UI
    val sessionState: StateFlow<SessionStatus> = userRepository.sessionState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionStatus.LoadingFromStorage
    )

    // Estados adicionales para controlar la UI durante el proceso de autenticación
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf("")

    // Nuevo estado para el nombre del usuario
    private val _userName = MutableStateFlow<String>("")
    val userName: StateFlow<String> = _userName

    private val _userRole = MutableStateFlow<Int?>(null)
    val userRole: StateFlow<Int?> = _userRole

    private val _isTec = MutableStateFlow(false)
    val isTec: StateFlow<Boolean> = _isTec

    // Estado para la confirmación de correo electrónico
    val isEmailConfirmed = MutableLiveData<Boolean>()

    // Estado para indicar si se ha enviado el correo de confirmación
    val emailNotConfirmed = MutableLiveData<Boolean>()

    init {
        // Observar cambios en el estado de la sesión
        viewModelScope.launch {
            sessionState.collect { status ->
                if (status is SessionStatus.Authenticated) {
                    fetchUserName()
                    fetchUserRole()
                    fetchIsTec()
                } else {
                    _userName.value = ""
                    _userRole.value = null
                    _isTec.value = false
                }
            }
        }
    }

    private fun fetchUserName() {
        viewModelScope.launch {
            try {
                val name = userRepository.getUserName()
                _userName.value = name ?: "Usuario"
            } catch (e: Exception) {
                errorMessage.value = "Error al obtener el nombre del usuario: ${e.message}"
                _userName.value = "Usuario"
            }
        }
    }

    private fun fetchUserRole() {
        viewModelScope.launch {
            try {
                val role = userRepository.getUserRole()
                _userRole.value = role
            } catch (e: Exception) {
                errorMessage.value = "Error al obtener el rol del usuario: ${e.message}"
                _userRole.value = null
            }
        }
    }

    private fun fetchIsTec() {
        viewModelScope.launch {
            try {
                val isTec = userRepository.getIsTecEmail()
                if (isTec != null) {
                    _isTec.value = isTec
                }
            } catch (e: Exception) {
                errorMessage.value = "Error al obtener el rol del usuario: ${e.message}"
                _isTec.value = false
            }
        }
    }

    // Método para manejar la confirmación de correo electrónico
    fun handleEmailConfirmed(accessToken: String) {
        viewModelScope.launch {
            try {
                // Puedes realizar una validación adicional con Supabase si es necesario
                // userRepository.confirmEmail(accessToken)

                // Actualizar el estado de confirmación de correo
                isEmailConfirmed.value = true
            } catch (e: Exception) {
                // Manejar errores
                isEmailConfirmed.value = false
                errorMessage.value = "Error al confirmar el correo: ${e.message}"
            }
        }
    }

    fun signIn(email: String, password: String) {
        isLoading.value = true
        errorMessage.value = ""
        viewModelScope.launch {
            try {
                userRepository.signIn(email, password)
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun signUp(
        email: String,
        password: String,
        name: String,
        firstLastName: String,
        secondLastName: String,
        phone: String
    ) {
        isLoading.value = true
        errorMessage.value = ""
        viewModelScope.launch {
            try {
                userRepository.signUp(email, password, name, firstLastName, secondLastName, phone)
                // Notificar que se envió un correo de confirmación
                emailNotConfirmed.value = true
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Unknown error"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userRepository.signOut()
        }
    }
}
// Biometric Authentication
val biometricAuthenticationResult = MutableLiveData<Boolean>()

fun onBiometricAuthenticated() {
    biometricAuthenticationResult.value = true
}

fun onBiometricFailed() {
    biometricAuthenticationResult.value = false
}
