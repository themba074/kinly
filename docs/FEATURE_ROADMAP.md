# Kinly Feature Roadmap

**Status:** Active delivery plan  
**Last updated:** 12 November 2026
**Current feature:** UI4 - remaining primary-tab experience preview (In progress)
**Current substep:** None - UI4.1 Places is complete; UI4.2 requires its own
proposal and explicit approval
**Next planned feature:** UI4 - remaining primary-tab experience preview
**Rule:** One feature, and one substep within that feature, may be in progress.

This is Kinly's single delivery-planning document. The PRD defines what the
product must do, the technical specification defines how the system is shaped,
and `AGENTS.md` defines how contributors work.

## 1. How features move through the roadmap

Each feature is proposed, explicitly approved, implemented, verified, and then
marked **Complete** before the next feature begins. Approval does not carry
forward.

The roadmap is authoritative. An agent must not invent, propose for
implementation, or start an unlisted feature ID. Adding, removing, renumbering,
reordering, splitting, or combining a feature requires an explicit roadmap
amendment approved by the user. A request such as “next feature” means the
single **Next planned feature** named here; it does not authorise a new feature
or implementation. After that feature is complete, the next entry follows the
execution order below unless the user explicitly amends the roadmap.

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

### 1.1 Execution order

The remaining default order is:

1. UI4;
2. 0E, 0F, and 0G;
3. Phase 1 through Phase 8 in feature-ID order.

No later feature may bypass an earlier incomplete feature merely because it is
easier or more visually interesting. If an earlier feature is genuinely
blocked, the user must approve its **Blocked** or **Deferred** state and name the
dependency-safe feature that becomes next. Executable iOS work remains a known
blocker until macOS/Xcode is available, but it must not be silently skipped.

## 2. Roadmap

### Phase 0 - Delivery foundations

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 0A | Start the Java backend and local PostGIS/Redis foundation. | Backend tests, disposable database/cache integration tests, restricted health probes, correlation IDs, migration, and CI pass. | **Complete** |
| 0B | Open the Android Ocean Sky shell and show truthful, non-sensitive backend availability. | Compose UI, offline/error/retry, accessibility, local health request, Android lint/test/build, and CI pass. No authentication, maps, push, or sensitive permission exists. | **Complete** |
| 0C | Record supported Android/iOS versions and the physical-device test matrix. | ADR is approved before platform-specific background behaviour begins. | **Complete** |
| 0D | View approved native Ocean Sky components and synthetic states without implying unfinished product behaviour. | Debug-only Android showcase, installable APK, accessibility tests, viewing instructions, and matching iOS component guidance pass. No product action, permission, or sensitive data is introduced. | **Complete** |
| 0E | Confirm the product and adult-only pilot guardrails, development AWS region, and recovery direction. | The working product-name decision, first-pilot audience, provisional quality gates, POPIA/cross-border assumptions, regional service availability, latency, recovery, and cost are recorded and approved before real-user data or cloud deployment. | Planned |
| 0F | Complete the development delivery foundation. | Reviewed Terraform provisions only the approved development resources; OpenAPI/versioning, formatting, secret and dependency scanning, migration checks, privacy-safe observability, health, rollback, environment isolation, and cost controls pass in CI. | Planned |
| 0G | Establish an executable native iOS foundation. | A macOS/Xcode build path runs the Swift/SwiftUI Ocean Sky shell, non-sensitive backend status, debug showcase equivalents, accessibility checks, tests, and CI without claiming unimplemented product behaviour. | Planned |

### Native mobile UI exploration

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| UI1 | Preview a truthful consent-aware Home experience before circle and location services exist. | Debug-only Compose states cover no circle, paused, approximate, fresh, stale/offline, and unavailable behaviour; accessibility tests, release exclusion, APK/screenshots, viewing guidance, and matching iOS guidance pass. No map, permission, real data, or product action exists. | **Complete** |
| UI2 | Preview adult-owned sharing controls before consent services exist. | Debug-only Compose states cover paused, approximate, precise, timed, and unavailable behaviour; audience, expiry, consent ownership, accessibility, release exclusion, APK/screenshots, viewing guidance, and matching iOS guidance pass. No setting is saved and no permission, service, real data, or product action exists. | **Complete** |
| UI3 | Preview a private circle, adult membership roles, and invitation states before Circle services exist. | Debug-only Compose states cover no circle, circle overview, pending and expired invitations, and unavailable behaviour; role clarity, consent separation, accessibility, release exclusion, APK/screenshots, viewing guidance, and matching iOS guidance pass. No circle or membership mutation exists. | **Complete** |
| UI4 | Complete the remaining primary-tab experience blueprint without pretending the underlying product features exist. | Sequential debug-only substeps cover Places; care and Safety states for check-ins and journeys with honest emergency limitations; and History/privacy states. Empty, populated, loading, offline/stale, unavailable, and relevant expired/failed states use synthetic data. Accessibility, release exclusion, APK/screenshots, viewing guidance, and matching iOS guidance pass. No map, coordinate, permission, notification, provider, backend, saved mutation, or emergency dispatch exists. | **In progress** |

UI4 is one grouped feature, not permission to implement every screen at once.
Its substeps must run sequentially: **UI4.1 Places**, **UI4.2 care and Safety**,
then **UI4.3 History and privacy**. Each substep needs an approved proposal
within the approved UI4 boundary, a small implementation, tests, and review
before the next substep starts. Exact SOS recipients and delivery claims remain
reserved for 6A; the preview may show only truthful unconfigured or unavailable
states.

UI4.1 completed a debug-only Places preview with loading, no-places,
overview/detail, uncertain-or-delayed, offline, and unavailable states.
Synthetic place cards
may show a name, radius, circle, and plain-language alert summary, but never a
map, centre coordinate, real address, or recipient data. The hierarchy must
state that each adult controls whether their location may power a place alert;
uncertain input must never be presented as a confirmed arrival or departure.
There is no create, edit, delete, save, retry, permission, or notification
action. UI4.2 and UI4.3 remain out of scope until the next substep is proposed
and approved.

### Phase 1 - Identity and devices

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 1A | Choose the identity provider, sign-in methods, recovery, and key-management boundary. | ADR and identity threat model are approved. | Planned |
| 1B | Register or sign in and retrieve the authenticated user's minimal profile. | Approved email/phone ownership verification, backend token validation, `/api/v1/me`, Android and iOS onboarding/sign-in, cancellation, expiry, recovery, secure storage, safe logs, rate limits, and invalid-token tests pass. | Planned |
| 1C | List and revoke a user's sessions/devices and manage push tokens. | Device registration is idempotent; the owner can list bounded non-sensitive device/session metadata and revoke access; token rotation, removal, ownership denial, cache invalidation, logout cleanup, and sensitive-log tests pass. No notification is sent yet. | Planned |

### Phase 2 - Circles and consent

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 2A | Create, rename, list, select, leave, and delete a private circle. | Circle-scoped backend and mobile states pass empty/error, ownership, last-owner, leave/delete confirmation, audit, authorization, and cross-circle denial tests. Destructive states are recoverable where policy allows, and no location data is shown. | Planned |
| 2B | Invite a member and manage membership. | Create, cancel, expire, accept, leave, remove, and administrator-transfer flows pass replay, last-admin, immediate access-loss, cache, and audit tests. | Planned |
| 2C | Give each adult member visible sharing controls. | Grant, pause, resume, precise/approximate, expiry, audience visibility, and mobile accessibility pass; only the subject can change their grant. No location is collected yet. | Planned |
| 2D | Harden consent and membership revocation. | Concurrent read/revoke, removed-member, cross-circle, expired-grant, cache invalidation, and abuse-case tests prove future access stops immediately. | Planned |

### Phase 3 - Location foundation

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 3A | Approve the server-side approximation algorithm. | ADR covers radius/transform, stability, boundary leakage, cache rules, and test vectors. | Planned |
| 3B | Approve location-history retention and backup expiry. | ADR receives product, legal, privacy, security, and operations approval before real location is stored; it covers default duration, latest-location handling, derived data, caches, exports, deletion, legal holds, and backup expiry. | Planned |
| 3C | Choose the maps, geocoding, and routing provider boundary. | ADR covers privacy, South African coverage, data location, SDK/server data flow, rate limits, cost, offline/degraded behaviour, replacement, and test strategy before map-dependent production UI begins. | Planned |
| 3D | Share and view one authorised foreground location. | Validated, idempotent, order-safe ingestion; authorised latest read; approved retention and server approximation; Android/iOS contextual permission and map/card states; freshness and uncertainty; and cross-circle/consent denial tests pass. | Planned |
| 3E | Buffer and ingest location while temporarily offline. | Batch limits, local encryption decision, retry, duplicate, order, invalid sample, consent change, reboot, logout deletion, and retention tests pass on the backend and both clients. | Planned |
| 3F | Receive live latest-location updates. | WebSocket subscription authorization, reconnect, revoke, order, backpressure, stale fallback, client lifecycle, and safe-log tests pass. | Planned |
| 3G | Collect background location on Android using an approved adaptive policy. | Proposal is approved first; physical-device tests cover foreground service, Doze, reboot, restriction, low power, offline, revoke, kill switch, and measured battery/data use. | Planned |
| 3H | Collect background location on iOS using an approved adaptive policy. | Proposal is approved first; physical-device tests cover authorization changes, relaunch, reduced accuracy, low power, offline, revoke, kill switch, and measured battery/data use. | Planned |

### Phase 4 - Notifications

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 4A | Choose push, SMS, and email provider boundaries. | ADR covers privacy, region, reliability, rate limits, cost, failure, and replacement. | Planned |
| 4B | Deliver non-sensitive notifications according to user preferences. | Transactional outbox, retry, idempotency, provider failure, token rotation, Android/iOS receipt, permission denial, foreground/background, quiet hours, timezone, and truthful delivery-state tests pass. | Planned |

### Phase 5 - Everyday care features

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 5A | Create places and receive arrival/departure alerts. | Circle-scoped place management, mobile UI, entry/exit hysteresis, uncertainty, delayed/out-of-order samples, subscriptions, deduplication, preferences, and provider-failure tests pass. | Planned |
| 5B | Request and respond to a check-in. | Selected audience, expiry, “I am okay”, “Call me”, dismiss/decline, mute/block/report, optional explicitly consented location, replay, rate limits, late response, offline/error, notification, and accessibility tests pass. Non-response is never labelled as danger. | Planned |
| 5C | Start, view, and finish a time-limited journey. | Audience, destination, expiry, cancellation, revocation, ETA/fallback, arrival/overdue event, background behaviour, offline state, cross-circle denial, and mobile accessibility tests pass. | Planned |

### Phase 6 - SOS and safety hardening

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 6A | Define exact SOS recipients, channels, copy, cancellation, and retry semantics. | ADR, threat model, abuse review, and product/legal approval are complete. It never implies unimplemented dispatch. | Planned |
| 6B | Trigger, notify, display, and cancel an SOS incident. | State machine, hold-to-activate, accidental trigger, cancellation, offline, low power, authorization, idempotency, partial delivery, retries, audit, Android/iOS lifecycle, and accessibility tests pass. | Planned |
| 6C | Prove SOS degrades safely. | Provider-failure drill, monitoring, user-visible delivery truth, recovery, stolen-device, malicious-admin, spam, replay, and support runbook are verified. | Planned |

### Phase 7 - History and privacy operations

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 7A | Retain, expire, and display authorised location history under the approved 3B policy. | Automatic expiry, own-history read, explicitly granted/revoked sharing, pagination, timezone, gaps, precision, cache invalidation, cross-circle denial, Android/iOS UI, and accessibility tests pass. | Planned |
| 7B | Export or delete a user's personal data through a verifiable workflow. | Re-authentication, encrypted expiring export, deletion grace/cancel policy, cascade/anonymisation, cache/provider cleanup, audit, failure recovery, and backup-expiry evidence pass. | Planned |
| 7C | Audit sensitive reads and tightly control support access. | Actor, subject, reason, retention, alerts, tamper resistance, approval, and no-sensitive-payload tests pass. | Planned |

### Phase 8 - Pilot readiness

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| 8A | Complete cross-platform accessibility and localisation-readiness validation. | TalkBack, VoiceOver, font scaling, contrast, touch targets, motion, keyboard/control, timezone presentation, externalised copy, and layout-expansion tests meet approved gates; initial language remains English until user research prioritises South African languages. | Planned |
| 8B | Complete battery, data, location, and place-alert field benchmarks. | Representative devices, modes, signal conditions, offline periods, freshness, uncertainty, and false-alert rates meet approved budgets. | Planned |
| 8C | Complete operational, security, privacy, and legal readiness. | Restore/recovery drill, independent assessment, blocker remediation, POPIA data map/notices/subprocessors, retention, emergency copy, monitoring, rollback, and incident response are approved. | Planned |
| 8D | Complete signed mobile distribution and store-readiness controls. | Reproducible signed Android/iOS release builds, protected credentials, privacy manifests and store disclosures, safe crash/analytics configuration, versioning, staged rollout, rollback, and internal distribution pass without debug previews or test data. | Planned |
| 8E | Release to a small adult-only pilot. | Support, feedback, consent education, success metrics, incident ownership, rollback, and explicit product, engineering, privacy, security, legal, and operations go/no-go approval are ready. | Planned |

## 3. MVP coverage check

This table traces every included PRD capability to the delivery plan. It is a
coverage check, not an alternate execution order.

| PRD capability | Roadmap coverage |
|---|---|
| Product/pilot guardrails, cloud, delivery automation, contracts, observability, and native platform foundations | 0A-0G |
| Coherent Ocean Sky experience across Home, Places, Safety, History, and Settings | UI1-UI4 |
| Managed identity, verification, recovery, profiles, sessions, devices, and push-token ownership | 1A-1C |
| Circle lifecycle, invitations, adult roles, consent, sharing modes, and immediate revocation | 2A-2D |
| Approximation, retention decision, maps/provider boundary, foreground/offline/live/background location | 3A-3H |
| Notification providers, preferences, truthful delivery states, retry, and quiet-hour behaviour | 4A-4B |
| Places/arrival-departure alerts, abuse-resistant check-ins, and time-limited journeys | 5A-5C |
| Honest circle-notification SOS, cancellation, failure handling, abuse review, and runbooks | 6A-6C |
| History expiry and authorised display, export/deletion, sensitive-read audit, and support access | 3B and 7A-7C |
| Accessibility, localisation readiness, device/battery/location benchmarks, security/privacy/legal operations, signed distribution, and adult-only pilot | 8A-8E |

## 4. Deferred work

These items require new product approval and, where applicable, an ADR. They
must not be added to a nearby feature as “small extras”:

- child or dependent accounts and guardianship;
- crash detection or driving behaviour scoring;
- professional emergency dispatch, roadside assistance, or automatic calling;
- wearables, vehicle, smart-home, or partner integrations;
- paid tiers, subscriptions, or billing;
- machine-learning risk prediction; and
- advertising, sale, model training, or third-party use of precise location.

## 5. Current boundary and completed foundation

Features 0A through 0D and UI1 through UI3 are complete and verified. The foundation now includes the
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

Feature UI3 close-out evidence: the debug-only UI Showcase opens a synthetic,
read-only circle and adult-membership preview with no-circle, overview, and
unavailable states. The overview distinguishes Owner, Administrator, and Member
roles; separates active membership from location-sharing consent; and shows
pending and expired invitation examples without tokens or actions. Six JVM
tests and twenty-seven Pixel 8 Android 16 Compose tests passed, including 48 dp
touch targets, large text, consent wording, invitation states, unavailable
behaviour, and showcase navigation. Android lint reported no issues; debug,
test, and release APKs built; the release manifest excludes all debug previews;
and emulator screenshots were visually checked. Android viewing instructions
and matching SwiftUI design guidance are documented. Executable iOS
verification remains blocked by macOS/Xcode.

UI4.1 close-out evidence: the debug-only UI Showcase opens a synthetic Places
preview with loading, no-places, overview/detail, uncertain/delayed, offline,
and unavailable states. It shows synthetic place names and radii, makes adult
place-alert consent explicit, never converts uncertainty into a confirmed
arrival, and exposes no map, address, coordinate, product action, permission,
network, or notification behaviour. Six JVM tests and thirty-seven Pixel 8
Android 16 Compose tests passed, including ten UI4.1 tests for truthful state
copy, negative action/data assertions, 48 dp touch targets, large text, and
showcase navigation. Android lint reported no issues; debug, test, and release
APKs built; the release manifest excludes every debug preview; and emulator
screenshots were visually checked for all states and the full place-card list.
Android viewing instructions and matching SwiftUI design guidance are
documented. Executable iOS verification remains blocked by macOS/Xcode.

UI4 remains **In progress**, but no substep is currently active. UI4.1 is
complete. UI4.2 is the next planned substep but is not proposed or approved;
UI4.3 also remains unapproved. After UI4 is complete, work returns to 0E and
then follows the execution order in section 1.1.
