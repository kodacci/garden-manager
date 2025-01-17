package ru.ra_tech.garden_manager.database;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ra_tech.garden_manager.database.configuration.DatabaseConfiguration;
import ru.ra_tech.garden_manager.database.repositories.auth.AuthUserRepositoryImpl;
import ru.ra_tech.garden_manager.database.repositories.user.CreateUserDto;
import ru.ra_tech.garden_manager.database.repositories.user.UserRepositoryImpl;
import ru.ra_tech.garden_manager.failure.DatabaseFailure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static ru.ra_tech.garden_manager.database.schema.tables.Users.USERS;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {
                TestApplication.class,
                AuthUserRepositoryImplIT.TestConfiguration.class,
                DatabaseConfiguration.class
        }
)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class AuthUserRepositoryImplIT {
    private static final String TEST_USER_LOGIN = "test";
    @Configuration
    static class TestConfiguration {
        @Bean
        public AuthUserRepositoryImpl authUserRepository(DSLContext dsl) {
            return new AuthUserRepositoryImpl(dsl);
        }
    }

    @Autowired
    private AuthUserRepositoryImpl authRepo;
    @Autowired
    private UserRepositoryImpl userRepo;

    @Autowired
    private DSLContext ctx;

    @AfterAll
    void afterAll() {
        ctx.deleteFrom(USERS).execute();
    }

    @Test
    void shouldReturnAppFailureOnError() {
        val dsl = mock(DSLContext.class);
        when(dsl.select(USERS.ID, USERS.LOGIN, USERS.PASSWORD, USERS.TOKENID, USERS.NAME))
                .thenThrow(new RuntimeException("Dummy exception"));

        val repo = new AuthUserRepositoryImpl(dsl);

        val result = repo.findById(TEST_USER_LOGIN);
        assertThat(result.isLeft()).isTrue();
        val failure = result.getLeft();
        assertThat(failure).isInstanceOf(DatabaseFailure.class);
        assertThat(failure.getCode())
                .isEqualTo(DatabaseFailure.DatabaseFailureCode.AUTH_USER_REPOSITORY_FAILURE.toString());
        assertThat(failure.getDetail()).isEqualTo("Database access error");
        assertThat(failure.getMessage()).isEqualTo("Dummy exception");
    }

    private void addUser(String login) {
        userRepo.create(new CreateUserDto(login, "Test User", null, "abc12345"))
                .peekLeft(failure -> log.error("Error creating user:", failure.getCause()));
    }

    private void addUser() {
        addUser(TEST_USER_LOGIN);
    }

    @Test
    void shouldCheckForUserExistence() {
        var result = authRepo.exists(TEST_USER_LOGIN);

        result.peekLeft(failure -> log.error("exists error:", failure.getCause()));

        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isFalse();

        addUser();

        result = authRepo.exists(TEST_USER_LOGIN);
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isTrue();
    }

    @Test
    void shouldNotFindDeletedUser() {
        addUser("deleted");
        ctx.update(USERS).set(USERS.DELETED, true).execute();

        assertThat(authRepo.findById("deleted").get().isEmpty()).isTrue();
    }
}
