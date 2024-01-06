package ru.ra_tech.garden_manager.database.repositories.user;

import org.springframework.lang.Nullable;

public record CreateUserDto(String login, String name, @Nullable String email, String password) {
}
