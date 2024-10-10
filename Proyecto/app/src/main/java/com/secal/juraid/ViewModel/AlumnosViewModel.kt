import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secal.juraid.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class AlumnosViewModel : ViewModel() {
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _addStudentResult = MutableStateFlow<AddStudentResult?>(null)
    val addStudentResult: StateFlow<AddStudentResult?> = _addStudentResult.asStateFlow()


    init {
        loadStudents()
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

    //TODO LO DE HORARIOS

    private val _horarioUrl = MutableStateFlow<String?>(null)
    val horarioUrl: StateFlow<String?> = _horarioUrl.asStateFlow()

    private val _insertHorarioResult = MutableStateFlow<InsertHorarioResult?>(null)
    val insertHorarioResult: StateFlow<InsertHorarioResult?> = _insertHorarioResult.asStateFlow()

    fun getHorarioUrlByStudentId(studentId: String) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    supabase
                        .from("Horarios")
                        .select(columns = Columns.list("url")) {
                            filter {
                                eq("id_alumno", studentId)
                            }
                        }
                        .decodeSingle<HorarioUrl>()
                }
                _horarioUrl.value = result.url
            } catch (e: Exception) {
                e.printStackTrace()
                _horarioUrl.value = null
            }
        }
    }

    fun insertHorario(studentId: String, url: String) {
        viewModelScope.launch {
            _insertHorarioResult.value = InsertHorarioResult.Loading
            try {
                withContext(Dispatchers.IO) {
                    supabase
                        .from("Horarios")
                        .insert(
                            Horario(
                                id_horario = null, // Supabase generará automáticamente el ID
                                url = url,
                                id_alumno = studentId
                            )
                        )
                }
                _insertHorarioResult.value = InsertHorarioResult.Success("Horario insertado exitosamente")
            } catch (e: Exception) {
                e.printStackTrace()
                _insertHorarioResult.value = InsertHorarioResult.Error("Error al insertar el horario: ${e.message}")
            }
        }
    }

    fun resetInsertHorarioResult() {
        _insertHorarioResult.value = null
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
    val id_horario: String? = null,
    val url: String,
    val id_alumno: String
)

sealed class InsertHorarioResult {
    object Loading : InsertHorarioResult()
    data class Success(val message: String) : InsertHorarioResult()
    data class Error(val message: String) : InsertHorarioResult()
}