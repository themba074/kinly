package com.kinly.app.data

internal object BackendHealthParser {
    private val upStatus = Regex("\"status\"\\s*:\\s*\"UP\"")

    fun isReady(statusCode: Int, body: String): Boolean =
        statusCode in 200..299 && upStatus.containsMatchIn(body)
}
