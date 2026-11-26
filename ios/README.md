# Kinly iOS

The native SwiftUI foundation uses the approved Ocean Sky four-tab shell:
Home, Safety, History, and Settings. A labelled Home control opens the
unavailable Places screen. Home shows truthful backend and sign-in status.
The local identity work package uses the system browser with authorization code
and PKCE, exact redirect/state validation, Keychain-backed session storage, and
`/api/v1/me` profile confirmation when provider settings are supplied. Empty
settings keep sign-in visibly unavailable. Sign-in does not join a circle,
request location permission, collect location, register push, or implement
safety behaviour.

## Build and test

The project requires macOS with stable Xcode. From the repository root:

```bash
xcodebuild test \
  -project ios/Kinly.xcodeproj \
  -scheme Kinly \
  -destination 'platform=iOS Simulator,name=iPhone 17,OS=latest' \
  CODE_SIGNING_ALLOWED=NO
```

Open `ios/Kinly.xcodeproj` in Xcode to run the app interactively. This Windows
workspace cannot launch an iOS simulator, so the public standard macOS workflow
is the executable verification path.

To open the synthetic UI showcase in a Debug run, add the launch argument
`--kinly-ui-showcase`. The showcase covers the approved Home, sharing, circle,
Places, care/Safety, and history/privacy states without live data or product
actions. Its code and launch route are compiled out of Release builds.

Debug builds may call the local development readiness endpoint at
`http://localhost:8080/actuator/health/readiness`. Only the Debug plist permits
that localhost exception. Release uses the deliberately invalid HTTPS host
`kinly.invalid` until a deployed environment is separately approved.

Provider settings are Info.plist values for an approved environment:
`KinlyAuthIssuer`, `KinlyAuthAuthorizeURL`, `KinlyAuthTokenURL`,
`KinlyAuthClientID`, and `KinlyAuthMeURL`. No provider credentials, real
accounts, access tokens, or callback secrets belong in source control, fixtures,
screenshots, or logs.

The approved minimum deployment target is iOS 17. The support policy and device
matrix are recorded in
`../docs/decisions/0001-supported-platforms-and-device-matrix.md`.
