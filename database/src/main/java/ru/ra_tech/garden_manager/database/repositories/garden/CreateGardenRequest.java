package ru.ra_tech.garden_manager.database.repositories.garden;

import org.springframework.lang.Nullable;

public record CreateGardenRequest(String name, @Nullable String address, long ownerId) {
}
