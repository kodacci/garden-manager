package ru.ra_tech.garden_manager.core.services.api;

import io.vavr.control.Either;
import ru.ra_tech.garden_manager.core.controllers.error_responses.AppErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.roles.dto.UserRoleData;

import java.util.List;

public interface UserRoleService {
    Either<AppErrorResponse, List<UserRoleData>> findAllRoles();
}
