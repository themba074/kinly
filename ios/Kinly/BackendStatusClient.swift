import Foundation

protocol BackendStatusProviding: Sendable {
    func isReady() async throws -> Bool
}

struct URLSessionBackendStatusClient: BackendStatusProviding {
    private let endpoint: URL
    private let session: URLSession

    init(endpoint: URL, session: URLSession = .shared) {
        self.endpoint = endpoint
        self.session = session
    }

    func isReady() async throws -> Bool {
        var request = URLRequest(
            url: endpoint,
            cachePolicy: .reloadIgnoringLocalCacheData,
            timeoutInterval: 3
        )
        request.httpMethod = "GET"

        let (data, response) = try await session.data(for: request)
        guard let httpResponse = response as? HTTPURLResponse else {
            return false
        }

        return BackendHealthParser.isReady(
            statusCode: httpResponse.statusCode,
            data: data
        )
    }
}
