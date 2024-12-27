package ru.ra_tech.garden_manager.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.ra_tech.garden_manager.failure.AppFailure;
import ru.ra_tech.garden_manager.failure.JwtFailure;

import java.security.Key;
import java.time.Duration;
import java.util.Date;

@RequiredArgsConstructor
public class JwtProvider {
    private static final Key SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final Duration VALID_DURATION = Duration.ofMinutes(30);

    public record TokenPair(String access, String refresh) {}

    private Either<AppFailure, String> createToken(JwtPrincipal principal, String tokenId, TokenType type) {
        return Try.of(() -> {
            val claims = Jwts.claims().setSubject(principal.login()).setId(tokenId);
            claims.put("role", "GARDEN_USER");
            claims.put("type", type.toString());
            claims.put("login", principal.login());
            claims.put("name", principal.name());
            claims.put("id", principal.id());

            val now = new Date();
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(Date.from(now.toInstant().plus(VALID_DURATION)))
                    .signWith(SECRET)
                    .compact();

        })
                .toEither()
                .mapLeft(error -> new JwtFailure(this.getClass().getName(), error));
    }

    public Either<AppFailure, TokenPair> createTokenPair(JwtPrincipal principal, String tokenId) {
        return createToken(principal, tokenId, TokenType.ACCESS)
                .map(token -> new TokenPair(token, ""))
                .flatMap(pair ->
                        createToken(principal, tokenId, TokenType.REFRESH)
                                .map(token -> new TokenPair(pair.access, token))
                );
    }

    private AppFailure toFailure(Throwable error) {
        return new JwtFailure(this.getClass().getName(), error);
    }

    public Either<AppFailure, Claims> getClaims(String token) {
        return Try.of(() ->
                Jwts.parserBuilder()
                        .setSigningKey(SECRET)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
        )
                .toEither()
                .mapLeft(this::toFailure);
    }
}
