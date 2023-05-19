package ru.ra_tech.garden_manager.core.controllers.gardens.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateGardenRequest(
    @NotNull @Size(min = 3, max = 255)
    String name,
    String address
) {}
