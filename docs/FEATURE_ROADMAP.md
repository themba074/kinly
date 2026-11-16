# Kinly Feature Roadmap

**Status:** Active delivery plan  
**Last updated:** 15 November 2026
**Current feature:** None; 0G is **Complete**
**Current substep:** None
**Next planned feature:** 1A - identity provider, sign-in, recovery, and key-management boundary
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

1. 0E, 0F, and 0G;
2. Phase 1 through Phase 8 in feature-ID order.

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
| 0E | Confirm the product, adult-only pilot, zero-spend boundary, and future cloud direction. | The working product-name decision, first-pilot audience, provisional quality gates, POPIA/cross-border assumptions, local recovery direction, and zero-cost boundary are recorded and approved before real-user data or any cloud deployment. | **Complete** |
| 0F | Complete the zero-spend local delivery foundation. | OpenAPI/versioning, formatting, secret and dependency scanning, migration checks, privacy-safe observability, health, local recovery, and environment isolation pass locally and through available no-charge CI. No cloud resource, Terraform apply, paid provider, or other billable service is introduced. | **Complete** |
| 0G | Establish an executable native iOS foundation. | A macOS/Xcode build path runs the Swift/SwiftUI Ocean Sky shell, non-sensitive backend status, debug showcase equivalents, accessibility checks, tests, and CI without claiming unimplemented product behaviour. | **Complete** |

#### 0E decision summary

ADR 0002 keeps Kinly as the working name, limits the future first pilot to
consenting South African adults, and sets a ZAR 0 spending ceiling. Development
is local and synthetic-only, with recovery by rebuilding from source,
containers, and migrations. AWS Cape Town is only a future candidate and must
be reassessed through an approved roadmap amendment before any cloud or paid
action. The PRD and technical specification carry the matching boundaries.

#### 0F proposal - local delivery checks only

**Outcome and scope:** Make the existing local backend and Android foundation
repeatably verifiable without deploying anything. Add a minimal valid OpenAPI
v1 contract foundation that describes only implemented behavior, keep API
versioning guidance with that contract, and extend the existing Gradle and
workflow checks for formatting/static analysis, secrets, dependencies,
migrations, health, and privacy-safe logs. Keep local startup and recovery
instructions in the existing READMEs. Existing GitHub Actions may run only when
they are available at no charge; the equivalent local commands remain the
canonical fallback.

Work proceeds sequentially inside this single feature: contract and versioning;
local/CI guardrails; then one close-out verification and documentation pass. It
does not add a product endpoint, identity, permission, map, location, provider,
Terraform, deployment environment, new programming language, or real data.

**Acceptance and negative checks:** The OpenAPI document validates and contains
no invented product operation. Backend formatting/static checks, targeted unit
and PostGIS/Redis/Flyway integration tests, Android unit/lint/build checks,
secret-pattern checks, and a no-charge dependency-vulnerability check have
documented local commands and pass once in the final affected scope. Health and
correlation behavior stays non-sensitive, logs contain no secret, token,
request body, or coordinate, and local services remain bound to loopback with
development-only credentials. No cloud resource, hosted deployment, paid
minute, trial, credential, real account, production coordinate, application
permission, or feature simulation is introduced.

**Design, alternatives, and rollback:** Reuse Gradle, Docker Compose, the two
existing workflows, and existing READMEs; pin any no-charge third-party CI
action to an immutable revision and provide a local alternative. A paid quality
platform, Terraform plan/apply, and a new umbrella build system are rejected as
costly or unnecessary. Duplicated policy documents and broad test reruns are
also excluded. Repository-only changes are reversible by reverting the affected
contract, build, workflow, and README edits; there is no external state to
recover.

**Effects and sanity-check:** Privacy and security improve through contract,
secret, dependency, and safe-log checks. Reliability improves only for local
build and migration repeatability; cloud latency, backups, recovery, and iOS
remain unverified. There is no mobile permission, battery, consent,
accessibility, or runtime product behavior change. Cost remains ZAR 0. Approval
should confirm that an empty or foundation-only v1 contract is preferable to
inventing future endpoints, and that hosted CI must be skipped whenever it is
not demonstrably free.

**Close-out:** The validated `contracts/openapi.json` foundation has an
intentionally empty product path set, a local `/api/v1` base, and explicit
additive/breaking-change rules. Resolved dependencies are locked, the shipped
dependency set has no OSV findings, repository/secret/safe-log/isolation checks
pass, and public-only CI uses immutable action and scanner revisions. Local
recovery is documented without adding a hosted environment or backup.

#### 0G proposal - native iOS foundation only

**Outcome and scope:** Add a checked-in native Swift/SwiftUI app under `ios/`
with an iOS 17 deployment target. It will mirror the existing foundation
boundary: an Ocean Sky shell, a truthful non-sensitive development
backend status on Home, unavailable copy for unfinished destinations, and
debug-only synthetic showcase equivalents. It adds no account, permission,
location, map, notification, background mode, provider, storage, real data, or
safety action.

The approved 15 November 2026 design amendment changes the future primary
navigation to Home, Safety, History, and Settings, with Places opened from a
labelled Home action. The already verified five-tab shell remains historical
0G evidence. Its four-tab navigation refactor is approved as a design and
roadmap amendment but has not started and is not permission to add a map or
product action inside 0G.

Work remains sequential inside 0G: first the Xcode project, shell, and backend
status; then debug-only showcase equivalents; then accessibility, tests, CI,
and close-out. Only one substep may be active. Approval of 0G does not approve
later identity or location features.

**Acceptance and negative checks:** A standard public GitHub-hosted macOS
runner builds Debug and Release configurations and runs unit and current
simulator UI tests using stable installed Xcode. The project declares iOS 17;
the iOS 17 simulator boundary must be executed when the runner provides it or
remain an explicit blocker rather than an inferred pass. Tests cover checking,
available, malformed/error, unavailable, and retry status; primary-navigation
and unavailable-state truthfulness; debug-showcase release exclusion; VoiceOver
labels/values, Dynamic Type reflow, and 44 pt controls. Release networking has
no cleartext exception, logs contain no response bodies or sensitive values,
and no sensitive entitlement or permission string exists.

**Design, alternatives, and rollback:** Use SwiftUI `TabView`, structured
concurrency, an observable main-actor status model, and a small protocol-backed
`URLSession` client so network behavior is replaceable in tests. Debug launch
configuration may select synthetic showcase states; Release must compile them
out. The workflow uses only a standard macOS runner and skips private
repositories, preserving the ZAR 0 boundary. A cross-platform framework, paid
Mac service, copied Android layout, generated live-feature simulation, and
unverified hand-authored release are rejected. Rollback is repository-only.

**Effects and sanity-check:** Privacy and consent do not change because the
status endpoint is operational and no personal data exists. Security is limited
to a debug-only local-network exception; battery impact is one foreground
status request with a short timeout. Reliability gains explicit loading,
failure, and retry states; accessibility is part of each substep. The team
should confirm that public standard macOS CI is acceptable as the executable
Xcode path and that CI-first development is acceptable while interactive iOS
viewing remains unavailable on this Windows machine. Approval confirmed both
points on 15 November 2026.

**Progress:** Approved on 15 November 2026. The iOS 17 Xcode project, original
five-tab Ocean Sky shell, and backend-status substep is complete. Xcode 26.6
built the Debug test host and Release app on a public standard macOS runner;
all seven parser/model tests passed on an iPhone 17 Pro simulator. A
documentation-only amendment subsequently approved the four-tab map-first
direction and nested Places navigation. The debug showcase equivalents and
navigation refinement were complete before the final verification below. No
map was started.

**Completed debug-showcase/navigation substep:** Approved, started, and
completed on 15 November 2026. The primary shell now has Home, Safety, History,
and Settings tabs, with a labelled Home control opening the unavailable Places
screen. Debug-only SwiftUI states cover Home, sharing, circles, Places, care and
Safety, and history/privacy without live product behaviour. Thirteen unit/UI
tests passed on public macOS CI, including all showcase routes, nested Places,
privacy-critical copy, and accessibility text size. Debug and Release builds
passed, and the Release-binary guard found no synthetic-showcase copy. The
repository guardrails also passed. The runner verified the iOS 17 deployment
target but used its current available simulator; execution on an iOS 17
simulator remains explicit final-close-out evidence when available. No map,
coordinate, permission, account, storage, notification, product mutation, or
emergency action was added. Evidence: GitHub Actions run `43712102836`.

**Final substep record:** Final accessibility/UI verification and 0G close-out
were approved and started on 15 November 2026. The scope is limited to
executable accessibility, target-size, Dynamic Type, orientation, navigation,
synthetic screenshot, Release-boundary, CI, and documentation evidence. It
adds no product behaviour or sensitive permission. Feature 0G may close only
if every gate passes; unavailable iOS 17 simulator execution remains an
explicit blocker rather than an inferred pass.

**Verification progress and blocker:** Public GitHub Actions run `43737492458`
passed the expanded Swift 6 verification job on `macos-26`. All thirteen
unit/UI test methods passed, including accessibility-tree labels and values,
focus-order geometry, 44-point controls, accessibility XXXL reflow,
portrait/landscape rotation, all four primary destinations, nested Places,
and every synthetic showcase topic. Fourteen retained screenshots are stored
in `.xcresult` artifact `14171007875`. Debug and Release builds passed, and
automated guards found no showcase copy in Release, Release cleartext
exception, location permission string, entitlement file, or unreviewed app
logging call. Repository guardrails passed in run `43737492482`.

The initial iOS 17.5 boundary job was cancelled before setup with no runner
allocated because GitHub has retired the standard `macos-14` label that
provided Xcode 15.4 and iOS 17.5. The unallocatable job was removed rather than
leave CI permanently unhealthy. Run `43737492458`, job `132883205742`,
records the cancellation. This was superseded by the following verification.

**0G close-out:** The approved final substep completed on 15 November 2026.
Apple's official component command installed iOS 17.5 on a public `macos-15`
runner after Xcode first-launch and CoreSimulator initialization. A Swift 6
main-actor correction to the UI test methods allowed the same thirteen test
methods to pass on the actual iOS 17.5 simulator and on the current `macos-26`
simulator. Both jobs and their `.xcresult` uploads passed in GitHub Actions run
`43741876218`; repository guardrails passed in run `43741876184`. The current
job also passed Debug/Release builds and the Release privacy/isolation guards.
No product data, location permission, map, notification, or emergency action
was added. Manual VoiceOver speech and visual review on a physical device or
interactive Mac remain unverified; automated tests cover accessibility labels,
values, target geometry, large text, rotation, and retained screenshots.

### Native mobile UI exploration

| ID | Feature outcome | Completion gate | Status |
|---|---|---|---|
| UI1 | Preview a truthful consent-aware Home experience before circle and location services exist. | Debug-only Compose states cover no circle, paused, approximate, fresh, stale/offline, and unavailable behaviour; accessibility tests, release exclusion, APK/screenshots, viewing guidance, and matching iOS guidance pass. No map, permission, real data, or product action exists. | **Complete** |
| UI2 | Preview adult-owned sharing controls before consent services exist. | Debug-only Compose states cover paused, approximate, precise, timed, and unavailable behaviour; audience, expiry, consent ownership, accessibility, release exclusion, APK/screenshots, viewing guidance, and matching iOS guidance pass. No setting is saved and no permission, service, real data, or product action exists. | **Complete** |
| UI3 | Preview a private circle, adult membership roles, and invitation states before Circle services exist. | Debug-only Compose states cover no circle, circle overview, pending and expired invitations, and unavailable behaviour; role clarity, consent separation, accessibility, release exclusion, APK/screenshots, viewing guidance, and matching iOS guidance pass. No circle or membership mutation exists. | **Complete** |
| UI4 | Complete the remaining primary-tab experience blueprint without pretending the underlying product features exist. | Sequential debug-only substeps cover Places; care and Safety states for check-ins and journeys with honest emergency limitations; and History/privacy states. Empty, populated, loading, offline/stale, unavailable, and relevant expired/failed states use synthetic data. Accessibility, release exclusion, APK/screenshots, viewing guidance, and matching iOS guidance pass. No map, coordinate, permission, notification, provider, backend, saved mutation, or emergency dispatch exists. | **Complete** |

UI4 is one grouped feature, not permission to implement every screen at once.
Its substeps must run sequentially: **UI4.1 Places**, **UI4.2 care and Safety**,
then **UI4.3 History and privacy**. Each substep needs an approved proposal
within the approved UI4 boundary, a small implementation, tests, and review
before the next substep starts. Exact SOS recipients and delivery claims remain
reserved for 6A; the preview may show only truthful unconfigured or unavailable
states.

Completed UI4 substeps:

- **UI4.1 Places:** synthetic loading, empty, overview, uncertain, offline, and
  unavailable states; no map, address, coordinate, alert claim, or action.
- **UI4.2 Care and Safety:** truthful synthetic check-in and journey states;
  emergency tools explicitly unconfigured; no live location, action, call,
  notification, help, or dispatch claim.

**UI4.3 completed:** a debug-only History-tab preview provides
loading, no-history, overview, privacy/access, offline, and unavailable states.
Synthetic history may show dates, gaps, precision, and freshness in text, but
never a map, route, address, coordinate, or real movement data. Approximate
history stays approximate. Privacy examples distinguish own history, no grant,
and revoked access; membership or an administrator role never grants history
access.

The retention default remains undecided, so the preview states that no policy
is configured and shows no invented duration. Export and deletion are described
as future workflows, with no button or request simulation. There is no storage,
backend, permission, network, provider, save, export, deletion, retry, or other
product action. Verification covered truthful empty/offline/unavailable and
revoked states, negative data/action assertions, accessibility, 48 dp targets,
large text, release exclusion, APKs, screenshots, and matching iOS guidance.
Map timelines and interactive export/deletion controls remain excluded because
they would imply unapproved providers, retention, and backend behaviour.

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
| 3F | Receive live authorised member updates across all of the viewer's circles. | Home defaults to All circles, returns each authorised person once across overlapping memberships, supports optional circle filtering, and passes WebSocket authorization, audience isolation, reconnect, revoke, order, backpressure, stale/offline fallback, accessibility, client lifecycle, and safe-log tests. | Planned |
| 3G | Collect background location on Android using an approved adaptive policy. | Proposal is approved first; physical-device tests cover foreground service, Doze, reboot, restriction, low power, offline, revoke, kill switch, and measured battery/data use. | Planned |
| 3H | Collect background location on iOS using an approved adaptive policy. | Proposal is approved first; physical-device tests cover authorization changes, relaunch, reduced accuracy, low power, offline, revoke, kill switch, and measured battery/data use. | Planned |

#### 3F scope amendment - all-circles Home map

**Approval boundary:** The user approved this roadmap scope amendment and the
related Ocean Sky design-baseline amendment on 15 November 2026. Feature 3F
remains **Planned**. This approval does not start 3F, bypass 0G or Features 1A
through 3E, select a map provider, or approve implementation when 3F is reached.

**Outcome and scope:** Home will default to an **All circles** map showing every
unique person whose current location the viewer is authorised to access through
at least one active shared circle. An optional circle filter narrows the view.
Overlapping membership produces one person and one current marker, with only
the shared-circle context the viewer may know. Places is no longer a primary
tab; a labelled Places action on Home opens the Places screen and back
navigation returns to the prior Home state.

The all-circles view does not combine consent grants, expose unauthorised
members, or silently target all circles. Check-ins, places, journeys, and
safety actions require their own explicit audience. Paused or revoked sharing
does not leave a live marker or silently substitute a last-known position.

**Acceptance and negative checks:** Backend and both native clients cover a
single authorised person, multiple circles, overlapping membership, one-circle
grants, server-side approximate sharing, precise sharing, stale and offline
state, delayed and out-of-order updates, and optional filtering. Tests prove
deduplication by subject, cross-circle and non-member denial, subject-consent
denial, immediate membership removal, pause, expiry, revocation, cache and
WebSocket re-authorization, and no expansion from client-supplied circle or
member identifiers. Map bounds, clustering, cached UI, logs, analytics,
screenshots, and accessibility text must not leak a hidden position. A
screen-reader-accessible non-map member list, large text, non-colour state
labels, and native back behaviour are required.

**Design and alternatives:** The server derives the authorised membership set,
authorises each subject and purpose, applies precision, and deduplicates before
returning the view. A selected-circle-only map was rejected because it requires
repeated switching. An unfiltered combined map was rejected because it obscures
context and audience. A separate all-circles destination was rejected because
it duplicates Home and adds navigation clutter. The approved design uses one
map-first Home, an All circles default, optional filters, four primary tabs,
and nested Places navigation.

**Effects and sanity-check:** The broader display increases the sensitivity of
one screen and the impact of an authorization or cache defect, so negative
access tests, bounded responses, immediate revocation, privacy-safe telemetry,
and a threat-model update are mandatory. It does not justify faster location
collection or more polling; battery and data policy remain adaptive. Marker
density, clustering without leakage, map-provider cost, South African coverage,
and degraded/offline behaviour must be validated after 3C selects the provider.
No cloud resource or spend is approved.

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

The production boundary is still the Java/Spring Boot and local-services
foundation plus the native Android readiness shell. It has no authentication,
sensitive permission, member data, map, push, location, or safety behaviour.

| Feature | Close-out evidence |
|---|---|
| 0A | Backend tests and CI verified PostGIS/Redis integration, Flyway, restricted health probes, and correlation IDs. |
| 0B | Six JVM and three Pixel 8/API 36 Compose tests passed; lint and APK builds passed; local readiness was verified. |
| 0C | ADR 0001 accepted Android API 26+, API 36 compile/target, iOS 17+, and the physical-device matrix. |
| 0D | Six JVM and five cumulative Compose tests passed; showcase viewing, release exclusion, APKs, lint, and iOS guidance verified. |
| 0E | ADR 0002 records the working name, adult-only South African pilot, synthetic local recovery, ZAR 0 ceiling, and conditional future cloud direction; the PRD, technical specification, roadmap, README, and agent boundary agree. No cloud or billable action occurred. |
| 0F | OpenAPI/versioning, locked dependencies, repository and OSV scans, backend tests, Android release assembly, Compose isolation, recovery guidance, and public-only CI guardrails were verified. No cloud resource, paid service, trial, real data, or product behavior was added. |
| UI1 | Six JVM and eleven cumulative Compose tests passed; Home privacy states, large text, screenshots, release exclusion, and guidance verified. |
| UI2 | Six JVM and nineteen cumulative Compose tests passed; sharing consent, touch targets, large text, screenshots, release exclusion, and guidance verified. |
| UI3 | Six JVM and twenty-seven cumulative Compose tests passed; roles, invitations, consent separation, accessibility, screenshots, and release exclusion verified. |
| UI4.1 | Ten new and thirty-seven cumulative Compose tests passed; Places states, negative claims, accessibility, screenshots, builds, lint, and release exclusion verified. |
| UI4.2 | Eleven new and forty-eight cumulative Compose tests passed; Care and Safety truthfulness, accessibility, screenshots, builds, lint, release exclusion, and CI verified. |
| UI4.3 | Twelve targeted Compose tests passed; History/privacy truthfulness, grant and revocation boundaries, accessibility, all six screenshots, builds, lint, and release exclusion verified. |

Features 0A through 0G and UI1 through UI4 are **Complete**. No feature or
substep is active. Feature 1A is next and requires its own proposal and explicit
approval. Interactive VoiceOver and physical-device review remain future
verification work. No cloud resource or real-user data is authorised.
