package ru.ra_tech.garden_manager.core.api;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.RefreshRequest;
import ru.ra_tech.garden_manager.core.controllers.error_responses.dto.ProblemResponse;
import ru.ra_tech.garden_manager.core.controllers.users.dto.CreateUserRequest;
import ru.ra_tech.garden_manager.core.controllers.users.dto.UserData;
import ru.ra_tech.garden_manager.database.repositories.user.CreateUserDto;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static ru.ra_tech.garden_manager.core.api.TestUtils.writeUser;
import static ru.ra_tech.garden_manager.database.schema.tables.Users.USERS;

@DisplayName("Users API test")
class UsersApiIT extends AbstractApiIT {

    private static final String USERS_API_URL = "/api/v1/users";

    @AfterAll
    void afterAll() {
        getDsl().deleteFrom(USERS).execute();
    }

    @Test
    @DisplayName("Should add new user on POST on /api/v1/users")
    void shouldAddNewUser() {
        val request = new CreateUserRequest(
                "tester", "Testov Tester", "test@example.com", "abc12345"
        );
        val response = getRestTemplate().postForEntity(
                USERS_API_URL, new HttpEntity<>(request, generateTraceHeaders()), UserData.class
        );

        assertHttpResponse(response, HttpStatus.CREATED);

        val body = response.getBody();
        assertThat(body).isNotNull().isInstanceOf(UserData.class);

        val expected = new UserData(body.id(), request.login(), request.name(), request.email());
        assertThat(response.getBody()).isEqualTo(expected);

        val row = getDsl().select(USERS.LOGIN, USERS.NAME, USERS.EMAIL)
                .from(USERS)
                .where(USERS.ID.eq(body.id()))
                .fetchOne();

        assertThat(row).isNotNull();
        assertThat(row.value1()).isEqualTo(expected.login());
        assertThat(row.value2()).isEqualTo(expected.name());
        assertThat(row.value3()).isEqualTo(expected.email());
    }

    @Test
    @DisplayName("Should get existing user by GET on /api/v1/user/{id}")
    void shouldGetExistingUser() {
        val user = new CreateUserDto(
                "newUser", "New User", "new-user-test@example.com", "abc12345"
        );

        val created = writeUser(getDsl(), user);
        assertThat(created).isNotNull();
        val url = String.format("%s/%d", USERS_API_URL, created.id());
        val entity = new HttpEntity<>(generateAuthHeaders(created));

        val response = getRestTemplate().exchange(url, HttpMethod.GET, entity, UserData.class);

        assertHttpResponse(response);

        val body = response.getBody();
        assertThat(body).isNotNull()
                .isInstanceOf(UserData.class)
                .isEqualTo(UserData.of(created));
    }

    @Test
    void shouldReturnErrorOnCreatingUserWithSameLogin() {
        val user = new CreateUserDto(
                "duplicateUser", "Duplicate user", null, "abc12345"
        );

        getRestTemplate().postForEntity(
                USERS_API_URL, new HttpEntity<>(user, generateTraceHeaders()), UserData.class
        );

        val response = getRestTemplate().postForEntity(
                USERS_API_URL, new HttpEntity<>(user, generateTraceHeaders()), ProblemResponse.class
        );
        assertHttpResponse(response, HttpStatus.CONFLICT, MediaType.APPLICATION_PROBLEM_JSON);

        val body = response.getBody();
        assertThat(body).isNotNull().isInstanceOf(ProblemResponse.class);
        assertThat(body.status()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(body.detail()).isEqualTo("User with specified login or email already exists");
    }

    @Test
    void shouldDeleteUser() {
        val user = new CreateUserDto(
                "deleteUser", "Delete User", null, "abc12345"
        );
        val created = writeUser(getDsl(), user);
        val id = created.id();

        val url = String.format("%s/%d", USERS_API_URL, id);
        val entity = new HttpEntity<>(generateAuthHeaders(created));
        val response = getRestTemplate().exchange(url, HttpMethod.DELETE, entity, Object.class);

        assertHttpResponse(response);

        val deleted = getDsl().select(USERS.DELETED).from(USERS).where(USERS.ID.eq(id)).fetchOneInto(Boolean.class);
        assertThat(deleted).isTrue();
    }

    @Test
    void shouldReturnErrorOnDeletingNonExistentUser() {
        val user = new CreateUserDto(
                "nonExistentAuth", "Some user", null, "abc12345"
        );
        val created = writeUser(getDsl(), user);

        val url = String.format("%s/12345", USERS_API_URL);
        val entity = new HttpEntity<>(generateAuthHeaders(created));

        val response = getRestTemplate().exchange(url, HttpMethod.DELETE, entity, ProblemResponse.class);

        assertProblemResponse(response, HttpStatus.NOT_FOUND);

        val body = response.getBody();
        assertThat(body).isNotNull().isInstanceOf(ProblemResponse.class);
        assertThat(body.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(body.detail()).isEqualTo("User with id #12345 was not found");
    }

    @Test
    @DisplayName("Should return error on sending invalid user data on POST /api/v1/users")
    void shouldReturnErrorOnAddingInvalidUser() {
        val user = new CreateUserRequest("a", null, "not-email", "123");

        val response = getRestTemplate().postForEntity(
                USERS_API_URL, new HttpEntity<>(user, generateTraceHeaders()), ProblemResponse.class
        );
        assertProblemResponse(response, HttpStatus.BAD_REQUEST);

        val body = response.getBody();
        assertThat(body).isNotNull().isInstanceOf(ProblemResponse.class);
        assertThat(body.title()).isEqualTo("Bad Request");
        assertThat(body.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(body.detail()).isEqualTo("Invalid request content.");
        assertThat(body.validationErrors()).hasSize(4);
        assertThat(body.validationErrors().get(0)).isInstanceOf(String.class);
    }

    @Test
    @DisplayName("Should return UNAUTHORIZED on POST without auth token")
    void shouldReturnUnauthorizedOnNoToken() {
        val response = getRestTemplate().postForEntity(
                String.format("%s/1", USERS_API_URL),
                new HttpEntity<>(new RefreshRequest(UUID.randomUUID().toString()), generateTraceHeaders()),
                ProblemResponse.class
        );

        assertProblemResponse(response, HttpStatus.UNAUTHORIZED);

        val body = response.getBody();
        assertThat(body).isNotNull().isInstanceOf(ProblemResponse.class);
        assertThat(body.title()).isEqualTo("Unauthorized");
        assertThat(body.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
