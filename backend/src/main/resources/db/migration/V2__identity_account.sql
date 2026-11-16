CREATE TABLE identity_account (
    id UUID PRIMARY KEY,
    issuer TEXT NOT NULL,
    provider_subject TEXT NOT NULL,
    verified_email TEXT NOT NULL,
    display_name TEXT,
    state TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (state IN ('ACTIVE', 'DISABLED')),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT identity_account_issuer_subject_unique UNIQUE (issuer, provider_subject),
    CONSTRAINT identity_account_issuer_not_blank CHECK (length(trim(issuer)) > 0),
    CONSTRAINT identity_account_subject_not_blank CHECK (length(trim(provider_subject)) > 0),
    CONSTRAINT identity_account_email_not_blank CHECK (length(trim(verified_email)) > 0)
);
