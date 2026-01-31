package ru.ra_tech.garden_manager.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwe;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.val;
import ru.ra_tech.garden_manager.failure.AppFailure;
import ru.ra_tech.garden_manager.failure.JwtFailure;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@RequiredArgsConstructor
public class JwtProvider {
    private static final SecretKey SECRET = Jwts.SIG.HS256.key().build();

    private final Duration validRefreshDuration;
    private final Duration validAccessDuration;

    public record TokenPair(String access, String refresh) {}

    private Either<AppFailure, String> createToken(JwtPrincipal principal, String tokenId, TokenType type, Duration expDuration) {
        return Try.of(() -> {
            val now = new Date();

            return Jwts.builder()
                    .subject(principal.login())
                    .id(tokenId)
                    .issuedAt(now)
                    .expiration(Date.from(now.toInstant().plus(expDuration)))
                    .claim("role", "GARDEN_USER")
                    .claim("type", type.toString())
                    .claim("login", principal.login())
                    .claim("name", principal.name())
                    .claim("id", principal.id())
                    .signWith(SECRET)
                    .compact();

        })
                .toEither()
                .mapLeft(error -> new JwtFailure(this.getClass().getName(), error));
    }

    public Either<AppFailure, TokenPair> createTokenPair(JwtPrincipal principal, String tokenId) {
        return createToken(principal, tokenId, TokenType.ACCESS, validRefreshDuration)
                .map(token -> new TokenPair(token, ""))
                .flatMap(pair ->
                        createToken(principal, tokenId, TokenType.REFRESH, validAccessDuration)
                                .map(token -> new TokenPair(pair.access, token))
                );
    }

    private AppFailure toFailure(Throwable error) {
        return new JwtFailure(this.getClass().getName(), error);
    }

    public Either<AppFailure, Claims> getClaims(String token) {
        return Try.of(() ->
                Jwts.parser()
                        .verifyWith(SECRET)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
        )
                .toEither()
                .mapLeft(this::toFailure);
    }
}
