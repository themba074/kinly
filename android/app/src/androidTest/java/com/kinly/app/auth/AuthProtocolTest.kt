package com.kinly.app.auth

import android.net.Uri
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.ResponseTypeValues
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthProtocolTest {
    private val config = AuthConfig(
        issuer = "https://issuer.example.invalid/pool",
        authorizeUrl = "https://login.example.invalid/oauth2/authorize",
        tokenUrl = "https://login.example.invalid/oauth2/token",
        clientId = "public-mobile-client",
        profileUrl = "https://api.example.invalid/api/v1/me",
    )

    @Test
    fun requestUsesCodePkceStateNonceAndExactRedirect() {
        val request = AuthProtocol.request(config)
        assertEquals("code", request.responseType)
        assertEquals(config.redirectUri, request.redirectUri.toString())
        assertEquals("openid email", request.scope)
        assertNotNull(request.state)
        assertNotNull(request.nonce)
        assertNotNull(request.codeVerifier)
        assertEquals("S256", request.codeVerifierChallengeMethod)
    }

    @Test
    fun alteredStateOrRedirectIsRejectedBeforeExchange() {
        val request = AuthProtocol.request(config)
        val valid = AuthorizationResponse.Builder(request)
            .setState(request.state)
            .setAuthorizationCode("synthetic-code")
            .build()
        assertTrue(AuthProtocol.validResponse(valid, config, request))

        val wrongState = AuthorizationResponse.Builder(request)
            .setState("altered-state")
            .setAuthorizationCode("synthetic-code")
            .build()
        assertFalse(AuthProtocol.validResponse(wrongState, config, request))
        assertFalse(AuthProtocol.validResponse(valid, config.copy(clientId = "another-client"), request))
        assertFalse(AuthProtocol.validResponse(valid, config, AuthProtocol.request(config)))

        val alteredRedirectRequest = AuthorizationRequest.Builder(
            request.configuration,
            request.clientId,
            ResponseTypeValues.CODE,
            Uri.parse("com.kinly.app://auth/other"),
        ).setState(request.state).setNonce(request.nonce)
            .setCodeVerifier(request.codeVerifier).build()
        val wrongRedirect = AuthorizationResponse.Builder(alteredRedirectRequest)
            .setState(request.state)
            .setAuthorizationCode("synthetic-code")
            .build()
        assertFalse(AuthProtocol.validResponse(wrongRedirect, config, request))
    }
}
