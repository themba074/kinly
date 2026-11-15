import XCTest

final class KinlyUITests: XCTestCase {
    override func setUpWithError() throws {
        continueAfterFailure = false
    }

    func testFourPrimaryTabsAndNestedPlaces() {
        let app = XCUIApplication()
        app.launch()

        let tabBar = app.tabBars.firstMatch
        XCTAssertTrue(tabBar.waitForExistence(timeout: 5))
        XCTAssertTrue(tabBar.buttons["Home"].exists)
        XCTAssertTrue(tabBar.buttons["Safety"].exists)
        XCTAssertTrue(tabBar.buttons["History"].exists)
        XCTAssertTrue(tabBar.buttons["Settings"].exists)
        XCTAssertFalse(tabBar.buttons["Places"].exists)

        let placesButton = app.buttons["home_places_button"]
        XCTAssertTrue(placesButton.waitForExistence(timeout: 5))
        placesButton.tap()
        XCTAssertTrue(
            app.descendants(matching: .any)["unavailable_places"].waitForExistence(timeout: 2)
        )
    }

    func testDebugShowcaseIsClearlySyntheticAndNavigable() {
        let app = XCUIApplication()
        app.launchArguments = ["--kinly-ui-showcase"]
        app.launch()

        XCTAssertTrue(app.descendants(matching: .any)["debug_showcase_root"].waitForExistence(timeout: 5))
        XCTAssertTrue(app.staticTexts["Synthetic preview only"].exists)

        let topics = [
            ("showcase_home", "Synthetic Home preview"),
            ("showcase_sharing_controls", "Synthetic Sharing controls preview"),
            ("showcase_circle_and_membership", "Synthetic Circle and membership preview"),
            ("showcase_places", "Synthetic Places preview"),
            ("showcase_care_and_safety", "Synthetic Care and Safety preview"),
            ("showcase_history_and_privacy", "Synthetic History and privacy preview"),
        ]

        for (identifier, title) in topics {
            let button = app.buttons[identifier]
            for _ in 0..<4 where !button.isHittable {
                app.swipeUp()
            }
            XCTAssertTrue(button.isHittable, identifier)
            button.tap()
            XCTAssertTrue(app.staticTexts[title].waitForExistence(timeout: 2), title)
            app.navigationBars.buttons.firstMatch.tap()
        }
    }

    func testShowcaseReflowsAtAccessibilityTextSize() {
        let app = XCUIApplication()
        app.launchArguments = [
            "--kinly-ui-showcase",
            "-UIPreferredContentSizeCategoryName",
            "UICTContentSizeCategoryAccessibilityXXXL",
        ]
        app.launch()

        XCTAssertTrue(app.descendants(matching: .any)["debug_showcase_root"].waitForExistence(timeout: 5))
        XCTAssertTrue(app.staticTexts["Synthetic preview only"].exists)
    }
}
