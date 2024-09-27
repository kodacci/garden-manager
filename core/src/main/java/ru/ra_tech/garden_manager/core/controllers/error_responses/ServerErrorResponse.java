package ru.ra_tech.garden_manager.core.controllers.error_responses;

import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import ru.ra_tech.garden_manager.failure.AppFailure;

public class ServerErrorResponse extends AbstractErrorResponse {
    public ServerErrorResponse(AppFailure failure) {
        super(
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        failure.getDetail()
                ),
                failure.getCause()
        );

        problem.setProperty("code", failure.getCode());
        problem.setProperty("source", failure.getSource());

        val cause = failure.getCause();
        if (cause != null) {
            problem.setProperty("message", cause.getMessage());
        }
    }
}
