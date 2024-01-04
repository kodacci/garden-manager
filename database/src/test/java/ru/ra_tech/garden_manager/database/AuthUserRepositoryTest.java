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
import ru.ra_tech.garden_manager.database.repositories.auth.AuthUserRepository;
import ru.ra_tech.garden_manager.database.repositories.user.CreateUserDto;
import ru.ra_tech.garden_manager.database.repositories.user.UserRepository;
import ru.ra_tech.garden_manager.failure.DatabaseFailure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static ru.ra_tech.garden_manager.database.schema.tables.Users.USERS;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {
                TestApplication.class,
                AuthUserRepositoryTest.TestConfiguration.class,
                DatabaseConfiguration.class
        }
)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class AuthUserRepositoryTest {
    private static final String TEST_USER_LOGIN = "test";
    @Configuration
    static class TestConfiguration {
        @Bean
        public AuthUserRepository authUserRepository(DSLContext dsl) {
            return new AuthUserRepository(dsl);
        }
    }

    @Autowired
    private AuthUserRepository authRepo;
    @Autowired
    private UserRepository userRepo;

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

        val repo = new AuthUserRepository(dsl);

        val result = repo.findById(TEST_USER_LOGIN);
        assertThat(result.isLeft()).isTrue();
        val failure = result.getLeft();
        assertThat(failure).isInstanceOf(DatabaseFailure.class);
        assertThat(failure.getCode())
                .isEqualTo(DatabaseFailure.DatabaseFailureCode.AUTH_USER_REPOSITORY_FAILURE.toString());
        assertThat(failure.getDetail()).isEqualTo("Database access error");
        assertThat(failure.getMessage()).isEqualTo("Dummy exception");
    }

    private void addUser() {
        userRepo.create(new CreateUserDto(
                TEST_USER_LOGIN, "Test User", null, "abc12345"
        ))
                .peekLeft(failure -> log.error("Error creating user:", failure.getCause()));
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
}
