import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NotificationService {
    private const val FCM_API = "https://fcm.googleapis.com/v1/projects/secal-402a6/messages:send"
    private const val SCOPE = "https://www.googleapis.com/auth/firebase.messaging"
    private const val TAG = "NotificationService"

    private var credentials: GoogleCredentials? = null
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    data class Message(
        val message: FCMMessage
    )

    data class FCMMessage(
        val token: String,
        val notification: NotificationData
    )

    data class NotificationData(
        val title: String,
        val body: String
    )

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

                val request = Request.Builder()
                    .url(FCM_API)
                    .addHeader("Authorization", "key=TU_SERVER_KEY_AQUI")  // Reemplaza con tu server key
                    .addHeader("Content-Type", "application/json")
                    .post(jsonBody.toRequestBody("application/json".toMediaType()))
                    .build()

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