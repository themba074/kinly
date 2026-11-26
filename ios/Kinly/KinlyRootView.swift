import SwiftUI

enum PrimaryDestination: String, CaseIterable, Identifiable {
    case home = "Home"
    case safety = "Safety"
    case history = "History"
    case settings = "Settings"

    var id: Self { self }

    var symbol: String {
        switch self {
        case .home: "house.fill"
        case .safety: "shield.checkered"
        case .history: "clock.arrow.circlepath"
        case .settings: "gearshape.fill"
        }
    }
}

struct KinlyRootView: View {
    @ObservedObject var statusModel: BackendStatusModel
    @ObservedObject var authModel: AuthModel
    @State private var selection = PrimaryDestination.home

    var body: some View {
        TabView(selection: $selection) {
            NavigationStack {
                HomeView(statusModel: statusModel, authModel: authModel)
                    .kinlyNavigationTitle()
            }
            .tabItem {
                Label(PrimaryDestination.home.rawValue, systemImage: PrimaryDestination.home.symbol)
            }
            .tag(PrimaryDestination.home)

            ForEach(PrimaryDestination.allCases.filter { $0 != .home }) { destination in
                NavigationStack {
                    UnavailableDestinationView(
                        title: destination.rawValue,
                        symbol: destination.symbol
                    )
                    .kinlyNavigationTitle()
                }
                .tabItem {
                    Label(destination.rawValue, systemImage: destination.symbol)
                }
                .tag(destination)
            }
        }
        .tint(.kinlyOcean700)
    }
}

private struct HomeView: View {
    @ObservedObject var statusModel: BackendStatusModel
    @ObservedObject var authModel: AuthModel

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 24) {
                VStack(alignment: .leading, spacing: 8) {
                    Text("Care starts with connection")
                        .font(.title.bold())
                        .foregroundStyle(Color.kinlyOcean900)
                        .accessibilityAddTraits(.isHeader)
                        .accessibilityIdentifier("home_heading")
                    Text(
                        "This early build checks only whether Kinly's development service is ready."
                    )
                    .font(.body)
                    .foregroundStyle(Color.kinlyInk600)
                }

                NavigationLink {
                    UnavailableDestinationView(
                        title: "Places",
                        symbol: "mappin.and.ellipse"
                    )
                    .navigationTitle("Places")
                    .navigationBarTitleDisplayMode(.inline)
                } label: {
                    Label("Open Places", systemImage: "mappin.and.ellipse")
                        .frame(maxWidth: .infinity, minHeight: 44)
                }
                .buttonStyle(.bordered)
                .tint(.kinlyOcean700)
                .accessibilityHint("Opens the unavailable Places screen")
                .accessibilityIdentifier("home_places_button")

                AuthStatusCard(authModel: authModel)

                BackendStatusCard(statusModel: statusModel)

                Divider()
                    .overlay(Color.kinlyBorder)

                Text(
                    "Signing in does not join a circle or start location sharing. Member, location, notification, and safety features are not available in this build."
                )
                .font(.subheadline)
                .foregroundStyle(Color.kinlyInk600)
                .accessibilityIdentifier("home_privacy_note")
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(24)
        }
        .background(Color.kinlySky50)
        .task {
            await statusModel.refresh()
            await authModel.restore()
        }
    }
}

struct AuthStatusCard: View {
    @ObservedObject var authModel: AuthModel

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Your Kinly account")
                .font(.title2.weight(.semibold))
                .foregroundStyle(Color.kinlyOcean900)

            statusContent
        }
        .padding(20)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(background)
        .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
        .overlay {
            RoundedRectangle(cornerRadius: 20, style: .continuous)
                .stroke(Color.kinlyBorder, lineWidth: 1)
        }
        .accessibilityElement(children: .contain)
        .accessibilityLabel("Kinly sign-in status")
        .accessibilityValue(authModel.state.accessibilityValue)
        .accessibilityIdentifier("auth_status_card")
    }

    @ViewBuilder
    private var statusContent: some View {
        switch authModel.state {
        case .checking:
            HStack(spacing: 12) {
                ProgressView()
                    .tint(.kinlyOcean700)
                    .accessibilityLabel("Checking sign-in")
                authText(title: "Checking sign-in", detail: "This usually takes only a moment.")
            }
        case .notConfigured:
            authText(title: "Sign-in unavailable", detail: "This local build has no identity provider configured.")
        case .signedOut:
            authText(title: "Signed out", detail: "Sign in to confirm your minimal Kinly profile.")
            Button("Sign in") { authModel.signIn() }
                .buttonStyle(.borderedProminent)
                .tint(.kinlyOcean700)
                .frame(minHeight: 44)
                .accessibilityIdentifier("sign_in_button")
        case .openingBrowser:
            authText(title: "Opening sign-in", detail: "Complete sign-in in the system browser, then return to Kinly.")
        case let .signedIn(name):
            authText(title: "Signed in", detail: "Profile confirmed for \(name).")
            Button("Sign out") { authModel.logout() }
                .buttonStyle(.bordered)
                .tint(.kinlyOcean700)
                .frame(minHeight: 44)
                .accessibilityIdentifier("sign_out_button")
        case .cancelled:
            authText(title: "Sign-in cancelled", detail: "No session was created.")
            retryButton
        case .expired:
            authText(title: "Session expired", detail: "Sign in again to confirm access.")
            retryButton
        case .offline:
            authText(title: "You appear to be offline", detail: "Connect to the internet and try again.")
            retryButton
        case .unavailable:
            authText(title: "Sign-in unavailable", detail: "Kinly could not confirm the identity service.")
            retryButton
        case .failed:
            authText(title: "Sign-in failed", detail: "Kinly could not complete sign-in. Try again.")
            retryButton
        }
    }

    private var retryButton: some View {
        Button("Try again") { authModel.signIn() }
            .buttonStyle(.borderedProminent)
            .tint(.kinlyOcean700)
            .frame(minHeight: 44)
            .accessibilityIdentifier("sign_in_retry_button")
    }

    private func authText(title: String, detail: String) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.headline)
                .foregroundStyle(Color.kinlyOcean900)
            Text(detail)
                .font(.subheadline)
                .foregroundStyle(Color.kinlyInk600)
        }
    }

    private var background: Color {
        switch authModel.state {
        case .signedIn: .kinlyFresh100
        case .unavailable, .failed, .expired: .kinlyDanger100
        default: .kinlySky100
        }
    }
}

struct BackendStatusCard: View {
    @ObservedObject var statusModel: BackendStatusModel

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Development service")
                .font(.title2.weight(.semibold))
                .foregroundStyle(Color.kinlyOcean900)

            statusContent

            if statusModel.status == .unavailable {
                Button {
                    Task { await statusModel.refresh() }
                } label: {
                    Label("Retry", systemImage: "arrow.clockwise")
                        .frame(maxWidth: .infinity, minHeight: 44)
                }
                .buttonStyle(.borderedProminent)
                .tint(.kinlyOcean700)
                .accessibilityHint("Checks the development service again")
            }
        }
        .padding(20)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(statusBackground)
        .clipShape(RoundedRectangle(cornerRadius: 20, style: .continuous))
        .overlay {
            RoundedRectangle(cornerRadius: 20, style: .continuous)
                .stroke(Color.kinlyBorder, lineWidth: 1)
        }
        .accessibilityElement(children: .contain)
        .accessibilityLabel("Kinly service status")
        .accessibilityValue(statusModel.status.accessibilityValue)
        .accessibilityIdentifier("backend_status_card")
    }

    @ViewBuilder
    private var statusContent: some View {
        switch statusModel.status {
        case .checking:
            HStack(spacing: 12) {
                ProgressView()
                    .tint(.kinlyOcean700)
                    .accessibilityLabel("Checking service")
                statusText(
                    title: "Checking availability",
                    detail: "This usually takes only a moment."
                )
            }
        case .available:
            HStack(spacing: 12) {
                Image(systemName: "checkmark.circle.fill")
                    .font(.title)
                    .foregroundStyle(Color.kinlyFresh600)
                    .accessibilityHidden(true)
                statusText(
                    title: "Available",
                    detail: "The development backend reports that it is ready."
                )
            }
        case .unavailable:
            HStack(spacing: 12) {
                Image(systemName: "icloud.slash.fill")
                    .font(.title)
                    .foregroundStyle(Color.kinlyDanger600)
                    .accessibilityHidden(true)
                statusText(
                    title: "Unavailable",
                    detail: "Kinly could not confirm that the development backend is ready."
                )
            }
        }
    }

    private func statusText(title: String, detail: String) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.headline)
                .foregroundStyle(Color.kinlyOcean900)
            Text(detail)
                .font(.subheadline)
                .foregroundStyle(Color.kinlyInk600)
        }
    }

    private var statusBackground: Color {
        switch statusModel.status {
        case .checking: .kinlySky100
        case .available: .kinlyFresh100
        case .unavailable: .kinlyDanger100
        }
    }
}

struct UnavailableDestinationView: View {
    let title: String
    let symbol: String

    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: symbol)
                .font(.system(size: 44))
                .foregroundStyle(Color.kinlyOcean700)
                .accessibilityHidden(true)
            Text(title)
                .font(.title2.bold())
                .foregroundStyle(Color.kinlyOcean900)
            Text(
                "Not available in this build. This area will be added through a separately approved feature."
            )
            .multilineTextAlignment(.center)
            .foregroundStyle(Color.kinlyInk600)
        }
        .padding(32)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.kinlySky50)
        .accessibilityElement(children: .combine)
        .accessibilityIdentifier("unavailable_\(title.lowercased())")
    }
}

extension View {
    func kinlyNavigationTitle() -> some View {
        navigationTitle("Kinly")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Image(systemName: "shield.fill")
                        .foregroundStyle(Color.kinlyOcean700)
                        .accessibilityHidden(true)
                }
            }
    }
}

extension Color {
    static let kinlyOcean900 = Color(red: 0.04, green: 0.16, blue: 0.27)
    static let kinlyOcean700 = Color(red: 0.04, green: 0.35, blue: 0.52)
    static let kinlySky500 = Color(red: 0.15, green: 0.66, blue: 0.91)
    static let kinlySky50 = Color(red: 0.96, green: 0.98, blue: 1.00)
    static let kinlySky100 = Color(red: 0.87, green: 0.95, blue: 0.99)
    static let kinlyInk600 = Color(red: 0.32, green: 0.40, blue: 0.48)
    static let kinlyBorder = Color(red: 0.84, green: 0.89, blue: 0.93)
    static let kinlyFresh600 = Color(red: 0.08, green: 0.60, blue: 0.45)
    static let kinlyFresh100 = Color(red: 0.86, green: 0.97, blue: 0.93)
    static let kinlyStale600 = Color(red: 0.72, green: 0.47, blue: 0.12)
    static let kinlyStale100 = Color(red: 1.00, green: 0.95, blue: 0.81)
    static let kinlyDanger600 = Color(red: 0.85, green: 0.25, blue: 0.20)
    static let kinlyDanger100 = Color(red: 0.99, green: 0.90, blue: 0.88)
}
