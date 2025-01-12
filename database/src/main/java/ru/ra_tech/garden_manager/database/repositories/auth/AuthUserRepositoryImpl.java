package ru.ra_tech.garden_manager.database.repositories.auth;

import io.micrometer.core.annotation.Timed;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.lang.Nullable;
import ru.ra_tech.garden_manager.database.repositories.api.AuthUserRepository;
import ru.ra_tech.garden_manager.failure.DatabaseFailure;
import ru.ra_tech.garden_manager.failure.AppFailure;

import static ru.ra_tech.garden_manager.database.schema.tables.Users.USERS;

@RequiredArgsConstructor
public class AuthUserRepositoryImpl implements AuthUserRepository {
    private final DSLContext dsl;

    private AppFailure toFailure(Throwable exception) {
        return new DatabaseFailure(
                DatabaseFailure.DatabaseFailureCode.AUTH_USER_REPOSITORY_FAILURE, exception, getClass().getName()
        );
    }

    @Override
    @Timed(
            value = "repository.call",
            extraTags = {"repository_name", "auth-user"},
            histogram = true,
            percentiles = {0.90, 0.95, 0.99}
    )
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
    @Timed(
            value = "repository.call",
            extraTags = {"repository_name", "auth-user"},
            histogram = true,
            percentiles = {0.90, 0.95, 0.99}
    )
    public Either<AppFailure, Boolean> exists(String login) {
        return Try.of(
                () -> dsl.fetchExists(
                        dsl.selectFrom(USERS)
                                .where(USERS.LOGIN.eq(login))
                )
        )
                .toEither()
                .mapLeft(this::toFailure);
    }

    @Override
    @Timed(
            value = "repository.call",
            extraTags = {"repository_name", "auth-user"},
            histogram = true,
            percentiles = {0.90, 0.95, 0.99}
    )
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

    @Override
    @Timed(
            value = "repository.call",
            extraTags = {"repository_name", "auth-user"},
            histogram = true,
            percentiles = {0.90, 0.95, 0.99}
    )
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
