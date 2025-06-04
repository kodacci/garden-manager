package ru.ra_tech.garden_manager.core.controllers.gardens.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import javax.annotation.Nullable;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenParticipantDto;
import ru.ra_tech.garden_manager.database.repositories.user_role.UserRole;

public record GardenParticipantData(
        @NotNull @Positive
        Long id,
        @NotNull @Size(min = 3, max = 255)
        String login,
        @NotNull @Size(min = 1, max = 255)
        String name,
        @Nullable @Email
        String email,
        @NotNull @Valid
        UserRole role
) {
    public static GardenParticipantData of(GardenParticipantDto dto) {
            return new GardenParticipantData(
                    dto.id(),
                    dto.login(),
                    dto.name(),
                    dto.email(),
                    dto.role()
            );
    }
}
