package ru.ra_tech.garden_manager.core.controllers.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import javax.annotation.Nullable;

public record CreateUserRequest(
        @NotNull @Size(min = 3, max = 255)
        String login,
        @NotNull @Size(min = 1, max = 255)
        String name,
        @Nullable @Email
        String email,
        @NotNull @Size(min = 8, max = 255)
        String password
) {}
