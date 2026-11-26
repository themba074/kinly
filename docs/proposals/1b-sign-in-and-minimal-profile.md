# Feature 1B proposal - sign-in and minimal profile

**Status:** Approved for local synthetic work packages; managed-provider verification remains gated
**Date:** 2026-11-16  
**Roadmap ID:** 1B

## Outcome and boundary

An adult can register with a verified email, sign in through the approved
managed provider on Android and iOS, and retrieve only their own minimal
profile from `GET /api/v1/me`. Recovery, cancellation, expiration, offline
failure, and revoked access have honest states. Sign-in alone never joins a
circle or starts location sharing.

This feature does not add circles, location, push tokens, permissions, maps,
or real-user pilot access. Device inventory and push-token ownership remain
Feature 1C. The first-pilot age and POPIA checks remain launch gates.

## Proposed implementation work packages

1. Define the `/api/v1/me` request/response and error contract, minimal
   profile fields, synthetic identity fixtures, and a threat-model test matrix.
   Add a Flyway migration for a local account keyed by provider issuer and
   immutable subject; never key account ownership by email alone.
2. Add the backend Identity application service and thin controller. Validate
   access-token issuer, signature, audience/client, token use, expiry, and
   account/session state. Fail closed when keys or provider state cannot be
   verified. Resolve the caller from the token, never a supplied user ID.
3. Add Android and iOS sign-in adapters using the system browser,
   authorization code plus PKCE, exact redirects, state/nonce, and secure
   token storage in Keystore/Keychain. Show signed-out, loading, signed-in,
   cancellation, expired, offline, and unavailable states. Keep hosted-login
   branding and accessibility expectations explicit.
4. Verify registration, confirmed email, self-service recovery to the
   previously verified email, session handling after reset, and the full
   mobile-to-backend path against the approved provider configuration.
5. Update API, runbook, threat model, and status documentation with results.

Packages 2 and 3 may proceed independently once their inputs are available.
Package 4 remains gated by the approved provider and spending boundary; it is
not authorised by this proposal or by the work-package amendment.

## Acceptance and negative checks

- Authenticated `/api/v1/me` returns only the caller's minimal ID, verified
  email, and presentation-safe profile metadata; it returns no token, circle,
  location, or provider secret.
- Unit and PostgreSQL integration tests cover successful account creation/read,
  repeated sign-in, validation, concurrent first sign-in, and migration.
- Security tests reject missing, malformed, forged, expired, wrong-issuer,
  wrong-audience, wrong-token-use, revoked-session, and another-account
  requests. JWKS failure denies access rather than trusting an old unknown key.
- Mobile unit and platform tests cover PKCE/redirect rejection, cancellation,
  expiry, offline mode, secure storage, logout cleanup, lifecycle, large text,
  screen-reader labels, and provider return flow.
- Recovery tests show neutral enumeration-resistant responses, verified-email
  requirement, reauthentication for address change, session re-check after
  reset, and no membership or sharing mutation.
- Contract, dependency lock, secret scan, safe-log inspection, Android build,
  and iOS CI pass. No real email address, credential, or token enters fixtures,
  logs, screenshots, or source control.

## Design and alternatives

Use the native platform authentication browser and a small provider adapter,
plus Spring Security resource-server validation behind the Identity module.
Keep API DTOs separate from persistence and use Flyway for the account table.
The backend owns account and authorization state; Cognito owns credentials.

A custom password endpoint would add credential custody and conflict with ADR
0003. A polished local fake sign-in could test navigation but cannot satisfy
the managed-provider acceptance gate. A backend-only slice would leave the
approved two-platform user outcome unverified. These remain rejected as
feature completion paths.

## Effects, dependency, and review decisions

Privacy exposure adds a verified email and account identifier, so storage,
logging, deletion, and support access need minimization. Recovery and device
theft are the largest security risks. Sign-in itself needs no location
permission or background battery use. Offline use cannot create a new
authenticated session; previously cached non-sensitive UI must not imply
fresh authorization. Provider email delivery, MAU pricing, region, operator
terms, and outage behavior affect reliability and cost.

ADR 0002 currently forbids any cloud resource or billable trial, including a
Cognito user pool. Thus steps 1-3 can be developed with synthetic local
fixtures after 1B approval, but step 4 and the 1B completion gate cannot be
met under the current ZAR 0 ceiling. Before provisioning, the team must
approve a separate roadmap/spending amendment, region and POPIA review,
budget ceiling, recovery plan, and exact provider configuration. Approval of
this proposal alone does not authorize those actions. If the team wishes to
ship only the local preparation, that requires an explicit roadmap split or
amendment rather than marking 1B complete.

Sanity-check the minimal profile fields, whether verified email plus password
is acceptable for the adult pilot, the recovery/session-revocation semantics,
and whether to approve only local preparation now or amend the ZAR 0 boundary
before the managed-provider verification. Rollback removes the new endpoint
and mobile entry points through a reviewed code change; an applied migration
is never edited or destructively reversed.

The team approved the local synthetic work packages on 16 November 2026. This does
not authorize Cognito provisioning or real email delivery. The feature remains
in progress until every completion gate can be verified.
