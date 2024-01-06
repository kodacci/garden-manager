package ru.ra_tech.garden_manager.core.controllers.error_responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ForbiddenResponse extends AbstractErrorResponse {
    public ForbiddenResponse(String message) {
        super(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, message));
    }
}
