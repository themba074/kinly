package com.kinly.backend.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class IdentityApiIntegrationTest {
    private static final String ISSUER = "https://issuer.example.invalid";
    private static final String EMAIL = "adult-one@example.invalid";
    private static final DockerImageName POSTGIS_IMAGE = DockerImageName
            .parse("postgis/postgis:16-3.4-alpine")
            .asCompatibleSubstituteFor("postgres");

    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer(POSTGIS_IMAGE)
            .withDatabaseName("kinly").withUsername("kinly").withPassword("kinly-test-only");

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7.4-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void services(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    MockMvc mvc;

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    IdentityService identity;

    @MockitoBean
    JwtDecoder decoder;

    @MockitoBean
    IdentityProvider provider;

    @Test
    void returnsOnlyCallerProfileAndKeepsAccountsSeparateEvenWhenEmailsMatch() throws Exception {
        when(decoder.decode("synthetic-one")).thenReturn(jwt("synthetic-one", "subject-one"));
        when(decoder.decode("synthetic-two")).thenReturn(jwt("synthetic-two", "subject-two"));
        when(provider.verify(any(Jwt.class))).thenAnswer(invocation -> {
            Jwt token = invocation.getArgument(0);
            return new VerifiedIdentity(ISSUER, token.getSubject(), EMAIL, "Adult One");
        });

        JsonMapper mapper = new JsonMapper();
        String first = mvc.perform(get("/api/v1/me")
                        .header("Authorization", "Bearer synthetic-one"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.verifiedEmail").value(EMAIL))
                .andExpect(jsonPath("$.displayName").value("Adult One"))
                .andExpect(jsonPath("$.providerSubject").doesNotExist())
                .andExpect(jsonPath("$.circles").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        String repeat = mvc.perform(get("/api/v1/me")
                        .header("Authorization", "Bearer synthetic-one"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String second = mvc.perform(get("/api/v1/me")
                        .header("Authorization", "Bearer synthetic-two")
                        .param("userId", mapper.readTree(first).path("id").asString()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        JsonNode firstProfile = mapper.readTree(first);
        assertThat(mapper.readTree(repeat).path("id").asString())
                .isEqualTo(firstProfile.path("id").asString());
        assertThat(mapper.readTree(second).path("id").asString())
                .isNotEqualTo(firstProfile.path("id").asString());
        Integer accountCount = jdbc.queryForObject(
                "SELECT count(*) FROM identity_account WHERE issuer = ? AND verified_email = ?",
                Integer.class, ISSUER, EMAIL);
        assertThat(accountCount).isEqualTo(2);
        assertThat(UUID.fromString(firstProfile.path("id").asString())).isNotNull();
    }

    @Test
    void rejectsMissingForgedAndUnverifiedIdentityWithoutCreatingAccounts() throws Exception {
        mvc.perform(get("/api/v1/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));
        when(decoder.decode("synthetic-forged")).thenThrow(new BadJwtException("Invalid signature"));
        mvc.perform(get("/api/v1/me").header("Authorization", "Bearer synthetic-forged"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));
        when(decoder.decode("synthetic-unverified"))
                .thenReturn(jwt("synthetic-unverified", "subject-unverified"));
        when(provider.verify(any(Jwt.class))).thenThrow(new IdentityFailure("ACCOUNT_UNVERIFIED"));
        mvc.perform(get("/api/v1/me").header("Authorization", "Bearer synthetic-unverified"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCOUNT_UNVERIFIED"));
        Integer count = jdbc.queryForObject(
                "SELECT count(*) FROM identity_account WHERE provider_subject = ?",
                Integer.class, "subject-unverified");
        assertThat(count).isZero();
    }

    @Test
    void unknownSigningKeyProviderOutageDeniesAccessWithoutAccountCreation() throws Exception {
        when(decoder.decode("synthetic-unknown-key"))
                .thenThrow(new BadJwtException("Signing keys unavailable"));
        mvc.perform(get("/api/v1/me")
                        .header("Authorization", "Bearer synthetic-unknown-key"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));
        Integer count = jdbc.queryForObject(
                "SELECT count(*) FROM identity_account WHERE provider_subject = ?",
                Integer.class, "subject-unknown-key");
        assertThat(count).isZero();
    }

    @Test
    void disabledAccountAndProviderOutageFailClosed() throws Exception {
        when(decoder.decode("synthetic-disabled"))
                .thenReturn(jwt("synthetic-disabled", "subject-disabled"));
        when(provider.verify(any(Jwt.class)))
                .thenReturn(new VerifiedIdentity(ISSUER, "subject-disabled", EMAIL, null));
        String response = mvc.perform(get("/api/v1/me")
                        .header("Authorization", "Bearer synthetic-disabled"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String accountId = new JsonMapper().readTree(response).path("id").asString();
        jdbc.update("UPDATE identity_account SET state = 'DISABLED' WHERE id = ?",
                UUID.fromString(accountId));
        mvc.perform(get("/api/v1/me").header("Authorization", "Bearer synthetic-disabled"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCOUNT_DISABLED"));
        when(decoder.decode("synthetic-outage"))
                .thenReturn(jwt("synthetic-outage", "subject-outage"));
        when(provider.verify(any(Jwt.class))).thenThrow(new IdentityFailure("IDENTITY_UNAVAILABLE"));
        mvc.perform(get("/api/v1/me").header("Authorization", "Bearer synthetic-outage"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("IDENTITY_UNAVAILABLE"));
    }

    @Test
    void rateLimitsRepeatedRequestsWithoutUsingAClientSuppliedIdentity() throws Exception {
        for (int index = 0; index < 60; index++) {
            mvc.perform(get("/api/v1/me").with(request -> {
                request.setRemoteAddr("192.0.2.81");
                return request;
            })).andExpect(status().isUnauthorized());
        }
        mvc.perform(get("/api/v1/me").with(request -> {
            request.setRemoteAddr("192.0.2.81");
            return request;
        })).andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "60"))
                .andExpect(jsonPath("$.code").value("RATE_LIMITED"));
    }

    @Test
    void concurrentFirstSignInCreatesOneAccount() throws Exception {
        when(provider.verify(any(Jwt.class)))
                .thenReturn(new VerifiedIdentity(ISSUER, "subject-concurrent",
                        "adult-concurrent@example.invalid", null));
        Jwt token = jwt("synthetic-concurrent", "subject-concurrent");
        try (var workers = Executors.newFixedThreadPool(6)) {
            List<Callable<String>> calls = java.util.stream.IntStream.range(0, 6)
                    .<Callable<String>>mapToObj(index -> () -> identity.me(token).id())
                    .toList();
            List<Future<String>> outcomes = workers.invokeAll(calls);
            String expected = outcomes.getFirst().get();
            for (Future<String> outcome : outcomes) {
                assertThat(outcome.get()).isEqualTo(expected);
            }
        }
        Integer rows = jdbc.queryForObject(
                "SELECT count(*) FROM identity_account WHERE issuer = ? AND provider_subject = ?",
                Integer.class, ISSUER, "subject-concurrent");
        assertThat(rows).isEqualTo(1);
    }

    private static Jwt jwt(String value, String subject) {
        return Jwt.withTokenValue(value).header("alg", "RS256").issuer(ISSUER)
                .subject(subject).claim("token_use", "access")
                .claim("client_id", "synthetic-client").build();
    }
}
