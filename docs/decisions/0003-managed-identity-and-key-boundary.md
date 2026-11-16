# ADR 0003 - Managed identity and key boundary

**Status:** Accepted  
**Date:** 2026-11-16  
**Owners:** Kinly team

## Context

Feature 1A must choose sign-in, recovery, and key ownership before Feature 1B
handles any real account. Kinly's first pilot is consenting South African
adults. Account takeover could expose future circle and location data. ADR
0002 prohibits cloud resources, billable trials, and real-user data while the
spending ceiling is ZAR 0. This record is a design choice for later funded
implementation, not permission to provision a user pool.

## Decision

- Use an Amazon Cognito user pool in the future approved deployment region as
  the managed OIDC identity provider. Choose the Essentials feature plan for
  its managed login and native public-client support. The Cape Town region is
  a candidate, subject to the separate ADR 0002 deployment review.
- Start with verified email and password through provider-hosted login. Use
  authorization code with PKCE and the system browser on Android and iOS;
  register separate public app clients without embedded client secrets. Do
  not use the implicit grant or build a Kinly password database. Defer phone
  sign-in, SMS, social federation, email OTP, and passkeys pending separate
  usability, cost, and recovery review.
- Configure recovery to a previously verified email address. A reset must not
  change circle membership, consent, or sharing. Notify the prior address and
  active sessions of credential or recovery-address changes where technically
  deliverable; require recent authentication for a recovery-address change.
  Revoke or re-check active sessions after a reset and deny sensitive reads
  until the new session and device state are confirmed. Manual support must
  never mark an unverified address verified merely to bypass recovery.
- Bind Kinly's internal account to the immutable provider issuer and subject,
  not email, phone, or a client-supplied user ID. The backend validates issuer,
  audience/client, signature, expiry, token type, and current account/device
  state. Circle scope and sharing consent remain server-owned checks.
- Store device tokens only in Android Keystore or iOS Keychain. The backend
  holds no password or signing private key. Cognito manages its signing keys
  and default user-pool encryption. Future application data uses managed
  service encryption and a separately reviewed KMS/secret design; do not
  create a customer-managed KMS key or imply application-level encryption in
  this feature.
- Develop and test the interface locally with synthetic identities and keys.
  Provider provisioning, email delivery, region selection, operator terms,
  funded cost ceiling, and real-user processing require later approval.

## Alternatives considered

- Firebase Authentication: mature native SDKs, but a second cloud ecosystem
  complicates the approved AWS direction and data-location review. Its no-cost
  email-link limits are unsuitable as an assumed production plan.
- Self-hosted identity or a custom password service: greater control but adds
  credential, patching, delivery, key, and recovery operations that the small
  team is not ready to own.
- SMS-first or passwordless email OTP: simpler initial entry for some users,
  but channel cost, delivery reliability, account takeover, and recovery
  dependencies require a separate funded review.
- Cognito Lite classic hosted UI: potentially lower usage cost, but fewer
  managed login and session features. Revisit the tier when a pilot cost model
  and feature configuration are known.

## Consequences

The provider performs credential verification while Kinly remains authoritative
for membership and consent. Email compromise is the primary recovery risk;
device theft, delayed revocation, enumeration, and coercion require the
controls in the identity threat model. No location, notification, battery, or
mobile permission behavior changes in 1A. Hosted login has a distinct visual
surface and must be accessibility tested in 1B. Provider dependence and
potential migration of issuer/subject mappings are accepted trade-offs.

The provider's free MAU allowance is not a spending authorization. Email,
SMS, advanced security features, KMS, and future cloud components may incur
separate charges. Actual pricing, POPIA operator terms, subprocessors,
retention, regional availability, and recovery behavior must be rechecked
before any resource or pilot is approved.

## Validation and review gates

Before 1B implementation, propose that feature separately. Prove synthetic
invalid issuer/audience/signature/expiry, token replay, revoked device,
recovery takeover, enumeration, and cross-account denial cases in the later
implementation. Verify provider configuration and accessibility on both native
platforms. Revisit this ADR if the pilot region, sign-in method, funded budget,
or key-management model changes.

The team approved this ADR and the identity threat model on 16 November 2026.
That approval does not authorize Feature 1B implementation or provider
provisioning.

## Sources checked 2026-11-16

- [Cognito pricing](https://aws.amazon.com/cognito/pricing/)
- [Cognito user-pool plans](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-sign-in-feature-plans.html)
- [Mobile public clients and PKCE](https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-settings-client-apps.html)
- [Password recovery constraints](https://docs.aws.amazon.com/cognito/latest/developerguide/managing-users-passwords.html)
- [Cognito data protection](https://docs.aws.amazon.com/cognito/latest/developerguide/data-protection.html)
- [Cape Town availability](https://aws.amazon.com/about-aws/whats-new/2023/12/amazon-cognito-africa-cape-town-region/)
- [Firebase Authentication limits](https://firebase.google.com/docs/auth/limits)
