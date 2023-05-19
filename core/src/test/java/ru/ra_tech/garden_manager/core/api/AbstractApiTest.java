package ru.ra_tech.garden_manager.core.api;

import lombok.Getter;
import lombok.val;
import org.jooq.DSLContext;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.ra_tech.garden_manager.core.MainApplication;
import ru.ra_tech.garden_manager.core.security.JwtProvider;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.ra_tech.garden_manager.database.schema.tables.Users.USERS;

@SpringBootTest(
        classes = { MainApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class AbstractApiTest {
    @Autowired
    @Getter
    private TestRestTemplate restTemplate;
    @Autowired
    @Getter
    private JwtProvider jwtProvider;
    @Autowired
    @Getter
    private DSLContext dsl;

    protected <T> void assertHttpResponse(ResponseEntity<T> response, HttpStatus desiredStatus, MediaType desiredType) {
        assertThat(response.getStatusCode()).isEqualTo(desiredStatus);
        assertThat(response.getHeaders().getContentType()).isEqualTo(desiredType);
    }

    protected <T> void assertHttpResponse(ResponseEntity<T> response) {
        assertHttpResponse(response, HttpStatus.OK, MediaType.APPLICATION_JSON);
    }

    protected <T> void assertHttpResponse(ResponseEntity<T> response, HttpStatus desiredStatus) {
        assertHttpResponse(response, desiredStatus, MediaType.APPLICATION_JSON);
    }

    protected <T> void assertProblemResponse(ResponseEntity<T> response, HttpStatus status) {
        assertHttpResponse(response, status, MediaType.APPLICATION_PROBLEM_JSON);
    }

    private void setUserTokenId(String tokenId) {
        getDsl().update(USERS).set(USERS.TOKENID, tokenId).execute();
    }

    protected String generateToken(String login) {
        val tokenId = UUID.randomUUID().toString();
        setUserTokenId(tokenId);

        return jwtProvider.createTokenPair(login, tokenId)
                .get()
                .access();
    }

    protected HttpHeaders generateAuthHeaders(String login) {
        val headers = new HttpHeaders();
        headers.setBearerAuth(generateToken(login));

        return headers;
    }
}
