import Foundation

enum AppConfiguration {
    private static let fallbackReadinessURL = URL(
        string: "https://kinly.invalid/actuator/health/readiness"
    )!

    static func backendReadinessURL(bundle: Bundle = .main) -> URL {
        guard let value = bundle.object(
            forInfoDictionaryKey: "KinlyBackendReadinessURL"
        ) as? String else {
            return fallbackReadinessURL
        }

        return URL(string: value) ?? fallbackReadinessURL
    }
}
