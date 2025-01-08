package ru.ra_tech.garden_manager.core.services.api;

import io.vavr.control.Either;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.LoginResponse;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.LogoutResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.AppErrorResponse;

public interface AuthService {
    Either<AppErrorResponse, LoginResponse> login(String login, String password);
    Either<AppErrorResponse, LoginResponse> refresh(String token);
    Either<AppErrorResponse, LogoutResponse> logout(long userId);
}
