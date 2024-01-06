package ru.ra_tech.garden_manager.core.controllers.auth.dto;

import jakarta.validation.constraints.NotNull;

public record RefreshRequest(@NotNull String refreshToken) {
}
