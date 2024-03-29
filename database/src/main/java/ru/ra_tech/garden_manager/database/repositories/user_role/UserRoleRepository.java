package ru.ra_tech.garden_manager.database.repositories.user_role;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Records;
import ru.ra_tech.garden_manager.database.repositories.ReadableRepository;
import ru.ra_tech.garden_manager.failure.AppFailure;
import ru.ra_tech.garden_manager.failure.DatabaseFailure;

import java.util.List;

import static ru.ra_tech.garden_manager.database.schema.tables.UserRoles.USER_ROLES;

@RequiredArgsConstructor
public class UserRoleRepository implements ReadableRepository<Integer, UserRoleDto> {
    private static final int SELECT_LIMIT = 100;

    private final DSLContext dsl;

    private AppFailure toFailure(Throwable error) {
        return new DatabaseFailure(
                DatabaseFailure.DatabaseFailureCode.USER_ROLE_REPOSITORY_FAILURE,
                error,
                getClass().getName()
        );
    }

    public Either<AppFailure, List<UserRoleDto>> findAll() {
        return Try.of(
                () -> dsl.select(USER_ROLES.ID, USER_ROLES.NAME, USER_ROLES.DESCRIPTION)
                        .from(USER_ROLES)
                        .limit(SELECT_LIMIT)
                        .fetch(Records.mapping(UserRoleDto::new))
        )
                .toEither()
                .mapLeft(this::toFailure);
    }

    @Override
    public Either<AppFailure, UserRoleDto> findById(Integer id) {
        return Try.of(
                () -> dsl.select(USER_ROLES.ID, USER_ROLES.NAME, USER_ROLES.DESCRIPTION)
                        .from(USER_ROLES)
                        .where(USER_ROLES.ID.eq(id))
                        .fetchOneInto(UserRoleDto.class)
        )
                .toEither()
                .mapLeft(this::toFailure);
    }

    @Override
    public Either<AppFailure, Boolean> exists(Integer id) {
        return Try.of(
                () -> dsl.selectCount()
                        .from(USER_ROLES)
                        .where(USER_ROLES.ID.eq(id))
                        .fetchOneInto(Integer.class)
        )
                .toEither()
                .mapLeft(this::toFailure)
                .map(count -> count > 0);
    }
}
