package com.kinly.app.auth

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthConfigTest {
    @Test
    fun requiresCompleteHttpsProviderConfiguration() {
        val valid = AuthConfig(
            issuer = "https://issuer.example.invalid/pool",
            authorizeUrl = "https://login.example.invalid/oauth2/authorize",
            tokenUrl = "https://login.example.invalid/oauth2/token",
            clientId = "public-mobile-client",
            profileUrl = "https://api.example.invalid/api/v1/me",
        )
        assertTrue(valid.isReady)
        assertFalse(valid.copy(clientId = "").isReady)
        assertFalse(valid.copy(authorizeUrl = "http://login.example.invalid/authorize").isReady)
        assertFalse(valid.copy(tokenUrl = "https://user:pass@login.example.invalid/token").isReady)
        assertFalse(valid.copy(profileUrl = "").isReady)
    }
}
