# Kinly - Technical Specification

**Version:** 0.1 (proposed baseline)  
**Status:** Draft for architecture review  
**Date:** 4 November 2026

## 1. Architecture drivers

Kinly's hardest requirements are reliable background location, continuing
consent, sensitive-data isolation, battery efficiency, truthful real-time state,
and failure-aware safety alerts. The architecture optimises for those concerns
before feature count or theoretical scale.

The initial system is a modular monolith with native mobile clients. This keeps
transactions and operations understandable for a small team while preserving
module boundaries that can be extracted only when measurement justifies it.

## 2. Approved technology direction

| Area | Choice | Reason |
| --- | --- | --- |
| Android | Kotlin, Jetpack Compose, coroutines/Flow | Direct access to Android location, permission, foreground-service, and battery behaviour |
| iOS | Swift, SwiftUI, structured concurrency, Core Location | Direct support for reduced accuracy, background relaunch, and iOS privacy behaviour |
| Backend | Java, Spring Boot, Gradle | Mature security, validation, transactions, observability, and team-aligned Java stack |
| Database | PostgreSQL with PostGIS, Flyway | Transactional domain data plus indexed geospatial queries and controlled migrations |
| Short-lived state | Redis | Presence, bounded caches, rate limits, and queue/lease coordination where necessary |
| Cloud | AWS managed through Terraform | Repeatable environments, managed data services, keys, secrets, monitoring, and regional choice |
| API | Versioned REST, WebSockets, HTTPS location batches | Simple commands/reads, efficient live fan-out, and resilient mobile ingestion |
| Delivery | Docker, GitHub Actions, Terraform | Reproducible builds and reviewed infrastructure changes |

An operations web console may later use TypeScript and React, but it is not part
of the first walking skeleton. Kotlin Multiplatform, Flutter, React Native,
Kafka, Kubernetes, and microservices are not approved defaults.

### Why native mobile

Background location is not a peripheral feature. Android and iOS use different
permission models, scheduling limits, accuracy states, relaunch behaviour, and
user-visible privacy indicators. Native clients make those constraints explicit
and testable. Shared API contracts and domain language provide consistency;
forcing UI or background services through one cross-platform abstraction does
not.

### Why Java for the backend

Java and Spring Boot support explicit application boundaries, transactional
workflows, security enforcement, PostGIS persistence, mature testing, and
structured operational tooling. Kotlin remains the Android language; using Java
on the server avoids making the production backend depend on mobile-language
convenience and matches the approved team direction.

## 3. System context

```text
Android app ----\
                 \  HTTPS REST / batch upload     +------------------+
iOS app -----------> API / WebSocket gateway ----> | Spring Boot      |
                                                    | modular monolith |
Operations UI ----/                                 +---+----+----+----+
                                                        |    |    |
                                              PostgreSQL   Redis  Outbox worker
                                                + PostGIS          |
                                                                   v
                                                    push / SMS / email / maps
```

- Mobile clients collect platform-authorised samples, buffer them locally, and
  upload idempotent batches.
- The backend is the authority for membership, consent, approximation,
  retention, alert state, and access.
- WebSockets publish authorised state changes but are never the only durable
  record or command path.
- External providers receive the minimum data required through adapters.

## 4. Repository layout

The planned monorepo layout is:

```text
android/                 Kotlin Android application
ios/                     Swift iOS application
backend/                 Java Spring Boot application
  src/main/java/.../
    identity/
    circles/
    consent/
    location/
    places/
    journeys/
    safety/
    notifications/
    devices/
    audit/
infra/                   Terraform modules and environment composition
contracts/               OpenAPI and event schemas; no generated secrets
docs/
  decisions/             ADRs
  threat-models/         feature and system threat models
  runbooks/              operational and incident procedures
dist/                    visual concept only; never a production dependency
```

Each backend module owns its entities, repositories, and migrations by
convention. Other modules use its public application services or durable events,
never its repository.

## 5. Backend modules

- **Identity:** identity-provider subject mapping and account lifecycle.
- **Devices:** app installations, session-visible device metadata, and push
  tokens.
- **Circles:** circles, invitations, membership, roles, and removal.
- **Consent:** sharing grants, precision, purpose, audience, expiry, and
  revocation.
- **Location:** sample validation, ingestion, latest state, approximation,
  history, and retention.
- **Places:** places, geofence evaluation, transitions, and subscriptions.
- **Journeys:** active journey state, audience, destination, ETA metadata, and
  expiry.
- **Safety:** check-ins and SOS incident orchestration.
- **Notifications:** preferences, notification jobs, adapters, attempts, and
  delivery semantics.
- **Audit:** privacy-safe immutable records for sensitive actions and reads.

The Location module may publish `LocationSampleAccepted`; Places and Journeys
consume it through application listeners initially. Events that must survive a
process crash are written to a transactional outbox in the same database
transaction and processed idempotently.

## 6. Data ownership and core model

All identifiers are opaque UUIDs. Persist timestamps as UTC `Instant`; keep a
user's IANA timezone separately.

| Entity | Important fields and rules |
| --- | --- |
| UserAccount | identity subject, status, timezone; no password when using managed identity |
| Device | user, platform, installation ID, push-token reference, last seen, revoked time |
| Circle | owner, name, status, creation/deletion times |
| CircleMembership | circle, user, role, state, joined/removed times; unique active membership |
| Invitation | circle, inviter, hashed token, intended recipient, expiry, used time |
| SharingGrant | subject, circle/audience, precision, purposes, start, expiry, revoked time, version |
| LocationSample | subject/device, client sample ID, device time, received time, geography point, accuracy, motion/source metadata |
| LatestLocation | subject, accepted sample, materialised latest state, sharing version |
| Place | circle, geography centre, radius, creator, status |
| PlaceSubscription | place, subject, recipients, enter/exit preferences, consent reference |
| PlaceTransition | subject/place, type, effective time, confidence, dedupe key |
| Journey | subject, audience, destination, start/expiry/end, state, consent reference |
| CheckIn | sender, recipient(s), message, expiry, state |
| SafetyIncident | initiator, type, start/cancel/end, latest available location reference |
| NotificationJob | event, recipient, channel, preference decision, idempotency key, state |
| AuditEntry | actor, action, target type/ID, circle, purpose, time, outcome; no raw coordinates |
| OutboxEvent | event ID/type/version, aggregate reference, payload, occurred/published times |

### Location storage

- Use PostGIS `geography(Point, 4326)` for precise coordinates.
- Partition location history by time after volume measurements justify the
  operational cost; design queries and retention so partitioning remains easy.
- Keep `latest_location` separate from history for predictable live reads.
- Index subject/time and authorised place queries; never provide unrestricted
  proximity search across users.
- Encryption at rest is necessary but not sufficient. Consider application- or
  column-level encryption only after an ADR covers key rotation, geospatial
  query limitations, incident response, and deletion.

## 7. Authorization and consent

Authentication answers who is calling. Circle membership answers their
relationship. Consent answers whether this subject allows this audience,
precision, purpose, and time. A location response requires all three.

Every sensitive request follows this order:

1. validate the authenticated identity and non-revoked device/session;
2. resolve server-owned circle membership from persistence;
3. resolve the location subject and active sharing grant;
4. verify audience, purpose, precision, and expiry;
5. apply server-side approximation or deny access;
6. audit the sensitive action without recording raw coordinates; and
7. return freshness and uncertainty metadata with the result.

Circle IDs and subject IDs supplied by a client are selectors, never proof of
access. A circle administrator has no implicit override of an adult's grant.

### Server-side approximation

Approximate mode must transform the stored precise point before it leaves the
Location module's authorised query boundary. The algorithm must resist simple
averaging attacks: a stable coarse cell or similarly reviewed approach is
preferred over adding fresh random noise on each request. Exact resolution and
stability require an ADR plus privacy tests.

### Revocation

Revocation increments a sharing version, invalidates bounded caches, closes or
re-authorises affected WebSocket subscriptions, stops new derived alerts, and
prevents history reads. Push messages already accepted by a provider cannot be
recalled, so their payloads contain no precise coordinates.

## 8. Location ingestion and processing

### API shape

`POST /api/v1/location-samples:batch` accepts a bounded batch containing:

- device installation ID;
- unique client sample ID for each sample;
- device timestamp and timezone-independent UTC instant;
- latitude, longitude, horizontal accuracy, and available platform metadata;
- capture mode such as foreground, significant-change, journey, or emergency;
  and
- schema version.

The API validates size, coordinates, timestamps, ownership, device state, and
consent. It returns per-sample accepted, duplicate, rejected, or deferred status.

### Processing rules

- Enforce uniqueness on device plus client sample ID.
- Do not overwrite a newer latest location with an older sample.
- Retain received time separately from device time.
- Flag impossible or suspicious movement for quality handling, not automatic
  accusations about the user.
- Accept safe duplicates idempotently.
- Re-evaluate consent at read time; acceptance does not grant permanent access.
- Publish an accepted event through the outbox for durable downstream work.

### Adaptive collection

Collection policy is platform-specific and remotely bounded, not a fixed
five-second timer. Inputs include active journey/SOS state, motion, distance,
elapsed time, requested precision, battery, low-power mode, connectivity, and
operating-system restrictions. Remote configuration may reduce load but must
never expand collection beyond local permission and active consent.

## 9. Places, journeys, and safety processing

### Places

Evaluate transitions using the point accuracy radius, place radius, previous
stable state, minimum dwell or confirmation, and hysteresis. Store a dedupe key
for the logical transition. Delayed samples may update history but must not
produce misleading current alerts after a configured lateness window.

### Journeys

A journey creates a time-bounded sharing purpose and increased collection mode.
ETA comes from a map-provider adapter and is always an estimate. Ending or
expiring the journey returns collection to the member's ordinary sharing policy.

### SOS incidents

`POST /api/v1/safety-incidents` uses an idempotency key. The transaction stores
the incident and outbox event. Notification orchestration creates one logical
job per recipient/channel, retries only safe failure classes, and records
provider acceptance separately from delivery. The API never returns "help is on
the way" unless a verified responder integration provides that state.

## 10. API and real-time conventions

- Base path `/api/v1`; JSON over TLS.
- OpenAPI is the contract source for REST APIs.
- Use problem-details error responses with stable machine codes.
- Require idempotency keys for invitations, sharing mutations, check-ins, and
  safety incidents where a retry could duplicate an effect.
- Use cursor pagination for history and audit collections.
- Apply explicit request and batch limits.
- WebSocket clients authenticate, subscribe to server-authorised circle views,
  and receive versioned events containing only the authorised precision.
- On reconnect, clients fetch current state through REST; they do not assume all
  transient events were replayed.

## 11. Mobile architecture

Both apps use a presentation/domain/data separation without forcing identical
framework names.

### Android

- Kotlin and Jetpack Compose for UI.
- Coroutines and Flow for asynchronous state.
- Room for encrypted/controlled offline operational data where justified.
- WorkManager for deferrable work and a foreground service only when Android
  policy and user-visible behaviour require it.
- Fused Location Provider behind a small location-source interface.
- Android Keystore for device-held secrets.

### iOS

- Swift and SwiftUI for UI.
- Structured concurrency and observable state appropriate to the supported iOS
  version.
- Core Location behind a location-source protocol.
- Background modes only for behaviours Apple permits and the user enables.
- Keychain for device-held secrets and protected local storage where justified.

### Shared mobile rules

- Explain value before the operating-system permission prompt.
- Handle denial, approximate/reduced accuracy, one-time permission, background
  restriction, reboot, revoked permission, low power, and offline use.
- Encrypt or avoid sensitive queued data; delete acknowledged batches according
  to a bounded policy.
- Never put raw coordinates into analytics, crash reports, screenshots, widgets,
  or notification text.
- Use accessibility semantics and test large text and screen readers.

## 12. External providers

Define backend interfaces for:

- identity verification;
- push notifications (FCM and APNs are platform necessities, wrapped by the
  Notifications module);
- SMS and email;
- maps, routing, and geocoding;
- object storage if attachments are introduced; and
- future verified emergency responders.

Provider selection must consider POPIA operator terms, data location,
subprocessors, retention, pricing, rate limits, delivery semantics, and exit
cost. Provider SDKs must not receive background location unless the approved
feature requires it.

## 13. AWS deployment

The initial production shape is deliberately modest:

- containerised Spring Boot service on ECS Fargate or an ADR-approved equivalent;
- Amazon RDS for PostgreSQL with PostGIS support verified in the chosen engine
  version and region;
- ElastiCache for Redis;
- Application Load Balancer and managed TLS;
- AWS KMS and Secrets Manager;
- CloudWatch logs/metrics/alarms with sensitive-field filtering;
- S3 for approved object storage and Terraform state with locking/versioning;
  and
- separate development, staging, and production accounts or strongly isolated
  environments.

The AWS region is not yet approved. Choose it only after service availability,
POPIA/cross-border transfer, latency, recovery, and cost review. Terraform plans
must be reviewed in CI; production apply and destructive actions require
explicit human approval.

## 14. Security, privacy, and abuse resistance

- Use an approved OIDC/OAuth 2.1 identity provider; do not invent password
  storage as an incidental feature.
- Validate JWT issuer, audience, signature, expiry, and relevant session/device
  state.
- Rate-limit authentication-adjacent, invitation, check-in, location upload,
  export, and safety endpoints.
- Use least-privilege IAM, private database/cache networking, managed keys, and
  dependency/container scanning.
- Establish a sensitive-field denylist for logging and telemetry.
- Make staff access just-in-time and audited.
- Maintain system and feature threat models covering stalking, coercion,
  invitation interception, device theft, account recovery, replay, enumeration,
  scraping, notification abuse, and insider threats.
- Conduct an independent security review before a real-user pilot involving
  continuous location.

## 15. Observability and operations

Use OpenTelemetry-compatible traces/metrics and structured logs with correlation
and event IDs, but never coordinates or tokens.

Minimum operational signals include:

- ingestion accepted/rejected/duplicate/stale counts and latency;
- age distribution of latest locations without user-identifying labels;
- WebSocket connection/fan-out failures;
- outbox backlog and oldest event age;
- place-transition deduplication and notification job age;
- notification attempts by channel/provider outcome;
- SOS creation-to-provider-acceptance latency and unresolved incidents;
- deletion/retention job failures;
- authorization denials and suspicious rate-limit activity; and
- database, Redis, and provider health.

Runbooks are required for lost push capability, delayed outbox, database
restore, compromised credentials, suspected location exposure, provider outage,
and stuck retention/deletion work.

## 16. Testing strategy

- **Unit tests:** domain rules, consent decisions, state machines, approximation,
  dedupe, and lateness logic.
- **Backend integration tests:** Spring security, PostgreSQL/PostGIS, Flyway,
  Redis, outbox, cross-circle denial, and consent denial using Testcontainers.
- **Contract tests:** OpenAPI compatibility and provider-adapter fixtures with
  synthetic coordinates only.
- **Mobile tests:** view models/domain logic, permission changes, lifecycle,
  offline queue, background transitions, accessibility, and platform UI.
- **End-to-end tests:** invitation through sharing and revocation; journey;
  place alert; check-in; SOS provider failure; account deletion.
- **Field tests:** real-device battery, background reliability, accuracy,
  geofence behaviour, low connectivity, reboot, and long-running sessions.
- **Security tests:** abuse cases, authorization matrix, token/session revocation,
  rate limits, log inspection, and dependency scanning.
- **Resilience tests:** delayed/out-of-order samples, duplicate events, Redis
  loss, provider outage, worker restart, and database restoration.

Production coordinates and real family data are prohibited in fixtures and test
artifacts.

## 17. CI/CD and environments

Every pull request should run formatting, static analysis, unit tests, backend
integration tests, migration validation, OpenAPI compatibility checks, secret
scanning, and dependency scanning for affected projects. Mobile builds and
platform tests should run on appropriate hosted runners or approved services.

Deploy to development automatically after main passes. Promote an immutable
artifact to staging, run smoke tests, and require approval for production.
Database migrations are forward-compatible and applied once; never edit an
applied migration. Risky changes use expand/migrate/contract steps.

## 18. Walking-skeleton plan

The first implementation proposal should create only enough to prove the
delivery path:

1. initialise Android, iOS, backend, contracts, and infrastructure projects;
2. establish formatting, test, build, and secret-scanning CI;
3. deploy a health/readiness endpoint and PostgreSQL migration to development;
4. integrate one approved sign-in path on both mobile platforms;
5. call one authenticated `/api/v1/me` endpoint;
6. register a device and synthetic push token in non-production;
7. emit privacy-safe telemetry and verify a basic alert; and
8. document local development and rollback.

It intentionally excludes continuous background location. That work starts only
after circle membership and consent foundations exist and have negative access
tests.

## 19. Architecture decisions still required

Create ADRs before implementation for:

- managed identity provider and sign-in/recovery methods;
- AWS region and recovery-region strategy;
- maps/geocoding/routing provider;
- location-history retention and backup expiry;
- exact server-side approximation algorithm;
- SOS channels, delivery/retry semantics, and emergency copy;
- local mobile database/encryption choices.

ADR 0001 resolves the supported Android/iOS versions and device-test matrix.

## 20. Current status

Phase 1 walking skeleton. Features 0A through 0D, UI1, and UI2 established the backend and
local-services foundation, the Android Ocean Sky shell, and the accepted
supported-platform/device-test decision, plus a debug-only Android UI showcase
and consent-aware synthetic Home and sharing-controls previews. Provider,
retention, identity, region, and emergency decisions remain open. Work follows
`docs/FEATURE_ROADMAP.md`, with no more than one approved feature and one
substep in progress. Feature UI3, a debug-only circle and membership preview,
is proposed but not approved; executable iOS work remains blocked by the
macOS/Xcode environment.
