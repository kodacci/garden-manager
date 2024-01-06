package ru.ra_tech.garden_manager.core.controllers.users.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.ra_tech.garden_manager.database.repositories.user.UserDto;

public record UserData(
        @Positive @NotNull
        Long id,
        @Size(min = 1, max = 255) @NotNull
        String login,
        @Size(min = 1, max = 255) @NotNull
        String name,
        @Nullable @Email
        String email
) {
        public static UserData of(UserDto dto) {
                return new UserData(dto.id(), dto.login(), dto.name(), dto.email());
        }
}
