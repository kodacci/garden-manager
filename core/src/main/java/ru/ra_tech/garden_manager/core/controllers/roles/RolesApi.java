package ru.ra_tech.garden_manager.core.controllers.roles;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import ru.ra_tech.garden_manager.core.controllers.ApiErrorResponses;
import ru.ra_tech.garden_manager.core.controllers.roles.dto.UserRoleData;

@Validated
@Tag(name = "User Roles")
public interface RolesApi extends ApiErrorResponses {
    @Operation(
            summary = "Get all user roles"
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Received all user roles",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array = @ArraySchema(schema = @Schema(implementation = UserRoleData.class))
                )
        )
    })
    ResponseEntity<Object> getAllRoles();
}
