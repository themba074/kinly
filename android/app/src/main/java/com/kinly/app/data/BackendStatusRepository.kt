package com.kinly.app.data

fun interface BackendStatusRepository {
    suspend fun isReady(): Boolean
}
