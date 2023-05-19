package ru.ra_tech.garden_manager.core.controllers.users;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ra_tech.garden_manager.core.controllers.AbstractController;
import ru.ra_tech.garden_manager.core.controllers.users.dto.CreateUserRequest;
import ru.ra_tech.garden_manager.core.services.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController extends AbstractController implements UsersApi {
    private final UserService service;

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<?> getUserById(@Positive @PathVariable Integer id) {
        return toResponse(service.findUser(id));
    }

    @PostMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest data) {
        return toResponse(service.createUser(data), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> deleteUser(@Positive @PathVariable Integer id) {
        return toEmptyResponse(service.deleteUser(id));
    }
}
