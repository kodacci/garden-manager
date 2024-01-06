package ru.ra_tech.garden_manager.core.services;

import io.vavr.control.Option;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.ra_tech.garden_manager.database.repositories.auth.AuthUserDto;
import ru.ra_tech.garden_manager.database.repositories.auth.AuthUserRepository;
import ru.ra_tech.garden_manager.failure.AppFailure;

import java.util.List;

@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthUserRepository repo;

    public CustomUserDetailsService(AuthUserRepository repo) {
        this.repo = repo;
    }

    private UserDetails handleFailure(AppFailure failure) {
        val cause = failure.getCause();
        if (cause != null) {
            throw new UsernameNotFoundException(cause.getMessage());
        }

        throw new UsernameNotFoundException("Unknown error");
    }

    private @Nullable UserDetails toUserDetails(Option<AuthUserDto> dto) {
        return dto.fold(
                () -> null,
                user -> User.builder()
                        .username(user.login())
                        .password(user.password())
                        .authorities(List.of(new SimpleGrantedAuthority("GARDEN_USER")))
                        .build()
        );
    }

    @Override
    public @Nullable UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repo.findById(username).fold(
                this::handleFailure,
                this::toUserDetails
        );
    }
}
