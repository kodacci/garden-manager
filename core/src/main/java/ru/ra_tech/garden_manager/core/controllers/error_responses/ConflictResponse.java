package ru.ra_tech.garden_manager.core.controllers.error_responses;

import io.vavr.collection.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ConflictResponse extends AbstractErrorResponse {

    public ConflictResponse(String entity, List<String> fields) {
        super(ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                String.format("%s with specified %s already exists", entity, String.join(" or ", fields))
        ));
    }
}
