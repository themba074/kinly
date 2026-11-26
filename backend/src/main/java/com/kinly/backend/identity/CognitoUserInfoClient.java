package com.kinly.backend.identity;

import java.net.URI;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.security.oauth2.jwt.Jwt;
import tools.jackson.databind.JsonNode;

@Component
final class CognitoUserInfoClient implements IdentityProvider {
    private final URI userInfoUri;
    private final RestClient client;

    @Autowired
    CognitoUserInfoClient(@Value("${kinly.identity.userinfo-uri:}") String configuredUri) {
        this.userInfoUri = configuredUri.isBlank() ? null : URI.create(configuredUri);
        if (userInfoUri != null && !"https".equals(userInfoUri.getScheme())) {
            throw new IllegalStateException("Identity userInfo URI must use HTTPS");
        }
        var requestFactory = new org.springframework.http.client.JdkClientHttpRequestFactory(
                java.net.http.HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(2)).build());
        requestFactory.setReadTimeout(Duration.ofSeconds(2));
        this.client = RestClient.builder().requestFactory(requestFactory).build();
    }

    CognitoUserInfoClient(URI userInfoUri, RestClient client) {
        this.userInfoUri = userInfoUri;
        this.client = client;
    }

    @Override
    public VerifiedIdentity verify(Jwt jwt) {
        if (userInfoUri == null) {
            throw new IdentityFailure("IDENTITY_UNAVAILABLE");
        }
        JsonNode details;
        try {
            details = client.get().uri(userInfoUri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue())
                    .retrieve().body(JsonNode.class);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 401) {
                throw new IdentityFailure("ACCOUNT_DISABLED");
            }
            throw new IdentityFailure("IDENTITY_UNAVAILABLE");
        } catch (RestClientException exception) {
            throw new IdentityFailure("IDENTITY_UNAVAILABLE");
        }
        if (details == null || !jwt.getSubject().equals(details.path("sub").asString())) {
            throw new IdentityFailure("IDENTITY_UNAVAILABLE");
        }
        if (!"true".equals(details.path("email_verified").asString())) {
            throw new IdentityFailure("ACCOUNT_UNVERIFIED");
        }
        String email = details.path("email").asString();
        if (email.isBlank() || email.length() > 320
                || !email.matches("[^@\\s]+@[^@\\s]+")) {
            throw new IdentityFailure("ACCOUNT_UNVERIFIED");
        }
        String name = details.path("name").asString();
        if (name.isBlank() || name.length() > 120
                || name.chars().anyMatch(Character::isISOControl)) {
            name = null;
        }
        return new VerifiedIdentity(jwt.getIssuer().toString(), jwt.getSubject(), email, name);
    }
}
