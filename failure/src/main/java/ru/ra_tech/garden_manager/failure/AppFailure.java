package ru.ra_tech.garden_manager.failure;

import org.springframework.lang.Nullable;

public interface AppFailure {
    String getCode();
    String getDetail();
    String getSource();
    @Nullable Throwable getCause();
    @Nullable String getMessage();
}
