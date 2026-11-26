package com.kinly.backend.identity;

import org.springframework.security.oauth2.jwt.Jwt;

interface IdentityProvider {
    VerifiedIdentity verify(Jwt jwt);
}
