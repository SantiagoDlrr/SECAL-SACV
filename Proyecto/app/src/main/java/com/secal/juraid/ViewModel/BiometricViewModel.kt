package com.secal.juraid.ViewModel

import androidx.lifecycle.AndroidViewModel
import android.app.Application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BiometricViewModel(application: Application) : AndroidViewModel(application) {

    private val _biometricState = MutableStateFlow<BiometricState>(BiometricState.Idle)
    val biometricState: StateFlow<BiometricState> = _biometricState

    fun setSuccess() {
        _biometricState.value = BiometricState.Success
    }

    fun setError(message: String) {
        _biometricState.value = BiometricState.Error(message)
    }

    fun resetState() {
        _biometricState.value = BiometricState.Idle
    }
}

sealed class BiometricState {
    object Idle : BiometricState()
    object Loading : BiometricState()
    object Success : BiometricState()
    data class Error(val message: String) : BiometricState()
}
