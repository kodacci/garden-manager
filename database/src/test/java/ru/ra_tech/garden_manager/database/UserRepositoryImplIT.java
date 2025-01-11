package ru.ra_tech.garden_manager.database;

import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ra_tech.garden_manager.database.repositories.user.CreateUserDto;
import ru.ra_tech.garden_manager.database.repositories.user.UserRepositoryImpl;
import ru.ra_tech.garden_manager.failure.DatabaseFailure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.ra_tech.garden_manager.database.schema.Tables.USERS;

@Slf4j
class UserRepositoryImplIT implements DatabaseIT {
    @Autowired
    private UserRepositoryImpl repo;

    @Autowired
    private DSLContext dsl;

    @AfterAll
    void afterAll() {
        dsl.deleteFrom(USERS).execute();
    }

    @Test
    @DisplayName("Should return application failure on database error")
    void shouldReturnAppFailureOnError() {
        val dslMock = mock(DSLContext.class);
        when(dslMock.selectCount()).thenThrow(new RuntimeException("Dummy exception"));

        val userRepo = new UserRepositoryImpl(dslMock);

        val result = userRepo.checkDataConflict("test", Option.of("test@example.com"));

        assertThat(result.isLeft()).isTrue();
        val failure = result.getLeft();
        assertThat(failure).isInstanceOf(DatabaseFailure.class);
        assertThat(failure.getCode())
                .isEqualTo(DatabaseFailure.DatabaseFailureCode.USER_REPOSITORY_FAILURE.toString());
        assertThat(failure.getDetail()).isEqualTo("Database access error");
        assertThat(failure.getMessage()).isEqualTo("Dummy exception");
        assertThat(failure.getSource()).isEqualTo(UserRepositoryImpl.class.getName());
    }

    @Test
    void shouldCheckIfUserExists() {
        var result = repo.exists(1L);

        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isFalse();

        val user = repo.create(new CreateUserDto(
                "", "Test user", null, "abc12345"
        ))
                .peekLeft(failure -> log.error("Error creating user:", failure.getCause()))
                .get();

        result = repo.exists(user.id());
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isTrue();
    }

    @Test
    void shouldThrowExceptionOnDbError() {
        try {
            dsl.insertInto(USERS).set(USERS.LOGIN, "abc").execute();
        } catch (Exception ex) {
            assertThat(ex.getMessage()).contains("Access database using JOOQ");
        }
    }
}
