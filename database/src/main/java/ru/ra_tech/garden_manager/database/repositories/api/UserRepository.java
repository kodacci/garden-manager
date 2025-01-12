package ru.ra_tech.garden_manager.database.repositories.api;

import io.vavr.control.Either;
import io.vavr.control.Option;
import ru.ra_tech.garden_manager.database.repositories.user.CreateUserDto;
import ru.ra_tech.garden_manager.database.repositories.user.UserDto;
import ru.ra_tech.garden_manager.failure.AppFailure;

public interface UserRepository extends ReadableRepository<Long, Option<UserDto>>,
        WritableRepository<Long, CreateUserDto, UserDto> {
    Either<AppFailure, Boolean> checkDataConflict(String login, Option<String> email);
}
