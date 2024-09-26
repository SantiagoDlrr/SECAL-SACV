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

class HomeViewModel : ViewModel() {
    private val _contentItems = MutableStateFlow<List<ContentItem>>(emptyList())
    val contentItems: StateFlow<List<ContentItem>> = _contentItems.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    init {
        loadContentItems()
    }

    private fun loadContentItems() {
        viewModelScope.launch {
            try {
                val items = getContentFromDatabase()
                _contentItems.value = items
            } catch (e: Exception) {
                println("Error loading content items: ${e.message}")
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            if (_categories.value.isEmpty()) {
                try {
                    val fetchedCategories = getCategoriesfromDatabase()
                    _categories.value = fetchedCategories
                } catch (e: Exception) {
                    println("Error loading categories: ${e.message}")
                }
            }
        }
    }

    private suspend fun getContentFromDatabase(): List<ContentItem> {
        return withContext(Dispatchers.IO) {
            try {
                val contentList = supabase
                    .from("Content")
                    .select()
                    .decodeList<ContentItem>()

                val categories = _categories.value.ifEmpty { getCategoriesfromDatabase() }

                contentList.forEach { content ->
                    content.category = categories.find { it.ID_Category == content.ID_Category }
                }

                Log.d("DatabaseDebug", "Contenido obtenido: $contentList")
                contentList
            } catch (e: Exception) {
                Log.e("DatabaseDebug", "Error obteniendo datos: ${e.message}", e)
                emptyList()
            }
        }
    }

    suspend fun getCategoriesfromDatabase(): List<Category> {
        return withContext(Dispatchers.IO) {
            try {
                val categories = supabase
                    .from("Categories")
                    .select()
                    .decodeList<Category>()
                Log.d("DatabaseDebug", "Categories: $categories")
                categories
            } catch (e: Exception) {
                Log.e("DatabaseDebug", "Error fetching categories: ${e.message}", e)
                emptyList()
            }
        }
    }

    fun addContentItem(title: String, category: Int, urlHeader: String, text: String) {
        viewModelScope.launch {
            try {
                val newItem = ContentInsert(
                    ID_Category = category,
                    title = title,
                    url_header = urlHeader,
                    text = text
                )


                val insertedItem = withContext(Dispatchers.IO) {
                    supabase.from("Content")
                        .insert(newItem)
                        .decodeSingle<ContentItem>()
                }

                _contentItems.value = _contentItems.value + insertedItem
                Log.d("DatabaseDebug", "Nuevo item añadido: $insertedItem")
            } catch (e: Exception) {
                Log.e("DatabaseDebug", "Error añadiendo nuevo item: ${e.message}", e)
            }
        }
    }

    @Serializable
    data class ContentInsert(
        val ID_Category: Int,
        val title: String,
        val url_header: String,
        val text: String
    )

    @Serializable
    data class ContentItem(
        val ID_Post: Int,
        val ID_Category: Int,
        val created_at: String,
        val title: String,
        val url_header: String,
        val text: String,
        var category: Category? = null
    )

    @Serializable
    data class Category(
        val ID_Category: Int,
        val name_category: String
    )

}