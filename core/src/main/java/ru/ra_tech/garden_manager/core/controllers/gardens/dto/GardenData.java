package ru.ra_tech.garden_manager.core.controllers.gardens.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import javax.annotation.Nullable;
import ru.ra_tech.garden_manager.core.controllers.users.dto.UserData;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenDto;

import java.util.List;

public record GardenData(
        @NotNull @Positive
        Long id,
        @NotNull @Size(min = 3, max = 255)
        String name,
        @Nullable
        String address,
        @NotNull @Valid
        UserData owner,
        @NotNull @Valid
        List<@Valid GardenParticipantData> participants
) {
        public static GardenData of(GardenDto dto) {
                return new GardenData(
                        dto.id(),
                        dto.name(),
                        dto.address(),
                        UserData.of(dto.owner()),
                        dto.participants().map(GardenParticipantData::of).asJava()
                );
        }
}
