package com.example.octavian

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.util.JsonReader

class FetchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Ensure activity_main.xml exists in res/layout

        // Call the fetchData function
        lifecycleScope.launch {
            val jsonResponse = fetchData("http://192.168.24.15/final_admin_api/") // Replace with your actual URL
            Log.d("JSON Response", jsonResponse) // Log the response
        }
    }

    private suspend fun fetchData(urlString: String): String {
        return withContext(Dispatchers.IO) {
            var jsonResponse = ""
            try {
                val url = URL(urlString)
                val urlConnection = url.openConnection() as HttpURLConnection
                try {
                    // Check for HTTP response code
                    if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream = urlConnection.inputStream
                        val jsonReader = JsonReader(InputStreamReader(inputStream))
                        jsonReader.isLenient = true // Allow lenient parsing

                        // Parse the JSON data here
                        jsonResponse = readJson(jsonReader)
                    } else {
                        Log.e("FetchData", "Error: HTTP response code ${urlConnection.responseCode}")
                    }
                } finally {
                    urlConnection.disconnect() // Ensure the connection is closed
                }
            } catch (e: Exception) {
                Log.e("FetchData", "Error fetching data", e)
            }
            jsonResponse
        }
    }

    private fun readJson(jsonReader: JsonReader): String {
        // Implement your JSON parsing logic here
        // Example: Read a JSON object and return a string
        val result = StringBuilder()
        jsonReader.beginObject() // Start reading the JSON object
        while (jsonReader.hasNext()) {
            val name = jsonReader.nextName()
            val value = jsonReader.nextString() // Assuming the value is a string
            result.append("$name: $value\n") // Append the name-value pair to the result
        }
        jsonReader.endObject() // End reading the JSON object
        return result.toString() // Return the parsed data as a string
    }
}