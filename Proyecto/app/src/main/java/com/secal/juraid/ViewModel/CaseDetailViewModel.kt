import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secal.juraid.ViewModel.Case
import com.secal.juraid.ViewModel.Hiperlink
import com.secal.juraid.ViewModel.unitInvestigation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.secal.juraid.supabase
import io.github.jan.supabase.postgrest.from

class CaseDetailViewModel : ViewModel() {
    private val _caseDetail = MutableStateFlow<Case?>(null)
    val caseDetail: StateFlow<Case?> = _caseDetail

    private val _hyperlinks = MutableStateFlow<List<Hiperlink>>(emptyList())
    val hyperlinks: StateFlow<List<Hiperlink>> = _hyperlinks

    private val _unitInvestigation = MutableStateFlow<unitInvestigation?>(null)
    val unitInvestigation: StateFlow<unitInvestigation?> = _unitInvestigation

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
}