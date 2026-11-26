package com.kinly.backend.identity;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class IdentityService {
    private final IdentityProvider provider;
    private final IdentityAccountRepository accounts;

    IdentityService(IdentityProvider provider, IdentityAccountRepository accounts) {
        this.provider = provider;
        this.accounts = accounts;
    }

    @Transactional
    public Profile me(Jwt jwt) {
        VerifiedIdentity identity = provider.verify(jwt);
        IdentityAccountRepository.Account account = accounts.upsert(identity);
        if (!"ACTIVE".equals(account.state())) {
            throw new IdentityFailure("ACCOUNT_DISABLED");
        }
        return new Profile(account.id().toString(), account.email(), account.displayName());
    }

    record Profile(String id, String verifiedEmail, String displayName) {
    }
}
