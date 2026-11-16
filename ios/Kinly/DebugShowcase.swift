#if DEBUG
import SwiftUI

enum DebugShowcaseTopic: String, CaseIterable, Identifiable {
    case home = "Home"
    case sharing = "Sharing controls"
    case circle = "Circle and membership"
    case places = "Places"
    case care = "Care and Safety"
    case history = "History and privacy"

    var id: Self { self }

    var accessibilityIdentifier: String {
        "showcase_" + rawValue.lowercased().replacingOccurrences(of: " ", with: "_")
    }

    var states: [SyntheticPreviewState] {
        switch self {
        case .home:
            [
                .init("No circle", "No circle yet", "Join or create a circle before sharing. No location is shown.", .information),
                .init("Fresh", "Sharing precisely", "Synthetic freshness: updated just now. Accuracy is illustrative only.", .fresh),
                .init("Approximate", "Sharing approximately", "The server would return a coarse area, not a precise point.", .information),
                .init("Paused", "Sharing paused", "No live or fabricated position is displayed.", .attention),
                .init("Stale or offline", "Last update is stale", "Freshness and uncertainty remain visible; this is not presented as live.", .attention),
                .init("Unavailable", "Home unavailable", "No member or location data can be confirmed.", .danger),
            ]
        case .sharing:
            [
                .init("Paused", "Paused by you", "No administrator can silently resume an adult's sharing.", .attention),
                .init("Approximate", "Approximate", "Approximation must be applied by the server.", .information),
                .init("Precise", "Precise", "Audience: synthetic household circle.", .fresh),
                .init("Timed", "Timed sharing", "Synthetic expiry: 18:00 Africa/Johannesburg.", .information),
                .init("Unavailable", "Controls unavailable", "No setting can be viewed or changed.", .danger),
            ]
        case .circle:
            [
                .init("No circle", "No circle yet", "Membership does not begin location sharing.", .information),
                .init("Overview", "Adult members", "Owner, administrator, and member roles remain separate from consent.", .fresh),
                .init("Pending invitation", "Invitation pending", "No invitation secret or automatic sharing is shown.", .information),
                .init("Expired invitation", "Invitation expired", "An expired invitation cannot be accepted.", .attention),
                .init("Unavailable", "Circle unavailable", "No membership mutation is available.", .danger),
            ]
        case .places:
            [
                .init("Loading", "Checking Places", "No arrival or departure is claimed while loading.", .information),
                .init("Empty", "No Places yet", "No address or coordinate is invented.", .information),
                .init("Overview", "Synthetic Places", "Home, School, and Work are labels only; no map or alerts exist.", .fresh),
                .init("Uncertain", "Boundary uncertain", "Low-confidence input never becomes a confirmed arrival.", .attention),
                .init("Offline", "Places offline", "Delayed data is not presented as current.", .attention),
                .init("Unavailable", "Places unavailable", "No alert delivery is claimed.", .danger),
            ]
        case .care:
            [
                .init("No activity", "No care activity", "No silence or non-response is labelled as danger.", .information),
                .init("Overview", "Care overview", "Check-ins and journeys remain separate, consent-aware tools.", .fresh),
                .init("Check-ins", "Synthetic check-in", "States may be sent, delivered where known, responded, expired, or failed.", .information),
                .init("Journeys", "Synthetic journey", "ETA is an estimate with audience, expiry, freshness, and confidence.", .information),
                .init("Offline", "Care tools offline", "No action or notification is performed.", .attention),
                .init("Unavailable", "Emergency tools unconfigured", "Kinly does not contact emergency services in this build.", .danger),
            ]
        case .history:
            [
                .init("Loading", "Checking history", "No movement is inferred while loading.", .information),
                .init("Empty", "No history", "No route, address, or coordinate is shown.", .information),
                .init("Overview", "Your synthetic history", "Gaps, precision, and freshness remain explicit.", .fresh),
                .init("Privacy and access", "Explicit grants only", "Membership and administrator roles do not grant history access.", .information),
                .init("Revoked", "History access revoked", "Future reads stop and cached sensitive data must expire.", .attention),
                .init("Offline", "History offline", "Cached examples are labelled and never presented as current.", .attention),
                .init("Unavailable", "History unavailable", "No retention duration, export, or deletion action is invented.", .danger),
            ]
        }
    }
}

struct SyntheticPreviewState: Identifiable, Equatable {
    enum Tone: Equatable {
        case information
        case fresh
        case attention
        case danger
    }

    let id: String
    let label: String
    let title: String
    let detail: String
    let tone: Tone

    init(_ label: String, _ title: String, _ detail: String, _ tone: Tone) {
        id = label
        self.label = label
        self.title = title
        self.detail = detail
        self.tone = tone
    }
}

struct DebugShowcaseView: View {
    var body: some View {
        NavigationStack {
            List {
                Section {
                    Label("Synthetic preview only", systemImage: "eye")
                        .font(.headline)
                        .foregroundStyle(Color.kinlyOcean900)
                    Text("No live data, product action, map, notification, or emergency contact is used.")
                        .foregroundStyle(Color.kinlyInk600)
                }

                Section("Screen previews") {
                    ForEach(DebugShowcaseTopic.allCases) { topic in
                        NavigationLink {
                            SyntheticPreviewScreen(topic: topic)
                        } label: {
                            Label(topic.rawValue, systemImage: topic.symbol)
                                .frame(minHeight: 44)
                        }
                        .accessibilityIdentifier(topic.accessibilityIdentifier)
                    }
                }

                Section("Semantic colors") {
                    SemanticColorRow("Information", "Selected and explanatory UI", .information)
                    SemanticColorRow("Fresh", "Current, delivered, active, or healthy", .fresh)
                    SemanticColorRow("Attention", "Delayed, stale, or needs review", .attention)
                    SemanticColorRow("Danger", "Errors, destructive actions, and genuine emergencies", .danger)
                }
            }
            .navigationTitle("UI showcase")
            .accessibilityIdentifier("debug_showcase_root")
        }
        .tint(.kinlyOcean700)
    }
}

private struct SyntheticPreviewScreen: View {
    let topic: DebugShowcaseTopic

    var body: some View {
        List {
            Section {
                Text("Synthetic \(topic.rawValue) preview")
                    .font(.headline)
                Text(topic.notice)
                    .foregroundStyle(Color.kinlyInk600)
            }
            Section("States") {
                ForEach(topic.states) { state in
                    SyntheticStateCard(state: state)
                }
            }
        }
        .navigationTitle(topic.rawValue)
        .navigationBarTitleDisplayMode(.inline)
        .accessibilityIdentifier("\(topic.accessibilityIdentifier)_screen")
    }
}

private struct SyntheticStateCard: View {
    let state: SyntheticPreviewState

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Label(state.label, systemImage: state.tone.symbol)
                .font(.headline)
                .foregroundStyle(state.tone.foreground)
            Text(state.title)
                .font(.title3.weight(.semibold))
                .foregroundStyle(Color.kinlyOcean900)
            Text(state.detail)
                .foregroundStyle(Color.kinlyInk600)
        }
        .padding(.vertical, 8)
        .accessibilityElement(children: .combine)
        .accessibilityLabel("\(state.label). \(state.title). \(state.detail)")
        .accessibilityIdentifier(
            "state_" + state.id.lowercased().replacingOccurrences(of: " ", with: "_")
        )
    }
}

private struct SemanticColorRow: View {
    let title: String
    let detail: String
    let tone: SyntheticPreviewState.Tone

    init(_ title: String, _ detail: String, _ tone: SyntheticPreviewState.Tone) {
        self.title = title
        self.detail = detail
        self.tone = tone
    }

    var body: some View {
        Label {
            VStack(alignment: .leading) {
                Text(title).font(.headline)
                Text(detail).font(.subheadline)
            }
        } icon: {
            Image(systemName: tone.symbol)
                .foregroundStyle(tone.foreground)
        }
        .accessibilityElement(children: .combine)
    }
}

private extension DebugShowcaseTopic {
    var symbol: String {
        switch self {
        case .home: "house"
        case .sharing: "location"
        case .circle: "person.3"
        case .places: "mappin.and.ellipse"
        case .care: "shield"
        case .history: "clock.arrow.circlepath"
        }
    }

    var notice: String {
        switch self {
        case .home: "No live circle or location data is used."
        case .sharing: "No sharing setting is saved."
        case .circle: "No real member or invitation data is used."
        case .places: "No address, coordinate, map, or alert data is used."
        case .care: "No real contact, message, journey, location, or emergency data is used."
        case .history: "No real movement, location, account, or retention data is used."
        }
    }
}

private extension SyntheticPreviewState.Tone {
    var symbol: String {
        switch self {
        case .information: "info.circle.fill"
        case .fresh: "checkmark.circle.fill"
        case .attention: "exclamationmark.triangle.fill"
        case .danger: "xmark.octagon.fill"
        }
    }

    var foreground: Color {
        switch self {
        case .information: .kinlyOcean700
        case .fresh: .kinlyFresh600
        case .attention: .kinlyStale600
        case .danger: .kinlyDanger600
        }
    }
}
#endif
