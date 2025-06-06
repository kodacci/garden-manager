package ru.ra_tech.garden_manager.core.controllers.gardens;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.ra_tech.garden_manager.core.controllers.ApiErrorResponses;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.CreateGardenRequest;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.GardenData;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.GardenParticipantData;

import java.time.OffsetDateTime;

@Validated
@Tag(name = "Gardens")
public interface GardensApi extends ApiErrorResponses {
    @Operation(summary = "Add new garden")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully created new garden",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = GardenData.class)
                            )
                    }
            )
    })
    ResponseEntity<Object> createGarden(
            @NotEmpty @RequestHeader("rqUid") String rqUid,
            @NotNull @RequestHeader("rqTm") OffsetDateTime rqTm,
            @RequestBody CreateGardenRequest request
    );

    @Operation(summary = "Find garden by id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully received specified garden",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = GardenData.class)
                            )
                    }
            )
    })
    ResponseEntity<Object> findGarden(
            @NotEmpty @RequestHeader("rqUid") String rqUid,
            @NotNull @RequestHeader("rqTm") OffsetDateTime rqTm,
            @Positive @PathVariable Long id
    );

    @Operation(summary = "List user related gardens")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully received related gardens list",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = GardenData.class))
                            )
                    }
            )
    })
    ResponseEntity<Object> listGardens(
            @NotEmpty @RequestHeader("rqUid") String rqUid,
            @NotNull @RequestHeader("rqTm") OffsetDateTime rqTm
    );

    @Operation(summary = "Add participant to garden")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully added new participant to garden",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = GardenParticipantData.class))
                            )
                    }
            )
    })
    ResponseEntity<Object> addParticipant(
            @NotEmpty @RequestHeader("rqUid") String rqUid,
            @NotNull @RequestHeader("rqTm") OffsetDateTime rqTm,
            @Positive @PathVariable Long id,
            @Positive @PathVariable Long participantId
    );

    @Operation(summary = "Delete garden")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully deleted garden"
            )
    })
    ResponseEntity<Object> deleteGarden(
            @NotEmpty @RequestHeader("rqUid") String rqUid,
            @NotNull @RequestHeader("rqTm") OffsetDateTime rqTm,
            @Positive @PathVariable Long id
    );

    @Operation(summary = "Update garden")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully updated garden",
                    content = {
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = GardenData.class)
                            )
                    }
            )
    })
    ResponseEntity<Object> updateGarden(
            @NotEmpty @RequestHeader("rqUid") String rqUid,
            @NotNull @RequestHeader("rqTm") OffsetDateTime rqTm,
            @Positive @PathVariable Long id,
            @RequestBody CreateGardenRequest update
    );
}
