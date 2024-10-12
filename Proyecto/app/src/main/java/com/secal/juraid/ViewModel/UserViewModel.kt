package com.secal.juraid.ViewModel

import android.util.Log
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

    val sessionState: StateFlow<SessionStatus> = userRepository.sessionState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionStatus.LoadingFromStorage
    )

    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf("")
    val verificationMessage = mutableStateOf("")
    val accountExistsMessage = mutableStateOf("")

    private val _userName = MutableStateFlow<String>("")
    val userName: StateFlow<String> = _userName

    private val _userRole = MutableStateFlow<Int?>(0)
    val userRole: StateFlow<Int?> = _userRole

    private val _isTec = MutableStateFlow(false)
    val isTec: StateFlow<Boolean> = _isTec

    private val _userId = MutableStateFlow<String>("")
    val userId: StateFlow<String> = _userId

    val isEmailConfirmed = MutableLiveData<Boolean>()
    val emailNotConfirmed = MutableLiveData<Boolean>()

    private val _isBiometricEnabled = MutableStateFlow(false)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled

    init {
        viewModelScope.launch {
            sessionState.collect { status ->
                if (status is SessionStatus.Authenticated) {
                    fetchUserName()
                    fetchUserRole()
                    fetchIsTec()
                    fetchBiometricSetting()
                    fetchUserId()
                } else {
                    _userName.value = ""
                    _userRole.value = null
                    _isTec.value = false
                    _isBiometricEnabled.value = false
                }
            }
        }
    }

    private fun fetchUserId() {
        viewModelScope.launch {
            try {
                val id = userRepository.getUserId()
                _userId.value = id ?: ""
            } catch (e: Exception) {
                errorMessage.value = "Error al obtener el ID del usuario: ${e.message}"
                _userId.value = ""
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
                _isTec.value = isTec ?: false
            } catch (e: Exception) {
                errorMessage.value = "Error al obtener el rol del usuario: ${e.message}"
                _isTec.value = false
            }
        }
    }

    // Método para obtener la configuración de autenticación biométrica
    private fun fetchBiometricSetting() {
        viewModelScope.launch {
            try {
                val isBiometricEnabled = userRepository.isBiometricEnabledForUser()
                _isBiometricEnabled.value = isBiometricEnabled
            } catch (e: Exception) {
                errorMessage.value = "Error al obtener la configuración de autenticación biométrica: ${e.message}"
            }
        }
    }

    // Método para actualizar la configuración de autenticación biométrica
    fun updateBiometricSetting(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userRepository.updateBiometricSetting(enabled)
                _isBiometricEnabled.value = enabled
            } catch (e: Exception) {
                errorMessage.value = "Error al actualizar la configuración de autenticación biométrica: ${e.message}"
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
        verificationMessage.value = ""
        accountExistsMessage.value = ""
        emailNotConfirmed.value = false

        viewModelScope.launch {
            try {
                userRepository.signUp(email, password, name, firstLastName, secondLastName, phone)
                verificationMessage.value = "Se ha enviado un correo de verificación a tu cuenta."
                emailNotConfirmed.value = true
            } catch (e: Exception) {
                when {
                    e.message?.contains("ya está registrado", ignoreCase = true) == true -> {
                        accountExistsMessage.value = "Este correo ya está registrado. Por favor, inicia sesión."
                    }
                    else -> {
                        errorMessage.value = e.message ?: "Error desconocido"
                    }
                }
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
