package ru.ra_tech.garden_manager.core.controllers.error_responses;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.web.ErrorResponse;

public abstract class AbstractErrorResponse implements AppErrorResponse {
    protected final ProblemDetail problem;
    @Getter
    protected final HttpStatusCode statusCode;
    @Getter
    @Nullable
    protected final Throwable throwable;

    protected AbstractErrorResponse(ProblemDetail problem) {
        this(problem, null);
    }

    protected AbstractErrorResponse(ProblemDetail problem, @Nullable Throwable throwable) {
        this.statusCode = HttpStatusCode.valueOf(problem.getStatus());
        this.problem = problem;
        this.throwable = throwable;
    }

    @Override
    public ProblemDetail getBody() {
        return problem;
    }
}
