package com.secal.juraid.Model

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.SessionStatus
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable


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
        supabase.auth.signInWith(Email){
            email = userEmail
            password = userPassword
        }
    }

    suspend fun signUp(userEmail: String, userPassword: String) {
        supabase.auth.signUpWith(Email) {
            email = userEmail
            password = userPassword
        }
        val user = User(
            user_id = 0,
            created_at = "",
            user_type = 1,
            mail = userEmail,
            password = userPassword,
            phone = 0,
            name = "Name",
            first_last_name = "First Last Name",
            second_last_name = "Second Last Name",
            street = "",
            ext_number = 0,
            int_number = 0,
            district = "",
            municipality = "",
            state = "",
            postal_code = ""
        )
        supabase.from("Users").upsert(user)

    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    suspend fun getUserName(): String? {
        return try {
            val user = supabase.auth.retrieveUserForCurrentSession()
            user.userMetadata?.get("full_name") as? String
        } catch (e: Exception) {
            null
        }
    }
}

@Serializable
data class User(
    val user_id: Int,
    val created_at: String,
    val user_type: Int,
    val mail: String,
    val password: String,
    val phone: Int,
    val name: String,
    val first_last_name: String,
    val second_last_name: String,
    val street: String,
    val ext_number: Int,
    val int_number: Int,
    val district: String,
    val municipality: String,
    val state: String,
    val postal_code: String,
)