package com.secal.juraid.ViewModel

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secal.juraid.supabase
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.http.ContentDisposition.Companion.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.io.IOException
import kotlin.time.Duration.Companion.minutes

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

    fun addContentItem(title: String, category: Int, imageUri: Uri?, text: String, context: Context) {
        viewModelScope.launch {
            try {
                val fileName = "post_images/${UUID.randomUUID()}.jpg"
                var imageUrl: String? = null

                if(imageUri != null) {
                    val imageByteArray = imageUri.uriToByteArray(context)
                    imageByteArray?.let {
                        uploadFile("postImage",fileName, imageByteArray)
                        // Obtener la URL pública inmediatamente después de cargar
                        imageUrl = supabase.storage["postImage"].publicUrl(fileName)
                    }
                }


                val newItem = ContentInsert(
                    ID_Category = category,
                    title = title,
                    url_header = imageUrl ?: "https://cdlpmnjnonnruremcszc.supabase.co/storage/v1/object/public/postImage/post_images/martillito.png",
                    text = text
                )

                val insertedItem = supabase.from("Content")
                    .insert(newItem)
                    .decodeSingle<ContentItem>()

                loadAllData()

            } catch (e: Exception) {
                Log.e("DatabaseDebug", "Error añadiendo nuevo item: ${e.message}", e)
            }
        }
    }

    suspend fun updateContentItem(postId: Int, title: String, category: Int, urlHeader: String, text: String, imageUri: Uri?, context: Context) {
        //para ver qué función llamamos
        Log.d(TAG, "updateContentItem() called")

        var url = urlHeader

        try {
            if(imageUri != null) {
                val fileName = "post_images/${UUID.randomUUID()}.jpg"
                val imageByteArray = imageUri.uriToByteArray(context)
                imageByteArray?.let {
                    uploadFile("postImage", fileName, imageByteArray)
                    // Obtener la URL pública inmediatamente después de cargar
                    url = supabase.storage["postImage"].publicUrl(fileName)
                }
                //deleteFileFromBucket("postImage", urlHeader)
            }
            supabase.from("Content").update(
                {
                    set("title", title)
                    set("ID_Category", category)
                    set("url_header", url)
                    set("text", text)
                }
            ) {
                filter {
                    eq("ID_Post", postId)
                }
            }
            Log.d("DatabaseDebug", "Item updated: $postId")
        } catch (e: Exception) {
            Log.e("DatabaseDebug", "Error updating item: ${e.message}", e)
        }
    }

    private fun extractFileNameFromUrl(url: String): String? {
        return try {
            val uri = Uri.parse(url)
            uri.lastPathSegment
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error extracting filename from URL: ${e.message}")
            null
        }
    }

    fun deleteFileFromBucket(bucketName: String, fileUrl: String) {
        viewModelScope.launch {
            try {
                val fileName = extractFileNameFromUrl(fileUrl)
                if (fileName != null) {
                    val bucket = supabase.storage.from(bucketName)
                    bucket.delete(fileName)
                    Log.d("HomeViewModel", "File deleted successfully: $fileName")
                } else {
                    Log.e("HomeViewModel", "Unable to extract filename from URL: $fileUrl")
                }
                Log.d("HomeViewModel", "File deleted successfully: $fileName")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error deleting file: ${e.message}", e)
            }
        }
    }


    fun uploadFile(bucketName: String, fileName: String, byteArray: ByteArray){
        viewModelScope.launch {
            try {
                val bucket = supabase.storage[bucketName]
                bucket.upload(fileName, byteArray)

            } catch (e: Exception) {
                Log.e("DatabaseDebug", "Error uploading image: ${e.message}", e)
            }
        }
    }

    fun deleteContentItem(postId: Int) {
        viewModelScope.launch {
            try {
                // Primero, obtenemos el artículo completo para conseguir la URL de la imagen
                val article = getFullContentItem(postId)

                // Eliminamos el artículo de la base de datos
                supabase.from("Content")
                    .delete {
                        filter { eq("ID_Post", postId) }
                    }

                // Si el artículo tiene una imagen asociada, la eliminamos del bucket
                /*article?.url_header?.let { imageUrl ->
                    if (imageUrl != "https://cdlpmnjnonnruremcszc.supabase.co/storage/v1/object/public/postImage/post_images/martillito.png") {
                        deleteFileFromBucket("postImage", imageUrl)
                    }
                }*/

                // Recargamos los datos para actualizar la lista de artículos
                loadAllData()

                Log.d("HomeViewModel", "Article with ID $postId deleted successfully")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error deleting article with ID $postId: ${e.message}", e)
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
        var category: Category? = null,
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

@Throws(IOException::class)
fun Uri.uriToByteArray(context: Context) =
    context.contentResolver.openInputStream(this)?.use { it.buffered().readBytes() }
