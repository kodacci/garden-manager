package ru.ra_tech.garden_manager.core.services;

import io.jsonwebtoken.Claims;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.ErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.LoginResponse;
import ru.ra_tech.garden_manager.core.controllers.auth.dto.LogoutResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.EntityNotFoundResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.ServerErrorResponse;
import ru.ra_tech.garden_manager.core.controllers.error_responses.UnauthorizedResponse;
import ru.ra_tech.garden_manager.core.security.JwtProvider;
import ru.ra_tech.garden_manager.core.security.TokenType;
import ru.ra_tech.garden_manager.database.repositories.auth.AuthUserDto;
import ru.ra_tech.garden_manager.database.repositories.auth.AuthUserRepository;
import ru.ra_tech.garden_manager.failure.AppFailure;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class AuthService {
    private static final String USER_ENTITY = "User";

    private final JwtProvider jwtProvider;
    private final AuthenticationManager authManager;
    private final AuthUserRepository repo;

    private ErrorResponse toUnauthorized() {
        return new UnauthorizedResponse();
    }

    private ErrorResponse toUnauthorized(Throwable error) {
        return toUnauthorized();
    }

    private ErrorResponse toUnauthorized(AppFailure failure) {
        return toUnauthorized();
    }

    private ErrorResponse toServerError(AppFailure failure) {
        return new ServerErrorResponse(failure);
    }

    private Either<ErrorResponse, JwtProvider.TokenPair> createTokenPair(String login) {
        val tokenId = UUID.randomUUID().toString();

        return jwtProvider.createTokenPair(login, tokenId)
                .mapLeft(this::toServerError)
                .flatMap(pair ->
                        repo.updateTokenId(login, tokenId)
                                .map(success -> pair)
                                .mapLeft(this::toServerError)
                );
    }

    public Either<ErrorResponse, LoginResponse> login(String login, String password) {
        return Try.of(() ->
                        authManager.authenticate(new UsernamePasswordAuthenticationToken(login, password))
                )
                .toEither()
                .mapLeft(this::toUnauthorized)
                .flatMap(auth -> Option.of(auth).toEither(this::toUnauthorized))
                .flatMap(auth -> createTokenPair(login))
                .map(LoginResponse::new);
    }

    private Either<ErrorResponse, Claims> validateToken(AuthUserDto user, Claims claims) {
        if (
                Objects.equals(user.tokenId(), claims.getId())
                        && Objects.equals(claims.get("type"), TokenType.REFRESH.toString())
        ) {
            return Either.right(claims);
        } else {
            return Either.left(new UnauthorizedResponse());
        }
    }

    private Either<ErrorResponse, Claims> validateClaims(Claims claims) {
        return repo.findById(claims.getSubject())
                .mapLeft(this::toServerError)
                .flatMap(user ->
                        user.toEither(toUnauthorized())
                                .flatMap(dto -> validateToken(dto, claims))
                );
    }

    public Either<ErrorResponse, LoginResponse> refresh(String token) {
        return jwtProvider.getClaims(token)
                .mapLeft(this::toUnauthorized)
                .flatMap(this::validateClaims)
                .flatMap(claims -> createTokenPair(claims.getSubject()))
                .map(LoginResponse::new);
    }

    public Either<ErrorResponse, LogoutResponse> logout(int userId) {
        return repo.clearSession(userId)
                .mapLeft(this::toServerError)
                .flatMap(
                        success -> success
                            ? Either.right(new LogoutResponse())
                            : Either.left(new EntityNotFoundResponse(USER_ENTITY, userId))
                );
    }
}
