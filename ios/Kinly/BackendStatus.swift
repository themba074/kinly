import Foundation

enum BackendStatus: Equatable, Sendable {
    case checking
    case available
    case unavailable

    var accessibilityValue: String {
        switch self {
        case .checking:
            "Checking backend availability"
        case .available:
            "Backend available"
        case .unavailable:
            "Backend unavailable"
        }
    }
}

enum BackendHealthParser {
    private static let maximumResponseBytes = 4_096

    static func isReady(statusCode: Int, data: Data) -> Bool {
        guard (200 ... 299).contains(statusCode),
              data.count <= maximumResponseBytes,
              let payload = try? JSONSerialization.jsonObject(with: data),
              let object = payload as? [String: Any],
              let status = object["status"] as? String
        else {
            return false
        }

        return status == "UP"
    }
}
