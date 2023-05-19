package ru.ra_tech.garden_manager.core.controllers.error_responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class EntityNotFoundResponse extends AbstractErrorResponse {
    private static final String MESSAGE = "%s with id #%d was not found";

    public EntityNotFoundResponse(String entity, int id) {
        super(ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                String.format(MESSAGE, entity, id)
        ));
    }
}
