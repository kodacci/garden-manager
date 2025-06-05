package ru.ra_tech.garden_manager.database.repositories.garden;

import io.vavr.collection.List;
import javax.annotation.Nullable;
import ru.ra_tech.garden_manager.database.repositories.user.UserDto;

public record GardenDto(
        long id,
        String name,
        @Nullable String address,
        UserDto owner,
        List<GardenParticipantDto> participants
) {
}
