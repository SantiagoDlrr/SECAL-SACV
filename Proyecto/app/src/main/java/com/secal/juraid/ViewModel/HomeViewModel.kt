package com.secal.juraid.ViewModel

import android.util.Log
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
import kotlinx.serialization.Serializer

class HomeViewModel : ViewModel() {
    private val _contentItems = MutableStateFlow<List<ContentItem>>(emptyList())
    val contentItems: StateFlow<List<ContentItem>> = _contentItems.asStateFlow()

    init {
        loadContentItems()
    }

    private fun loadContentItems() {
        viewModelScope.launch {
            try {
                val items = getContentFromDatabase()
                _contentItems.value = items
            } catch (e: Exception) {
                // Manejar el error aquí
                println("Error loading content items: ${e.message}")
            }
        }
    }

    private suspend fun getContentFromDatabase(): List<ContentItem> {
        var contentList: List<ContentItem> = emptyList()
        withContext(Dispatchers.IO) {
            try {
                contentList = supabase
                    .from("Content")
                    .select()
                    .decodeList<ContentItem>()


            } catch (e: Exception) {
                Log.e("DatabaseDebug", "Error obteniendo datos: ${e.message}", e)
            }
        }

        Log.d("DatabaseDebug", "Finalizando la función con resultados: $contentList")

        return contentList
    }

}

@Serializable
data class ContentItem(
    val ID_Post: Int,
    val ID_Category: Int,
    val created_at: String,
    val title: String,
    val url_header: String,
    val text: String
)