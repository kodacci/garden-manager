package ru.ra_tech.garden_manager.database.repositories.garden;

import io.vavr.collection.List;
import org.springframework.lang.Nullable;
import ru.ra_tech.garden_manager.database.repositories.user.UserDto;

public record GardenDto(
        int id,
        String name,
        @Nullable String address,
        UserDto owner,
        List<GardenParticipantDto> participants
) {
}
