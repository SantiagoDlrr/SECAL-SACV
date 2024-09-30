package com.secal.juraid.ViewModel

import android.util.Log
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

class HomeViewModel : ViewModel() {
    private val _contentItems = MutableStateFlow<List<ContentItemPreview>>(emptyList())
    val contentItems: StateFlow<List<ContentItemPreview>> = _contentItems.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                loadCategories()
                loadContentPreviews()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading data: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun loadCategories() {
        if (_categories.value.isEmpty()) {
            try {
                val fetchedCategories = getCategoriesFromDatabase()
                _categories.value = fetchedCategories
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading categories: ${e.message}", e)
            }
        }
    }

    private suspend fun loadContentPreviews() {
        try {
            val items = getContentPreviewsFromDatabase()
            _contentItems.value = items
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error loading content previews: ${e.message}", e)
        }
    }

    private suspend fun getContentPreviewsFromDatabase(): List<ContentItemPreview> {
        return withContext(Dispatchers.IO) {
            try {
                val contentList = supabase
                    .from("Content")
                    .select(columns = Columns.list("ID_Post, ID_Category, created_at, title, url_header"))
                    .decodeList<ContentItemPreview>()

                val categories = _categories.value.ifEmpty { getCategoriesFromDatabase() }

                contentList.forEach { content ->
                    content.category = categories.find { it.ID_Category == content.ID_Category }
                }

                Log.d("DatabaseDebug", "Content previews obtained: $contentList")
                contentList
            } catch (e: Exception) {
                Log.e("DatabaseDebug", "Error getting data: ${e.message}", e)
                emptyList()
            }
        }
    }

    suspend fun getFullContentItem(postId: Int): ContentItem? {
        return withContext(Dispatchers.IO) {
            try {
                val fullContent = supabase
                    .from("Content")
                    .select(){
                        filter { eq("ID_Post", postId) }
                    }
                    .decodeSingleOrNull<ContentItem>()



                Log.d("DatabaseDebug", "Full content obtained: $fullContent")
                fullContent
            } catch (e: Exception) {
                Log.e("DatabaseDebug", "Error getting full content: ${e.message}", e)
                null
            }
        }
    }

    suspend fun getCategoriesFromDatabase(): List<Category> {
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
    data class ContentItemPreview(
        val ID_Post: Int,
        val ID_Category: Int,
        val created_at: String,
        val title: String,
        val url_header: String,
        var category: Category? = null
    )

    @Serializable
    data class ContentItem(
        val ID_Post: Int,
        val ID_Category: Int,
        val created_at: String,
        val title: String,
        val url_header: String,
        val text: String? = null, // Hacer opcional el campo text
        var category: Category? = null
    )

    @Serializable
    data class Category(
        val ID_Category: Int,
        val name_category: String
    )
}