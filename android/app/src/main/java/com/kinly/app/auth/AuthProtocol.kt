package com.kinly.app.auth

import android.net.Uri
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

internal object AuthProtocol {
    fun request(config: AuthConfig): AuthorizationRequest {
        require(config.isReady)
        val endpoints = AuthorizationServiceConfiguration(
            Uri.parse(config.authorizeUrl),
            Uri.parse(config.tokenUrl),
        )
        return AuthorizationRequest.Builder(
            endpoints,
            config.clientId,
            ResponseTypeValues.CODE,
            Uri.parse(config.redirectUri),
        ).setScope("openid email").build()
    }

    fun validResponse(
        response: AuthorizationResponse,
        config: AuthConfig,
        expected: AuthorizationRequest,
    ): Boolean {
        val request = response.request
        return response.authorizationCode != null &&
            !request.state.isNullOrBlank() &&
            response.state == request.state &&
            request.state == expected.state &&
            !request.nonce.isNullOrBlank() &&
            request.nonce == expected.nonce &&
            !request.codeVerifier.isNullOrBlank() &&
            request.codeVerifier == expected.codeVerifier &&
            request.codeVerifierChallengeMethod == "S256" &&
            request.clientId == config.clientId &&
            request.redirectUri.toString() == config.redirectUri &&
            request.configuration.authorizationEndpoint.toString() == config.authorizeUrl &&
            request.configuration.tokenEndpoint.toString() == config.tokenUrl
    }
}
