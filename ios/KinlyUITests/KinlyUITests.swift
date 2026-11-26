import XCTest

final class KinlyUITests: XCTestCase {
    @MainActor
    func testPrimaryScreensExposeAccessibleNavigationAndNestedPlaces() {
        let app = launchApp()

        let tabBar = app.tabBars.firstMatch
        XCTAssertTrue(tabBar.waitForExistence(timeout: 5))
        for destination in ["Home", "Safety", "History", "Settings"] {
            let button = tabBar.buttons[destination]
            XCTAssertTrue(button.exists, destination)
            assertMinimumTargetSize(button, named: destination)
        }
        XCTAssertFalse(tabBar.buttons["Places"].exists)

        let heading = app.descendants(matching: .any)["home_heading"]
        let placesButton = app.buttons["home_places_button"]
        let authCard = app.descendants(matching: .any)["auth_status_card"]
        let statusCard = app.descendants(matching: .any)["backend_status_card"]
        XCTAssertTrue(heading.waitForExistence(timeout: 5))
        XCTAssertTrue(placesButton.waitForExistence(timeout: 5))
        XCTAssertTrue(authCard.waitForExistence(timeout: 5))
        XCTAssertEqual(authCard.label, "Kinly sign-in status")
        XCTAssertTrue(app.staticTexts["Sign-in unavailable"].waitForExistence(timeout: 5))
        XCTAssertFalse(app.buttons["Sign in"].exists)
        XCTAssertTrue(statusCard.waitForExistence(timeout: 5))
        XCTAssertEqual(statusCard.label, "Kinly service status")
        XCTAssertFalse((statusCard.value as? String ?? "").isEmpty)
        XCTAssertLessThan(heading.frame.minY, placesButton.frame.minY)
        XCTAssertLessThan(placesButton.frame.minY, statusCard.frame.minY)
        assertMinimumTargetSize(placesButton, named: "Open Places")
        keepScreenshot(app, named: "Home-portrait")

        for destination in ["Safety", "History", "Settings"] {
            tabBar.buttons[destination].tap()
            let unavailable = app.descendants(matching: .any)["unavailable_\(destination.lowercased())"]
            XCTAssertTrue(unavailable.waitForExistence(timeout: 2), destination)
            XCTAssertTrue(unavailable.label.contains("Not available in this build"), destination)
            keepScreenshot(app, named: "\(destination)-portrait")
        }

        tabBar.buttons["Home"].tap()
        placesButton.tap()
        let unavailablePlaces = app.descendants(matching: .any)["unavailable_places"]
        XCTAssertTrue(unavailablePlaces.waitForExistence(timeout: 2))
        XCTAssertTrue(unavailablePlaces.label.contains("Places"))
        XCTAssertTrue(unavailablePlaces.label.contains("Not available in this build"))
        keepScreenshot(app, named: "Places-nested-portrait")
    }

    @MainActor
    func testDebugShowcaseIsClearlySyntheticAndNavigable() {
        let app = launchApp(arguments: ["--kinly-ui-showcase"])

        XCTAssertTrue(app.descendants(matching: .any)["debug_showcase_root"].waitForExistence(timeout: 5))
        XCTAssertTrue(app.staticTexts["Synthetic preview only"].exists)
        keepScreenshot(app, named: "Showcase-index")

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
            assertMinimumTargetSize(button, named: identifier)
            button.tap()
            XCTAssertTrue(app.staticTexts[title].waitForExistence(timeout: 2), title)
            let firstState = app.descendants(matching: .any).matching(
                NSPredicate(format: "identifier BEGINSWITH %@", "state_")
            ).firstMatch
            XCTAssertTrue(firstState.exists, title)
            XCTAssertFalse(firstState.label.isEmpty, title)
            keepScreenshot(app, named: identifier)
            app.navigationBars.buttons.firstMatch.tap()
        }
    }

    @MainActor
    func testAccessibilityXXXLReflowsAndRotatesWithoutLosingContent() {
        let app = launchApp(arguments: [
            "--kinly-ui-showcase",
            "-UIPreferredContentSizeCategoryName",
            "UICTContentSizeCategoryAccessibilityXXXL",
        ])

        XCTAssertTrue(app.descendants(matching: .any)["debug_showcase_root"].waitForExistence(timeout: 5))
        XCTAssertTrue(app.staticTexts["Synthetic preview only"].exists)
        keepScreenshot(app, named: "Showcase-accessibility-XXXL-portrait")

        XCUIDevice.shared.orientation = .landscapeLeft
        XCTAssertTrue(app.staticTexts["Synthetic preview only"].waitForExistence(timeout: 3))
        keepScreenshot(app, named: "Showcase-accessibility-XXXL-landscape")
    }

    @MainActor
    private func launchApp(arguments: [String] = []) -> XCUIApplication {
        continueAfterFailure = false
        XCUIDevice.shared.orientation = .portrait
        let app = XCUIApplication()
        app.launchArguments = arguments
        app.launch()
        return app
    }

    @MainActor
    private func assertMinimumTargetSize(
        _ element: XCUIElement,
        named name: String,
        file: StaticString = #filePath,
        line: UInt = #line
    ) {
        XCTAssertGreaterThanOrEqual(element.frame.width, 44, "\(name) width", file: file, line: line)
        XCTAssertGreaterThanOrEqual(element.frame.height, 44, "\(name) height", file: file, line: line)
    }

    @MainActor
    private func keepScreenshot(_ app: XCUIApplication, named name: String) {
        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = name
        attachment.lifetime = .keepAlways
        add(attachment)
    }
}
