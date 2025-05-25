package ru.ra_tech.garden_manager.core.controllers;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
import ru.ra_tech.garden_manager.core.controllers.error_responses.AppErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.UnauthorizedResponse;
import ru.ra_tech.garden_manager.core.security.JwtPrincipal;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
public abstract class AbstractController {
    @JsonSerialize
    private record EmptyResponse() {}

    private HttpHeaders generateTraceHeaders(String rqUid, OffsetDateTime rqTm) {
        val headers = new HttpHeaders();
        headers.set("rqUID", rqUid);
        headers.set("rqTm", rqTm.format(DateTimeFormatter.ISO_DATE_TIME));

        return headers;
    }

    private ResponseEntity<Object> toError(AppErrorResponse error, String rqUid, OffsetDateTime rqTm) {
        val headers = generateTraceHeaders(rqUid, rqTm);
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        Optional.ofNullable(error.getThrowable())
                .ifPresent(ex -> log.error("Error while handling request: {}", ex.getMessage()));

        return new ResponseEntity<>(error.getBody(), headers, error.getStatusCode());
    }

    protected <T> ResponseEntity<Object> toResponse(
            Either<? extends AppErrorResponse, T> result,
            HttpStatus status,
            String rqUid,
            OffsetDateTime rqTm
    ) {
        return result.fold(
                error -> toError(error, rqUid, rqTm),
                data -> new ResponseEntity<>(data, status)
        );
    }

    protected <T> ResponseEntity<Object> toResponse(
            Either<? extends AppErrorResponse, T> result,
            String rqUid,
            OffsetDateTime rqTm
    ) {
        return toResponse(result, HttpStatus.OK, rqUid, rqTm);
    }

    protected <T> ResponseEntity<Object> toEmptyResponse(
            Either<? extends  AppErrorResponse, T> result,
            String rqUid,
            OffsetDateTime rqTm
    ) {
        val headers = generateTraceHeaders(rqUid, rqTm);

        return result.fold(
                error -> toError(error, rqUid, rqTm),
                data -> new ResponseEntity<>(new EmptyResponse(), headers, HttpStatus.OK)
        );
    }

    private Either<AppErrorResponse, Long> getPrincipalId(Object principal) {
        return principal instanceof JwtPrincipal realPrincipal
                ? Either.right(realPrincipal.id())
                : Either.left(new UnauthorizedResponse());
    }

    protected Either<AppErrorResponse, Long> getUserId() {
        return Option.of(SecurityContextHolder.getContext().getAuthentication())
                .toEither((AppErrorResponse) new UnauthorizedResponse())
                .map(Authentication::getPrincipal)
                .flatMap(this::getPrincipalId);
    }
}
