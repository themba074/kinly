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
    void declaresAnEmptyVersionedLocalProductApi() throws Exception {
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
        assertThat(document.path("paths").size()).isZero();
        assertThat(document.path("x-kinly-contract-state").asString())
                .isEqualTo("foundation-only");
        assertThat(document.path("x-kinly-versioning").path("basePath").asString())
                .isEqualTo("/api/v1");
        assertThat(document.has("security")).isFalse();
    }
}
