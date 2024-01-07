package ru.ra_tech.garden_manager.core.controllers.roles.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UserRoleData(
        @Positive @NotNull
        long id,
        @NotNull @NotEmpty @Size(min = 1, max = 255)
        String name,
        @Nullable
        String description
) {
}
