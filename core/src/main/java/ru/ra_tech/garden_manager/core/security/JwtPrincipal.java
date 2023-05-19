package ru.ra_tech.garden_manager.core.security;

public record JwtPrincipal(int id, String login, String name) {
}
