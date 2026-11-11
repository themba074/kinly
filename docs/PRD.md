# Kinly - Product Requirements Document

**Version:** 0.1 (discovery baseline)  
**Status:** Draft for team review  
**Date:** 4 November 2026  
**Product name:** Kinly (working name)

## 1. Vision

Kinly is a native mobile family-safety application that helps trusted people
stay connected without turning care into surveillance. It combines voluntary
location sharing, private circles, place alerts, check-ins, journey awareness,
and honest emergency escalation.

The product should provide the reassurance people seek from apps such as
Life360 while improving the member's control, the clarity of consent, the
visibility of stale or uncertain data, and the usefulness of the experience in
South African network and device conditions.

Kinly is an independent product. We will not copy Life360 branding, wording,
source code, private APIs, or proprietary implementation details.

## 2. Problem statement

Families and trusted groups want to know whether the people they care about are
safe, but existing approaches create difficult trade-offs:

- phone calls and messaging require repeated manual updates;
- ordinary map sharing is often temporary or fragmented;
- continuous tracking can feel coercive, especially when controls are unequal;
- location can appear precise and current when it is stale or inaccurate;
- background tracking may drain batteries or stop silently;
- emergency buttons can create false confidence about who was contacted; and
- sensitive movement history can be retained or exposed longer than expected.

Kinly should reduce uncertainty while giving each member understandable control
over what is shared and an auditable view of who can access it.

## 3. Product principles

1. **Care, not surveillance.** The experience should reassure without rewarding
   obsessive monitoring.
2. **Consent is a continuing state.** Joining a circle once is not permanent
   permission for every location use.
3. **Symmetric controls for adults.** An administrator may manage a circle but
   cannot silently override an adult's sharing choice.
4. **Truthful safety language.** The product states what it knows, how fresh it
   is, and whom an alert reached.
5. **Useful under imperfect conditions.** Offline buffering, low battery, weak
   networks, and denied permissions are normal product states.
6. **Minimise sensitive data.** Collect, retain, and expose only what a feature
   needs.
7. **Safety is end to end.** UI, mobile background execution, backend rules,
   notifications, support access, and deletion all form part of the feature.

## 4. Goals and success measures

### 4.1 MVP goals

- A person can create a private circle and invite trusted members.
- Every member can choose precise, approximate, timed, or paused sharing.
- Authorised members can see a truthful live-status view with freshness and
  accuracy indicators.
- A member can create places and receive dependable arrival/departure alerts.
- A member can request and answer a gentle check-in.
- A member can share an active journey and expected arrival.
- An SOS flow can notify configured trusted contacts without implying emergency
  dispatch that does not exist.
- A member can review sharing access and request deletion of location history.
- The apps remain understandable and useful when connectivity or permissions
  degrade.

### 4.2 Validation measures

- At least three pilot circles use Kinly for four consecutive weeks.
- At least 80% of pilot participants can explain and change their sharing mode
  without assistance.
- At least 95% of accepted, technically deliverable location samples are
  reflected to authorised viewers within 15 seconds during an active journey.
- Place alerts meet an agreed precision benchmark in field testing and show no
  duplicate user-visible alert for one transition.
- Median background battery impact stays within a field-tested target that the
  team approves for representative Android and iOS devices.
- No cross-circle or consent-bypass finding remains open before a pilot release.
- Pilot users report that freshness, accuracy, and delivery state are clear.

Exact service-level targets must be baselined with real devices before they are
treated as contractual promises.

## 5. Users and roles

### 5.1 Primary users

- Families coordinating daily travel, school, work, and caregiving.
- Partners or adult relatives who want reciprocal, controllable sharing.
- Caregivers and dependants using an explicitly configured relationship.
- Small trusted groups coordinating journeys or events.

### 5.2 Circle roles

- **Owner:** creates the circle, manages membership, and may transfer ownership.
- **Administrator:** can invite or remove members and manage shared circle
  settings, but cannot override an adult member's consent.
- **Member:** controls their own sharing and can use permitted circle features.
- **Dependent profile:** deferred until its consent, guardianship, age, recovery,
  and jurisdiction rules are separately approved.

The MVP must not treat adult members as dependants. Child and vulnerable-person
features require legal and ethical review and are not obtained merely by adding
an `is_child` flag.

## 6. Representative journeys

### Journey A - create a trusted circle

Themba registers, verifies the account, creates a circle, invites Nandi, and
sees the invitation state. Nandi reviews who invited her, which permissions
will be requested, and what joining does not automatically share. She joins and
then chooses her sharing mode.

### Journey B - daily reassurance

Nandi opens Kinly and sees that Themba's last update was two minutes ago with a
rough accuracy range. She can see that Lwazi's sharing is paused without seeing
a fabricated position. No one receives an alert merely for opening the map.

### Journey C - place alert

Lwazi opts into sharing and a permitted member creates a school place. The
system detects an arrival using a boundary plus hysteresis and sends one alert.
If the location is too uncertain, the app waits or labels the event uncertain
instead of asserting an arrival.

### Journey D - check-in

A member sends a low-pressure check-in. The recipient can answer "I'm okay",
"Call me", or dismiss it. Non-response is shown as non-response, not danger.

### Journey E - active journey

Themba starts a journey to home, chooses who may follow it, and sees the sharing
duration. Viewers receive an ETA and freshness state. The journey closes on
arrival, manual stop, or expiry.

### Journey F - SOS

After a deliberate hold-and-confirm action, Kinly records an incident, shares
the member's latest available location with configured circle contacts, and
sends notifications through configured channels. The screen states which
actions succeeded, failed, or remain pending and offers the device's phone
dialler for emergency services. MVP Kinly does not claim to dispatch responders.

## 7. Functional requirements

### 7.1 Identity and devices

- Register and sign in through an approved managed identity provider.
- Verify email or phone ownership according to the approved sign-in method.
- List active sessions/devices and revoke a device.
- Securely register push tokens without exposing them to other members.
- Provide account recovery that does not weaken circle or location access.

### 7.2 Circles and membership

- Create, rename, leave, and delete a circle.
- Invite through a single-use, expiring invitation.
- Accepting an invitation does not silently enable location sharing.
- Remove a member and immediately revoke future circle access.
- Show members, roles, invitation status, and last meaningful app status without
  exposing sensitive device diagnostics unnecessarily.

### 7.3 Consent and sharing

- Support precise, server-enforced approximate, timed, and paused modes.
- Show who can access the member's location and when a grant expires.
- Allow immediate revocation and propagate it to caches and live sessions.
- Record consent grants, changes, and revocations in a privacy-safe audit trail.
- Require renewed confirmation for material expansions in data use.
- Never substitute a last-known location for paused sharing without clearly
  labelling that it predates the pause and is still authorised for display.

### 7.4 Location and live map

- Collect samples using adaptive, platform-compliant policies.
- Upload offline batches with unique sample IDs and device timestamps.
- Display latest authorised location, accuracy radius, source timestamp, and a
  freshness label.
- Treat samples as duplicated, delayed, or out of order until validated.
- Apply approximate sharing on the server before returning coordinates.
- Avoid exposing raw coordinates through logs, analytics, support tooling, or
  notification payloads.

### 7.5 Places and alerts

- Create named circular places with a centre and radius.
- Let the location subject control whether their location may power a place
  alert and which members may receive it.
- Detect enter and exit events using accuracy-aware hysteresis.
- Deduplicate alerts and tolerate delayed samples.
- Allow per-place and per-member notification preferences.

### 7.6 Check-ins

- Send a check-in with an optional short message.
- Let the recipient respond with simple safe actions.
- Show sent, delivered where known, responded, expired, or failed states.
- Rate-limit use and provide mute/block/report controls for abuse.

### 7.7 Journeys

- Start, share, update, and stop a journey.
- Choose destination, audience, and maximum duration.
- Display ETA as an estimate, with freshness and confidence.
- Detect likely arrival without auto-extending consent.
- Expire abandoned journeys automatically.

### 7.8 Safety and SOS

- Use a deliberate gesture to reduce accidental activation.
- Record an incident ID and idempotently trigger configured notifications.
- Continue retrying safe, retryable delivery attempts and show delivery truth.
- Offer a direct call action using the locally appropriate number; do not place
  a call without clear user action unless a later approved integration supports
  that exact behaviour.
- Allow cancellation with a visible audit record; cancellation must not erase
  already delivered alerts.
- Provide an abuse and coercion threat model before pilot release.

### 7.9 Notifications

- Support push first, with SMS/email adapters available for approved cases.
- Respect member preferences except for explicitly agreed critical notices.
- Keep notification text and payloads free of precise coordinates.
- Track provider acceptance separately from device delivery and user reading.

### 7.10 History, privacy, and deletion

- Show a member their own stored location history and current retention rule.
- Restrict another member's history behind a specific, revocable grant.
- Provide export and deletion-request workflows.
- Make deletion verifiable across primary storage, caches, derived views, and
  documented backup expiry.
- Default retention remains an open decision requiring an ADR and legal review.

### 7.11 Audit and support access

- Audit membership, consent, sensitive reads, sharing changes, place changes,
  journey state, SOS state, export, and deletion actions.
- Do not put raw precise coordinates, tokens, or sensitive payloads in audit
  records.
- Any staff support access must be time-bound, reason-bound, least-privileged,
  and visible in an audit trail. Support impersonation is not an MVP shortcut.

## 8. Non-functional requirements

### Privacy and legal

- Design for POPIA and obtain qualified legal review before real-user launch.
- Document the lawful purpose, minimisation, retention, data-subject workflows,
  operator agreements, breach response, and cross-border transfers.
- Complete child/dependant-specific review before supporting those accounts.
- Never sell precise location or use it for advertising or model training under
  this PRD.

### Security

- Enforce authentication, circle scope, subject consent, and purpose at the
  server for every sensitive operation.
- Encrypt in transit and at rest; use managed key storage and rotation.
- Apply rate limits, device/session revocation, dependency scanning, and secure
  secret management.
- Threat-model stalking, coercion, invitation theft, account takeover, scraped
  history, notification abuse, and insider access.

### Reliability and performance

- Make safety-critical commands idempotent and observable.
- Use at-least-once processing with deduplication; do not claim exactly-once
  external delivery.
- Define degraded states for provider, Redis, database, network, and push
  failures.
- Back up data and regularly test restoration.
- Keep ordinary authenticated API reads within an initial p95 target of 500 ms
  server-side, excluding third-party provider latency.

### Battery and data use

- Use adaptive collection based on motion, journey state, requested precision,
  operating-system policy, and battery state.
- Avoid polling solely to make the map look live.
- Publish measured battery and mobile-data results for representative devices.

### Accessibility and localisation

- Meet WCAG 2.2 AA principles where applicable to native mobile UI.
- Support screen readers, large text, contrast, reduced motion, and non-colour
  state indicators.
- Store IANA timezones and design copy for localisation. Initial language is
  English; South African language priorities require user research.

## 9. MVP scope

### Included

- identity and device registration;
- private circles and invitation lifecycle;
- adult-member consent and sharing modes;
- adaptive location ingestion and authorised live map;
- places and arrival/departure alerts;
- check-ins;
- active journeys;
- circle-notification SOS with honest status;
- notification preferences;
- limited history, export/deletion request, and audit trail; and
- operational monitoring and support runbooks for safety-critical paths.

### Deferred

- automatic crash detection;
- professional monitoring or emergency-service dispatch;
- child/dependant accounts;
- roadside assistance;
- driving-score or insurance features;
- smart-device and vehicle integrations;
- public discovery or social feeds;
- paid tiers and billing;
- machine-learning risk predictions; and
- advertising or monetisation of precise location.

## 10. Delivery phases

0. **Discovery and approval:** validate the PRD, threat model, retention, legal
   assumptions, and architecture decisions.
1. **Walking skeleton:** native sign-in, one authenticated backend request,
   database migration, push registration, CI, infrastructure, and observability.
2. **Circle and consent:** invitation, membership, sharing grant, revocation,
   and negative access tests.
3. **Location foundation:** foreground samples, offline batch ingestion, latest
   authorised view, freshness/accuracy, then measured background behaviour.
4. **Care features:** places, check-ins, and journeys as separate end-to-end
   slices.
5. **Safety hardening:** SOS, retries, incident state, provider failure drills,
   and abuse testing.
6. **Pilot readiness:** deletion, retention jobs, support controls, restore test,
   field battery/location benchmark, accessibility, and legal sign-off.

Each phase must exit through agreed acceptance criteria. A polished UI alone is
not evidence that the phase is complete.

The phases above describe product sequencing, not implementation batch size.
`docs/FEATURE_ROADMAP.md` groups them into reviewable features that are
separately proposed, approved, implemented, tested, and closed. Only one feature
and one sequential substep within it may be in progress at a time.

## 11. Risks and mitigations

- **Coercive monitoring:** symmetric controls, visible access, revocation, abuse
  reporting, and careful dependent-account design.
- **False reassurance:** freshness and uncertainty everywhere; honest delivery
  and emergency wording.
- **Background restrictions:** native platform implementations, field testing,
  offline buffering, and visible degraded states.
- **Battery drain:** adaptive policies and measured budgets before expansion.
- **Location leakage:** layered authorization, server-side approximation,
  minimised logs, encryption, short retention, and negative tests.
- **Alert storms or false geofences:** hysteresis, deduplication, uncertainty
  handling, and field calibration.
- **Scope growth:** ship one end-to-end capability at a time and keep deferred
  features outside the MVP until evidence changes priorities.
- **Provider lock-in:** interfaces around maps, messaging, storage, and identity;
  accept deliberate AWS infrastructure coupling where it reduces operational
  risk.

## 12. Decisions required before implementation

1. Confirm Kinly as the product name or mark it temporary.
2. Confirm the first pilot audience and whether all members are adults.
3. Select the managed identity provider and primary sign-in method.
4. Choose AWS region(s) after POPIA, latency, service availability, and cost
   review.
5. Approve a location-history retention default through an ADR.
6. Define the exact MVP SOS recipients, channels, retry window, and copy.
7. Select maps/geocoding and SMS/email providers after a cost/privacy review.
8. Approve measurable battery, location, and alert-quality release gates.

## 13. Product status

Phase 1 walking skeleton. Features 0A through 0D and UI1 through UI3 are complete.
They establish the backend/local-services foundation, native Android Ocean Sky
shell, supported-platform decision, debug-only UI Showcase, and synthetic Home
adult-owned sharing-controls preview, and read-only circle and adult-membership
preview. They do not implement identity, circles, consent services, location,
notifications, or safety behaviour.

No next feature is selected or approved in `docs/FEATURE_ROADMAP.md`. The
`dist/` directory remains a disposable visual concept. This document does not
authorise later slices and does not resolve the open product decisions above.
Executable iOS work remains blocked until macOS/Xcode is available.
