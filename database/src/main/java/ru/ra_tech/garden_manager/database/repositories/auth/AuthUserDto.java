package ru.ra_tech.garden_manager.database.repositories.auth;

import org.springframework.lang.Nullable;

public record AuthUserDto(
        int id,
        String login,
        String password,
        @Nullable String tokenId,
        String name
) {}
