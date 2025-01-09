package ru.ra_tech.garden_manager.database.repositories.api;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import ru.ra_tech.garden_manager.database.repositories.garden.CreateGardenRequest;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenDto;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenParticipantDto;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenUsersDto;
import ru.ra_tech.garden_manager.database.repositories.user_role.UserRole;
import ru.ra_tech.garden_manager.failure.AppFailure;

public interface GardenRepository {
    Either<AppFailure, Option<GardenDto>> findById(Long id);
    Either<AppFailure, GardenDto> create(CreateGardenRequest garden);
    Either<AppFailure, List<GardenDto>> listByUserId(long id);
    Either<AppFailure, Option<GardenUsersDto>> getGardenUsers(long gardenId);
    Either<AppFailure, Boolean> addParticipant(long gardenId, long userId, UserRole role);
    Either<AppFailure, List<GardenParticipantDto>> listParticipants(long gardenId);
    Either<AppFailure, Boolean> deleteById(Long id);
    Either<AppFailure, Option<GardenDto>> update(Long id, CreateGardenRequest update);
}
