import Combine
import Foundation

@MainActor
final class BackendStatusModel: ObservableObject {
    @Published private(set) var status: BackendStatus = .checking

    private let client: any BackendStatusProviding

    init(client: any BackendStatusProviding) {
        self.client = client
    }

    func refresh() async {
        status = .checking

        do {
            let isReady = try await client.isReady()
            guard !Task.isCancelled else {
                return
            }
            status = isReady ? .available : .unavailable
        } catch is CancellationError {
            return
        } catch {
            status = .unavailable
        }
    }
}
