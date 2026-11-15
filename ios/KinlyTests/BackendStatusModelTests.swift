import XCTest
@testable import Kinly

@MainActor
final class BackendStatusModelTests: XCTestCase {
    func testReadyResponseBecomesAvailable() async {
        let model = BackendStatusModel(
            client: StubBackendStatusClient(results: [.ready(true)])
        )

        await model.refresh()

        XCTAssertEqual(model.status, .available)
    }

    func testFalseResponseBecomesUnavailable() async {
        let model = BackendStatusModel(
            client: StubBackendStatusClient(results: [.ready(false)])
        )

        await model.refresh()

        XCTAssertEqual(model.status, .unavailable)
    }

    func testFailureBecomesUnavailableAndRetryCanRecover() async {
        let client = StubBackendStatusClient(
            results: [.failure, .ready(true)]
        )
        let model = BackendStatusModel(client: client)

        await model.refresh()
        XCTAssertEqual(model.status, .unavailable)

        await model.refresh()
        XCTAssertEqual(model.status, .available)
    }
}

private enum StubResult: Sendable {
    case ready(Bool)
    case failure
}

private enum StubError: Error {
    case unavailable
}

private actor StubBackendStatusClient: BackendStatusProviding {
    private var results: [StubResult]

    init(results: [StubResult]) {
        self.results = results
    }

    func isReady() async throws -> Bool {
        guard !results.isEmpty else {
            throw StubError.unavailable
        }

        switch results.removeFirst() {
        case let .ready(value):
            return value
        case .failure:
            throw StubError.unavailable
        }
    }
}
