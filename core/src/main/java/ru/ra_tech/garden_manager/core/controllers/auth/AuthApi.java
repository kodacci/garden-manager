package ru.ra_tech.garden_manager.core.controllers.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import ru.ra_tech.garden_manager.core.controllers.ApiErrorResponses;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.LoginRequest;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.LoginResponse;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.LogoutResponse;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.RefreshRequest;

@Validated
@Tag(name = "Authorization")
public interface AuthApi extends ApiErrorResponses {
    @Operation(summary = "Authorize")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authorized with login and password",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = LoginResponse.class)
                            )
                    }
            )
    })
    ResponseEntity<Object> login(@Valid @RequestBody LoginRequest request);

    @Operation(summary = "Refresh token")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully refreshed token pair",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = LoginResponse.class)
                            )
                    }
            )
    })
    ResponseEntity<Object> refresh(@Valid @RequestBody RefreshRequest request);

    @Operation(summary = "Logout")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully logged out",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = LogoutResponse.class)
                            )
                    }
            )
    })
    ResponseEntity<Object> logout();
}
