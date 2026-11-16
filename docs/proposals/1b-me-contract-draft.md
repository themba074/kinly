# Feature 1B local `/me` contract draft

**Status:** Design for approved local synthetic implementation; the endpoint is not implemented yet  
**Date:** 2026-11-16

This draft defines the first 1B substep. The published
`contracts/openapi.json` remains foundation-only until the endpoint and its
security checks exist. The client sends a provider access token with
`openid email` scopes in the
`Authorization: Bearer` header. It sends no account ID, provider subject,
email, or circle selector in the path, query, or body. A first valid sign-in
binds the issuer and immutable subject to one Kinly account idempotently;
subsequent reads return that account. The backend validates the access token,
then obtains `email` and `email_verified` from the provider's userInfo endpoint.
It verifies that userInfo's subject matches the token subject and fails closed
if userInfo is unavailable. A verified email is required before creating or
updating the local account. Email changes never change its owner.

## Proposed operation

`GET /api/v1/me` has no request body and returns `Cache-Control: no-store`.

Successful response (`200`):

```json
{
  "id": "00000000-0000-4000-8000-000000000001",
  "verifiedEmail": "adult-one@example.invalid",
  "displayName": "Adult One"
}
```

`id` is a server-owned opaque UUID. `verifiedEmail` comes from verified
provider userInfo and is private to the caller. `displayName` is optional,
presentation-safe text with a bounded length; it is never treated as an
identity or authorization key. No token, provider subject, session secret,
circle, membership, sharing, or location data is returned. The server must
not log the response body or email. The client must not persist this response
in a shared cache.

| Status | Code | Meaning |
| --- | --- | --- |
| 401 | `AUTHENTICATION_REQUIRED` | Missing, malformed, forged, expired, wrong-issuer, wrong-client, or wrong-token-use token. |
| 403 | `ACCOUNT_UNVERIFIED` | Valid identity lacks a verified email. |
| 403 | `ACCOUNT_DISABLED` | The server-owned account or session is disabled or revoked. |
| 429 | `RATE_LIMITED` | Bounded abuse control; a retry delay may be supplied. |
| 503 | `IDENTITY_UNAVAILABLE` | Required provider keys or session state cannot be verified. |

Errors contain only `code` and a safe generic `message`; they never echo a
token, email, subject, identifier, or provider response. Network failure is a
client state, not an HTTP response. The backend verifies the token signature,
issuer, client/audience, token use, expiry, userInfo subject, and current
account/session state before returning a profile. Unknown signing keys fail
closed. The exact provider claim mapping and session lookup must be fixed in backend substep 2
and verified against the approved provider in gated substep 4.

## Synthetic cases for implementation

All addresses use reserved `.invalid` domains; identities and signing keys are
local test material only. The fixtures must cover:

| Case | Expected result |
| --- | --- |
| First valid sign-in for issuer A, subject one, verified email A | Create one local account and return only its profile. |
| Repeated or concurrent sign-in for the same issuer and subject | Return the same account ID and one database row. |
| Same verified email with a different issuer or subject | Keep separate accounts; never merge by email. |
| Same issuer and subject with a newly verified email | Preserve the account ID and apply the approved email-change policy. |
| Unverified email, disabled account, or revoked session | Deny the profile. |
| Missing, malformed, forged, expired, wrong-issuer, wrong-client, or wrong-token-use token | Deny without account creation. |
| Unknown key or provider state unavailable | Fail closed without account creation. |
| Caller-supplied account identifier, including another account's ID | Ignore as an authorization source; the operation accepts no such selector. |

No case creates a circle or sharing grant. Cross-circle and subject-consent
denial become executable when those modules exist; identity alone must never
imply either permission.

Provider claim source: [Amazon Cognito userInfo endpoint](https://docs.aws.amazon.com/cognito/latest/developerguide/userinfo-endpoint.html)
and [access-token verification](https://docs.aws.amazon.com/cognito/latest/developerguide/amazon-cognito-user-pools-using-tokens-verifying-a-jwt.html).
