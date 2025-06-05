package ru.ra_tech.garden_manager.core.controllers;

import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import javax.annotation.Nullable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.BindErrorUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String TIMESTAMP_PROP_NAME = "timestamp";

    private String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    private HttpHeaders getProblemHeaders() {
        val headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        return headers;
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        val problem = ex.getBody();
        problem.setProperty(TIMESTAMP_PROP_NAME, timestamp());

        val validations = BindErrorUtils.resolve(ex.getAllErrors()).values().stream().toList();
        problem.setProperty("validationErrors", validations);

        return handleExceptionInternal(ex, problem, getProblemHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    @Nullable
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return handleExceptionInternal(ex, problem, getProblemHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(Exception.class)
    @Nullable
    public ResponseEntity<Object> handleUnknownException(Exception ex, WebRequest request) {
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setProperty(TIMESTAMP_PROP_NAME, timestamp());

        return handleExceptionInternal(ex, problem, getProblemHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
