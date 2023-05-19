package ru.ra_tech.garden_manager.database.repositories.user_role;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRole {
    ADMIN("ADMIN"), CHIEF("CHIEF"), EXECUTOR("EXECUTOR");

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
