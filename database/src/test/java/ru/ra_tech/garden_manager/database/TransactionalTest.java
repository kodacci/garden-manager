package ru.ra_tech.garden_manager.database;

import io.vavr.control.Either;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import ru.ra_tech.garden_manager.failure.DatabaseFailure;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionalTest {
    @Test
    @DisplayName("Should return failure on transaction exception")
    void shouldReturnFailureOnException() {
        val manager = mock(PlatformTransactionManager.class);
        when(manager.getTransaction(any())).thenThrow(new RuntimeException("Dummy exception"));

        val transactional = new Transactional(manager);
        val result = transactional.execute(status -> Either.right("Test"), Function.identity());

        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isInstanceOf(DatabaseFailure.class);
        val cause = result.getLeft().getCause();
        assertThat(cause).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Dummy exception");
    }
}
