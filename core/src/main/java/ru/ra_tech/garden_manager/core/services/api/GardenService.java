package ru.ra_tech.garden_manager.core.services.api;

import io.vavr.control.Either;
import ru.ra_tech.garden_manager.core.controllers.error_responses.AppErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.CreateGardenRequest;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.GardenData;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.GardenParticipantData;

import java.util.List;

public interface GardenService {
    Either<AppErrorResponse, GardenData> createGarden(CreateGardenRequest request, long ownerId);
    Either<AppErrorResponse, GardenData> findGarden(long id, long userId);
    Either<AppErrorResponse, List<GardenData>> listGardens(long userId);
    Either<AppErrorResponse, List<GardenParticipantData>> addParticipant(long gardenId, long participantId, long userId);
    Either<AppErrorResponse, Boolean> delete(long id, long userId);
}
