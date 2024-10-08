package com.secal.juraid.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secal.juraid.supabase
import io.github.jan.supabase.postgrest.from
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
                // Aquí podrías manejar el error, por ejemplo con un estado de error
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
                        .select()
                        {
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