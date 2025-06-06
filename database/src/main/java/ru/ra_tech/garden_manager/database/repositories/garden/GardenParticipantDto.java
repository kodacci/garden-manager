package ru.ra_tech.garden_manager.database.repositories.garden;

import javax.annotation.Nullable;
import ru.ra_tech.garden_manager.database.repositories.user_role.UserRole;

public record GardenParticipantDto(long id, String login, String name, @Nullable String email, UserRole role) {
}
