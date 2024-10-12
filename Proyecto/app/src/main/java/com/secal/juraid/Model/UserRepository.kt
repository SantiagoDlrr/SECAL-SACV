package com.secal.juraid.Model

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.SessionStatus
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UserRepository(private val supabase: SupabaseClient, scope: CoroutineScope) {

    private val _sessionState = MutableStateFlow<SessionStatus>(SessionStatus.LoadingFromStorage)
    val sessionState: StateFlow<SessionStatus> get() = _sessionState

    init {
        scope.launch {
            // Listener para cambios de sesión
            supabase.auth.sessionStatus.collect { sessionStatus ->
                _sessionState.value = sessionStatus
            }
        }
    }

    suspend fun signIn(userEmail: String, userPassword: String) {
        try {
            supabase.auth.signInWith(Email) {
                email = userEmail
                password = userPassword
            }
            updateFCMToken()
        } catch (e: Exception) {
            throw Exception("Error al iniciar sesión: ${e.message}")
        }
    }

    suspend fun signUp(
        userEmail: String,
        userPassword: String,
        name: String,
        firstLastName: String,
        secondLastName: String,
        phone: String
    ) {
        try {
            val signUpResult = supabase.auth.signUpWith(Email) {
                email = userEmail
                password = userPassword
                data = buildJsonObject {
                    put("name", name)
                    put("first_last_name", firstLastName)
                    put("second_last_name", secondLastName)
                    put("phone", phone)
                    put("role", 0)
                    put("is_tec_email", userEmail.endsWith("@tec.mx"))
                    put("biometric_enabled", false)  // Inicialmente desactivar biometría
                }
            }

            // Verificar si el registro fue exitoso
            if (signUpResult != null) {
                // Registro exitoso
                updateFCMToken()
            } else {
                // Si signUpResult es null, probablemente la cuenta ya existe
                throw Exception("La cuenta ya existe o hubo un error en el registro")
            }
        } catch (e: Exception) {
            when {
                e.message?.contains("User already registered", ignoreCase = true) == true -> {
                    throw Exception("Este correo ya está registrado")
                }
                e.message?.contains("La cuenta ya existe", ignoreCase = true) == true -> {
                    throw Exception("Este correo ya está registrado")
                }
                else -> {
                    throw Exception("Error al registrarse: ${e.message}")
                }
            }
        }
    }

    suspend fun signOut() {
        try {
            supabase.auth.signOut()
        } catch (e: Exception) {
            throw Exception("Error al cerrar sesión: ${e.message}")
        }
    }

    public suspend fun updateFCMToken() {
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            val user = supabase.auth.retrieveUserForCurrentSession()
            supabase.from("users")
                .update(
                    {
                        set("fcm_token", token)
                    }
                ) {
                    filter {
                        eq("id", user.id)
                    }
                }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating FCM token", e)
        }
    }

    suspend fun getUserName(): String? {
        return try {
            val user = supabase.auth.retrieveUserForCurrentSession()
            val metadata = user.userMetadata
            val name = metadata?.get("name")?.toString()?.trim()?.replace("\"", "") ?: ""
            val firstName = metadata?.get("first_last_name")?.toString()?.trim()?.replace("\"", "") ?: ""
            val secondName = metadata?.get("second_last_name")?.toString()?.trim()?.replace("\"", "") ?: ""

            listOf(name, firstName, secondName)
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { null }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserRole(): Int {
        return try {
            val user = supabase.auth.retrieveUserForCurrentSession()
            val result = supabase.from("users")
                .select(columns = Columns.list("role")) {
                    filter {
                        eq("id", user.id)
                    }
                }
                .decodeSingle<UserRole>()
            result.role
        } catch (e: Exception) {
            Log.e("DatabaseDebug", "Error getting user role: ${e.message}")
            0 // Retornar rol predeterminado si hay un error
        }
    }

    suspend fun getUserId(): String {
        return try {
            val user = supabase.auth.retrieveUserForCurrentSession()
            user.id
        } catch (e: Exception) {
            throw Exception("Error al obtener el ID del usuario: ${e.message}")
        }
    }

    suspend fun getIsTecEmail(): Boolean {
        return try {
            val user = supabase.auth.retrieveUserForCurrentSession()
            val metadata = user.userMetadata
            metadata?.get("is_tec_email")?.toString()?.toBoolean() ?: false
        } catch (e: Exception) {
            throw Exception("Error al obtener el email Tec: ${e.message}")
        }
    }

    // Función para obtener si la autenticación biométrica está habilitada
    suspend fun isBiometricEnabledForUser(): Boolean {
        return try {
            val user = supabase.auth.retrieveUserForCurrentSession()
            val metadata = user.userMetadata
            metadata?.get("biometric_enabled")?.toString()?.toBoolean() ?: false
        } catch (e: Exception) {
            throw Exception("Error al obtener configuración biométrica: ${e.message}")
        }
    }

    // Función para actualizar la configuración de autenticación biométrica
    suspend fun updateBiometricSetting(enabled: Boolean) {
        try {
            val user = supabase.auth.retrieveUserForCurrentSession()
            val updatedMetadata = user.userMetadata?.toMutableMap() ?: mutableMapOf()
            updatedMetadata["biometric_enabled"] = JsonPrimitive(enabled)

            supabase.auth.updateUser {
                data = buildJsonObject {
                    updatedMetadata.forEach { (key, value) ->
                        put(key, value)
                    }
                }
            }
        } catch (e: Exception) {
            throw Exception("Error al actualizar configuración biométrica: ${e.message}")
        }
    }
}
@Serializable
data class UserRole(val role: Int)