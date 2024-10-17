import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secal.juraid.ViewModel.Case
import com.secal.juraid.ViewModel.CasesViewModel.FCMToken
import com.secal.juraid.ViewModel.unitInvestigation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.secal.juraid.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable

class CaseDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val _caseDetail = MutableStateFlow<Case?>(null)
    val caseDetail: StateFlow<Case?> = _caseDetail

    private val _hyperlinks = MutableStateFlow<List<Hiperlink>>(emptyList())
    val hyperlinks: StateFlow<List<Hiperlink>> = _hyperlinks

    private val _unitInvestigation = MutableStateFlow<unitInvestigation?>(null)
    val unitInvestigation: StateFlow<unitInvestigation?> = _unitInvestigation

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val notificationService = NotificationService(application)


    fun loadCaseDetail(caseId: Int) {
        viewModelScope.launch {
            try {
                val case = supabase
                    .from("Cases")
                    .select(){
                        filter { eq("id", caseId) }
                    }
                    .decodeSingleOrNull<Case>()
                _caseDetail.value = case

                case?.let {
                    // Fetch unit investigation data
                    val unit = supabase
                        .from("Units")
                        .select(){
                            filter { case.id_unidad_investigacion?.let { it1 -> eq("id", it1) } }
                        }
                        .decodeSingleOrNull<unitInvestigation>()
                    _unitInvestigation.value = unit

                    // Fetch hyperlinks for the case
                    val links = supabase
                        .from("Hiperlinks")
                        .select(){
                            filter { eq("id_caso", caseId) }
                        }
                        .decodeList<Hiperlink>()
                    _hyperlinks.value = links
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading case detail", e)
            }
        }
    }

    fun updateCase(
        caseId: Int,
        nuc: String,
        carpetaJudicial: String,
        carpetaInvestigacion: String,
        acceso_fv: String,
        pass_fv: String,
        fiscalTitular: String,
        id_unidad_investigacion: Int?,
        drive: String
    ) {
        viewModelScope.launch {
            try {
                withTimeout(5000L) {  // 5 seconds timeout
                supabase.from("Cases")
                    .update(
                        {
                            set("NUC", nuc)
                            set("carpeta_judicial", carpetaJudicial)
                            set("carpeta_investigacion", carpetaInvestigacion)
                            set("acceso_fv", acceso_fv)
                            set("pass_fv", pass_fv)
                            set("fiscal_titular", fiscalTitular)
                            set("id_unidad_investigacion", id_unidad_investigacion)
                            set("drive", drive)
                        }
                    ) {
                        filter { eq("id", caseId) }
                    }

                    notifyAllLawyers("Modificaciones a Caso", "Se ha modificado el caso con NUC: $nuc")

                    // Reload case detail to reflect changes
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error updating case", e)
            }
        }
    }

    fun addHyperlink(caseId: Int, texto: String, link: String) {
        viewModelScope.launch {
            try {

                val newItem = HiperlinkInsert(
                    id_caso = caseId,
                    texto = texto,
                    link = link
                )

                val insertedItem = withContext(Dispatchers.IO) {
                    supabase.from("Hiperlinks")
                        .insert(newItem)
                        .decodeSingle<Hiperlink>()
                }

                // Actualizar la lista local de hipervínculos
                val currentList = _hyperlinks.value.toMutableList()
                currentList.add(insertedItem)
                _hyperlinks.value = currentList

                // Reload case detail to reflect changes
                loadCaseDetail(caseId)
                Log.d("DatabaseDebug", "Nuevo item añadido: $insertedItem")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding hyperlink", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun notifyAllLawyers(title: String, message: String) {
        val lawyerTokens = getAllLawyerTokens()

        lawyerTokens.forEach { token ->
            try {
                notificationService.sendNotification(token, title, message)
                Log.d("CasesViewModel", "Notification sent successfully to lawyer")
            } catch (e: Exception) {
                Log.e("CasesViewModel", "Error sending notification to lawyer", e)
            }
        }
    }

    private suspend fun getAllLawyerTokens(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val result = supabase
                    .from("users")
                    .select(columns = Columns.list("fcm_token")) {
                        filter {
                            eq("role", 1) // Asumiendo que el rol 1 corresponde a los abogados
                        }
                    }
                    .decodeList<FCMToken>()
                result.mapNotNull { it.fcm_token }
            } catch (e: Exception) {
                Log.e("CasesViewModel", "Error getting lawyer tokens", e)
                emptyList()
            }
        }
    }

    suspend fun updateHyperlink(hyperlinkId: Int, texto: String, link: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                supabase.from("Hiperlinks")
                    .update(
                        {
                            set("texto", texto)
                            set("link", link)
                        }
                    ) {
                        filter { eq("id", hyperlinkId) }
                    }

                // Recargar los detalles del caso para reflejar los cambios
                _caseDetail.value?.id?.let { loadCaseDetail(it) }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating hyperlink", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteHyperlink(hyperlinkId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                supabase.from("Hiperlinks")
                    .delete{
                        filter { eq("id", hyperlinkId) }
                    }

                // Recargar los detalles del caso para reflejar los cambios
                _caseDetail.value?.id?.let { loadCaseDetail(it) }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting hyperlink", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    @Serializable
    private data class FCMToken(val fcm_token: String?)

    @Serializable
    data class Hiperlink(
        val id: Int,
        val id_caso: Int,
        val texto: String,
        val link: String
    )
    @Serializable
    data class HiperlinkInsert(
        val id_caso: Int,
        val texto: String,
        val link: String
    )
}