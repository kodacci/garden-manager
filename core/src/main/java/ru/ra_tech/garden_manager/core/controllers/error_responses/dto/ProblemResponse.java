package ru.ra_tech.garden_manager.core.controllers.error_responses.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ProblemResponse(
        @NotNull
        String type,
        @NotEmpty @NotNull
        String title,
        @Positive
        Integer status,
        @NotEmpty @NotNull
        String detail,
        @Nullable
        String instance,
        @Nullable
        String message,
        @Nullable @Pattern(regexp = "^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2}(?:\\.\\d*)?)((-(\\d{2}):(\\d{2})|Z)?)$")
        String timestamp,
        @Nullable
        String source,
        @Nullable
        List<String> validationErrors
) {}
