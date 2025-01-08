package ru.ra_tech.garden_manager.core.controllers.gardens;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ra_tech.garden_manager.core.controllers.AbstractController;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.CreateGardenRequest;
import ru.ra_tech.garden_manager.core.services.api.GardenService;

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
    @Timed("gardens.create")
    public ResponseEntity<Object> createGarden(CreateGardenRequest request) {
        return toResponse(getUserId().flatMap(userId -> service.createGarden(request, userId)), HttpStatus.CREATED);
    }

    @Override
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed("gardens.find")
    public ResponseEntity<Object> findGarden(@PathVariable Long id) {
        return toResponse(getUserId().flatMap(userId -> service.findGarden(id, userId)));
    }

    @Override
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed("gardens.list")
    public ResponseEntity<Object> listGardens() {
        return toResponse(getUserId().flatMap(service::listGardens));
    }

    @Override
    @PostMapping(value = "/{id}/add_participant/{participantId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed("gardens.add-user")
    public ResponseEntity<Object> addParticipant(@PathVariable Long id, @PathVariable Long participantId) {
        return toResponse(getUserId().flatMap(userId -> service.addParticipant(id, participantId, userId)));
    }

    @Override
    @DeleteMapping(
            value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROBLEM_JSON_VALUE}
    )
    @Timed("gardens.delete")
    public ResponseEntity<Object> deleteGarden(@Positive @PathVariable Long id) {
        return toEmptyResponse(getUserId().flatMap(userId -> service.delete(id, userId)));
    }
}
