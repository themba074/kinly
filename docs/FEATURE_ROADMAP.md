# Kinly Feature Roadmap

**Status:** Active delivery plan  
**Last updated:** 11 November 2026
**Current feature:** UI3 - circle and membership preview (Proposed; awaiting approval)
**Rule:** One feature, and one substep within that feature, may be in progress.

This is Kinly's single delivery-planning document. The PRD defines what the
product must do, the technical specification defines how the system is shaped,
and `AGENTS.md` defines how contributors work.

## 1. How features move through the roadmap

Each feature is proposed, explicitly approved, implemented, verified, and then
marked **Complete** before the next feature begins. Approval does not carry
forward.

Features group closely related work so routine backend, Android, and iOS tasks
do not become dozens of artificial roadmap entries. Work inside an approved
feature still happens sequentially:

1. contract, threat model, and schema when needed;
2. backend domain and API behaviour;
3. Android behaviour;
4. iOS behaviour; and
5. end-to-end verification and documentation.

Only one of those substeps may be active at a time. A feature may omit substeps
that do not apply. Commits and reviews should remain small even when they belong
to the same feature.

Before implementation, every feature proposal must state:

- the user outcome and explicit exclusions;
- acceptance criteria and required negative tests;
- design, alternatives, dependencies, and rollback;
- privacy, consent, security, battery, reliability, accessibility, and cost
  effects; and
- decisions the team should sanity-check.

Allowed states are **Planned**, **Proposed**, **Approved**, **In progress**,
**Complete**, **Blocked**, and **Deferred**. Only one feature may be **In
progress**. “Complete” requires the Definition of Done in `AGENTS.md`, including
verification and close-out evidence; a working happy path or polished UI is not
enough.

## 2. Roadmap

### Phase 0 - Delivery foundations

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 0A | Start the Java backend and local PostGIS/Redis foundation. | Backend tests, disposable database/cache integration tests, restricted health probes, correlation IDs, migration, and CI pass. | **Complete** |
| 0B | Open the Android Ocean Sky shell and show truthful, non-sensitive backend availability. | Compose UI, offline/error/retry, accessibility, local health request, Android lint/test/build, and CI pass. No authentication, maps, push, or sensitive permission exists. | **Complete** |
| 0C | Record supported Android/iOS versions and the physical-device test matrix. | ADR is approved before platform-specific background behaviour begins. | **Complete** |
| 0D | View approved native Ocean Sky components and synthetic states without implying unfinished product behaviour. | Debug-only Android showcase, installable APK, accessibility tests, viewing instructions, and matching iOS component guidance pass. No product action, permission, or sensitive data is introduced. | **Complete** |
| 0E | Choose the development AWS region and recovery direction. | ADR covers POPIA, latency, service availability, recovery, and cost. | Planned |
| 0F | Provision the minimal development environment. | Reviewed Terraform creates only the required development resources; health, rollback, secrets, observability, and cost controls are verified. | Planned |

### Native mobile UI exploration

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| UI1 | Preview a truthful consent-aware Home experience before circle and location services exist. | Debug-only Compose states cover no circle, paused, approximate, fresh, stale/offline, and unavailable behaviour; accessibility tests, release exclusion, APK/screenshots, viewing guidance, and matching iOS guidance pass. No map, permission, real data, or product action exists. | **Complete** |
| UI2 | Preview adult-owned sharing controls before consent services exist. | Debug-only Compose states cover paused, approximate, precise, timed, and unavailable behaviour; audience, expiry, consent ownership, accessibility, release exclusion, APK/screenshots, viewing guidance, and matching iOS guidance pass. No setting is saved and no permission, service, real data, or product action exists. | **Complete** |
| UI3 | Preview a private circle, adult membership roles, and invitation states before Circle services exist. | Debug-only Compose states cover no circle, circle overview, pending and expired invitations, and unavailable behaviour; role clarity, consent separation, accessibility, release exclusion, APK/screenshots, viewing guidance, and matching iOS guidance pass. No circle or membership mutation exists. | **Proposed** |

### Phase 1 - Identity and devices

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 1A | Choose the identity provider, sign-in methods, recovery, and key-management boundary. | ADR and identity threat model are approved. | Planned |
| 1B | Sign in and retrieve the authenticated user's minimal profile. | Backend token validation and `/api/v1/me`, followed by Android and iOS sign-in, pass expiry, cancellation, recovery, secure-storage, safe-log, and invalid-token tests. | Planned |
| 1C | Register and revoke a user's devices and push tokens. | Registration is idempotent; token rotation, removal, ownership denial, session/device revocation, cache invalidation, and sensitive-log tests pass. No notification is sent yet. | Planned |

### Phase 2 - Circles and consent

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 2A | Create, list, select, and display a private circle. | Circle-scoped backend and mobile states pass empty/error, authorization, and cross-circle denial tests. No location data is shown. | Planned |
| 2B | Invite a member and manage membership. | Create, cancel, expire, accept, leave, remove, and administrator-transfer flows pass replay, last-admin, immediate access-loss, cache, and audit tests. | Planned |
| 2C | Give each adult member visible sharing controls. | Grant, pause, resume, precise/approximate, expiry, audience visibility, and mobile accessibility pass; only the subject can change their grant. No location is collected yet. | Planned |
| 2D | Harden consent and membership revocation. | Concurrent read/revoke, removed-member, cross-circle, expired-grant, cache invalidation, and abuse-case tests prove future access stops immediately. | Planned |

### Phase 3 - Location foundation

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 3A | Approve the server-side approximation algorithm. | ADR covers radius/transform, stability, boundary leakage, cache rules, and test vectors. | Planned |
| 3B | Share and view one authorised foreground location. | Validated, idempotent, order-safe ingestion; authorised latest read; server approximation; Android/iOS permission and map/card states; freshness and uncertainty; and cross-circle/consent denial tests pass. | Planned |
| 3C | Buffer and ingest location while temporarily offline. | Batch limits, encryption decision, retry, duplicate, order, invalid sample, consent change, reboot, logout deletion, and retention tests pass on the backend and both clients. | Planned |
| 3D | Receive live latest-location updates. | WebSocket subscription authorization, reconnect, revoke, order, backpressure, stale fallback, client lifecycle, and safe-log tests pass. | Planned |
| 3E | Collect background location on Android using an approved adaptive policy. | Proposal is approved first; physical-device tests cover foreground service, Doze, reboot, restriction, low power, offline, revoke, kill switch, and measured battery/data use. | Planned |
| 3F | Collect background location on iOS using an approved adaptive policy. | Proposal is approved first; physical-device tests cover authorization changes, relaunch, reduced accuracy, low power, offline, revoke, kill switch, and measured battery/data use. | Planned |

### Phase 4 - Notifications

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 4A | Choose push, SMS, and email provider boundaries. | ADR covers privacy, region, reliability, rate limits, cost, failure, and replacement. | Planned |
| 4B | Deliver non-sensitive notifications according to user preferences. | Transactional outbox, retry, idempotency, provider failure, token rotation, Android/iOS receipt, permission denial, foreground/background, quiet hours, timezone, and truthful delivery-state tests pass. | Planned |

### Phase 5 - Everyday care features

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 5A | Create places and receive arrival/departure alerts. | Circle-scoped place management, mobile UI, entry/exit hysteresis, uncertainty, delayed/out-of-order samples, subscriptions, deduplication, preferences, and provider-failure tests pass. | Planned |
| 5B | Request and respond to a check-in. | Selected audience, expiry, “I am okay”, decline, optional explicitly consented location, replay, late response, offline/error, notification, and accessibility tests pass. | Planned |
| 5C | Choose the maps, geocoding, and routing provider. | ADR covers privacy, coverage, rate limits, cost, degradation, and replacement. | Planned |
| 5D | Start, view, and finish a time-limited journey. | Audience, destination, expiry, cancellation, revocation, ETA/fallback, arrival/overdue event, background behaviour, offline state, cross-circle denial, and mobile accessibility tests pass. | Planned |

### Phase 6 - SOS and safety hardening

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 6A | Define exact SOS recipients, channels, copy, cancellation, and retry semantics. | ADR, threat model, abuse review, and product/legal approval are complete. It never implies unimplemented dispatch. | Planned |
| 6B | Trigger, notify, display, and cancel an SOS incident. | State machine, hold-to-activate, accidental trigger, cancellation, offline, low power, authorization, idempotency, partial delivery, retries, audit, Android/iOS lifecycle, and accessibility tests pass. | Planned |
| 6C | Prove SOS degrades safely. | Provider-failure drill, monitoring, user-visible delivery truth, recovery, stolen-device, malicious-admin, spam, replay, and support runbook are verified. | Planned |

### Phase 7 - History and privacy operations

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 7A | Approve location-history retention and backup expiry. | ADR receives product, legal, privacy, security, and operations approval. | Planned |
| 7B | Retain, expire, and display authorised location history. | Automatic expiry, own-history read, explicitly granted/revoked sharing, pagination, timezone, gaps, precision, cache invalidation, cross-circle denial, Android/iOS UI, and accessibility tests pass. | Planned |
| 7C | Export or delete a user's personal data through a verifiable workflow. | Re-authentication, encrypted expiring export, deletion grace/cancel policy, cascade/anonymisation, cache/provider cleanup, audit, failure recovery, and backup-expiry evidence pass. | Planned |
| 7D | Audit sensitive reads and tightly control support access. | Actor, subject, reason, retention, alerts, tamper resistance, approval, and no-sensitive-payload tests pass. | Planned |

### Phase 8 - Pilot readiness

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 8A | Complete cross-platform accessibility validation. | TalkBack, VoiceOver, font scaling, contrast, touch targets, motion, and keyboard/control tests meet approved gates. | Planned |
| 8B | Complete battery, data, location, and place-alert field benchmarks. | Representative devices, modes, signal conditions, offline periods, freshness, uncertainty, and false-alert rates meet approved budgets. | Planned |
| 8C | Complete operational, security, privacy, and legal readiness. | Restore/recovery drill, independent assessment, blocker remediation, POPIA data map/notices/subprocessors, retention, emergency copy, monitoring, rollback, and incident response are approved. | Planned |
| 8D | Release to a small adult-only pilot. | Support, feedback, metrics, rollback, incident ownership, and explicit go/no-go approval are ready. | Planned |

## 3. Deferred work

These items require new product approval and, where applicable, an ADR. They
must not be added to a nearby feature as “small extras”:

- child or dependent accounts and guardianship;
- crash detection or driving behaviour scoring;
- professional emergency dispatch, roadside assistance, or automatic calling;
- wearables, vehicle, smart-home, or partner integrations;
- paid tiers, subscriptions, or billing;
- machine-learning risk prediction; and
- advertising, sale, model training, or third-party use of precise location.

## 4. Current boundary and completed foundation

Features 0A through 0D, UI1, and UI2 are complete and verified. The foundation now includes the
Java 21/Spring Boot backend, local PostgreSQL/PostGIS and Redis, Flyway `V1`,
restricted health probes, correlation IDs, backend CI, and a native Android
Kotlin/Compose Ocean Sky shell. The shell makes a real non-sensitive readiness
request and handles checking, available, unavailable, and retry states. It has
no authentication, sensitive permissions, member data, map, push, location, or
safety behaviour.

Feature 0B close-out evidence: six JVM tests and three Pixel 8 Android 16
Compose tests passed; Android lint reported no issues; debug app and test APKs
built; and the emulator rendered `Available` after reaching the local backend
with healthy PostGIS and Redis dependencies. Android CI compiles the
instrumentation tests in addition to running unit tests, lint, and the app
build.

Feature 0C accepted
`docs/decisions/0001-supported-platforms-and-device-matrix.md`: Android remains
API 26+ with API 36 compile/target at acceptance, iOS begins at iOS 17, and six
risk-based physical-device classes gate later background-location work.

Feature 0D close-out evidence: the debug-only Android UI Showcase renders the
real status component, typography, generic action states, semantic colors, and
the five-destination navigation using visibly labelled synthetic data. Six JVM
tests and five Pixel 8 Android 16 Compose tests passed; Android lint reported no
issues; debug, test, and release APKs built; and merged-manifest inspection
proved the showcase is absent from release. The Android viewing workflow and
matching iOS component guidance are documented.

Feature UI1 close-out evidence: the debug-only Android UI Showcase opens a
selectable consent-aware Home preview with no-circle, circle, and unavailable
states. Its synthetic circle state shows paused sharing without any last-known
location, plus precise/fresh, approximate, and stale/offline member cards with
plain-language timestamps and uncertainty. Six JVM tests and eleven Pixel 8
Android 16 Compose tests passed, including large-text and privacy assertions;
Android lint reported no issues; debug, test, and release APKs built; and the
release merged manifest contains neither the showcase nor Home preview. Emulator
screenshots were visually checked for all states. Viewing instructions and
matching SwiftUI design guidance are documented; executable iOS verification
remains blocked by the lack of macOS/Xcode.

Feature UI2 close-out evidence: the debug-only UI Showcase opens adult-owned
sharing controls with paused, approximate, precise, timed, and unavailable
synthetic states. Audience, explicit SAST/UTC expiry, consent ownership, and the
administrator limitation are visible; paused mode exposes no location; the
unavailable state disables all modes; and no save action or service exists. Six
JVM tests and nineteen Pixel 8 Android 16 Compose tests passed, including 48 dp
touch targets, large text, disabled controls, privacy wording, and navigation.
Android lint reported no issues; debug, test, and release APKs built; the release
manifest excludes all debug previews; and emulator screenshots were visually
checked. Android viewing instructions and matching SwiftUI design guidance are
documented. Executable iOS verification remains blocked by macOS/Xcode.

Feature UI3 is proposed but not approved. No UI3 implementation may begin until
explicit approval. Executable SwiftUI remains blocked until a macOS/Xcode
environment is available. Infrastructure features remain planned but are not
the current priority.
