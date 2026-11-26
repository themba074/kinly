package com.kinly.backend.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

class JwtSignatureValidationTest {
    private static final String ISSUER = "https://issuer.example.invalid";

    @Test
    void verifiedSignatureDoesNotOverrideIssuerClientUseOrExpiryChecks() throws Exception {
        KeyPair trusted = keyPair();
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withPublicKey((RSAPublicKey) trusted.getPublic()).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(ISSUER),
                IdentityConfiguration.clientAndUseValidator(Set.of("mobile-client"))));

        assertThat(decoder.decode(token(trusted, ISSUER, "mobile-client", "access", 300))
                .getSubject()).isEqualTo("subject-one");
        assertThatThrownBy(() -> decoder.decode(
                token(keyPair(), ISSUER, "mobile-client", "access", 300)))
                .isInstanceOf(BadJwtException.class);
        assertThatThrownBy(() -> decoder.decode(
                token(trusted, "https://wrong.example.invalid", "mobile-client", "access", 300)))
                .isInstanceOf(BadJwtException.class);
        assertThatThrownBy(() -> decoder.decode(
                token(trusted, ISSUER, "wrong-client", "access", 300)))
                .isInstanceOf(BadJwtException.class);
        assertThatThrownBy(() -> decoder.decode(
                token(trusted, ISSUER, "mobile-client", "id", 300)))
                .isInstanceOf(BadJwtException.class);
        assertThatThrownBy(() -> decoder.decode(
                token(trusted, ISSUER, "mobile-client", "access", -300)))
                .isInstanceOf(BadJwtException.class);
    }

    @Test
    void unknownSigningKeyFailsClosedWhenJwksCannotSupplyIt() throws Exception {
        KeyPair trusted = keyPair();
        RSAKey publicKey = new RSAKey.Builder((RSAPublicKey) trusted.getPublic())
                .keyID("trusted").build();
        String body = "{\"keys\":[" + publicKey.toJSONString() + "]}";
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/jwks", exchange -> {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            try (var output = exchange.getResponseBody()) {
                output.write(bytes);
            }
        });
        server.start();
        try {
            String uri = "http://127.0.0.1:" + server.getAddress().getPort() + "/jwks";
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(uri).build();
            assertThat(decoder.decode(token(trusted, ISSUER, "mobile-client", "access", 300))
                    .getSubject()).isEqualTo("subject-one");
            assertThatThrownBy(() -> decoder.decode(
                    token(keyPair(), ISSUER, "mobile-client", "access", 300)))
                    .isInstanceOf(BadJwtException.class);
        } finally {
            server.stop(0);
        }
    }

    private static KeyPair keyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private static String token(
            KeyPair signingKey, String issuer, String client, String use, long expiresInSeconds)
            throws Exception {
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(issuer).subject("subject-one")
                .issueTime(Date.from(now.minusSeconds(60)))
                .expirationTime(Date.from(now.plusSeconds(expiresInSeconds)))
                .claim("client_id", client).claim("token_use", use)
                .claim("scope", "openid email").build();
        SignedJWT signed = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
        signed.sign(new RSASSASigner(signingKey.getPrivate()));
        return signed.serialize();
    }
}
