package ru.ra_tech.garden_manager.failure;

import org.springframework.lang.Nullable;

public class UnknownFailure extends AbstractFailure {
    public UnknownFailure(String source, @Nullable Throwable cause) {
        super(source, cause);
    }

    @Override
    public String getCode() {
        return "GM:UNKNOWN_ERROR";
    }

    @Override
    public String getDetail() {
        return String.format("Unknown error: %s", getMessage());
    }
}
