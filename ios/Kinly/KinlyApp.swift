import SwiftUI

@main
@MainActor
struct KinlyApp: App {
    @StateObject private var statusModel: BackendStatusModel
    @StateObject private var authModel: AuthModel

    init() {
        let client = URLSessionBackendStatusClient(
            endpoint: AppConfiguration.backendReadinessURL()
        )
        _statusModel = StateObject(
            wrappedValue: BackendStatusModel(client: client)
        )
        _authModel = StateObject(
            wrappedValue: AuthModel(
                repository: IOSAuthRepository(configuration: AuthConfiguration.from())
            )
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
            KinlyRootView(statusModel: statusModel, authModel: authModel)
        }
#else
        KinlyRootView(statusModel: statusModel, authModel: authModel)
#endif
    }
}
