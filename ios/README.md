# Kinly iOS

The native SwiftUI foundation mirrors the current Android walking-skeleton
boundary: an Ocean Sky five-tab shell and a truthful, non-sensitive development
backend status. It contains no identity, permission, location, notification,
background, or safety feature.

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

Debug builds may call the local development readiness endpoint at
`http://localhost:8080/actuator/health/readiness`. Only the Debug plist permits
that localhost exception. Release uses the deliberately invalid HTTPS host
`kinly.invalid` until a deployed environment is separately approved.

The approved minimum deployment target is iOS 17. The support policy and device
matrix are recorded in
`../docs/decisions/0001-supported-platforms-and-device-matrix.md`.
