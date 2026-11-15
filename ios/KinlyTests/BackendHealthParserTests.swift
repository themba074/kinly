import Foundation
import XCTest
@testable import Kinly

final class BackendHealthParserTests: XCTestCase {
    func testAcceptsUpFromSuccessfulSmallResponse() {
        let data = Data(#"{"status":"UP"}"#.utf8)

        XCTAssertTrue(BackendHealthParser.isReady(statusCode: 200, data: data))
    }

    func testRejectsNonSuccessResponse() {
        let data = Data(#"{"status":"UP"}"#.utf8)

        XCTAssertFalse(BackendHealthParser.isReady(statusCode: 503, data: data))
    }

    func testRejectsMalformedOrUnexpectedStatus() {
        XCTAssertFalse(
            BackendHealthParser.isReady(
                statusCode: 200,
                data: Data(#"{"status":"UNKNOWN"}"#.utf8)
            )
        )
        XCTAssertFalse(
            BackendHealthParser.isReady(
                statusCode: 200,
                data: Data("not-json".utf8)
            )
        )
    }

    func testRejectsOversizedResponse() {
        let data = Data(repeating: 0x20, count: 4_097)

        XCTAssertFalse(BackendHealthParser.isReady(statusCode: 200, data: data))
    }
}
