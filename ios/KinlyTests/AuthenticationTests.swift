import CryptoKit
import XCTest
@testable import Kinly

final class AuthenticationTests: XCTestCase {
    func testConfigurationRequiresProviderAndClientValues() {
        XCTAssertFalse(makeConfiguration().isReady)

        let configured = AuthConfiguration(
            issuer: "https://issuer.example",
            authorizeURL: URL(string: "https://issuer.example/authorize")!,
            tokenURL: URL(string: "https://issuer.example/token")!,
            clientID: "kinly-ios",
            profileURL: URL(string: "https://api.example/api/v1/me")!
        )
        XCTAssertTrue(configured.isReady)
    }

    func testAuthorizationRequestUsesStateNonceAndS256PKCE() throws {
        let configuration = configured()
        let request = try XCTUnwrap(AuthProtocol.request(configuration: configuration))
        let query = try XCTUnwrap(URLComponents(url: request.url, resolvingAgainstBaseURL: false)?.queryItems)
        let values = Dictionary(uniqueKeysWithValues: query.compactMap { item in
            item.value.map { (item.name, $0) }
        })

        XCTAssertEqual(values["response_type"], "code")
        XCTAssertEqual(values["client_id"], configuration.clientID)
        XCTAssertEqual(values["redirect_uri"], configuration.redirectURL.absoluteString)
        XCTAssertEqual(values["state"], request.state)
        XCTAssertEqual(values["nonce"], request.nonce)
        XCTAssertEqual(values["code_challenge_method"], "S256")
        let expectedChallenge = Data(SHA256.hash(data: Data(request.codeVerifier.utf8)))
            .base64EncodedString()
            .replacingOccurrences(of: "+", with: "-")
            .replacingOccurrences(of: "/", with: "_")
            .replacingOccurrences(of: "=", with: "")
        XCTAssertEqual(values["code_challenge"], expectedChallenge)
    }

    func testCallbackRequiresExactRedirectAndMatchingState() throws {
        let configuration = configured()
        let request = try XCTUnwrap(AuthProtocol.request(configuration: configuration))
        let valid = URL(string: "\(configuration.redirectURL.absoluteString)?code=abc&state=\(request.state)")!
        XCTAssertTrue(AuthProtocol.validCallback(valid, request: request, configuration: configuration))

        let wrongState = URL(string: "\(configuration.redirectURL.absoluteString)?code=abc&state=wrong")!
        let wrongHost = URL(string: "com.kinly.app://other/oauth2redirect?code=abc&state=\(request.state)")!
        XCTAssertFalse(AuthProtocol.validCallback(wrongState, request: request, configuration: configuration))
        XCTAssertFalse(AuthProtocol.validCallback(wrongHost, request: request, configuration: configuration))
    }

    @MainActor
    func testKeychainSessionStoreRoundTrips() {
        let store = KeychainSessionStore(
            service: "com.kinly.app.tests.auth.\(UUID().uuidString)",
            account: "access-token"
        )
        let session = StoredSession(accessToken: "synthetic-token", expiresAt: Date().addingTimeInterval(3600))
        store.clear()
        store.save(session)
        guard let loaded = store.load() else {
            XCTFail("The Keychain session could not be loaded")
            return
        }
        XCTAssertEqual(loaded.accessToken, session.accessToken)
        XCTAssertEqual(loaded.expiresAt.timeIntervalSince(session.expiresAt), 0, accuracy: 1)
    }

    @MainActor
    func testKeychainSessionStoreClears() {
        let store = KeychainSessionStore(
            service: "com.kinly.app.tests.auth.\(UUID().uuidString)",
            account: "access-token"
        )
        let session = StoredSession(accessToken: "synthetic-token", expiresAt: Date().addingTimeInterval(3600))
        store.save(session)
        store.clear()
        XCTAssertNil(store.load())
    }

    private func configured() -> AuthConfiguration {
        AuthConfiguration(
            issuer: "https://issuer.example",
            authorizeURL: URL(string: "https://issuer.example/authorize")!,
            tokenURL: URL(string: "https://issuer.example/token")!,
            clientID: "kinly-ios",
            profileURL: URL(string: "https://api.example/api/v1/me")!
        )
    }

    private func makeConfiguration() -> AuthConfiguration {
        AuthConfiguration(
            issuer: "",
            authorizeURL: URL(string: "https://kinly.invalid")!,
            tokenURL: URL(string: "https://kinly.invalid")!,
            clientID: "",
            profileURL: URL(string: "https://kinly.invalid")!
        )
    }
}
