package ru.ra_tech.garden_manager.core.security;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class JwtAuthenticationTest {
    private static final String TEST_ROLE = "TEST_ROLE";

    private JwtAuthentication getAuthentication() {
        val principal = mock(JwtPrincipal.class);
        val expire = new Date(OffsetDateTime.now().plusHours(1).toEpochSecond() * 1000);

        return new JwtAuthentication(principal, expire, TEST_ROLE);
    }

    @Test
    @DisplayName("Should return credentials and details")
    void shouldGetCredsAndDetails() {
        val auth = getAuthentication();

        assertThat(auth.getDetails()).isNull();
        assertThat(auth.getCredentials()).isNull();
    }

    @Test
    @DisplayName("Should throw exception on manually setting authenticated to true")
    void shouldThrowExceptionOnSetAuthTrue() {
        val auth = getAuthentication();

        assertThatThrownBy(() -> auth.setAuthenticated(true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Manually setting authenticated to true is illegal");
    }

    @Test
    @DisplayName("Should be able to manually set authenticated to false")
    void shouldBeAbleToSetAuthenticatedToFalse() {
        val auth = getAuthentication();

        assertThat(auth.isAuthenticated()).isTrue();
        auth.setAuthenticated(false);
        assertThat(auth.isAuthenticated()).isFalse();
    }

    @Test
    @DisplayName("Should return unauthenticated after expiration date")
    void shouldReturnUnauthAfterExp() {
        val principal = mock(JwtPrincipal.class);
        val expire = new Date(OffsetDateTime.now().minusMinutes(1).toEpochSecond() * 1000);
        val auth = new JwtAuthentication(principal, expire, TEST_ROLE);

        assertThat(auth.isAuthenticated()).isFalse();
    }
}
