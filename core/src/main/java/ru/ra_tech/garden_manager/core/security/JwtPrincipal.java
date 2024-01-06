package ru.ra_tech.garden_manager.core.security;

import java.io.Serial;
import java.io.Serializable;

public record JwtPrincipal(long id, String login, String name) implements Serializable {
    @Serial
    private static final long serialVersionUID = 2024_01_06_22_00_00L;
}
