package ru.ra_tech.garden_manager.core.api;

import jakarta.annotation.Nullable;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.LoginRequest;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.LoginResponse;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.LogoutResponse;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.RefreshRequest;
import ru.ra_tech.garden_manager.core.controllers.error_responses.dto.ProblemResponse;
import ru.ra_tech.garden_manager.core.security.TokenType;
import ru.ra_tech.garden_manager.database.repositories.user.CreateUserDto;

import static org.assertj.core.api.Assertions.*;
import static ru.ra_tech.garden_manager.core.api.TestUtils.writeUser;
import static ru.ra_tech.garden_manager.database.schema.Tables.USERS;

@DisplayName("Authorization API test")
class AuthApiTest extends AbstractApiTest {
    private static final String AUTH_API_URL = "/api/v1/auth";
    private static final String LOGIN_URL = String.format("%s/login", AUTH_API_URL);

    @AfterAll
    void afterAll() {
        getDsl().deleteFrom(USERS).execute();
    }

    private @Nullable String readTokenId(String login) {
        return getDsl().select(USERS.TOKENID)
                .from(USERS)
                .where(USERS.LOGIN.eq(login))
                .fetchOneInto(String.class);
    }

    void assertToken(String token, TokenType type, String login, String tokenId) {
        val claims = getJwtProvider().getClaims(token);

        assertThat(claims.isRight()).isTrue();
        assertThat(claims.get().getSubject()).isEqualTo(login);
        assertThat(claims.get().get("type", String.class)).isEqualTo(type.toString());
        assertThat(claims.get().getId()).isInstanceOf(String.class).isEqualTo(tokenId);
    }

    @Test
    @DisplayName("Should authenticate on POST on /api/v1/auth/login")
    void shouldAuthenticateWithValidUser() {
        val user = new CreateUserDto(
                "authUser", "Auth user", null, "abc12345"
        );

        writeUser(getDsl(), user);

        val request = new LoginRequest(user.login(), user.password());
        val response = getRestTemplate().postForEntity(LOGIN_URL, request, LoginResponse.class);

        assertHttpResponse(response);

        val body = response.getBody();
        assertThat(body).isNotNull().isInstanceOf(LoginResponse.class);
        assertThat(body.accessToken()).isInstanceOf(String.class);
        assertThat(body.refreshToken()).isInstanceOf(String.class);

        val tokenId = readTokenId(user.login());

        assertToken(body.accessToken(), TokenType.ACCESS, user.login(), tokenId);
        assertToken(body.refreshToken(), TokenType.REFRESH, user.login(),tokenId);
    }

    @Test
    @DisplayName("Should logout on POST on /api/v1/auth/logout")
    void shouldLogout() {
        val user = new CreateUserDto(
                "logoutUser", "Logout user", null, "abc12345"
        );

        writeUser(getDsl(), user);

        val request = new LoginRequest(user.login(), user.password());
        val tokens = getRestTemplate().postForEntity(LOGIN_URL, request, LoginResponse.class).getBody();
        assertThat(tokens).isNotNull();
        assertThat(tokens.accessToken()).isNotNull().isInstanceOf(String.class);

        val headers = new HttpHeaders();
        headers.setBearerAuth(tokens.accessToken());
        val entity = new HttpEntity<>(headers);

        val response = getRestTemplate().postForEntity(
                String.format("%s/logout", AUTH_API_URL), entity, LogoutResponse.class
        );

        assertHttpResponse(response);
        assertThat(readTokenId(user.login())).isNull();
    }

    @Test
    @DisplayName("Should refresh token on POST on /api/v1/auth/refresh")
    void shouldRefresh() {
        val user = new CreateUserDto(
                "refreshUser", "Refresh user", null, "abc12345"
        );
        writeUser(getDsl(), user);

        val request = new LoginRequest(user.login(), user.password());
        val tokens = getRestTemplate().postForEntity(LOGIN_URL, request, LoginResponse.class).getBody();
        assertThat(tokens).isNotNull();
        assertThat(tokens.refreshToken()).isNotNull().isInstanceOf(String.class);

        val response = getRestTemplate().postForEntity(
                String.format("%s/refresh", AUTH_API_URL),
                new RefreshRequest(tokens.refreshToken()),
                LoginResponse.class
        );

        assertHttpResponse(response);

        val body = response.getBody();
        val tokenId = readTokenId(user.login());
        assertThat(body).isNotNull().isInstanceOf(LoginResponse.class);
        assertToken(body.accessToken(), TokenType.ACCESS, user.login(), tokenId);
        assertToken(body.refreshToken(), TokenType.REFRESH, user.login(), tokenId);
    }

    @Test
    @DisplayName("Should return error on auth with non existent user on POST on /api/v1/auth/login")
    void shouldRejectAuth() {
        val url = String.format("http://localhost:%d%s", getServletCtx().getWebServer().getPort(), LOGIN_URL);
        val request = new LoginRequest("nonExistent", "abc12345");
        val response = getCustomRestTemplate().postForEntity(url, request, ProblemResponse.class);

        assertProblemResponse(response, HttpStatus.UNAUTHORIZED);

        val body = response.getBody();
        assertThat(body).isNotNull().isInstanceOf(ProblemResponse.class);
        assertThat(body.title()).isEqualTo("Unauthorized");
        assertThat(body.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
