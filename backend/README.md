# Kinly backend

This is the Java/Spring Boot foundation for Kinly's modular monolith. The
current local synthetic slice includes an authenticated `GET /api/v1/me`
profile read. Managed sign-in has not been configured or verified, and there
is no real-user account, circle, consent, or location feature.

## Prerequisites

- JDK 21
- Docker Desktop with Linux containers

A global Gradle installation is not required; use the checked-in wrapper.

## Run locally

From the repository root:

```powershell
docker compose up -d postgres redis
cd backend
.\gradlew.bat run
```

The local services are:

- liveness: <http://localhost:8080/actuator/health/liveness>
- readiness: <http://localhost:8080/actuator/health/readiness>
- PostgreSQL/PostGIS: `127.0.0.1:5433`
- Redis: `127.0.0.1:6380`

`/api/v1/me` fails closed until an approved identity provider configuration
is supplied. The local tests provide test-only identities; the default running
app does not accept them. A valid access token must be signed by the
configured issuer, issued to an allowed public app client, have
`token_use=access`, include `openid email` scopes, and be unexpired. The
backend calls userInfo for a matching subject and verified email on every
read. The response contains only the internal account ID, verified email, and
optional display name, with `Cache-Control: no-store`.

The following settings are for a separately approved provider configuration:

- `KINLY_IDENTITY_ISSUER`: exact HTTPS issuer;
- `KINLY_IDENTITY_JWKS_URI`: HTTPS signing-key endpoint;
- `KINLY_IDENTITY_CLIENT_IDS`: comma-separated allowed public mobile client IDs;
- `KINLY_IDENTITY_USERINFO_URI`: HTTPS userInfo endpoint.

The endpoint uses a 60-request-per-minute limit by remote address in Redis.
It returns `503` if rate-limit state cannot be checked. A future deployment
must set its trusted proxy boundary before treating the remote address as the
public client address.

The credentials in `compose.yaml` are fixed local-development values, not
production secrets. Production configuration must use AWS Secrets Manager or
another approved secret source.

Stop the services without deleting local data:

```powershell
docker compose down
```

Do not add `--volumes` unless you intentionally want to delete local database
and Redis data.

## Recover the local environment

The local environment is disposable: source control, Gradle locks, Compose,
and Flyway migrations are the recovery source. Stop the backend, run
`docker compose down`, then start PostgreSQL and Redis again with the command
above. Run `./gradlew.bat test` from `backend/` to verify migrations, health,
and service connectivity. Deleting Compose volumes destroys local data and
requires the explicit destructive-action approval described in `AGENTS.md`.

## Test

Docker must be running because the integration test uses disposable PostGIS and
Redis containers:

```powershell
cd backend
.\gradlew.bat test
```

Test reports are written below `backend/build/reports/tests/test/` and are not
committed.

## Configuration

Local defaults can be overridden without committing an `.env` file:

- `KINLY_DATABASE_URL`
- `KINLY_DATABASE_USER`
- `KINLY_DATABASE_PASSWORD`
- `KINLY_REDIS_HOST`
- `KINLY_REDIS_PORT`

Health responses intentionally omit component details and credentials.

