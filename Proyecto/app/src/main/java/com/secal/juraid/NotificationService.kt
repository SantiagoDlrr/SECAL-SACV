import android.content.Context
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class NotificationService(private val context: Context) {
    private companion object {
        const val FCM_API = "https://fcm.googleapis.com/v1/projects/secal-402a6/messages:send"
        const val SCOPE = "https://www.googleapis.com/auth/firebase.messaging"
        const val TAG = "NotificationService"
    }

    private var credentials: GoogleCredentials? = null
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    data class Message(val message: FCMMessage)
    data class FCMMessage(val token: String, val notification: NotificationData)
    data class NotificationData(val title: String, val body: String)

    private fun getAccessToken(): String {
        if (credentials == null) {
            try {
                context.assets.open("service-account.json").use { inputStream ->
                    Log.d(TAG, "Reading service account file")
                    val jsonContent = inputStream.bufferedReader().use { it.readText() }
                    Log.d(TAG, "Service account file content (first 100 chars): ${jsonContent.take(100)}")
                    credentials = GoogleCredentials
                        .fromStream(jsonContent.byteInputStream())
                        .createScoped(listOf(SCOPE))
                    Log.d(TAG, "Credentials created successfully")
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error reading service account file", e)
                throw IllegalStateException("Failed to read service account file", e)
            }
        }
        try {
            Log.d(TAG, "Refreshing credentials")
            credentials?.refresh()
            val token = credentials?.accessToken?.tokenValue
            Log.d(TAG, "Access token obtained (first 10 chars): ${token?.take(10)}")
            return token ?: throw IllegalStateException("Failed to obtain access token")
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing credentials", e)
            throw IllegalStateException("Failed to refresh credentials", e)
        }
    }

    suspend fun sendNotification(token: String, title: String, message: String) {
        withContext(Dispatchers.IO) {
            try {
                require(token.isNotEmpty()) { "FCM token cannot be empty" }
                require(title.isNotEmpty()) { "Notification title cannot be empty" }
                require(message.isNotEmpty()) { "Notification message cannot be empty" }

                val fcmMessage = Message(
                    FCMMessage(
                        token = token,
                        notification = NotificationData(
                            title = title,
                            body = message
                        )
                    )
                )

                val jsonBody = Gson().toJson(fcmMessage)
                Log.d(TAG, "Prepared JSON body: $jsonBody")

                val accessToken = getAccessToken()
                Log.d(TAG, "Obtained access token")

                val request = Request.Builder()
                    .url(FCM_API)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("Content-Type", "application/json")
                    .post(jsonBody.toRequestBody("application/json".toMediaType()))
                    .build()

                Log.d(TAG, "Sending FCM request")
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string()
                        Log.e(TAG, "FCM API error: $errorBody")
                        throw Exception("Failed to send notification: $errorBody")
                    }
                    Log.d(TAG, "Notification sent successfully to token: $token")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending notification", e)
                throw e
            }
        }
    }
}