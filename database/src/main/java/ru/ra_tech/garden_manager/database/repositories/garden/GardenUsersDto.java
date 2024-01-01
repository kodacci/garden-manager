package ru.ra_tech.garden_manager.database.repositories.garden;

import io.vavr.collection.List;

public record GardenUsersDto(long id, long ownerId, List<Long> participants) {}
