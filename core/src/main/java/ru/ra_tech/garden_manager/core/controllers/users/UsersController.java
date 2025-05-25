package ru.ra_tech.garden_manager.core.controllers.users;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ra_tech.garden_manager.core.controllers.AbstractController;
import ru.ra_tech.garden_manager.core.controllers.users.dto.CreateUserRequest;
import ru.ra_tech.garden_manager.core.services.api.UserService;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController extends AbstractController implements UsersApi {
    private final UserService service;

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Timed("users.get")
    @Override
    public ResponseEntity<Object> getUserById(
            @RequestHeader("rqUid") String rqUid,
            @RequestHeader("rqTm") OffsetDateTime rqTm,
            @PathVariable Integer id
    ) {
        return toResponse(service.findUser(id), rqUid, rqTm);
    }

    @PostMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Timed("users.create")
    @Override
    public ResponseEntity<Object> createUser(
            @RequestHeader("rqUid") String rqUid,
            @RequestHeader("rqTm") OffsetDateTime rqTm,
            @RequestBody CreateUserRequest data
    ) {
        return toResponse(service.createUser(data), HttpStatus.CREATED, rqUid, rqTm);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Object> deleteUser(
            @RequestHeader("rqUid") String rqUid,
            @RequestHeader("rqTm") OffsetDateTime rqTm,
            @PathVariable Integer id
    ) {
        return toEmptyResponse(service.deleteUser(id), rqUid, rqTm);
    }
}
