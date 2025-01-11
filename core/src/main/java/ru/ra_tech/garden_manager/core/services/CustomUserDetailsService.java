package ru.ra_tech.garden_manager.core.services;

import io.vavr.control.Option;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.ra_tech.garden_manager.core.security.CustomUserDetails;
import ru.ra_tech.garden_manager.database.repositories.api.AuthUserRepository;
import ru.ra_tech.garden_manager.database.repositories.auth.AuthUserDto;
import ru.ra_tech.garden_manager.failure.AppFailure;

@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthUserRepository repo;

    private UserDetails handleFailure(AppFailure failure) {
        val cause = failure.getCause();
        if (cause != null) {
            throw new UsernameNotFoundException(cause.getMessage());
        }

        throw new UsernameNotFoundException("Unknown error");
    }

    private UserDetails toUserDetails(AuthUserDto user) {
        return new CustomUserDetails(
                user.id(),
                user.login(),
                user.password(),
                user.name()
        );
    }

    private @Nullable UserDetails toUserDetails(Option<AuthUserDto> dto) {
        return dto.fold(() -> null, this::toUserDetails);
    }

    @Override
    public @Nullable UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repo.findById(username)
                .peekLeft(
                        failure -> log.error("Error searching for user {}: {}", username, failure.getCause())
                )
                .fold(
                    this::handleFailure,
                    this::toUserDetails
                );
    }
}
