package com.kinly.backend.identity;

import java.util.Map;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
final class IdentityController {
    private final IdentityService identity;

    IdentityController(IdentityService identity) {
        this.identity = identity;
    }

    @GetMapping("/api/v1/me")
    ResponseEntity<IdentityService.Profile> me(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(identity.me(jwt));
    }

    @ExceptionHandler(IdentityFailure.class)
    ResponseEntity<Map<String, String>> failure(IdentityFailure failure) {
        HttpStatus status = "IDENTITY_UNAVAILABLE".equals(failure.code())
                ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).cacheControl(CacheControl.noStore())
                .body(Map.of("code", failure.code(), "message", "Profile unavailable"));
    }
}
