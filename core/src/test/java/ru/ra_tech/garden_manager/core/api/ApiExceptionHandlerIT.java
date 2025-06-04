package ru.ra_tech.garden_manager.core.api;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.LoginRequest;
import ru.ra_tech.garden_manager.core.controllers.error_responses.dto.ProblemResponse;
import ru.ra_tech.garden_manager.core.services.api.AuthService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ApiExceptionHandlerIT extends AbstractApiIT {
    private static final String LOGIN_URL = "/api/v1/auth/login";

    @MockitoBean
    private AuthService authService;

    @Test()
    @DisplayName("Should handle Unknown Application Exception")
    void shouldHandleUnknownException() {
        val message = "Dummy exception";
        when(authService.login(anyString(), anyString())).thenThrow(new RuntimeException(message));

        val request = new LoginRequest("test", "abc12345");
        val response = getRestTemplate().postForEntity(
                LOGIN_URL, new HttpEntity<>(request, generateTraceHeaders()), ProblemResponse.class
        );

        assertProblemResponse(response, HttpStatus.INTERNAL_SERVER_ERROR);

        val body = response.getBody();
        assertThat(body).isNotNull().isInstanceOf(ProblemResponse.class);
        assertThat(body.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(body.detail()).isEqualTo(message);
    }

    @Test()
    @DisplayName("Should handle AuthorizationException")
    void shouldHandleAuthorizationException() {
        val message = "Dummy exception";
        when(authService.login(anyString(), anyString())).thenThrow(new AuthenticationException(message) {});
        val request = new LoginRequest("test", "abc12345");
        val response = getRestTemplate().postForEntity(
                LOGIN_URL, new HttpEntity<>(request, generateTraceHeaders()), ProblemResponse.class
        );

        assertProblemResponse(response, HttpStatus.UNAUTHORIZED);

        val body = response.getBody();
        assertThat(body).isNotNull().isInstanceOf(ProblemResponse.class);
        assertThat(body.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(body.detail()).isEqualTo(message);
    }
}
