# Architecture Decision Records

Use this directory for decisions that change or lock an important part of
Kinly's architecture, privacy model, or safety semantics.

Name records sequentially, for example:

```text
0001-managed-identity-provider.md
0002-location-history-retention.md
```

Each ADR should use this structure:

```markdown
# ADR NNNN - Decision title

**Status:** Proposed | Accepted | Superseded
**Date:** YYYY-MM-DD
**Owners:** Names

## Context

What problem, constraints, evidence, and product invariants apply?

## Decision

What is being approved?

## Alternatives considered

What credible alternatives were evaluated and why were they rejected?

## Consequences

Describe benefits, costs, privacy/security effects, operational effects,
reversibility, and migration needs.

## Validation

How will the team prove the decision works, and when will it be revisited?
```

An ADR records a team decision; an agent must not mark a consequential proposal
as accepted without explicit human approval.
