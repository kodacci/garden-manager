package ru.ra_tech.garden_manager.database.repositories.auth;

import javax.annotation.Nullable;

public record AuthUserDto(
        long id,
        String login,
        String password,
        @Nullable String tokenId,
        String name
) {}
