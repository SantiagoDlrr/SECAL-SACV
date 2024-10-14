import android.content.Context
import android.net.Uri
import android.util.Log
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.secal.juraid.ViewModel.HomeViewModel
import com.secal.juraid.ViewModel.HomeViewModel.ContentInsert
import com.secal.juraid.ViewModel.HomeViewModel.ContentItem
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

class AlumnosViewModel(application: Application) : AndroidViewModel(application) {
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _addStudentResult = MutableStateFlow<AddStudentResult?>(null)
    val addStudentResult: StateFlow<AddStudentResult?> = _addStudentResult.asStateFlow()

    private val notificationService = NotificationService(application)

    init {
        loadStudents()
    }

    // New method to load all data
    fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load students
                loadStudents()

                // If there are other data that needs to be loaded, add them here
                // For example, if you need to load related data or additional information

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun sendNotification(token: String?, title: String, message: String) {
        if (token == null) {
            Log.w("AlumnosViewModel", "Attempted to send notification but token was null")
            return
        }

        viewModelScope.launch {
            try {
                notificationService.sendNotification(token, title, message)
                Log.d("AlumnosViewModel", "Notification sent successfully")
            } catch (e: Exception) {
                Log.e("AlumnosViewModel", "Error sending notification", e)
            }
        }
    }



    private fun loadStudents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val studentList = getStudentsFromDatabase()
                _students.value = studentList
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getStudentsFromDatabase(): List<Student> {
        return withContext(Dispatchers.IO) {
            try {
                supabase
                    .from("users")
                    .select() {
                        filter {
                            eq("role", 2)
                        }
                    }
                    .decodeList<Student>()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    fun getStudentById(id: String): StateFlow<Student?> {
        val studentFlow = MutableStateFlow<Student?>(null)
        viewModelScope.launch {
            val student = withContext(Dispatchers.IO) {
                try {
                    supabase
                        .from("users")
                        .select() {
                            filter {
                                eq("id", id)
                            }
                        }
                        .decodeSingle<Student>()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            studentFlow.value = student
        }
        return studentFlow
    }

    private val _profilePictures = MutableStateFlow<Map<String, String>>(emptyMap())
    val profilePictures: StateFlow<Map<String, String>> = _profilePictures.asStateFlow()
    private val _profilePictureUrl = MutableStateFlow<String?>(null)
    val profilePictureUrl: StateFlow<String?> = _profilePictureUrl.asStateFlow()

    fun loadProfilePictures(studentIds: List<String>) {
        viewModelScope.launch {
            val pictures = studentIds.associateWith { studentId ->
                getPFPUrl(studentId)
            }
            _profilePictures.value = pictures
        }
    }

    fun loadProfilePictureUrl(studentId: String) {
        viewModelScope.launch {
            _profilePictureUrl.value = getPFPUrl(studentId)
        }
    }

    private suspend fun getPFPUrl(studentId: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val result = supabase
                    .from("profile")
                    .select(columns = Columns.list("url_image")) {
                        filter {
                            eq("user_id", studentId)
                        }
                    }
                    .decodeSingle<ProfilePicture>()
                result.url_image ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }

    @Serializable
    data class ProfilePicture(val url_image: String?)



    fun deactivateStudent(studentId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    supabase
                        .from("users")
                        .update(
                            {
                                Student::role setTo 0
                            }
                        ) {
                            filter {
                                eq("id", studentId)
                            }
                        }
                }
                loadStudents() // Recargar la lista después de la actualización
                val token = getStudentToken(studentId)
                Log.d("AlumnosViewModel", "Token: $token")
                if (token != null) {
                    sendNotification(token, "Cuenta desactivada", "Tu cuenta de alumno ha sido desactivada")
                }
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                onError("Error al desactivar el estudiante")
            }
        }
    }

    fun addStudentByEmail(email: String) {
        viewModelScope.launch {
            _addStudentResult.value = AddStudentResult.Loading
            try {
                val existingUser = withContext(Dispatchers.IO) {
                    supabase
                        .from("users")
                        .select() {
                            filter {
                                eq("email", email)
                            }
                        }
                        .decodeList<Student>()
                }

                if (existingUser.isNullOrEmpty()) {
                    _addStudentResult.value = AddStudentResult.Error("No se encontró un usuario con ese correo electrónico")
                } else {
                    val user = existingUser.first()
                    if (user.role == 2) {
                        _addStudentResult.value = AddStudentResult.Error("Este usuario ya es un alumno")
                    } else {
                        withContext(Dispatchers.IO) {
                            supabase
                                .from("users")
                                .update(
                                    {
                                        Student::role setTo 2
                                    }
                                ) {
                                    filter {
                                        eq("id", user.id)
                                    }
                                }
                        }

                        val token = getStudentToken(user.id)
                        if (token != null) {
                            sendNotification(token, "Bienvenido", "Has sido añadido como alumno")
                        }

                        loadStudents() // Reload the list after updating
                        _addStudentResult.value = AddStudentResult.Success("Alumno añadido exitosamente")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _addStudentResult.value = AddStudentResult.Error("Error al añadir el alumno: ${e.message}")
            }
        }
    }



    fun resetAddStudentResult() {
        _addStudentResult.value = null
    }

    private suspend fun getStudentToken(studentId: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val result = supabase
                    .from("users")
                    .select(columns = Columns.list("fcm_token")) {
                        filter {
                            eq("id", studentId)
                        }
                    }
                    .decodeSingle<FCMToken>()
                result.fcm_token
            } catch (e: Exception) {
                Log.e("AlumnosViewModel", "Error getting student token", e)
                null
            }
        }
    }

    @Serializable
    private data class FCMToken(val fcm_token: String?)



    private val _horarioUrl = MutableStateFlow<String?>(null)
    val horarioUrl: StateFlow<String?> = _horarioUrl.asStateFlow()

    private val _insertHorarioResult = MutableStateFlow<InsertHorarioResult?>(null)


    suspend fun getHorarioUrlByStudentId(studentId: String) {
        val url = try {
            withContext(Dispatchers.IO) {
                val result = supabase.from("Horarios")
                    .select(columns = Columns.list("url")) {
                        filter {
                            eq("id_alumno", studentId)
                        }
                    }
                    .decodeSingle<HorarioUrl>()
                result.url
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        _horarioUrl.value = url
    }


    fun insertHorario(studentId: String, imageUri: Uri, context: Context) {
        val homeviewModel = HomeViewModel()
        viewModelScope.launch {
            try {
                val fileName = "horarios/${UUID.randomUUID()}.jpg"
                var imageUrl: String? = null

                val imageByteArray = imageUri.uriToByteArray(context)
                imageByteArray?.let {
                    homeviewModel.uploadFile("horarios",fileName, imageByteArray)
                    // Obtener la URL pública inmediatamente después de cargar
                    imageUrl = supabase.storage["horarios"].publicUrl(fileName)
                }

                // Verificar si ya existe un registro para este estudiante
                val existingHorario = supabase.from("Horarios")
                    .select() {
                        filter {
                            eq("id_alumno", studentId)
                        }
                    }
                    .decodeList<Horario>()

                if (existingHorario.isNotEmpty()) {
                    // Actualizar el registro existente
                    supabase.from("Horarios")
                        .update(
                            {
                                set("url", imageUrl ?: "")
                            }
                        ) {
                            filter {
                                eq("id_alumno", studentId)
                            }
                        }
                    Log.d("DatabaseDebug", "Horario actualizado para el estudiante: $studentId")
                } else {
                    // Insertar un nuevo registro
                    val newItem = Horario(
                        id_horario = null,
                        id_alumno = studentId,
                        url = imageUrl ?: ""
                    )
                    supabase.from("Horarios")
                        .insert(newItem)
                        .decodeSingle<Horario>()
                    Log.d("DatabaseDebug", "Nuevo horario insertado para el estudiante: $studentId")
                }

                // Actualizar el estado del ViewModel
                _horarioUrl.value = imageUrl

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DatabaseDebug", "Error añadiendo nuevo horario: ${e.message}", e)
            }
        }
    }

}

sealed class AddStudentResult {
    object Loading : AddStudentResult()
    data class Success(val message: String) : AddStudentResult()
    data class Error(val message: String) : AddStudentResult()
}


@Serializable
data class Student(
    val id: String,
    val email: String,
    val name: String,
    val first_last_name: String,
    val second_last_name: String,
    val phone: String,
    val role: Int
)

//PARA HORARIOS
@Serializable
data class HorarioUrl(
    val url: String
)

@Serializable
data class Horario(
    val id_horario: Int? = null,
    val id_alumno: String,
    val url: String

)

sealed class InsertHorarioResult {
    object Loading : InsertHorarioResult()
    data class Success(val message: String) : InsertHorarioResult()
    data class Error(val message: String) : InsertHorarioResult()
}

