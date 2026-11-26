package com.kinly.backend.identity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
final class IdentityRateLimitFilter extends OncePerRequestFilter {
    private static final int LIMIT_PER_MINUTE = 60;
    private static final DefaultRedisScript<Long> SCRIPT = new DefaultRedisScript<>(
            "local n = redis.call('INCR', KEYS[1]); "
                    + "if n == 1 then redis.call('EXPIRE', KEYS[1], 60) end; return n",
            Long.class);
    private final StringRedisTemplate redis;

    IdentityRateLimitFilter(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"/api/v1/me".equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        String key = "identity:me:rate:" + digest(request.getRemoteAddr());
        try {
            Long count = redis.execute(SCRIPT, List.of(key));
            if (count == null) {
                unavailable(response);
                return;
            }
            if (count > LIMIT_PER_MINUTE) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.setHeader("Cache-Control", "no-store");
                response.setHeader("Retry-After", "60");
                response.getWriter().write(
                        "{\"code\":\"RATE_LIMITED\",\"message\":\"Please retry later\"}");
                return;
            }
        } catch (DataAccessException exception) {
            unavailable(response);
            return;
        }
        chain.doFilter(request, response);
    }

    private static void unavailable(HttpServletResponse response) throws IOException {
        response.setStatus(503);
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-store");
        response.getWriter().write(
                "{\"code\":\"IDENTITY_UNAVAILABLE\",\"message\":\"Profile unavailable\"}");
    }

    private static String digest(String address) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(address.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }
}
