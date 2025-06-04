package ru.ra_tech.garden_manager.core.controllers.error_responses;

import javax.annotation.Nullable;
import org.springframework.web.ErrorResponse;

public interface AppErrorResponse extends ErrorResponse {
    @Nullable
    Throwable getThrowable();
}
