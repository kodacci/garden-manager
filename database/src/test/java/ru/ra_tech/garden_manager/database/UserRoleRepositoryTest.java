package ru.ra_tech.garden_manager.database;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ra_tech.garden_manager.database.repositories.user_role.UserRoleRepository;
import ru.ra_tech.garden_manager.failure.DatabaseFailure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
class UserRoleRepositoryTest implements DatabaseTest {
    private static final int ADMIN_ID = 1;
    private static final int CHIEF_ID = 2;
    private static final int EXECUTOR_ID = 3;

    @Autowired
    private UserRoleRepository repo;
    @Autowired
    private DSLContext dsl;

    @Test
    void shouldFindRoleById() {
        var res = repo.findById(ADMIN_ID);

        assertThat(res.isRight()).isTrue();
        var role = res.get();
        assertThat(role.name()).isEqualTo("ADMIN");

        res = repo.findById(CHIEF_ID);
        assertThat(res.isRight()).isTrue();
        role = res.get();
        assertThat(role.name()).isEqualTo("CHIEF");

        res = repo.findById(EXECUTOR_ID);
        assertThat(res.isRight()).isTrue();
        role = res.get();
        assertThat(role.name()).isEqualTo("EXECUTOR");
    }

    @Test
    void shouldCheckRoleExistence() {
        var res = repo.exists(CHIEF_ID);
        assertThat(res.isRight()).isTrue();
        assertThat(res.get()).isTrue();

        res = repo.exists(100100);
        assertThat(res.isRight()).isTrue();
        assertThat(res.get()).isFalse();
    }

    @Test
    void shouldReturnAppFailureOnDbError() {
        val dsl = mock(DSLContext.class);
        when(dsl.selectCount()).thenThrow(new RuntimeException("Dummy exception"));

        val roleRepo = new UserRoleRepository(dsl);
        val res = roleRepo.exists(1);

        assertThat(res.isLeft()).isTrue();
        val failure = res.getLeft();
        assertThat(failure).isInstanceOf(DatabaseFailure.class);
        assertThat(failure.getCode())
                .isEqualTo(DatabaseFailure.DatabaseFailureCode.USER_ROLE_REPOSITORY_FAILURE.toString());
        assertThat(failure.getMessage()).contains("Dummy exception");
        assertThat(failure.getSource()).isEqualTo(UserRoleRepository.class.getName());
    }
}
