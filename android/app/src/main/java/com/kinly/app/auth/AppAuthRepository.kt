package com.kinly.app.auth

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenResponse
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume

class AppAuthRepository(
    context: Context,
    private val config: AuthConfig,
) : AuthRepository, AutoCloseable {
    private val service = if (config.isReady) AuthorizationService(context.applicationContext) else null
    private val sessions = KeystoreSessionStore(context.applicationContext)
    private var pendingRequest: AuthorizationRequest? = null

    override val configured: Boolean get() = config.isReady

    override fun authorizationIntent(): Intent? {
        val authService = service ?: return null
        val request = AuthProtocol.request(config)
        pendingRequest = request
        return authService.getAuthorizationRequestIntent(request)
    }

    override suspend fun complete(result: Intent?): AuthResult {
        val authService = service ?: return AuthResult.Unavailable
        if (result == null) {
            pendingRequest = null
            return AuthResult.Cancelled
        }
        val expected = pendingRequest ?: return AuthResult.Failed
        pendingRequest = null
        val response = runCatching { AuthorizationResponse.fromIntent(result) }.getOrNull()
        if (response == null) {
            return if (runCatching { AuthorizationException.fromIntent(result) }.getOrNull() == null) {
                AuthResult.Cancelled
            } else {
                AuthResult.Failed
            }
        }
        if (!AuthProtocol.validResponse(response, config, expected)) return AuthResult.Failed
        val exchanged = suspendCancellableCoroutine<TokenResponse?> { continuation ->
            authService.performTokenRequest(response.createTokenExchangeRequest()) { token, error ->
                if (continuation.isActive) continuation.resume(if (error == null) token else null)
            }
        } ?: return AuthResult.Unavailable
        val accessToken = exchanged.accessToken ?: return AuthResult.Failed
        val expiresAt = exchanged.accessTokenExpirationTime ?: return AuthResult.Failed
        if (expiresAt <= System.currentTimeMillis() + 30_000) return AuthResult.Expired
        val profile = profile(accessToken)
        if (profile is AuthResult.SignedIn) {
            return try {
                sessions.save(StoredSession(accessToken, expiresAt))
                profile
            } catch (_: Exception) {
                sessions.clear()
                AuthResult.Failed
            }
        }
        return profile
    }

    override suspend fun restore(): AuthResult {
        if (!configured) return AuthResult.Unavailable
        val session = sessions.load() ?: return AuthResult.SignedOut
        if (session.expiresAtMillis <= System.currentTimeMillis() + 30_000) {
            sessions.clear()
            return AuthResult.Expired
        }
        val profile = profile(session.accessToken)
        if (profile == AuthResult.Expired) sessions.clear()
        return profile
    }

    override fun clear() {
        pendingRequest = null
        sessions.clear()
    }

    override fun close() {
        service?.dispose()
    }

    private suspend fun profile(accessToken: String): AuthResult = withContext(Dispatchers.IO) {
        val connection = try {
            URL(config.profileUrl).openConnection() as HttpURLConnection
        } catch (_: IOException) {
            return@withContext AuthResult.Offline
        }
        try {
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer " + accessToken)
            connection.connectTimeout = 3_000
            connection.readTimeout = 3_000
            connection.useCaches = false
            when (connection.responseCode) {
                200 -> {
                    val body = connection.inputStream.bufferedReader().use { it.readText() }
                    if (body.length > 8_192) return@withContext AuthResult.Failed
                    val json = JSONObject(body)
                    val id = json.optString("id")
                    val email = json.optString("verifiedEmail")
                    if (id.isBlank() || email.isBlank()) return@withContext AuthResult.Failed
                    AuthResult.SignedIn(
                        MinimalProfile(
                            id = id,
                            verifiedEmail = email,
                            displayName = json.optString("displayName").ifBlank { null },
                        ),
                    )
                }
                401 -> AuthResult.Expired
                403, 429, 503 -> AuthResult.Unavailable
                else -> AuthResult.Failed
            }
        } catch (_: IOException) {
            AuthResult.Offline
        } catch (_: org.json.JSONException) {
            AuthResult.Failed
        } finally {
            connection.disconnect()
        }
    }
}
