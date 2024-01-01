package ru.ra_tech.garden_manager.database.repositories.auth;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.lang.Nullable;
import ru.ra_tech.garden_manager.failure.DatabaseFailure;
import ru.ra_tech.garden_manager.database.repositories.ReadableRepository;
import ru.ra_tech.garden_manager.failure.AppFailure;

import static ru.ra_tech.garden_manager.database.schema.tables.Users.USERS;

public class AuthUserRepository implements ReadableRepository<String, Option<AuthUserDto>> {
    private final DSLContext dsl;

    public AuthUserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private AppFailure toFailure(Throwable exception) {
        return new DatabaseFailure(
                DatabaseFailure.DatabaseFailureCode.AUTH_USER_REPOSITORY_FAILURE, exception, getClass().getName()
        );
    }

    @Override
    public Either<AppFailure, Option<AuthUserDto>> findById(String login) {
        return Try.of(
                () -> dsl.select(
                                USERS.ID,
                                USERS.LOGIN,
                                USERS.PASSWORD,
                                USERS.TOKENID,
                                USERS.NAME
                        )
                        .from(USERS)
                        .where(USERS.LOGIN.eq(login))
                        .fetchOneInto(AuthUserDto.class)
                )
                .toEither()
                .mapLeft(this::toFailure)
                .map(Option::of);
    }

    @Override
    public Either<AppFailure, Boolean> exists(String login) {
        return Try.of(
                () -> dsl.fetchCount(
                        DSL.selectFrom(USERS)
                                .where(USERS.LOGIN.eq(login))
                                .limit(1))
        )
                .toEither()
                .mapLeft(this::toFailure)
                .map(count -> count > 0);
    }

    public Either<AppFailure, Boolean> updateTokenId(String login, @Nullable String tokenId) {
        return Try.of(
                () -> dsl.update(USERS)
                        .set(USERS.TOKENID, tokenId)
                        .where(USERS.LOGIN.eq(login))
                        .execute()
        )
                .toEither()
                .map(count -> count > 0)
                .mapLeft(this::toFailure);
    }

    public Either<AppFailure, Boolean> clearSession(long userId) {
        return Try.of(
                () -> dsl.update(USERS)
                        .set(USERS.TOKENID, (String) null)
                        .where(USERS.ID.eq(userId))
                        .execute()
        )
                .toEither()
                .map(count -> count > 0)
                .mapLeft(this::toFailure);
    }
}
