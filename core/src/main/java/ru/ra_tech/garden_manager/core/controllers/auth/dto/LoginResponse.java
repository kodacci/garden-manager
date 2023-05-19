package ru.ra_tech.garden_manager.core.controllers.auth.dto;

import jakarta.validation.constraints.NotNull;
import ru.ra_tech.garden_manager.core.security.JwtProvider;

public record LoginResponse(
        @NotNull
        String accessToken,
        @NotNull
        String refreshToken
) {
        public LoginResponse(JwtProvider.TokenPair pair) {
                this(pair.access(), pair.refresh());
        }
}
