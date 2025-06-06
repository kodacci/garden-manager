package ru.ra_tech.garden_manager.core.controllers.gardens;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ra_tech.garden_manager.core.controllers.AbstractController;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.CreateGardenRequest;
import ru.ra_tech.garden_manager.core.services.api.GardenService;

import java.time.OffsetDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/v1/gardens",
        consumes = APPLICATION_JSON_VALUE,
        produces = {APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE}
)
public class GardensController extends AbstractController implements GardensApi {
    private final GardenService service;

    @Override
    @PostMapping("")
    public ResponseEntity<Object> createGarden(
            @RequestHeader("rqUid") String rqUid,
            @RequestHeader("rqTm") OffsetDateTime rqTm,
            @RequestBody CreateGardenRequest request
    ) {
        return toResponse(
                getUserId().flatMap(userId -> service.createGarden(request, userId)),
                HttpStatus.CREATED,
                rqUid,
                rqTm
        );
    }

    @Override
    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> findGarden(
            @RequestHeader("rqUid") String rqUid,
            @RequestHeader("rqTm") OffsetDateTime rqTm,
            @PathVariable Long id
    ) {
        return toResponse(getUserId().flatMap(userId -> service.findGarden(id, userId)), rqUid, rqTm);
    }

    @Override
    @GetMapping(value = "")
    public ResponseEntity<Object> listGardens(
            @RequestHeader("rqUid") String rqUid,
            @RequestHeader("rqTm") OffsetDateTime rqTm
    ) {
        return toResponse(getUserId().flatMap(service::listGardens), rqUid, rqTm);
    }

    @Override
    @PostMapping(value = "/{id}/add_participant/{participantId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addParticipant(
            @RequestHeader("rqUid") String rqUid,
            @RequestHeader("rqTm") OffsetDateTime rqTm,
            @PathVariable Long id,
            @PathVariable Long participantId
    ) {
        return toResponse(getUserId().flatMap(userId -> service.addParticipant(id, participantId, userId)), rqUid, rqTm);
    }

    @Override
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteGarden(
            @RequestHeader("rqUid") String rqUid,
            @RequestHeader("rqTm") OffsetDateTime rqTm,
            @PathVariable Long id
    ) {
        return toEmptyResponse(getUserId().flatMap(userId -> service.deleteGarden(id, userId)), rqUid, rqTm);
    }

    @Override
    @PutMapping(value = "/{id}")
    public ResponseEntity<Object> updateGarden(
            @RequestHeader("rqUid") String rqUid,
            @RequestHeader("rqTm") OffsetDateTime rqTm,
            @PathVariable Long id,
            @RequestBody CreateGardenRequest update
    ) {
        return toResponse(getUserId().flatMap(userId -> service.updateGarden(id, userId, update)), rqUid, rqTm);
    }
}
