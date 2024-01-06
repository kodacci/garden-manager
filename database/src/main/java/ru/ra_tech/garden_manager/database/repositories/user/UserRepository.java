package ru.ra_tech.garden_manager.database.repositories.user;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.val;
import org.jooq.Condition;
import org.jooq.DSLContext;
import ru.ra_tech.garden_manager.database.repositories.AbstractRWRepository;
import ru.ra_tech.garden_manager.database.schema.tables.records.UsersRecord;
import ru.ra_tech.garden_manager.failure.DatabaseFailure;
import ru.ra_tech.garden_manager.failure.AppFailure;

import java.util.Objects;

import static ru.ra_tech.garden_manager.database.schema.tables.Users.USERS;

public class UserRepository extends AbstractRWRepository<Long, CreateUserDto, UserDto, UsersRecord> {

    public UserRepository(DSLContext dsl) {
        super(dsl, USERS);
    }

    private Either<AppFailure, Option<UserDto>> find(Condition condition) {
        return Try.of(
                () -> getContext().select(USERS.ID, USERS.LOGIN, USERS.NAME, USERS.EMAIL)
                        .from(USERS)
                        .where(condition.and(USERS.DELETED.eq(false)))
                        .fetchOneInto(UserDto.class)
        )
                .toEither()
                .mapLeft(this::toFailure)
                .map(Option::of);
    }

    @Override
    public Either<AppFailure, Option<UserDto>> findById(Long id) {
        return find(USERS.ID.eq(id));
    }

    public Either<AppFailure, Boolean> checkDataConflict(String login, Option<String> email) {
        val condition = email.fold(
                () -> USERS.LOGIN.eq(login),
                mail -> USERS.LOGIN.eq(login).or(USERS.EMAIL.eq(mail))
        );

        return Try.of(
                () -> getContext().selectCount()
                        .from(USERS)
                        .where(condition)
                        .limit(1)
                        .fetchOne(0, int.class)
        )
                .toEither()
                .mapLeft(this::toFailure)
                .map(count -> count > 0);
    }

    @Override
    protected AppFailure toFailure(Throwable exception) {
        return new DatabaseFailure(
                DatabaseFailure.DatabaseFailureCode.USER_REPOSITORY_FAILURE,
                exception,
                getClass().getName()
        );
    }

    @Override
    public Either<AppFailure, UserDto> create(CreateUserDto user) {
        return Try.of(
                () -> getContext().insertInto(USERS)
                        .set(USERS.LOGIN, user.login())
                        .set(USERS.NAME, user.name())
                        .set(USERS.EMAIL, user.email())
                        .set(USERS.PASSWORD, user.password())
                        .returningResult(USERS.ID, USERS.LOGIN, USERS.NAME, USERS.EMAIL)
                        .fetchOneInto(UserDto.class)
        )
                .andThen(Objects::requireNonNull)
                .toEither()
                .mapLeft(this::toFailure);
    }
}
