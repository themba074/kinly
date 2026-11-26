package com.kinly.app.auth

import android.content.Intent

data class MinimalProfile(val id: String, val verifiedEmail: String, val displayName: String?)

sealed interface AuthResult {
    data object SignedOut : AuthResult
    data class SignedIn(val profile: MinimalProfile) : AuthResult
    data object Cancelled : AuthResult
    data object Expired : AuthResult
    data object Offline : AuthResult
    data object Unavailable : AuthResult
    data object Failed : AuthResult
}

interface AuthRepository {
    val configured: Boolean
    fun authorizationIntent(): Intent?
    suspend fun restore(): AuthResult
    suspend fun complete(result: Intent?): AuthResult
    fun clear()
}
