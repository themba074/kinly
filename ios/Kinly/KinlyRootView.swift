import SwiftUI

private enum Destination: String, CaseIterable, Identifiable {
    case home = "Home"
    case places = "Places"
    case safety = "Safety"
    case history = "History"
    case settings = "Settings"

    var id: Self { self }

    var symbol: String {
        switch self {
        case .home:
            "house.fill"
        case .places:
            "mappin.and.ellipse"
        case .safety:
            "shield.checkered"
        case .history:
            "clock.arrow.circlepath"
        case .settings:
            "gearshape.fill"
        }
    }
}

struct KinlyRootView: View {
    @ObservedObject var statusModel: BackendStatusModel
    @State private var selection = Destination.home

    var body: some View {
        TabView(selection: $selection) {
            NavigationStack {
                HomeView(statusModel: statusModel)
                    .kinlyNavigationTitle()
            }
            .tabItem {
                Label(Destination.home.rawValue, systemImage: Destination.home.symbol)
            }
            .tag(Destination.home)

            ForEach(Destination.allCases.filter { $0 != .home }) { destination in
                NavigationStack {
                    UnavailableDestinationView(destination: destination)
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

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 24) {
                VStack(alignment: .leading, spacing: 8) {
                    Text("Care starts with connection")
                        .font(.title.bold())
                        .foregroundStyle(Color.kinlyOcean900)
                    Text(
                        "This early build checks only whether Kinly's development service is ready."
                    )
                    .font(.body)
                    .foregroundStyle(Color.kinlyInk600)
                }

                BackendStatusCard(statusModel: statusModel)

                Divider()
                    .overlay(Color.kinlyBorder)

                Text(
                    "No account, member, location, notification, or safety data is used in this build."
                )
                .font(.subheadline)
                .foregroundStyle(Color.kinlyInk600)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(24)
        }
        .background(Color.kinlySky50)
        .task {
            await statusModel.refresh()
        }
    }
}

private struct BackendStatusCard: View {
    @ObservedObject var statusModel: BackendStatusModel

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Development service")
                .font(.title2.weight(.semibold))
                .foregroundStyle(Color.kinlyOcean900)

            statusContent

            if statusModel.status == .unavailable {
                Button {
                    Task {
                        await statusModel.refresh()
                    }
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
        case .checking:
            .kinlySky100
        case .available:
            .kinlyFresh100
        case .unavailable:
            .kinlyDanger100
        }
    }
}

private struct UnavailableDestinationView: View {
    let destination: Destination

    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: destination.symbol)
                .font(.system(size: 44))
                .foregroundStyle(Color.kinlyOcean700)
                .accessibilityHidden(true)
            Text(destination.rawValue)
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
    }
}

private extension View {
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

private extension Color {
    static let kinlyOcean900 = Color(red: 0.04, green: 0.16, blue: 0.27)
    static let kinlyOcean700 = Color(red: 0.04, green: 0.35, blue: 0.52)
    static let kinlySky50 = Color(red: 0.96, green: 0.98, blue: 1.00)
    static let kinlySky100 = Color(red: 0.87, green: 0.95, blue: 0.99)
    static let kinlyInk600 = Color(red: 0.32, green: 0.40, blue: 0.48)
    static let kinlyBorder = Color(red: 0.84, green: 0.89, blue: 0.93)
    static let kinlyFresh600 = Color(red: 0.08, green: 0.60, blue: 0.45)
    static let kinlyFresh100 = Color(red: 0.86, green: 0.97, blue: 0.93)
    static let kinlyDanger600 = Color(red: 0.85, green: 0.25, blue: 0.20)
    static let kinlyDanger100 = Color(red: 0.99, green: 0.90, blue: 0.88)
}
