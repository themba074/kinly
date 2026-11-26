package com.kinly.backend.identity;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class IdentityRateLimitFilterTest {
    @Test
    void missingRateLimitStateDeniesProfileRead() throws Exception {
        StringRedisTemplate redis = org.mockito.Mockito.mock(StringRedisTemplate.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/me");
        request.setRemoteAddr("192.0.2.82");
        MockHttpServletResponse response = new MockHttpServletResponse();
        new IdentityRateLimitFilter(redis).doFilter(request, response, new MockFilterChain());
        assertThat(response.getStatus()).isEqualTo(503);
        assertThat(response.getContentAsString()).contains("IDENTITY_UNAVAILABLE");
        assertThat(response.getHeader("Cache-Control")).isEqualTo("no-store");
    }
}
