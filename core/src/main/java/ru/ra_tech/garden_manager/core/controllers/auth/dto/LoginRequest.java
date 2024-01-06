package ru.ra_tech.garden_manager.core.controllers.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotNull @Size(min = 3, max = 255) String login,
        @NotNull @Size(min = 8, max = 255) String password
) {}
