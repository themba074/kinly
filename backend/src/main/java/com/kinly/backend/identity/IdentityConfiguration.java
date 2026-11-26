package com.kinly.backend.identity;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class IdentityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/actuator/health/**", "/actuator/info")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/me").authenticated()
                        .anyRequest().denyAll())
                .httpBasic(basic -> basic.disable())
                .formLogin(login -> login.disable())
                .csrf(csrf -> csrf.disable())
                .oauth2ResourceServer(oauth -> oauth
                        .authenticationEntryPoint((request, response, exception) -> {
                            response.setStatus(401);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setHeader("Cache-Control", "no-store");
                            response.getWriter().write(
                                    "{\"code\":\"AUTHENTICATION_REQUIRED\","
                                            + "\"message\":\"Authentication required\"}");
                        })
                        .jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    JwtDecoder identityJwtDecoder(
            @Value("${kinly.identity.issuer:}") String issuer,
            @Value("${kinly.identity.jwks-uri:}") String jwksUri,
            @Value("${kinly.identity.client-ids:}") String clientIds) {
        if (issuer.isBlank() || jwksUri.isBlank() || clientIds.isBlank()) {
            return token -> {
                throw new BadJwtException("Identity provider is not configured");
            };
        }
        URI keyUri = URI.create(jwksUri);
        Set<String> allowedClients = Arrays.stream(clientIds.split(","))
                .map(String::trim).filter(value -> !value.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
        if (!"https".equals(keyUri.getScheme())
                || !"https".equals(URI.create(issuer).getScheme())
                || allowedClients.isEmpty()) {
            throw new IllegalStateException("Identity provider configuration is invalid");
        }
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer),
                clientAndUseValidator(allowedClients)));
        return token -> {
            try {
                return decoder.decode(token);
            } catch (BadJwtException exception) {
                throw exception;
            } catch (JwtException exception) {
                throw new BadJwtException("Token could not be verified");
            }
        };
    }

    static OAuth2TokenValidator<Jwt> clientAndUseValidator(Set<String> allowedClients) {
        return jwt -> {
            String clientId = jwt.getClaimAsString("client_id");
            String tokenUse = jwt.getClaimAsString("token_use");
            String scope = jwt.getClaimAsString("scope");
            boolean valid = allowedClients.contains(clientId)
                    && "access".equals(tokenUse)
                    && scope != null
                    && Arrays.asList(scope.split(" ")).containsAll(Set.of("openid", "email"));
            return valid ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(
                            new OAuth2Error("invalid_token", "Invalid client, use, or scope", null));
        };
    }
}
