package ru.ra_tech.garden_manager.failure;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtFailureTest {
    @Test
    void shouldGetCodeAndDetail() {
        val source = getClass().getName();
        val failure = new JwtFailure(source, null);

        assertThat(failure.getCode()).isEqualTo("GM:JWT_FAILURE");
        assertThat(failure.getDetail()).isEqualTo("JWT failure: Unknown error");
    }
}
