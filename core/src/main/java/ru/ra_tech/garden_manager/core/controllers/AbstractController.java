package ru.ra_tech.garden_manager.core.controllers;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.ErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.UnauthorizedResponse;
import ru.ra_tech.garden_manager.core.security.JwtPrincipal;

@Slf4j
public abstract class AbstractController {
    private record EmptyResponse() {}

    private ResponseEntity<Object> toError(ErrorResponse error) {
        val headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        log.error("Error while handling request: {}", error.getBody().getDetail());

        return new ResponseEntity<>(error.getBody(), headers, error.getStatusCode());
    }

    protected <T> ResponseEntity<Object> toResponse(Either<? extends ErrorResponse, T> result, HttpStatus status) {
        return result.fold(
                    this::toError,
                    data -> new ResponseEntity<>(data, status)
                );
    }

    protected <T> ResponseEntity<Object> toResponse(Either<? extends ErrorResponse, T> result) {
        return toResponse(result, HttpStatus.OK);
    }

    protected <T> ResponseEntity<Object> toEmptyResponse(Either<? extends  ErrorResponse, T> result) {
        return result.fold(
                this::toError,
                data -> new ResponseEntity<>(new EmptyResponse(), HttpStatus.OK)
        );
    }

    private Either<ErrorResponse, Long> getPrincipalId(Object principal) {
        return principal instanceof JwtPrincipal realPrincipal
                ? Either.right(realPrincipal.id())
                : Either.left(new UnauthorizedResponse());
    }

    protected Either<ErrorResponse, Long> getUserId() {
        return Option.of(SecurityContextHolder.getContext().getAuthentication())
                .toEither((ErrorResponse) new UnauthorizedResponse())
                .map(Authentication::getPrincipal)
                .flatMap(this::getPrincipalId);
    }
}
