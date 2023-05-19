package ru.ra_tech.garden_manager.core.controllers.users;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.ra_tech.garden_manager.core.controllers.ApiErrorResponses;
import ru.ra_tech.garden_manager.core.controllers.users.dto.CreateUserRequest;
import ru.ra_tech.garden_manager.core.controllers.users.dto.UserData;

@Validated
@Tag(name = "Users")
public interface UsersApi extends ApiErrorResponses {
    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Got user with specified id",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserData.class)
                            )
                    }
            ),
    })
    ResponseEntity<?> getUserById(@Positive @PathVariable Integer id);

    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully created new user",
                    content = {
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = UserData.class)
                            )
                    }
            )
    })
    ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest data);

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully deleted user"
            )
    })
    ResponseEntity<?> deleteUser(@Positive @PathVariable Integer id);
}
