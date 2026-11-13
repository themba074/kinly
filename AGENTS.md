# Kinly - Agent Context and Working Agreement

## Project

Kinly is a native mobile family-safety application built around consent-first
location sharing, private circles, place alerts, check-ins, journeys, and
carefully designed emergency escalation. The product must feel like care, not
surveillance.

Read `docs/PRD.md` and `docs/TECH_SPEC.md` before proposing or implementing a
feature. Those documents are the product and architecture baselines. This file
summarizes how an agent must work; it does not replace either specification.

## Approved Direction

- Android: Kotlin + Jetpack Compose
- iOS: Swift + SwiftUI
- Backend: Java + Spring Boot + Gradle
- Database: PostgreSQL + PostGIS, with Flyway migrations
- Cache/short-lived state: Redis
- Cloud: AWS through Terraform
- Architecture: modular monolith first, with durable events and explicit module
  boundaries
- APIs: versioned REST for commands and ordinary reads; WebSockets for live
  member updates; HTTPS batch ingestion for location samples
- Visual direction: Ocean Sky. Use `docs/DESIGN_SYSTEM.md` and the canonical
  `docs/mockups/kinly-selected-ui.png` concept as the design baseline.

Do not introduce a new language, framework, cloud, database, message broker, or
cross-platform mobile framework without an approved architecture decision
record (ADR).

Do not replace the Ocean Sky design direction or its core semantic color roles
without explicit approval. Platform-native differences are expected, but they
must preserve the shared hierarchy, accessibility, and interaction meaning.

## How We Want You to Work

The team is learning while building; the agent is a collaborator, not an
unattended code generator. For every non-trivial task:

1. Read the relevant product and technical sections and inspect the existing
   implementation and tests.
2. Restate the user outcome, scope, assumptions, and acceptance criteria in
   plain language.
3. Explain the proposed design and why it fits the existing architecture.
4. Present meaningful alternatives and the trade-offs that caused you to reject
   them.
5. Identify privacy, consent, security, battery, reliability, and cost effects.
6. Call out decisions the team should sanity-check.
7. **Wait for explicit approval before writing non-trivial implementation
   code.** Documentation work, investigation, and small mechanical changes may
   proceed when directly requested.
8. Implement the smallest end-to-end slice that satisfies the approved
   acceptance criteria.
9. Test it at the appropriate layers and report exactly what was and was not
   verified.
10. Update relevant documentation and the project status in the same change.

Examples of non-trivial work include a new feature, schema change, permission
change, background-location behavior, notification behavior, provider
integration, retention change, or architectural refactor.

## Product Invariants

These are stronger than ordinary feature requirements:

- Every location access requires an authenticated user, active circle
  membership, and an explicit sharing grant.
- Circle administrators cannot silently override an adult member's sharing
  choice.
- A person can see who can access their location and can pause or reduce
  precision without navigating through deceptive UI.
- Approximate sharing is approximate on the server, not merely blurred in the
  client.
- The API must never infer authorization from a user-controlled circle or member
  identifier alone.
- A removed member loses future access immediately; previously cached sensitive
  data must expire or be invalidated.
- Location history is private by default, retained only for an approved period,
  and deleted through a verifiable workflow.
- Emergency features must state whether they notify the circle, call a phone
  number, or contact a verified responder. Never imply unimplemented dispatch.
- Location freshness and uncertainty must be visible. Never present stale or
  low-confidence data as live and precise.
- Safety alerts must degrade safely: deduplicate retries, tolerate late/out-of-
  order events, and avoid false claims of delivery.
- Secrets, raw tokens, precise coordinates, and sensitive request bodies must
  never appear in logs, analytics, crash reports, fixtures, or screenshots.
- There is no sale of precise location data. Advertising based on precise
  location is outside the product boundary unless the product documents are
  explicitly revised after legal and ethical review.

## Architecture Rules

- Keep the backend a modular monolith until measured scale or team ownership
  proves a service split is needed.
- Initial backend modules are: Identity, Circles, Consent, Location, Places,
  Journeys, Safety, Notifications, Devices, and Audit.
- Modules communicate through public application-service interfaces or durable
  events. Never call another module's repository directly.
- Controllers are thin. Business rules belong in application/domain services;
  persistence belongs behind repositories.
- Separate API DTOs from persistence entities. Validate every inbound DTO.
- Every circle-owned query must include circle scope and subject-level access
  checks. Add a negative authorization test that proves another circle cannot
  access the record.
- Treat location uploads as at-least-once and potentially out of order. Require
  an idempotency key or client sample ID and device timestamp.
- Use UTC instants for storage. Preserve the user's IANA timezone separately for
  presentation and schedules.
- Use Flyway for every schema change. Never edit an applied migration.
- Use a transactional outbox for reliable domain-event publication. Do not add
  Kafka merely for anticipated scale.
- External providers sit behind interfaces: maps/geocoding, push, SMS, email,
  object storage, and emergency integrations.
- Keep provider credentials and personally identifiable data out of source
  control.

## Coding Standards

### Java backend

- Use the repository's configured Java version and Spring Boot version; do not
  upgrade them as part of unrelated work.
- Prefer records for immutable request/response values where appropriate.
- Prefer constructor injection, composition, small services, and explicit names.
- Avoid field injection, global mutable state, reflection-heavy shortcuts, and
  generic `Util` classes.
- Use Bean Validation at the API boundary and domain validation for invariants.
- Use `BigDecimal` for money and `Instant` for persisted timestamps.
- Use structured logging with correlation IDs and a sensitive-field denylist.
- Unit-test business rules with JUnit 5. Use integration tests with Testcontainers
  for PostgreSQL/PostGIS, Redis, security, and migrations.

### Android

- Use Kotlin, Jetpack Compose, coroutines/Flow, and platform location APIs.
- Keep permission prompts contextual and explain the value before the operating
  system prompt.
- Background work must respect Doze, foreground-service, and battery policies.
- Test permission denial, approximate-only permission, background restriction,
  offline buffering, reboot, and low-power modes.

### iOS

- Use Swift, SwiftUI, structured concurrency, and Core Location.
- Support reduced accuracy, authorization changes, background relaunch, and the
  platform's visible location indicators.
- Do not attempt to bypass iOS background-execution or privacy behavior.

## Required Tests by Change Type

- New backend endpoint: service unit tests, authenticated success integration
  test, cross-circle denial test, subject-consent denial test where applicable,
  validation test, and API contract update.
- Location ingestion: duplicate, out-of-order, impossible/invalid coordinate,
  stale sample, offline batch, and retention tests.
- Sharing change: grant, pause, expiry, revocation, and cache invalidation tests.
- Place alert: boundary uncertainty, enter/exit hysteresis, deduplication, and
  delayed-event tests.
- Notification: retries, idempotency, provider failure, preference filtering,
  and delivery-status semantics.
- Mobile feature: unit tests plus platform tests for permissions, offline mode,
  accessibility, and lifecycle/background transitions.
- Security-sensitive change: threat-model update and an abuse-case test.

## Definition of Done

A feature is done only when:

- approved acceptance criteria are satisfied;
- authorization and consent are enforced server-side;
- success, failure, offline, stale-data, and revoked-access states are handled;
- automated tests pass and include relevant negative cases;
- logs and analytics expose no precise location or secrets;
- accessibility labels and keyboard/screen-reader behavior are covered;
- operational metrics and alerts exist for safety-critical paths;
- API, schema, ADR, PRD, or runbook documentation is updated when affected;
- the agent reports remaining risks and unverified assumptions.

## Change Control

Create an ADR under `docs/decisions/` before changing any of the following:

- native versus cross-platform mobile strategy;
- Java/Spring backend direction;
- database, event backbone, or cloud provider;
- identity provider or encryption/key-management model;
- location-history retention default;
- emergency escalation semantics;
- tenant/circle authorization model;
- any use of precise location for advertising, model training, or third-party
  sharing.

## Do Not

- Do not copy Life360 code, branding, text, proprietary behavior, or reverse-
  engineered APIs. Similar product goals do not permit copying implementation.
- Do not build a feature from the static `dist/` concept without reconciling it
  with the PRD.
- Do not add microservices, Kafka, Kubernetes, or machine learning before a
  measured need and approved ADR.
- Do not hide missing backend behavior behind a convincing UI simulation in the
  production app.
- Do not weaken consent or authorization "temporarily."
- Do not commit `.env` files, credentials, real family data, or production
  coordinates.
- Do not run destructive database or infrastructure operations without naming
  the exact target, explaining recovery, and receiving explicit approval.
- Do not claim completion when required tests were skipped or could not run.

## Current Phase

Phase 1: walking skeleton, implemented one slice at a time. Features 0A through
0D, UI1 through UI3, UI4.1, and UI4.2 are complete: the Java/Spring Boot and local-services foundation, the
native Android Kotlin/Compose Ocean Sky shell and non-sensitive backend-status
client, the accepted supported-platform/device-matrix ADR, and the debug-only
native UI showcase/viewing workflow with consent-aware synthetic Home,
adult-owned sharing-controls, and read-only circle and adult-membership previews.
No identity, circle, consent, location, notification, or safety feature exists
yet. The `dist/` directory remains a visual concept. UI4 remains in progress;
UI4.1 Places and UI4.2 care and Safety are complete. No substep is currently
active; UI4.3 is next but is not proposed or approved. Executable iOS/SwiftUI work remains
blocked until macOS/Xcode is available; do not imply it has been verified.

`docs/FEATURE_ROADMAP.md` is the single delivery plan. Keep only one feature and
one sequential substep in progress. Each feature needs its own proposal and
explicit approval; completing one feature does not approve the next. Update its
state and close-out evidence in the same change that finishes it.

Treat the roadmap's **Next planned feature** and execution order as binding. Do
not invent or start an unlisted feature, infer a new feature from “next
feature,” or skip an incomplete feature because another task is easier. Any new
ID, reordering, split, merge, bypass, or scope expansion requires an explicit
user-approved amendment to `docs/FEATURE_ROADMAP.md` first. A roadmap amendment
does not approve implementation; the amended feature still needs its own
proposal and explicit approval. Follow this rule unless the user explicitly
instructs otherwise.
