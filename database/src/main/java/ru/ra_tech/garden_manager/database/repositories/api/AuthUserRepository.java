package ru.ra_tech.garden_manager.database.repositories.api;

import io.vavr.control.Either;
import io.vavr.control.Option;
import org.springframework.lang.Nullable;
import ru.ra_tech.garden_manager.database.repositories.auth.AuthUserDto;
import ru.ra_tech.garden_manager.failure.AppFailure;

public interface AuthUserRepository extends ReadableRepository<String, Option<AuthUserDto>> {
    Either<AppFailure, Boolean> updateTokenId(String login, @Nullable String tokenId);
    Either<AppFailure, Boolean> clearSession(long userId);
}
