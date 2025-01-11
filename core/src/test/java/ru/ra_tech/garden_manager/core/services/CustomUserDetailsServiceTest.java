package ru.ra_tech.garden_manager.core.services;

import io.vavr.control.Either;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.ra_tech.garden_manager.database.repositories.auth.AuthUserRepositoryImpl;
import ru.ra_tech.garden_manager.failure.DatabaseFailure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {
    @Test
    void shouldThrowOnError() {
        val repo = mock(AuthUserRepositoryImpl.class);
        when(repo.findById(anyString()))
                .thenReturn(Either.left(new DatabaseFailure(
                        DatabaseFailure.DatabaseFailureCode.AUTH_USER_REPOSITORY_FAILURE,
                        new RuntimeException("Dummy exception"),
                        getClass().getName()
                )));
        val service = new CustomUserDetailsService(repo);

        assertThatThrownBy(() -> service.loadUserByUsername("test"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Dummy exception");

        when(repo.findById(anyString())).thenReturn(Either.left(new DatabaseFailure(
                DatabaseFailure.DatabaseFailureCode.AUTH_USER_REPOSITORY_FAILURE,
                null,
                getClass().getName()
        )));

        assertThatThrownBy(() -> service.loadUserByUsername(("test")))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Unknown error");
    }
}
