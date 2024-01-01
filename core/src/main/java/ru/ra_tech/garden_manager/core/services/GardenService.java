package ru.ra_tech.garden_manager.core.services;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.web.ErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.EntityNotFoundResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.ForbiddenResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.ServerErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.CreateGardenRequest;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.GardenData;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.GardenParticipantData;
import ru.ra_tech.garden_manager.database.repositories.garden.CreateGardenDto;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenDto;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenRepository;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenUsersDto;
import ru.ra_tech.garden_manager.database.repositories.user_role.UserRole;
import ru.ra_tech.garden_manager.failure.AppFailure;

import java.util.List;

@RequiredArgsConstructor
public class GardenService {
    private static final String GARDEN_ENTITY = "Garden";

    private final GardenRepository repo;

    private CreateGardenDto toDto(CreateGardenRequest request, long ownerId) {
        return new CreateGardenDto(request.name(), request.address(), ownerId);
    }

    private ErrorResponse toServerError(AppFailure failure) {
        return new ServerErrorResponse(failure);
    }

    public Either<ErrorResponse, GardenData> createGarden(CreateGardenRequest request, long ownerId) {
        return repo.create(toDto(request, ownerId))
                .mapLeft(this::toServerError)
                .map(GardenData::of);
    }

    private Either<ErrorResponse, GardenDto> checkUser(GardenDto garden, long userId) {
        if (garden.owner().id() == userId) {
            return Either.right(garden);
        } else {
            return Either.left(new ForbiddenResponse("User is not related to this garden"));
        }
    }

    public Either<ErrorResponse, GardenData> findGarden(long id, long userId) {
        return repo.findById(id)
                .mapLeft(this::toServerError)
                .flatMap(option -> option.toEither(new EntityNotFoundResponse(GARDEN_ENTITY, id)))
                .flatMap(garden -> checkUser(garden, userId))
                .map(GardenData::of);
    }

    public Either<ErrorResponse, List<GardenData>> listGardens(long userId) {
        return repo.listByUserId(userId)
                .mapLeft(this::toServerError)
                .map(list -> list.map(GardenData::of).asJava());
    }

    private Either<ErrorResponse, GardenUsersDto> checkOwner(long userId, GardenUsersDto garden) {
        return garden.ownerId() == userId
                ? Either.right(garden)
                : Either.left(new ForbiddenResponse("Only owner can add participants"));
    }

    private Either<ErrorResponse, Boolean> checkAndAddParticipant(
            GardenUsersDto gardenUsers, long participantId, long userId
    ) {
        return gardenUsers.participants().contains(participantId) || userId == participantId
                ? Either.right(false)
                : repo.addParticipant(gardenUsers.id(), participantId, UserRole.EXECUTOR).mapLeft(this::toServerError);
    }

    public Either<ErrorResponse, List<GardenParticipantData>> addParticipant(long gardenId, long participantId, long userId) {
        return repo.getGardenUsers(gardenId)
                .mapLeft(this::toServerError)
                .flatMap(users -> users.toEither(new EntityNotFoundResponse(GARDEN_ENTITY, gardenId)))
                .flatMap(garden -> checkOwner(userId, garden))
                .flatMap(garden -> checkAndAddParticipant(garden, participantId, userId))
                .flatMap(added -> repo.listParticipants(gardenId).mapLeft(this::toServerError))
                .map(participants -> participants.map(GardenParticipantData::of).asJava());
    }
}
