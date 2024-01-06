package ru.ra_tech.garden_manager.core.controllers.error_responses;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

public abstract class AbstractErrorResponse implements ErrorResponse {
    protected final ProblemDetail problem;

    protected final HttpStatusCode statusCode;

    protected AbstractErrorResponse(ProblemDetail problem) {
        this.statusCode = HttpStatusCode.valueOf(problem.getStatus());
        this.problem = problem;
    }

    @Override
    public ProblemDetail getBody() {
        return problem;
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
