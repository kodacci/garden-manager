package ru.ra_tech.garden_manager.core.services;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import ru.ra_tech.garden_manager.core.controllers.error_responses.AppErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.EntityNotFoundResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.ForbiddenResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.ServerErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.CreateGardenRequest;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.GardenData;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.GardenParticipantData;
import ru.ra_tech.garden_manager.database.Transactional;
import ru.ra_tech.garden_manager.database.repositories.garden.CreateGardenDto;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenDto;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenRepository;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenUsersDto;
import ru.ra_tech.garden_manager.database.repositories.user_role.UserRole;
import ru.ra_tech.garden_manager.failure.AppFailure;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class GardenService {
    private static final String GARDEN_ENTITY = "Garden";

    private final GardenRepository repo;
    private final Transactional transactional;

    private CreateGardenDto toDto(CreateGardenRequest request, long ownerId) {
        return new CreateGardenDto(request.name(), request.address(), ownerId);
    }

    private AppErrorResponse toServerError(AppFailure failure) {
        return new ServerErrorResponse(failure);
    }

    public Either<AppErrorResponse, GardenData> createGarden(CreateGardenRequest request, long ownerId) {
        return repo.create(toDto(request, ownerId))
                .mapLeft(this::toServerError)
                .map(GardenData::of);
    }

    private Either<AppErrorResponse, GardenDto> checkUser(GardenDto garden, long userId) {
        if (garden.owner().id() == userId) {
            return Either.right(garden);
        } else {
            return Either.left(new ForbiddenResponse("User is not related to this garden"));
        }
    }

    private Either<AppErrorResponse, GardenDto> handleOption(Option<GardenDto> option, Long id) {
        return option.toEither(new EntityNotFoundResponse(GARDEN_ENTITY, id));
    }

    public Either<AppErrorResponse, GardenData> findGarden(long id, long userId) {
        return repo.findById(id)
                .mapLeft(this::toServerError)
                .flatMap(option -> handleOption(option, id))
                .flatMap(garden -> checkUser(garden, userId))
                .map(GardenData::of);
    }

    public Either<AppErrorResponse, List<GardenData>> listGardens(long userId) {
        return repo.listByUserId(userId)
                .mapLeft(this::toServerError)
                .map(list -> list.map(GardenData::of).asJava());
    }

    private <T> Either<AppErrorResponse, T> checkOwner(long userId, long ownerId, Supplier<T> supplier) {
        return ownerId == userId
                ? Either.right(supplier.get())
                : Either.left(new ForbiddenResponse("Only owner can add participants or delete garden"));
    }

    private Either<AppErrorResponse, Boolean> checkAndAddParticipant(
            GardenUsersDto gardenUsers, long participantId, long userId
    ) {
        return gardenUsers.participants().contains(participantId) || userId == participantId
                ? Either.right(false)
                : repo.addParticipant(gardenUsers.id(), participantId, UserRole.EXECUTOR).mapLeft(this::toServerError);
    }

    public Either<AppErrorResponse, List<GardenParticipantData>> addParticipant(long gardenId, long participantId, long userId) {
        return transactional.execute(
                status -> repo.getGardenUsers(gardenId)
                        .mapLeft(this::toServerError)
                        .flatMap(users -> users.toEither(new EntityNotFoundResponse(GARDEN_ENTITY, gardenId)))
                        .flatMap(garden -> checkOwner(userId, garden.ownerId(), () -> garden))
                        .flatMap(garden -> checkAndAddParticipant(garden, participantId, userId))
                        .flatMap(added -> repo.listParticipants(gardenId).mapLeft(this::toServerError))
                        .map(participants -> participants.map(GardenParticipantData::of).asJava()),
                this::toServerError
        );
    }

    public Either<AppErrorResponse, Boolean> delete(long id, long userId) {
        return transactional.execute(
                status -> repo.findById(id)
                            .mapLeft(this::toServerError)
                            .flatMap(option -> handleOption(option, id))
                            .flatMap(garden -> checkOwner(userId, garden.owner().id(), () -> garden))
                            .flatMap(garden -> repo.deleteById(garden.id()).mapLeft(this::toServerError))
                            .flatMap(
                                    deleted -> Boolean.TRUE.equals(deleted)
                                            ? Either.right(true)
                                            : Either.left(new EntityNotFoundResponse(GARDEN_ENTITY, id))
                            ),
                this::toServerError
        );
    }
}
