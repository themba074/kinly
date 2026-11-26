package com.kinly.backend.foundation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.health.contributor.Status;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class FoundationIntegrationTest {

    private static final DockerImageName POSTGIS_IMAGE = DockerImageName
            .parse("postgis/postgis:16-3.4-alpine")
            .asCompatibleSubstituteFor("postgres");

    @Container
    static final PostgreSQLContainer postgres = new PostgreSQLContainer(POSTGIS_IMAGE)
            .withDatabaseName("kinly")
            .withUsername("kinly")
            .withPassword("kinly-test-only");

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7.4-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureServices(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    HealthEndpoint healthEndpoint;

    @Autowired
    MockMvc mvc;

    @Test
    void startsWithPostgisMigrationAndHealthyDependencies() {
        Integer postgisExtensionCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pg_extension WHERE extname = 'postgis'",
                Integer.class);
        Integer migrationCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true",
                Integer.class);

        assertThat(postgisExtensionCount).isEqualTo(1);
        assertThat(migrationCount).isEqualTo(2);
        Integer identityTableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables "
                        + "WHERE table_schema = 'public' AND table_name = 'identity_account'",
                Integer.class);
        assertThat(identityTableCount).isEqualTo(1);
        assertThat(healthEndpoint.health().getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void identityMigrationKeysOwnershipByIssuerAndSubjectRatherThanEmail() {
        UUID firstId = UUID.fromString("00000000-0000-4000-8000-000000000101");
        UUID secondId = UUID.fromString("00000000-0000-4000-8000-000000000102");
        String insert = "INSERT INTO identity_account "
                + "(id, issuer, provider_subject, verified_email, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, now(), now())";

        try {
            jdbcTemplate.update(insert, firstId, "https://issuer.example.invalid", "subject-one",
                    "adult-one@example.invalid");
            jdbcTemplate.update(insert, secondId, "https://issuer.example.invalid", "subject-two",
                    "adult-one@example.invalid");

            assertThatThrownBy(() -> jdbcTemplate.update(insert, UUID.randomUUID(),
                    "https://issuer.example.invalid", "subject-one",
                    "another-adult@example.invalid"))
                    .isInstanceOf(DataIntegrityViolationException.class);

            Integer rows = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM identity_account WHERE verified_email = ?",
                    Integer.class, "adult-one@example.invalid");
            assertThat(rows).isEqualTo(2);
        } finally {
            jdbcTemplate.update("DELETE FROM identity_account WHERE id IN (?, ?)", firstId, secondId);
        }
    }

    @Test
    void unconfiguredProviderRejectsProfileAccess() throws Exception {
        mvc.perform(get("/api/v1/me").header("Authorization", "Bearer synthetic-unconfigured"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));
    }
}

