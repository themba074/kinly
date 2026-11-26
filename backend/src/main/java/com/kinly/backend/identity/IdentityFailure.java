package com.kinly.backend.identity;

final class IdentityFailure extends RuntimeException {
    private final String code;

    IdentityFailure(String code) {
        super(code);
        this.code = code;
    }

    String code() {
        return code;
    }
}
