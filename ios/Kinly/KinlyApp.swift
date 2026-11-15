import SwiftUI

@main
@MainActor
struct KinlyApp: App {
    @StateObject private var statusModel: BackendStatusModel

    init() {
        let client = URLSessionBackendStatusClient(
            endpoint: AppConfiguration.backendReadinessURL()
        )
        _statusModel = StateObject(
            wrappedValue: BackendStatusModel(client: client)
        )
    }

    var body: some Scene {
        WindowGroup {
            KinlyRootView(statusModel: statusModel)
        }
    }
}
