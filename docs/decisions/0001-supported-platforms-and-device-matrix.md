# ADR 0001 - Supported mobile platforms and device-test matrix

**Status:** Accepted  
**Date:** 2026-11-09  
**Owners:** Kinly team

## Context

Kinly must behave truthfully under denied permissions, weak networks, low power,
process termination, device reboot, and operating-system background limits. Its
South African product context also requires meaningful coverage of budget and
older devices, not only current flagships.

Supporting every OS and handset combination is not feasible. Supporting only
recent reference devices would hide the OEM battery restrictions and constrained
hardware most likely to affect location freshness and alert reliability. A
device farm broadens compatibility coverage, but it cannot replace physical,
long-running battery and background-execution tests.

Android 8.0 (API 26) is a useful lower boundary because it introduced the
background execution and location limits that shape Kinly's Android design. The
existing Android shell already uses `minSdk` 26 and compiles and targets API 36.
API 36 also meets the Google Play target requirement in force when this decision
was accepted.

For iOS, a deployment target of iOS 17 balances older-device access with a
maintainable SwiftUI baseline and physical-device support in current Xcode
tooling. The iOS application does not exist yet, so Feature 0D must apply this
decision when creating it.

Primary platform references:

- [Android background location limits](https://developer.android.com/about/versions/oreo/background-location-limits)
- [Google Play target API requirements](https://developer.android.com/google/play/requirements/target-sdk)
- [Xcode SDK and system requirements](https://developer.apple.com/xcode/system-requirements)
- [Apple deployment-target guidance](https://developer.apple.com/documentation/xcode/running-code-on-a-specific-version/)

## Decision

### Supported platform baseline

- Android installs on Android 8.0/API 26 and later.
- Android `compileSdk` and `targetSdk` are API 36 at acceptance. They must track
  Google Play requirements without silently raising `minSdk`.
- The future iOS application has a minimum deployment target of iOS 17.
- iOS builds use a current stable Xcode and SDK accepted by App Store Connect.
- Release validation covers the latest stable iOS plus the previous two major
  iOS releases when those releases can run on the required device classes.
- Newer platform APIs may be used behind explicit availability checks. A newer
  API must not make a supported device appear to complete a safety operation
  that it cannot perform.
- Raising either minimum version requires a superseding ADR with usage data,
  security and maintenance evidence, migration effects, and a user
  communication plan.

“Supported” means the applicable approved Kinly features pass their required
functional, consent, security, accessibility, offline, and lifecycle tests. It
does not promise identical background scheduling across operating systems or
OEMs. The UI must expose material degradation rather than presenting stale or
unconfirmed data as current.

### Automated OS matrix

The normal mobile verification matrix is:

| Platform | Required virtual coverage | Purpose |
|---|---|---|
| Android | API 26 | Installation floor and oldest supported framework behaviour |
| Android | API 29 | Background-location permission boundary |
| Android | API 33 | Modern notification and runtime-permission behaviour |
| Android | API 36 | Current target, policy, and UI behaviour |
| iOS | iOS 17 | Deployment floor |
| iOS | Previous two stable major releases | Compatibility and lifecycle regression |
| iOS | Latest stable major release | Current SDK and policy behaviour |

Duplicate rows collapse when the deployment floor overlaps the latest-three
window. Pull requests run the smallest affected subset; scheduled and release
verification run the full available matrix. Virtual-device success never
replaces a required physical-device result.

### Physical-device matrix

The following six device classes are required before the related background
location feature can be accepted. Locally available devices may be reused.

| ID | Required class | Minimum characteristics | Primary risk represented |
|---|---|---|---|
| A1 | Android support-floor device | API 26-28, Google Play services, no more than 3 GB RAM | Old framework, memory pressure, slow storage and CPU |
| A2 | Budget Samsung Galaxy A-class | API 33 or later, 3-4 GB RAM | Common budget hardware and Samsung power management |
| A3 | Restrictive non-Samsung Android | API 33 or later; Redmi, Xiaomi, Oppo, Vivo, or equivalent | OEM-specific background killing and autostart controls |
| A4 | Android reference device | Google Pixel on the latest stable Android | Reference permission, foreground-service, and SDK behaviour |
| I1 | Oldest supported iPhone class | iPhone XR, XS, or SE (2nd generation) running iOS 17 | Deployment floor, memory pressure, older battery |
| I2 | Current iPhone class | Current-generation iPhone on the latest stable iOS | Current Core Location, power, and lifecycle behaviour |

A substitute is allowed only when the test record names the replacement and
shows that it preserves the OS, memory/performance, OEM, and battery-management
risk represented by the class. Replacing A3 with a Pixel or replacing I1 with a
current iPhone is not equivalent.

Availability of A1-A4 is a prerequisite for approving implementation of
Feature 3E. Availability of I1-I2 and a macOS/Xcode environment is a prerequisite
for approving implementation of Feature 3F. Cloud device services may add
coverage but do not satisfy these prerequisites.

### Verification cadence and evidence

| Event | Required verification |
|---|---|
| Pull request | Affected unit/UI tests plus floor and current virtual-device builds where relevant |
| Mobile feature close-out | Affected supported OS boundaries and at least one applicable physical device |
| Permission, lifecycle, notification, or background change | All affected physical-device classes, denial/revocation, offline, reboot, low-power, and process-death cases |
| Release candidate | Full virtual matrix and all six physical-device classes |
| New major OS or material store-policy change | Compatibility run and ADR review before claiming support |
| Before pilot | Long-running battery, data, freshness, uncertainty, and recovery benchmarks on the full physical matrix |

Each physical test record must contain the device class and model, OS/build,
Kinly build, permission state, battery and power mode, network condition, test
duration, expected result, observed result, and anonymised measurements. It must
also identify any OEM exemption or setting changed for the test.

Test accounts and routes must be synthetic. Evidence must not contain precise
production coordinates, real family data, tokens, credentials, or sensitive
screenshots. Exact battery, data, freshness, and alert thresholds remain part of
the relevant feature proposal and pilot benchmark; this ADR defines coverage,
not unmeasured promises.

The team reviews this ADR at least annually and whenever Apple, Google, a major
OEM, or the app stores materially change SDK, permission, background-execution,
or submission requirements.

## Alternatives considered

### Support only recent flagship devices

This reduces acquisition and compatibility cost, but it conflicts with Kinly's
budget-device and imperfect-network goals and misses important OEM power
management behaviour.

### Support Android API 29+ only

This simplifies Android permission handling and reduces the OS span, but drops
devices that can still exercise the API 26 background model. API 26 is retained
until usage, security support, and maintenance evidence justify a separate
minimum-version change.

### Support iOS 15 or 16

This extends installation to older devices but increases compatibility and test
cost below the current physical-device debugging baseline of the selected Xcode
tooling. iOS 17 is the supported floor.

### Use emulators, simulators, and device farms only

This is cheaper and useful for broad compatibility, but it cannot produce
trustworthy long-duration battery, thermal, weak-network, OEM restriction, or
background-relaunch evidence.

### Name permanent commercial device models

Exact models are easy to purchase against initially but become unavailable and
stale. Risk-based device classes with controlled substitution preserve the
reason each device is in the matrix.

## Consequences

- Android keeps its existing API 26 minimum and API 36 compile/target values.
- Feature 0D must create the iOS project with an iOS 17 deployment target.
- The team must obtain or identify up to six qualifying physical devices before
  background-location implementation can be approved.
- CI and scheduled testing will grow as the iOS project and sensitive mobile
  features are introduced.
- Physical testing creates time and device costs, but reduces the risk of false
  safety claims caused by OEM restrictions, low memory, old batteries, or OS
  lifecycle behaviour.
- No location permission, background capability, or data collection is enabled
  by this ADR.

## Validation

Feature 0C is validated when:

- this ADR is accepted by the team;
- the existing Android configuration is confirmed as `minSdk` 26 and
  `compileSdk`/`targetSdk` 36;
- Feature 0D applies iOS 17 when an iOS project can be created on macOS/Xcode;
- future Features 3E and 3F refuse approval until their physical-device
  prerequisites are available; and
- the roadmap and contributor guidance reference this decision.

The decision must be revisited before raising a minimum OS, removing a device
class, treating cloud testing as a physical-test replacement, or changing the
background-location validation boundary.
