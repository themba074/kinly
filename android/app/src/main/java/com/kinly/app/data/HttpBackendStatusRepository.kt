package com.kinly.app.data

import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HttpBackendStatusRepository(
    private val readinessUrl: String,
    private val connectTimeoutMillis: Int = 3_000,
    private val readTimeoutMillis: Int = 3_000,
) : BackendStatusRepository {
    override suspend fun isReady(): Boolean = withContext(Dispatchers.IO) {
        val connection = URL(readinessUrl).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = connectTimeoutMillis
            connection.readTimeout = readTimeoutMillis
            connection.useCaches = false

            val statusCode = connection.responseCode
            val body = if (statusCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                ""
            }
            BackendHealthParser.isReady(statusCode, body)
        } finally {
            connection.disconnect()
        }
    }
}
