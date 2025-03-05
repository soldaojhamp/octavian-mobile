import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.JsonReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

suspend fun fetchData(urlString: String): String {
    return withContext(Dispatchers.IO) {
        var jsonResponse = ""
        try {
            val url = URL(urlString)
            val urlConnection = url.openConnection() as HttpURLConnection
            try {
                val inputStream = urlConnection.inputStream
                val jsonReader = JsonReader(InputStreamReader(inputStream))
                jsonReader.isLenient = true // Allow lenient parsing

                // Parse the JSON data here
                jsonResponse = readJson(jsonReader)
            } finally {
                urlConnection.disconnect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonResponse
    }
}

private fun readJson(jsonReader: JsonReader): String {
    // Implement your JSON parsing logic here
    return "" // Replace with actual parsing logic
}