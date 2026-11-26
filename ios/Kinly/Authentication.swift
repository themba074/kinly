import AuthenticationServices
import Combine
import CryptoKit
import Foundation
import Security
import UIKit

struct AuthConfiguration: Equatable, Sendable {
    let issuer: String
    let authorizeURL: URL
    let tokenURL: URL
    let clientID: String
    let profileURL: URL
    let redirectURL = URL(string: "com.kinly.app://auth/oauth2redirect")!

    var isReady: Bool {
        Self.isHTTPS(issuer)
            && Self.isHTTPS(authorizeURL)
            && Self.isHTTPS(tokenURL)
            && !clientID.isEmpty
            && !clientID.contains(where: \.isWhitespace)
            && (Self.isHTTPS(profileURL) || Self.isDebugLocal(profileURL))
    }

    static func from(bundle: Bundle = .main) -> AuthConfiguration {
        func value(_ key: String) -> String {
            bundle.object(forInfoDictionaryKey: key) as? String ?? ""
        }

        return AuthConfiguration(
            issuer: value("KinlyAuthIssuer"),
            authorizeURL: URL(string: value("KinlyAuthAuthorizeURL")) ?? URL(string: "https://kinly.invalid")!,
            tokenURL: URL(string: value("KinlyAuthTokenURL")) ?? URL(string: "https://kinly.invalid")!,
            clientID: value("KinlyAuthClientID"),
            profileURL: URL(string: value("KinlyAuthMeURL")) ?? URL(string: "https://kinly.invalid")!
        )
    }

    private static func isHTTPS(_ value: String) -> Bool {
        guard let url = URL(string: value) else { return false }
        return isHTTPS(url)
    }

    private static func isHTTPS(_ url: URL) -> Bool {
        url.scheme == "https" && url.host != nil && url.user == nil && url.fragment == nil
    }

    private static func isDebugLocal(_ url: URL) -> Bool {
#if DEBUG
        return url.scheme == "http" && url.host == "localhost"
#else
        return false
#endif
    }
}

struct MinimalProfile: Equatable, Sendable {
    let id: String
    let email: String
    let displayName: String
}

enum AuthState: Equatable, Sendable {
    case checking
    case notConfigured
    case signedOut
    case openingBrowser
    case signedIn(String)
    case cancelled
    case expired
    case offline
    case unavailable
    case failed

    var accessibilityValue: String {
        switch self {
        case .checking: "Checking sign-in status"
        case .notConfigured: "Sign-in is not configured for this build"
        case .signedOut: "Signed out"
        case .openingBrowser: "Opening sign-in in the system browser"
        case let .signedIn(name): "Signed in as \(name)"
        case .cancelled: "Sign-in cancelled"
        case .expired: "Session expired"
        case .offline: "Offline"
        case .unavailable: "Sign-in service unavailable"
        case .failed: "Sign-in failed"
        }
    }
}

struct StoredSession: Codable, Equatable, Sendable {
    let accessToken: String
    let expiresAt: Date
}

@MainActor
protocol SessionStoring {
    func load() -> StoredSession?
    func save(_ session: StoredSession)
    func clear()
}

@MainActor
final class KeychainSessionStore: SessionStoring {
    private let service = "com.kinly.app.auth.session"
    private let account = "access-token"

    func load() -> StoredSession? {
        var query = baseQuery
        query[kSecReturnData as String] = true
        query[kSecMatchLimit as String] = kSecMatchLimitOne
        var result: AnyObject?
        guard SecItemCopyMatching(query as CFDictionary, &result) == errSecSuccess,
              let data = result as? Data else { return nil }
        return try? JSONDecoder().decode(StoredSession.self, from: data)
    }

    func save(_ session: StoredSession) {
        guard let data = try? JSONEncoder().encode(session) else { return }
        SecItemDelete(baseQuery as CFDictionary)
        var query = baseQuery
        query[kSecValueData as String] = data
        query[kSecAttrAccessible as String] = kSecAttrAccessibleWhenUnlockedThisDeviceOnly
        SecItemAdd(query as CFDictionary, nil)
    }

    func clear() {
        SecItemDelete(baseQuery as CFDictionary)
    }

    private var baseQuery: [String: Any] {
        [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: account,
        ]
    }
}

struct AuthorizationRequest: Equatable, Sendable {
    let url: URL
    let state: String
    let nonce: String
    let codeVerifier: String
}

enum AuthProtocol {
    static func request(configuration: AuthConfiguration) -> AuthorizationRequest? {
        guard configuration.isReady else { return nil }
        let state = randomString()
        let nonce = randomString()
        let verifier = randomString(length: 64)
        let challenge = base64URL(SHA256.hash(data: Data(verifier.utf8)))
        var components = URLComponents(url: configuration.authorizeURL, resolvingAgainstBaseURL: false)
        components?.queryItems = [
            URLQueryItem(name: "response_type", value: "code"),
            URLQueryItem(name: "client_id", value: configuration.clientID),
            URLQueryItem(name: "redirect_uri", value: configuration.redirectURL.absoluteString),
            URLQueryItem(name: "scope", value: "openid email"),
            URLQueryItem(name: "state", value: state),
            URLQueryItem(name: "nonce", value: nonce),
            URLQueryItem(name: "code_challenge", value: challenge),
            URLQueryItem(name: "code_challenge_method", value: "S256"),
        ]
        guard let url = components?.url else { return nil }
        return AuthorizationRequest(url: url, state: state, nonce: nonce, codeVerifier: verifier)
    }

    static func validCallback(
        _ callback: URL,
        request: AuthorizationRequest,
        configuration: AuthConfiguration
    ) -> Bool {
        guard callback.scheme == configuration.redirectURL.scheme,
              callback.host == configuration.redirectURL.host,
              callback.path == configuration.redirectURL.path,
              callback.fragment == nil,
              let components = URLComponents(url: callback, resolvingAgainstBaseURL: false),
              let code = components.queryItems?.first(where: { $0.name == "code" })?.value,
              !code.isEmpty,
              let state = components.queryItems?.first(where: { $0.name == "state" })?.value else {
            return false
        }
        return state == request.state
    }

    private static func randomString(length: Int = 32) -> String {
        var bytes = [UInt8](repeating: 0, count: length)
        guard SecRandomCopyBytes(kSecRandomDefault, bytes.count, &bytes) == errSecSuccess else {
            return UUID().uuidString.replacingOccurrences(of: "-", with: "").prefix(length).description
        }
        return base64URL(Data(bytes)).prefix(length).description
    }

    private static func base64URL(_ digest: SHA256.Digest) -> String {
        Data(digest).base64EncodedString().replacingOccurrences(of: "+", with: "-")
            .replacingOccurrences(of: "/", with: "_").replacingOccurrences(of: "=", with: "")
    }

    private static func base64URL(_ data: Data) -> String {
        data.base64EncodedString().replacingOccurrences(of: "+", with: "-")
            .replacingOccurrences(of: "/", with: "_").replacingOccurrences(of: "=", with: "")
    }
}

@MainActor
final class IOSAuthRepository: NSObject, ASWebAuthenticationPresentationContextProviding {
    private struct TokenResponse: Decodable {
        let accessToken: String
        let expiresIn: TimeInterval?

        enum CodingKeys: String, CodingKey {
            case accessToken = "access_token"
            case expiresIn = "expires_in"
        }
    }

    private struct ProfileResponse: Decodable {
        let id: String
        let email: String
        let displayName: String?

        enum CodingKeys: String, CodingKey {
            case id
            case email = "verifiedEmail"
            case displayName = "displayName"
        }
    }

    let configuration: AuthConfiguration
    private let sessions: SessionStoring
    private var authenticationSession: ASWebAuthenticationSession?
    private var pendingRequest: AuthorizationRequest?
    private var completion: (@MainActor (AuthState) -> Void)?

    init(configuration: AuthConfiguration, sessions: SessionStoring = KeychainSessionStore()) {
        self.configuration = configuration
        self.sessions = sessions
    }

    func beginSignIn(completion: @escaping @MainActor (AuthState) -> Void) -> Bool {
        guard let request = AuthProtocol.request(configuration: configuration) else { return false }
        pendingRequest = request
        self.completion = completion
        let session = ASWebAuthenticationSession(
            url: request.url,
            callbackURLScheme: configuration.redirectURL.scheme,
            completionHandler: { [weak self] callback, error in
                Task { @MainActor in
                    await self?.finish(callback: callback, error: error)
                }
            }
        )
        session.presentationContextProvider = self
        session.prefersEphemeralWebBrowserSession = true
        authenticationSession = session
        return session.start()
    }

    func restore() async -> AuthState {
        guard configuration.isReady else { return .notConfigured }
        guard let session = sessions.load() else { return .signedOut }
        guard session.expiresAt > Date().addingTimeInterval(30) else {
            sessions.clear()
            return .expired
        }
        return await profile(using: session.accessToken)
    }

    func logout() {
        authenticationSession?.cancel()
        authenticationSession = nil
        pendingRequest = nil
        sessions.clear()
    }

    func presentationAnchor(for session: ASWebAuthenticationSession) -> ASPresentationAnchor {
        UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap(\.windows)
            .first(where: \.isKeyWindow) ?? UIWindow(frame: .zero)
    }

    private func finish(callback: URL?, error: Error?) async {
        defer {
            authenticationSession = nil
            pendingRequest = nil
            completion = nil
        }
        if let error = error as? ASWebAuthenticationSessionError,
           error.code == .canceledLogin {
            completion?(.cancelled)
            return
        }
        guard let callback, let request = pendingRequest,
              AuthProtocol.validCallback(callback, request: request, configuration: configuration) else {
            completion?(.failed)
            return
        }
        guard let code = URLComponents(url: callback, resolvingAgainstBaseURL: false)?.queryItems?
            .first(where: { $0.name == "code" })?.value else {
            completion?(.failed)
            return
        }
        let result = await exchange(code: code, request: request)
        completion?(result)
    }

    private func exchange(code: String, request: AuthorizationRequest) async -> AuthState {
        var tokenRequest = URLRequest(url: configuration.tokenURL)
        tokenRequest.httpMethod = "POST"
        tokenRequest.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        tokenRequest.httpBody = [
            "grant_type=authorization_code",
            "code=\(encode(code))",
            "client_id=\(encode(configuration.clientID))",
            "redirect_uri=\(encode(configuration.redirectURL.absoluteString))",
            "code_verifier=\(encode(request.codeVerifier))",
        ].joined(separator: "&").data(using: .utf8)
        do {
            let (data, response) = try await URLSession.shared.data(for: tokenRequest)
            guard let http = response as? HTTPURLResponse else { return .failed }
            guard (200..<300).contains(http.statusCode), let token = try? JSONDecoder().decode(TokenResponse.self, from: data),
                  !token.accessToken.isEmpty else {
                switch http.statusCode {
                case 401: return .expired
                case 429, 503: return .unavailable
                default: return .failed
                }
            }
            let session = StoredSession(
                accessToken: token.accessToken,
                expiresAt: Date().addingTimeInterval(token.expiresIn ?? 3600)
            )
            let result = await profile(using: session.accessToken)
            if case .signedIn = result { sessions.save(session) }
            return result
        } catch let error as URLError where error.code == .notConnectedToInternet || error.code == .timedOut {
            return .offline
        } catch {
            return .failed
        }
    }

    private func profile(using accessToken: String) async -> AuthState {
        var request = URLRequest(url: configuration.profileURL)
        request.setValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")
        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            guard let http = response as? HTTPURLResponse else { return .failed }
            switch http.statusCode {
            case 200:
                guard let profile = try? JSONDecoder().decode(ProfileResponse.self, from: data),
                      !profile.id.isEmpty, !profile.email.isEmpty else { return .failed }
                return .signedIn(profile.displayName?.isEmpty == false ? profile.displayName! : profile.email)
            case 401:
                sessions.clear()
                return .expired
            case 403, 429, 503:
                return .unavailable
            default:
                return .failed
            }
        } catch let error as URLError where error.code == .notConnectedToInternet || error.code == .timedOut {
            return .offline
        } catch {
            return .failed
        }
    }

    private func encode(_ value: String) -> String {
        value.addingPercentEncoding(withAllowedCharacters: .alphanumerics) ?? value
    }
}

@MainActor
final class AuthModel: ObservableObject {
    @Published private(set) var state: AuthState = .checking
    private let repository: IOSAuthRepository

    init(repository: IOSAuthRepository) {
        self.repository = repository
    }

    func restore() async {
        state = await repository.restore()
    }

    func signIn() {
        let started = repository.beginSignIn { [weak self] result in self?.state = result }
        guard started else {
            state = repository.configuration.isReady ? .failed : .notConfigured
            return
        }
        state = .openingBrowser
    }

    func logout() {
        repository.logout()
        state = .signedOut
    }
}
