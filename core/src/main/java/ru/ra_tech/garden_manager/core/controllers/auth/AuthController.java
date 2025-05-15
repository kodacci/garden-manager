package ru.ra_tech.garden_manager.core.controllers.auth;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ra_tech.garden_manager.core.controllers.AbstractController;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.LoginRequest;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.RefreshRequest;
import ru.ra_tech.garden_manager.core.services.api.AuthService;

@RestController
@RequestMapping(
        value = "/api/v1/auth",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE}
)
@RequiredArgsConstructor
@Slf4j
public class AuthController extends AbstractController implements AuthApi {
    private final AuthService service;

    @Override
    @PostMapping("/login")
    @Timed("auth.login")
    public ResponseEntity<Object> login(
            @RequestHeader("rqUid") String rqUid,
            @RequestBody LoginRequest request
    ) {
        log.info("Authenticating user {}", request.login());

        return toResponse(service.login(request.login(), request.password()));
    }

    @Override
    @PostMapping("/refresh")
    @Timed("auth.refresh")
    public ResponseEntity<Object> refresh(
            @RequestHeader("rqUid") String rqUid,
            @RequestBody RefreshRequest request
    ) {
        log.info("Refreshing user token");

        return toResponse(service.refresh(request.refreshToken()));
    }

    @Override
    @PostMapping(value = "/logout", consumes = MediaType.ALL_VALUE)
    @Timed("auth.logout")
    public ResponseEntity<Object> logout(@RequestHeader("rqUid") String rqUid) {
        return toResponse(
                getUserId().peek(userId -> log.info("Logging out for {}", userId))
                        .flatMap(service::logout)
        );
    }
}
