package ru.ra_tech.garden_manager.database.repositories.user;

import javax.annotation.Nullable;

public record UserDto(long id, String login, String name, @Nullable String email) {
}
