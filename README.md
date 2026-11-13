# Kinly

Kinly is a consent-first family safety and location-sharing application. It is
intended to provide the reassurance people expect from products such as
Life360 while giving every circle member clear, equal control over what they
share, with whom, and for how long.

This repository is in the **incremental walking-skeleton phase**. The static
files under `dist/` are a visual concept only. They are not the production
application and do not determine the final architecture.

## Start here

1. Read [the product requirements](docs/PRD.md) for the problem, users, MVP,
   safety model, and success measures.
2. Read [the technical specification](docs/TECH_SPEC.md) for the Java-first
   architecture, mobile approach, data model, and delivery plan.
3. Read [the design system](docs/DESIGN_SYSTEM.md) for the approved Ocean Sky
   visual direction and shared UI tokens.
4. Use [the incremental feature roadmap](docs/FEATURE_ROADMAP.md) to select one
   small implementation slice at a time.
5. Read [the agent guide](AGENTS.md) before asking an AI agent to change the
   project.

## Approved stack

- Android: Kotlin, Jetpack Compose, Android location APIs
- iOS: Swift, SwiftUI, Core Location
- Backend: Java, Spring Boot, Gradle
- Data: PostgreSQL with PostGIS; Redis for short-lived state and queues
- Events: begin with a transactional outbox; introduce Kafka only when load or
  independent consumers justify it
- Infrastructure: AWS, Docker, Terraform, GitHub Actions
- Web operations console: TypeScript and React, introduced only when needed

Native mobile applications are deliberate. Reliable background location,
battery-aware tracking, permissions, and emergency behavior are core product
capabilities, so they should not depend on a cross-platform abstraction that
hides platform-specific behavior.

## Current artifacts

- `dist/`: disposable interactive visual concept for Kinly
- `docs/PRD.md`: product source of truth
- `docs/TECH_SPEC.md`: technical source of truth
- `docs/DESIGN_SYSTEM.md`: approved Ocean Sky visual direction
- `docs/FEATURE_ROADMAP.md`: the single gated delivery plan, current boundary,
  and completed-foundation record
- `docs/mockups/kinly-selected-ui.png`: canonical five-tab UI concept
- `docs/decisions/`: architecture decision records as decisions are approved
- `AGENTS.md`: working agreement for human and AI contributors

## Important product boundary

Kinly can help trusted people share location and coordinate during stressful
situations. It must never claim to guarantee safety, prevent crime, detect every
crash, or dispatch emergency services unless a verified integration actually
does so. Emergency copy and flows must always state their real limitations.

## Status

The backend/local-services foundation, Android Ocean Sky shell, and debug UI
exploration through UI4 are complete. No feature is active; 0E is next but is
not proposed or approved. Identity, real product features, iOS, and AWS remain
unimplemented. See `docs/FEATURE_ROADMAP.md` for the single current status and
delivery order.
