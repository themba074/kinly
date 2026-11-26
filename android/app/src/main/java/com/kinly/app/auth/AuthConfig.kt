package com.kinly.app.auth

import com.kinly.app.BuildConfig
import java.net.URI

data class AuthConfig(
    val issuer: String,
    val authorizeUrl: String,
    val tokenUrl: String,
    val clientId: String,
    val profileUrl: String,
) {
    val redirectUri: String = "com.kinly.app://auth/oauth2redirect"

    val isReady: Boolean
        get() = listOf(issuer, authorizeUrl, tokenUrl).all(::isHttps)
            && clientId.isNotBlank()
            && clientId.none { it.isWhitespace() }
            && (isHttps(profileUrl) ||
                (BuildConfig.DEBUG && runCatching {
                    URI.create(profileUrl).let {
                        it.scheme == "http" && it.host == "10.0.2.2"
                    }
                }.getOrDefault(false)))

    private fun isHttps(value: String): Boolean =
        runCatching {
            val uri = URI.create(value)
            uri.scheme == "https" && !uri.host.isNullOrBlank() &&
                uri.userInfo == null && uri.fragment == null
        }.getOrDefault(false)

    companion object {
        fun fromBuild(): AuthConfig = AuthConfig(
            BuildConfig.AUTH_ISSUER,
            BuildConfig.AUTH_AUTHORIZE_URL,
            BuildConfig.AUTH_TOKEN_URL,
            BuildConfig.AUTH_CLIENT_ID,
            BuildConfig.AUTH_ME_URL,
        )
    }
}
