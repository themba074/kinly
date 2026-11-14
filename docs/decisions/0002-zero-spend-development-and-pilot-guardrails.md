# ADR 0002 - Zero-spend development and pilot guardrails

**Status:** Accepted  
**Date:** 2026-11-13  
**Owners:** Kinly team

## Context

Kinly needs clear product and pilot boundaries before real-user data or cloud
deployment. The current project budget is zero. A free tier or trial can still
create payment obligations, so it is not an acceptable substitute for approval.

## Decision

- Kinly remains the working product name until brand, domain, and legal checks
  are affordable and explicitly approved.
- The future first pilot is limited to consenting South African adults aged 18
  or older in private trusted circles. Child, dependent, vulnerable-person, and
  guardianship accounts remain deferred.
- Current development uses local services, synthetic accounts, and synthetic
  data only. The environment is recoverable by rebuilding it from
  version-controlled source, containers, and Flyway migrations.
- The spending ceiling is **ZAR 0**. Do not create cloud resources, start trials
  that can become billable, buy domains or developer memberships, or use paid
  providers. Any non-zero spend requires an approved roadmap amendment.
- AWS remains the future cloud direction. Africa (Cape Town), `af-south-1`, is
  only a candidate for a South African pilot, not an approved deployment
  target. Availability, versions, latency, POPIA and cross-border handling,
  recovery objectives, and cost must be reviewed again before deployment.
- The PRD validation measures remain provisional release gates, not current
  service-level claims. Qualified POPIA review is required before real-user
  processing.

## Alternatives considered

- **Deploy through an AWS free tier or credit:** rejected because usage can
  become billable and deployment is unnecessary for the present local work.
- **Select and provision Cape Town now:** rejected because no deployment is
  authorised and service availability and pricing can change.
- **Use a European or multi-region deployment:** rejected because it adds cost,
  latency, cross-border, recovery, and operational complexity.
- **Include children or dependants in the first pilot:** rejected pending the
  required legal, ethical, consent, recovery, and guardianship design.

## Consequences

The team can continue backend and Android development locally without financial
commitment. No cloud backup, recovery region, paid identity, messaging, maps,
store distribution, or production reliability claim is available. Local work
cannot validate internet latency or cloud recovery. The decision changes no
runtime permission, battery behavior, consent rule, or accessibility behavior.

## Validation

Feature work must run with the repository's local services and no-charge tools
already available to the team. Reviews must reject credentials, real personal
or location data, billable resources, and child flows. Revisit this ADR only
through an approved roadmap amendment when funding or deployment becomes
possible.
