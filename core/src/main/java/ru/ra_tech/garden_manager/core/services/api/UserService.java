package ru.ra_tech.garden_manager.core.services.api;

import io.vavr.control.Either;
import ru.ra_tech.garden_manager.core.controllers.error_responses.AppErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.users.dto.CreateUserRequest;
import ru.ra_tech.garden_manager.core.controllers.users.dto.UserData;

public interface UserService {
    Either<AppErrorResponse, UserData> findUser(long id);
    Either<AppErrorResponse, UserData> createUser(CreateUserRequest user);
    Either<AppErrorResponse, Boolean> deleteUser(long id);
}
