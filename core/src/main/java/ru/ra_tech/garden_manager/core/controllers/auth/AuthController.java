package ru.ra_tech.garden_manager.core.controllers.auth;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ra_tech.garden_manager.core.controllers.AbstractController;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.LoginRequest;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.RefreshRequest;
import ru.ra_tech.garden_manager.core.services.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController extends AbstractController implements AuthApi {
    private final AuthService service;

    @Override
    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Timed("auth.login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        return toResponse(service.login(request.login(), request.password()));
    }

    @Override
    @PostMapping(
            value = "/refresh",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Timed("auth.refresh")
    public ResponseEntity<Object> refresh(@RequestBody RefreshRequest request) {
        return toResponse(service.refresh(request.refreshToken()));
    }

    @Override
    @PostMapping(
            value = "/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Timed("auth.logout")
    public ResponseEntity<Object> logout() {
        return toResponse(getUserId().flatMap(service::logout));
    }
}
