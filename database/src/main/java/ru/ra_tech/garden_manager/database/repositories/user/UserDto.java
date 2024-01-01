package ru.ra_tech.garden_manager.database.repositories.user;

import org.springframework.lang.Nullable;

public record UserDto(long id, String login, String name, @Nullable String email) {
}
