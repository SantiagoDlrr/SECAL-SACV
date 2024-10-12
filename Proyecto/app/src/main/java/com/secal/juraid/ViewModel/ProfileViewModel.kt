import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.secal.juraid.Model.UserRepository
import com.secal.juraid.ViewModel.uriToByteArray
import com.secal.juraid.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.UUID

class ProfileViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AndroidViewModel(application) {
    private val _profileData = MutableStateFlow(ProfileData())
    val profileData: StateFlow<ProfileData> = _profileData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _updateResult = MutableStateFlow<UpdateProfileResult?>(null)
    val updateResult: StateFlow<UpdateProfileResult?> = _updateResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _imageUploadStatus = MutableStateFlow<ImageUploadStatus>(ImageUploadStatus.Idle)
    val imageUploadStatus: StateFlow<ImageUploadStatus> = _imageUploadStatus.asStateFlow()

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = Columns.raw("user_id, url_image, desc".trimIndent())
                val userId = userRepository.getUserId()
                Log.d("ProfileViewModel", "User ID: $userId")
                val profile = withContext(Dispatchers.IO) {
                    supabase
                        .from("profile")
                        .select(columns = data) {
                            filter {
                                eq("user_id", userId)
                            }
                        }
                        .decodeSingle<ProfileData>()
                }

                Log.d("ProfileViewModel", "Profile data: $profile")

                val userName = userRepository.getUserName() ?: ""
                val userEmail = userRepository.getUserEmail() ?: ""
                val userPhone = userRepository.getUserPhone() ?: ""

                _profileData.value = profile.copy(
                    name = userName,
                    email = userEmail,
                    phone = userPhone
                )
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading profile data", e)
                _errorMessage.value = "Error al cargar el perfil: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri, context: Context) {
        viewModelScope.launch {
            _imageUploadStatus.value = ImageUploadStatus.Uploading
            try {
                val fileName = "profile_pictures/${UUID.randomUUID()}.jpg"
                val imageByteArray = imageUri.uriToByteArray(context)
                imageByteArray?.let {
                    withContext(Dispatchers.IO) {
                        supabase.storage["profile_pictures"].upload(fileName, imageByteArray)
                    }
                    val imageUrl = supabase.storage["profile_pictures"].publicUrl(fileName)
                    _imageUploadStatus.value = ImageUploadStatus.Success(imageUrl)

                    // Update local state with new image URL
                    _profileData.value = _profileData.value.copy(url_image = imageUrl)

                    // Update profile data in database with new image URL
                    updateProfileImageUrl(imageUrl)
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error uploading image", e)
                _imageUploadStatus.value = ImageUploadStatus.Error("Error al subir la imagen: ${e.message}")
            }
        }
    }

    private suspend fun updateProfileImageUrl(imageUrl: String) {
        try {
            val userId = userRepository.getUserId()
            withContext(Dispatchers.IO) {
                supabase
                    .from("profile")
                    .update(
                        {
                            ProfileData::url_image setTo imageUrl
                        }
                    ) {
                        filter {
                            eq("user_id", userId)
                        }
                    }
            }
            Log.d("ProfileViewModel", "Profile image URL updated successfully")
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "Error updating profile image URL", e)
            throw e  // Re-throw the exception to handle it in the calling function
        }
    }


    fun updateProfile(desc: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = userRepository.getUserId()

                // Update profile data using the syntax from the documentation
                withContext(Dispatchers.IO) {
                    supabase
                        .from("profile")
                        .update(
                            {
                                ProfileData::desc setTo desc
                            }
                        ) {
                            filter {
                                eq("user_id", userId)
                            }
                        }
                }

                // Update local state
                _profileData.value = _profileData.value.copy(desc = desc)

                _updateResult.value = UpdateProfileResult.Success("Perfil actualizado exitosamente")
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating profile", e)
                _updateResult.value = UpdateProfileResult.Error("Error al actualizar el perfil: ${e.message}")
                _errorMessage.value = "Error al actualizar el perfil: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetUpdateResult() {
        _updateResult.value = null
        _errorMessage.value = null
    }

    fun resetImageUploadStatus() {
        _imageUploadStatus.value = ImageUploadStatus.Idle
    }
}

@Serializable
data class ProfileData(
    val user_id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val url_image: String = "",
    val desc: String = ""
)

sealed class UpdateProfileResult {
    data class Success(val message: String) : UpdateProfileResult()
    data class Error(val message: String) : UpdateProfileResult()
}

sealed class ImageUploadStatus {
    object Idle : ImageUploadStatus()
    object Uploading : ImageUploadStatus()
    data class Success(val imageUrl: String) : ImageUploadStatus()
    data class Error(val message: String) : ImageUploadStatus()
}

class ProfileViewModelFactory(
    private val application: Application,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(application, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}