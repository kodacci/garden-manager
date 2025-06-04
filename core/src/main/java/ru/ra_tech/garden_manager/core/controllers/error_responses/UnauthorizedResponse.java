package ru.ra_tech.garden_manager.core.controllers.error_responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import javax.annotation.Nullable;

import java.net.URI;

public class UnauthorizedResponse extends AbstractErrorResponse {
    public UnauthorizedResponse(@Nullable String path) {
        super(ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized access"
        ));

        if (path != null) {
            problem.setInstance(URI.create(path));
        }
    }

    public UnauthorizedResponse() {
        super(ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized access"
        ));
    }
}
