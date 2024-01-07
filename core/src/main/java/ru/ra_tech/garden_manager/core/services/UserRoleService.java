package ru.ra_tech.garden_manager.core.services;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.web.ErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.ServerErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.roles.dto.UserRoleData;
import ru.ra_tech.garden_manager.database.repositories.user_role.UserRoleDto;
import ru.ra_tech.garden_manager.database.repositories.user_role.UserRoleRepository;

import java.util.List;

@RequiredArgsConstructor
public class UserRoleService {
    private final UserRoleRepository repo;

    private UserRoleData toData(UserRoleDto dto) {
            return new UserRoleData(
                    dto.id(),
                    dto.name(),
                    dto.description()
            );
    }

    public Either<ErrorResponse, List<UserRoleData>> findAllRoles() {
        return repo.findAll()
                .map(roles -> roles.stream().map(this::toData).toList())
                .mapLeft(ServerErrorResponse::new);
    }
}
