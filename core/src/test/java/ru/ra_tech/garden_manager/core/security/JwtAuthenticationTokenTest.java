package ru.ra_tech.garden_manager.core.security;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationTokenTest {
    @Test
    void shouldImplementRequiredMethods() {
        val token = new JwtAuthenticationToken("test");

        assertThat(token.getAuthorities()).isEmpty();
        assertThat(token.getDetails()).isNull();
        assertThat(token.getPrincipal()).isNull();
        assertThat(token.getName()).isNull();
        assertThat(token.isAuthenticated()).isFalse();
        token.setAuthenticated(true);
        assertThat(token.isAuthenticated()).isTrue();
    }
}
