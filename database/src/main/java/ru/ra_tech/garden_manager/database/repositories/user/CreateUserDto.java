package ru.ra_tech.garden_manager.database.repositories.user;

import javax.annotation.Nullable;

public record CreateUserDto(String login, String name, @Nullable String email, String password) {
}
