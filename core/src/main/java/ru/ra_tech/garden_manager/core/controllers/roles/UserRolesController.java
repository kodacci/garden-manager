package ru.ra_tech.garden_manager.core.controllers.roles;


import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ra_tech.garden_manager.core.controllers.AbstractController;
import ru.ra_tech.garden_manager.core.services.api.UserRoleService;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class UserRolesController extends AbstractController implements RolesApi {
    private final UserRoleService service;

    @Override
    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getAllRoles() {
        return toResponse(service.findAllRoles());
    }
}
