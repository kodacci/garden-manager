package ru.ra_tech.garden_manager.database.repositories.api;

import io.vavr.control.Either;
import ru.ra_tech.garden_manager.database.repositories.user_role.UserRoleDto;
import ru.ra_tech.garden_manager.failure.AppFailure;

import java.util.List;

public interface UserRoleRepository extends ReadableRepository<Integer, UserRoleDto> {
    Either<AppFailure, List<UserRoleDto>> findAll();
}
