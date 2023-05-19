package ru.ra_tech.garden_manager.core.security;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TokenType {
    ACCESS("ACCESS"), REFRESH("REFRESH");

    private final String type;

    @Override
    public String toString() {
        return type;
    }
}
