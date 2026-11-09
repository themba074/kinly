# Kinly backend

This is the Java/Spring Boot foundation for Kinly's modular monolith. The
current slice exposes operational health only. It contains no identity, circle,
consent, or location feature.

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

The credentials in `compose.yaml` are fixed local-development values, not
production secrets. Production configuration must use AWS Secrets Manager or
another approved secret source.

Stop the services without deleting local data:

```powershell
docker compose down
```

Do not add `--volumes` unless you intentionally want to delete local database
and Redis data.

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

