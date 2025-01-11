package ru.ra_tech.garden_manager.core.services.impl;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import ru.ra_tech.garden_manager.core.controllers.error_responses.AppErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.ServerErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.roles.dto.UserRoleData;
import ru.ra_tech.garden_manager.core.services.api.UserRoleService;
import ru.ra_tech.garden_manager.database.repositories.api.UserRoleRepository;
import ru.ra_tech.garden_manager.database.repositories.user_role.UserRoleDto;

import java.util.List;

@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepository repo;

    private UserRoleData toData(UserRoleDto dto) {
            return new UserRoleData(
                    dto.id(),
                    dto.name(),
                    dto.description()
            );
    }

    public Either<AppErrorResponse, List<UserRoleData>> findAllRoles() {
        return repo.findAll()
                .map(roles -> roles.stream().map(this::toData).toList())
                .mapLeft(ServerErrorResponse::new);
    }
}
