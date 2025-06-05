package ru.ra_tech.garden_manager.database.repositories.garden;

import javax.annotation.Nullable;

public record CreateGardenRequest(String name, @Nullable String address, long ownerId) {
}
