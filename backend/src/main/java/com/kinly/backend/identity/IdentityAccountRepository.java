package com.kinly.backend.identity;

import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
class IdentityAccountRepository {
    private final JdbcTemplate jdbc;

    IdentityAccountRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    Account upsert(VerifiedIdentity identity) {
        UUID proposedId = UUID.randomUUID();
        var matches = jdbc.query(
                "INSERT INTO identity_account "
                        + "(id, issuer, provider_subject, verified_email, display_name, created_at, updated_at) "
                        + "VALUES (?, ?, ?, ?, ?, now(), now()) "
                        + "ON CONFLICT (issuer, provider_subject) DO UPDATE SET "
                        + "verified_email = EXCLUDED.verified_email, "
                        + "display_name = EXCLUDED.display_name, updated_at = now() "
                        + "WHERE identity_account.state = 'ACTIVE' "
                        + "RETURNING id, verified_email, display_name, state",
                (rs, row) -> new Account(rs.getObject("id", UUID.class),
                        rs.getString("verified_email"), rs.getString("display_name"),
                        rs.getString("state")),
                proposedId, identity.issuer(), identity.subject(), identity.email(),
                identity.displayName());
        if (matches.isEmpty()) {
            throw new IdentityFailure("ACCOUNT_DISABLED");
        }
        return matches.getFirst();
    }

    record Account(UUID id, String email, String displayName, String state) {
    }
}
