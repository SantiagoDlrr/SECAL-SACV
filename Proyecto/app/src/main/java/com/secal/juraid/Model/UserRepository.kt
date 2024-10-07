package com.secal.juraid.Model

import android.util.Log
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
import kotlinx.serialization.Serializable
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
            supabase.auth.signUpWith(Email) {
                email = userEmail
                password = userPassword
                data = buildJsonObject {
                    put("name", name)
                    put("first_last_name", firstLastName)
                    put("second_last_name", secondLastName)
                    put("phone", phone)
                    put("role", 0)
                    put("is_tec_email", userEmail.endsWith("@tec.mx"))
                }
            }
        } catch (e: Exception) {
            throw Exception("Error al registrarse: ${e.message}")
        }
    }

    suspend fun signOut() {
        try {
            supabase.auth.signOut()
        } catch (e: Exception) {
            throw Exception("Error al cerrar sesión: ${e.message}")
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
            0 // Default role if there's an error
        }
    }

    suspend fun getIsTecEmail(): Boolean? {
        return try {
            val user = supabase.auth.retrieveUserForCurrentSession()
            val metadata = user.userMetadata
            metadata?.get("is_tec_email")?.toString()?.toBoolean() ?: false
        } catch (e: Exception) {
            null
        }
    }
}

@Serializable
data class UserRole(val role: Int)
