package ru.ra_tech.garden_manager.core.security;

public record JwtPrincipal(long id, String login, String name) {
}
