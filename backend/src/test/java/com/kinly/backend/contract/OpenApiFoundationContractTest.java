package com.kinly.backend.contract;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

class OpenApiFoundationContractTest {

    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Test
    void declaresOnlyTheAuthenticatedMinimalProfileRead() throws Exception {
        Path contract = Path.of(System.getProperty("user.dir"), "..", "contracts", "openapi.json")
                .normalize();

        assertThat(contract).isRegularFile();

        JsonNode document = objectMapper.readTree(Files.readString(contract));

        assertThat(document.path("openapi").asString()).isEqualTo("3.1.0");
        assertThat(document.path("info").path("title").asString()).isEqualTo("Kinly API");
        assertThat(document.path("info").path("version").asString()).isEqualTo("0.1.0");
        assertThat(document.path("servers").path(0).path("url").asString())
                .isEqualTo("http://localhost:8080/api/v1");
        assertThat(document.path("paths").isObject()).isTrue();
        assertThat(document.path("paths").size()).isEqualTo(1);
        JsonNode profileRead = document.path("paths").path("/me").path("get");
        assertThat(profileRead.path("operationId").asString()).isEqualTo("getMyProfile");
        assertThat(profileRead.path("security").path(0).has("bearerAccessToken")).isTrue();
        assertThat(profileRead.path("responses").has("200")).isTrue();
        assertThat(profileRead.path("responses").has("401")).isTrue();
        assertThat(profileRead.path("responses").has("403")).isTrue();
        assertThat(profileRead.path("responses").has("429")).isTrue();
        assertThat(profileRead.path("responses").has("503")).isTrue();
        JsonNode profile = document.path("components").path("schemas").path("MinimalProfile");
        assertThat(profile.path("additionalProperties").asBoolean()).isFalse();
        assertThat(profile.path("properties").size()).isEqualTo(3);
        assertThat(profile.path("properties").has("id")).isTrue();
        assertThat(profile.path("properties").has("verifiedEmail")).isTrue();
        assertThat(profile.path("properties").has("displayName")).isTrue();
        assertThat(document.path("x-kinly-contract-state").asString())
                .isEqualTo("local-synthetic-identity");
        assertThat(document.path("x-kinly-versioning").path("basePath").asString())
                .isEqualTo("/api/v1");
        assertThat(document.has("security")).isFalse();
    }
}
