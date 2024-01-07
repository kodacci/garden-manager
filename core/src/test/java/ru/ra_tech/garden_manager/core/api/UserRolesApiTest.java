package ru.ra_tech.garden_manager.core.api;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import ru.ra_tech.garden_manager.core.controllers.roles.dto.UserRoleData;
import ru.ra_tech.garden_manager.database.repositories.user.CreateUserDto;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.ra_tech.garden_manager.database.schema.Tables.USERS;

@DisplayName("User roles API test")
@Slf4j
public class UserRolesApiTest extends AbstractApiTest {
    private static final String BASE_PATH = "/api/v1/roles";
    private static final String USER_LOGIN = "rolesTestUser";

    @Autowired
    private DSLContext dsl;

    @BeforeAll
    void beforeAll() {
        val user = new CreateUserDto(
                USER_LOGIN, "User roles Test User", null, "abc12345"
        );

        TestUtils.writeUser(dsl, user);
    }

    @AfterAll
    void afterAll() {
        dsl.deleteFrom(USERS).execute();
    }

    @Test
    @DisplayName("should get all user roles on GET on /api/v1/roles")
    void shouldGetAllRoles() {
        val response = getRestTemplate().exchange(
                BASE_PATH,
                HttpMethod.GET,
                new HttpEntity<>(generateAuthHeaders(USER_LOGIN)),
                UserRoleData[].class
        );

        assertHttpResponse(response);
        val body = response.getBody();

        val expected = List.of(
                new UserRoleData(1, "ADMIN", "Администратор"),
                new UserRoleData(2, "CHIEF", "Заведующий"),
                new UserRoleData(3, "EXECUTOR", "Исполнитель")
        );

        assertThat(body).isNotNull();
        assertThat(Arrays.asList(body)).isEqualTo(expected);
    }
}
