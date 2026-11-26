package com.kinly.backend.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class CognitoUserInfoClientTest {
    private static final URI ENDPOINT = URI.create(
            "https://provider.example.invalid/oauth2/userInfo");

    @Test
    void acceptsMatchingVerifiedSubjectAndEmail() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(ENDPOINT))
                .andRespond(withSuccess(
                        """
                        {"sub":"subject-one","email":"adult-one@example.invalid",
                         "email_verified":"true"}
                        """, MediaType.APPLICATION_JSON));
        VerifiedIdentity result = new CognitoUserInfoClient(ENDPOINT, builder.build())
                .verify(jwt());
        assertThat(result.subject()).isEqualTo("subject-one");
        assertThat(result.email()).isEqualTo("adult-one@example.invalid");
        server.verify();
    }

    @Test
    void deniesMismatchedSubjectAndProviderFailure() {
        RestClient.Builder mismatchBuilder = RestClient.builder();
        MockRestServiceServer mismatchServer =
                MockRestServiceServer.bindTo(mismatchBuilder).build();
        mismatchServer.expect(requestTo(ENDPOINT))
                .andRespond(withSuccess(
                        """
                        {"sub":"subject-two","email":"adult-one@example.invalid",
                         "email_verified":"true"}
                        """, MediaType.APPLICATION_JSON));
        assertThatThrownBy(() -> new CognitoUserInfoClient(ENDPOINT, mismatchBuilder.build())
                .verify(jwt()))
                .isInstanceOf(IdentityFailure.class)
                .hasMessage("IDENTITY_UNAVAILABLE");
        mismatchServer.verify();

        RestClient.Builder outageBuilder = RestClient.builder();
        MockRestServiceServer outageServer = MockRestServiceServer.bindTo(outageBuilder).build();
        outageServer.expect(requestTo(ENDPOINT)).andRespond(withServerError());
        assertThatThrownBy(() -> new CognitoUserInfoClient(ENDPOINT, outageBuilder.build())
                .verify(jwt()))
                .isInstanceOf(IdentityFailure.class)
                .hasMessage("IDENTITY_UNAVAILABLE");
        outageServer.verify();
    }

    @Test
    void deniesUnverifiedEmail() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(ENDPOINT))
                .andRespond(withSuccess(
                        """
                        {"sub":"subject-one","email":"adult-one@example.invalid",
                         "email_verified":"false"}
                        """, MediaType.APPLICATION_JSON));
        assertThatThrownBy(() -> new CognitoUserInfoClient(ENDPOINT, builder.build())
                .verify(jwt()))
                .isInstanceOf(IdentityFailure.class)
                .hasMessage("ACCOUNT_UNVERIFIED");
        server.verify();
    }

    @Test
    void treatsProviderRevocationResponseAsDisabledSession() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(ENDPOINT)).andRespond(withUnauthorizedRequest());
        assertThatThrownBy(() -> new CognitoUserInfoClient(ENDPOINT, builder.build())
                .verify(jwt()))
                .isInstanceOf(IdentityFailure.class)
                .hasMessage("ACCOUNT_DISABLED");
        server.verify();
    }

    private static Jwt jwt() {
        return Jwt.withTokenValue("synthetic-userinfo").header("alg", "RS256")
                .issuer("https://issuer.example.invalid").subject("subject-one").build();
    }
}
