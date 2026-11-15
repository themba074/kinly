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
            appRoot
        }
    }

    @ViewBuilder
    private var appRoot: some View {
#if DEBUG
        if ProcessInfo.processInfo.arguments.contains("--kinly-ui-showcase") {
            DebugShowcaseView()
        } else {
            KinlyRootView(statusModel: statusModel)
        }
#else
        KinlyRootView(statusModel: statusModel)
#endif
    }
}
