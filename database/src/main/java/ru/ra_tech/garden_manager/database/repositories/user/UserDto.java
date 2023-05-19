package ru.ra_tech.garden_manager.database.repositories.user;

import org.springframework.lang.Nullable;

public record UserDto(int id, String login, String name, @Nullable String email) {
}
