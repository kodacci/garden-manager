package ru.ra_tech.garden_manager.core.security;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import ru.ra_tech.garden_manager.database.repositories.auth.AuthUserDto;
import ru.ra_tech.garden_manager.database.repositories.auth.AuthUserRepository;
import ru.ra_tech.garden_manager.failure.AppFailure;

import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final AuthUserRepository repo;
    private final JwtProvider jwtProvider;

    private void logError(AppFailure failure) {
        log.error("Authentication failure: {}", failure.getDetail());
    }

    private @Nullable Authentication validateToken(AuthUserDto user, Claims claims) {
        if (!Objects.equals(user.tokenId(), claims.getId())
                || !Objects.equals(claims.get("type", String.class), TokenType.ACCESS.toString())) {
            log.warn("Invalid claims data: claims: {}, tokenId: {}", claims, user.tokenId());
            return null;
        }

        return new JwtAuthentication(
                new JwtPrincipal(user.id(), user.login(), user.name()),
                claims.getExpiration(),
                claims.get("role", String.class)
        );
    }

    private @Nullable Authentication validateUser(Claims claims) {
        return repo.findById(claims.getSubject())
                .peekLeft(this::logError)
                .map(user -> user.fold(
                        () -> null,
                        dto -> validateToken(dto, claims)
                ))
                .getOrElseGet(failure -> null);
    }

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Authenticating {}", authentication.getPrincipal());
        val token = (JwtAuthenticationToken) authentication;

        return jwtProvider.getClaims(token.getCredentials())
                .map(this::validateUser)
                .peekLeft(this::logError)
                .getOrElseGet(failure -> null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == JwtAuthenticationToken.class;
    }
}
