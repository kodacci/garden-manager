package ru.ra_tech.garden_manager.core.api;

import lombok.Getter;
import lombok.val;
import org.jooq.DSLContext;
import org.jooq.User;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ra_tech.garden_manager.core.MainApplication;
import ru.ra_tech.garden_manager.core.security.JwtPrincipal;
import ru.ra_tech.garden_manager.core.security.JwtProvider;
import ru.ra_tech.garden_manager.database.configuration.DatabaseConfiguration;
import ru.ra_tech.garden_manager.database.repositories.user.UserDto;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.ra_tech.garden_manager.database.schema.tables.Users.USERS;

@Getter
@SpringBootTest(
        classes = {
                MainApplication.class,
                DatabaseConfiguration.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Testcontainers
class AbstractApiIT {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private DSLContext dsl;
    @Autowired
    private ServletWebServerApplicationContext servletCtx;

    private final RestTemplate customRestTemplate = new RestTemplate();

    {
        customRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        customRestTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                HttpStatusCode statusCode = response.getStatusCode();
                return statusCode.is5xxServerError();
            }
        });
    }

    protected <T> void assertHttpResponse(ResponseEntity<T> response, HttpStatus desiredStatus, MediaType desiredType) {
        assertThat(response.getStatusCode()).isEqualTo(desiredStatus);
        assertThat(desiredType.equalsTypeAndSubtype(response.getHeaders().getContentType())).isTrue();
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

    protected String generateToken(JwtPrincipal principal) {
        val tokenId = UUID.randomUUID().toString();
        setUserTokenId(tokenId);

        return jwtProvider.createTokenPair(principal, tokenId)
                .get()
                .access();
    }

    private JwtPrincipal toPrincipal(UserDto user) {
        return new JwtPrincipal(user.id(), user.login(), user.name());
    }

    protected HttpHeaders generateAuthHeaders(UserDto user) {
        val headers = new HttpHeaders();
        headers.setBearerAuth(generateToken(toPrincipal(user)));
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }
}
