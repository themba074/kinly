import XCTest
@testable import Kinly

final class NavigationAndShowcaseTests: XCTestCase {
    func testPrimaryNavigationHasFourDestinationsWithoutPlaces() {
        XCTAssertEqual(
            PrimaryDestination.allCases.map(\.rawValue),
            ["Home", "Safety", "History", "Settings"]
        )
        XCTAssertFalse(PrimaryDestination.allCases.map(\.rawValue).contains("Places"))
    }

#if DEBUG
    func testEveryShowcaseTopicHasDistinctStates() {
        for topic in DebugShowcaseTopic.allCases {
            XCTAssertGreaterThanOrEqual(topic.states.count, 5, topic.rawValue)
            XCTAssertEqual(Set(topic.states.map(\.id)).count, topic.states.count, topic.rawValue)
        }
    }

    func testPrivacyCriticalSyntheticStatesRemainExplicit() {
        let homeDetails = DebugShowcaseTopic.home.states.map(\.detail).joined(separator: " ")
        XCTAssertTrue(homeDetails.contains("No live or fabricated position"))

        let careDetails = DebugShowcaseTopic.care.states.map(\.detail).joined(separator: " ")
        XCTAssertTrue(careDetails.contains("does not contact emergency services"))

        let historyDetails = DebugShowcaseTopic.history.states.map(\.detail).joined(separator: " ")
        XCTAssertTrue(historyDetails.contains("No retention duration"))
    }
#endif
}
