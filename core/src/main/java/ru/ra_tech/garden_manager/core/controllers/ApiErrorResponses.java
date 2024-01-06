package ru.ra_tech.garden_manager.core.controllers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import ru.ra_tech.garden_manager.core.controllers.error_responses.dto.ProblemResponse;

@ApiResponses(value = {
        @ApiResponse(
                responseCode = "400",
                description = "Bad request parameters",
                content = {
                        @Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = @Schema(implementation = ProblemResponse.class)
                        )
                }
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Entity not found",
                content = {
                        @Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = @Schema(implementation = ProblemResponse.class)
                        )
                }
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = {
                        @Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = @Schema(implementation = ProblemResponse.class)
                        )
                }
        ),
        @ApiResponse(
                responseCode = "500",
                description = "InternalServerError",
                content = {
                        @Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = @Schema(implementation = ProblemResponse.class)
                        )
                }
        ),
        @ApiResponse(
                responseCode = "409",
                description = "Conflict",
                content = {
                        @Content(
                                mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                schema = @Schema(implementation = ProblemResponse.class)
                        )
                }
        )
})
public interface ApiErrorResponses {
}
