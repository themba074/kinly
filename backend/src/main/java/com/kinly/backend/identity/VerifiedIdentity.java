package com.kinly.backend.identity;

record VerifiedIdentity(String issuer, String subject, String email, String displayName) {
}
