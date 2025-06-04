package ru.ra_tech.garden_manager.failure;

import lombok.RequiredArgsConstructor;
import lombok.val;
import javax.annotation.Nullable;

@RequiredArgsConstructor
public abstract class AbstractFailure implements AppFailure {
    private final String source;
    private final @Nullable Throwable cause;

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public @Nullable Throwable getCause() {
        return cause;
    }

    @Override
    public @Nullable String getMessage() {
        if (cause == null) {
            return "Unknown error";
        }

        val message = cause.getMessage();
        return message == null ? cause.toString() : message;
    }
}
