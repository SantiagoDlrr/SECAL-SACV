package com.secal.juraid.ViewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import com.secal.juraid.supabase
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CasesViewModel : ViewModel() {
    private val _cases = MutableStateFlow<List<Case>>(emptyList())
    val cases: StateFlow<List<Case>> = _cases.asStateFlow()

    // Nuevo StateFlow para casos activos
    private val _activeCases = MutableStateFlow<List<Case>>(emptyList())
    val activeCases: StateFlow<List<Case>> = _activeCases.asStateFlow()

    private val _unitInvestigations = MutableStateFlow<List<unitInvestigation>>(emptyList())
    val unitInvestigations: StateFlow<List<unitInvestigation>> = _unitInvestigations.asStateFlow()

    private val _hiperlinks = MutableStateFlow<List<Hiperlink>>(emptyList())
    val hiperlinks: StateFlow<List<Hiperlink>> = _hiperlinks.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _nucList = MutableStateFlow<List<String>>(emptyList())
    val nucList: StateFlow<List<String>> = _nucList.asStateFlow()

    private val _assignedCases = MutableStateFlow<List<StudentCaseRelation>>(emptyList())
    val assignedCases: StateFlow<List<StudentCaseRelation>> = _assignedCases.asStateFlow()

    init {
        loadAllData()
    }

    private suspend fun loadAssignedCases() {
        try {
            val items = getAssignedCasesFromDatabase()
            _assignedCases.value = items
        } catch (e: Exception) {
            Log.e("CasesViewModel", "Error loading assigned cases: ${e.message}", e)
        }
    }

    private suspend fun getAssignedCasesFromDatabase(): List<StudentCaseRelation> {
        return withContext(Dispatchers.IO) {
            try {
                val assignedCasesList = supabase
                    .from("Alumnos_Casos") // Asegúrate que este sea el nombre correcto de tu tabla
                    .select()
                    .decodeList<StudentCaseRelation>()
                assignedCasesList
            } catch (e: Exception) {
                Log.e("CasesViewModel", "Error getting assigned cases from database: ${e.message}", e)
                emptyList()
            }
        }
    }

    suspend fun assignCaseToStudent(studentId: String, caseNUC: String) {
        viewModelScope.launch {
            try {
                // Primero, encuentra el ID del caso usando el NUC
                val case = _cases.value.find { it.NUC == caseNUC }
                case?.let {
                    val relation = StudentCaseRelation(
                        id_alumno = studentId,
                        id_Caso = it.id
                    )

                    // Inserta la relación en la base de datos
                    withContext(Dispatchers.IO) {
                        supabase.from("Alumnos_Casos")
                            .insert(relation)
                    }

                    // Actualiza el estado local
                    _assignedCases.value = _assignedCases.value + relation
                }
            } catch (e: Exception) {
                Log.e("CasesViewModel", "Error assigning case to student: ${e.message}", e)
            }
        }
    }

    fun getAssignedCasesForStudent(studentId: String): StateFlow<List<Case>> {
        val assignedCasesFlow = MutableStateFlow<List<Case>>(emptyList())

        viewModelScope.launch {
            try {
                // Asegurarse de que las relaciones estén cargadas
                if (_assignedCases.value.isEmpty()) {
                    loadAssignedCases() // Asegúrate de que esta función está siendo llamada antes
                }

                // Obtener las relaciones para este estudiante
                val relations = _assignedCases.value.filter { it.id_alumno == studentId }

                // Filtrar los casos asignados usando las relaciones
                val studentCases = _cases.value.filter { case ->
                    relations.any { relation -> relation.id_Caso == case.id }
                }

                // Actualizar el flujo con los casos asignados
                assignedCasesFlow.value = studentCases
            } catch (e: Exception) {
                Log.e("CasesViewModel", "Error getting assigned cases for student: ${e.message}", e)
            }
        }

        return assignedCasesFlow.asStateFlow()
    }


    // Actualiza loadAllData para incluir los casos asignados
    fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                loadCases()
                loadUnitInvestigations()
                loadHiperlinks()
                loadNucList()
                loadAssignedCases() // Asegúrate de que este método está siendo llamado aquí
            } catch (e: Exception) {
                Log.e("CasesViewModel", "Error loading data: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    private fun loadNucList() {
        viewModelScope.launch {
            try {
                val nucList = getCasesFromDatabase().map { it.NUC }
                _nucList.value = nucList
            } catch (e: Exception) {
                Log.e("CasesViewModel", "Error loading NUC list: ${e.message}", e)
            }
        }
    }


    private suspend fun loadCases() {
        if (_cases.value.isEmpty()) {
            try {
                val fetchedCases = getCasesFromDatabase()
                _cases.value = fetchedCases

                // Filtrar casos activos
                _activeCases.value = fetchedCases.filter { it.status != 0 }
            } catch (e: Exception) {
                Log.e("CasesViewModel", "Error loading cases: ${e.message}", e)
            }
        }
    }

    private suspend fun loadUnitInvestigations() {
        try {
            val items = getUnitInvestigationsFromDatabase()
            _unitInvestigations.value = items
        } catch (e: Exception) {
            Log.e("CasesViewModel", "Error loading unit investigations: ${e.message}", e)
        }
    }

    private suspend fun loadHiperlinks() {
        try {
            val items = getHiperlinksFromDatabase()
            _hiperlinks.value = items
        } catch (e: Exception) {
            Log.e("CasesViewModel", "Error loading hiperlinks: ${e.message}", e)
        }
    }

    private suspend fun getCasesFromDatabase(): List<Case> {
        return withContext(Dispatchers.IO) {
            try {
                val casesList = supabase
                    .from("Cases")
                    .select()
                    .decodeList<Case>()
                casesList
            } catch (e: Exception) {
                Log.e("CasesViewModel", "Error getting cases from database", e)
                when (e) {
                    is kotlinx.serialization.SerializationException -> {
                        Log.e("CasesViewModel", "Serialization error. Check if the Case data class matches the database schema", e)
                    }
                    is io.github.jan.supabase.exceptions.RestException -> {
                        Log.e("CasesViewModel", "Supabase REST API error", e)
                    }
                    else -> {
                        Log.e("CasesViewModel", "Unknown error occurred", e)
                    }
                }
                emptyList()
            }
        }
    }

    private suspend fun getUnitInvestigationsFromDatabase(): List<unitInvestigation> {
        return withContext(Dispatchers.IO) {
            try {
                val unitInvestigationsList = supabase
                    .from("Units")
                    .select()
                    .decodeList<unitInvestigation>()
                unitInvestigationsList
            } catch (e: Exception) {
                Log.e("CasesViewModel", "Error getting unit investigations from database: ${e.message}", e)
                emptyList()
            }
        }
    }

    private suspend fun getHiperlinksFromDatabase(): List<Hiperlink> {
        return withContext(Dispatchers.IO) {
            try {
                val hiperlinksList = supabase
                    .from("Hiperlinks")
                    .select()
                    .decodeList<Hiperlink>()
                hiperlinksList
            } catch (e: Exception) {
                Log.e("CasesViewModel", "Error getting hiperlinks from database: ${e.message}", e)
                emptyList()
            }
        }
    }


    suspend fun deleteCase(caseId: Int) {
        try {
            supabase.from("Cases")
                .update(
                    { set<Int>("status", 0) }

                ) { filter { eq("id", caseId) } }

            _cases.value = _cases.value.map { case ->
                if (case.id == caseId) {
                    case.copy(status = 0)
                } else {
                    case
                }
            }

            _activeCases.value = _activeCases.value.filterNot { it.id == caseId }

        } catch (e: Exception) {
            Log.e(TAG, "Error updating status case (deleting)", e)
        }

    }

    @Serializable
    data class StudentCaseRelation(
        val id: Int? = null,
        val id_alumno: String,
        val id_Caso: Int
    )

    suspend fun addCase(
        nombreAbogado: String,
        nombreCliente: String,
        nuc: String,
        carpetaJudicial: String,
        carpetaInvestigacion: String,
        accesoFv: String,
        passFv: String,
        fiscalTitular: String,
        idUnidadInvestigacion: Int?,
        drive: String
    ) {
        viewModelScope.launch {
            try {
                val newCase = CaseInsert(
                    nombre_abogado = nombreAbogado,
                    nombre_cliente = nombreCliente,
                    NUC = nuc,
                    carpeta_judicial = carpetaJudicial,
                    carpeta_investigacion = carpetaInvestigacion,
                    acceso_fv = accesoFv,
                    pass_fv = passFv,
                    fiscal_titular = fiscalTitular,
                    id_unidad_investigacion = idUnidadInvestigacion,
                    drive = drive,
                    status = 1
                )

                val insertedCase = withContext(Dispatchers.IO) {
                    supabase.from("Cases")
                        .insert(newCase)
                        .decodeSingle<Case>()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error adding case", e)
            }
        }
    }

    @Serializable
    data class CaseInsert(
        val nombre_abogado: String,
        val nombre_cliente: String,
        val NUC: String,  // Changed from Int to String
        val carpeta_judicial: String,
        val carpeta_investigacion: String,
        val acceso_fv: String,
        val pass_fv: String,
        val fiscal_titular: String,
        val id_unidad_investigacion: Int?,  // Made nullable as it can be NULL in the database
        val drive: String,
        val status: Int?  // Made nullable as it can be NULL in the database
    )

}

@Serializable
data class Case(
    val id: Int,
    val created_at: String,
    val nombre_abogado: String,
    val nombre_cliente: String,
    val NUC: String,  // Changed from Int to String
    val carpeta_judicial: String,
    val carpeta_investigacion: String,
    val acceso_fv: String,
    val pass_fv: String,
    val fiscal_titular: String,
    val id_unidad_investigacion: Int?,  // Made nullable as it can be NULL in the database
    val drive: String,
    val status: Int?  // Made nullable as it can be NULL in the database
)



@Serializable
data class unitInvestigation(
    val id: Int,
    val nombre: String,
    val direccion: String
)

@Serializable
data class Hiperlink(
    val id: Int,
    val id_caso: Int,
    val texto: String,
    val link: String
)