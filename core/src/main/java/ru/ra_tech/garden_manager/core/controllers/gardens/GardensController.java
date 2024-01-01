package ru.ra_tech.garden_manager.core.controllers.gardens;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ra_tech.garden_manager.core.controllers.AbstractController;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.CreateGardenRequest;
import ru.ra_tech.garden_manager.core.services.GardenService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/gardens")
public class GardensController extends AbstractController implements GardensApi {
    private final GardenService service;

    @Override
    @PostMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> createGarden(CreateGardenRequest request) {
        return toResponse(getUserId().flatMap(userId -> service.createGarden(request, userId)), HttpStatus.CREATED);
    }

    @Override
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findGarden(Long id) {
        return toResponse(getUserId().flatMap(userId -> service.findGarden(id, userId)));
    }

    @Override
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listGardens() {
        return toResponse(getUserId().flatMap(service::listGardens));
    }

    @Override
    @PostMapping(value = "/{id}/add_participant/{participantId}")
    public ResponseEntity<?> addParticipant(Long id, Long participantId) {
        return toResponse(getUserId().flatMap(userId -> service.addParticipant(id, participantId, userId)));
    }
}
