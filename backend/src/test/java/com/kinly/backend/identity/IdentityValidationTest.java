package com.kinly.backend.identity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;

class IdentityValidationTest {
    private static final String ISSUER = "https://issuer.example.invalid";

    @Test
    void acceptsOnlyExpectedClientAccessTokenAndScopes() {
        var validator = IdentityConfiguration.clientAndUseValidator(Set.of("mobile-client"));
        assertThat(validator.validate(jwt("mobile-client", "access", "openid email"))
                .hasErrors()).isFalse();
        assertThat(validator.validate(jwt("other-client", "access", "openid email"))
                .hasErrors()).isTrue();
        assertThat(validator.validate(jwt("mobile-client", "id", "openid email"))
                .hasErrors()).isTrue();
        assertThat(validator.validate(jwt("mobile-client", "access", "openid"))
                .hasErrors()).isTrue();
    }

    @Test
    void rejectsExpiredAndWrongIssuerClaims() {
        var validator = JwtValidators.createDefaultWithIssuer(ISSUER);
        assertThat(validator.validate(jwt("mobile-client", "access", "openid email"))
                .hasErrors()).isFalse();
        Jwt expired = Jwt.withTokenValue("synthetic-expired").header("alg", "RS256")
                .issuer(ISSUER).subject("subject-one")
                .issuedAt(Instant.now().minusSeconds(600))
                .expiresAt(Instant.now().minusSeconds(300)).build();
        assertThat(validator.validate(expired).hasErrors()).isTrue();
        Jwt wrongIssuer = Jwt.withTokenValue("synthetic-wrong-issuer").header("alg", "RS256")
                .issuer("https://other.example.invalid").subject("subject-one")
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(300)).build();
        assertThat(validator.validate(wrongIssuer).hasErrors()).isTrue();
    }

    private static Jwt jwt(String clientId, String tokenUse, String scopes) {
        return Jwt.withTokenValue("synthetic-claims").header("alg", "RS256")
                .issuer(ISSUER).subject("subject-one")
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(300))
                .claim("client_id", clientId).claim("token_use", tokenUse)
                .claim("scope", scopes).build();
    }
}
