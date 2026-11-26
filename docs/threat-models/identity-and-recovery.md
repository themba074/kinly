# Identity and recovery threat model

**Status:** Approved for Feature 1A  
**Date:** 2026-11-16  
**Scope:** Adult accounts, managed sign-in, recovery, token validation, and
device binding. No provider or real account exists yet.

## Assets and trust boundaries

Assets are account control, provider tokens, verified recovery address,
device/session state, and the future ability to request circle and location
data. The mobile app is an untrusted public client; the provider authenticates
credentials; the Kinly backend independently validates identity and owns
account, device, circle, and consent authorization. Email delivery is an
external recovery channel and never proves circle consent.

## Abuse cases and required controls

| Threat | Failure to prevent | Required control and later verification |
| --- | --- | --- |
| Intercepted login redirect or code | Attacker redeems authorization code | System browser, exact redirect allowlist, state/nonce, PKCE; altered verifier/redirect tests |
| Forged or wrong-audience JWT | Cross-app or attacker token accepted | Issuer, audience/client, signature/JWKS, token type, expiry checks; negative token tests |
| Stolen device or refresh token | Continued access after owner revokes device | Secure device storage, bounded sessions, server device state, revocation and cache checks; stolen-device test |
| Compromised email or coerced recovery | Account takeover and future location access | Verified-address recovery, change notices, recent-auth challenge, session re-check, support restrictions; recovery takeover drill |
| Account enumeration and reset spam | Reveals membership or overwhelms recipient | Neutral responses, rate limits, abuse metrics without raw addresses; enumeration and retry tests |
| Email address reassignment | Old address gains another person's account | Internal account keyed by issuer and immutable subject; address-change verification and conflict test |
| Malicious circle administrator | Uses role to bypass adult sharing | Identity never implies membership or consent; server-side cross-circle and grant-denial tests |
| Insider or support misuse | Silent verification override or token disclosure | Least privilege, reason-bound audited support, no impersonation shortcut, no manually forced verification |
| Provider outage or stale key set | Unsafe fallback accepts unverifiable token | Fail closed for new sensitive reads, bounded JWKS cache and refresh, clear unavailable UI; outage test |
| Token or personal data in telemetry | Long-lived secret exposure | Sensitive-field denylist, no request bodies/tokens in logs, crash reports or screenshots; log inspection |

## Residual risks and launch gates

Email recovery is vulnerable to email-account takeover and delivery failure.
No claim of secure recovery is warranted until the provider configuration and
mobile flows are tested. Provider and email subprocessors, POPIA transfer,
retention/deletion, cost, and incident procedures need review before real-user
processing. MFA/passkeys and alternative recovery require a separate decision.
The future independent security review remains required before a location
pilot. No threat control here authorizes sharing without an active grant.

## Feature 1B local test matrix

The proposed `/me` contract and synthetic cases are recorded in
`docs/proposals/1b-me-contract-draft.md`. Backend substep 2 must test invalid
signature, issuer, client/audience, token use, expiry, unknown key, unverified
email, disabled account, revoked session, concurrent first sign-in, and
cross-account selection. Mobile substep 3 must test altered redirect, state,
nonce, and PKCE verifier, plus cancellation, offline state, secure token
cleanup, and lifecycle return. Provider recovery, email delivery, session
reset, and real mobile-to-backend behavior remain gated by ADR 0002 and must
not be marked verified using synthetic fixtures.

The local 1B backend validates JWT signature, issuer, client, token use,
scope, and expiry; calls userInfo for a matching subject and verified email;
denies disabled accounts; and uses a bounded Redis-backed rate limit. Synthetic
tests cover forged and wrong-claim tokens, mismatched userInfo, provider
failure, cross-account selection, and rate exhaustion. Provider-side
revocation, recovery after reset, hosted login, and key rotation still need
verification against the approved provider before the feature can close.
