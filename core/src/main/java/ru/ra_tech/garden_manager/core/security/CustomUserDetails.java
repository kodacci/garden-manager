package ru.ra_tech.garden_manager.core.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {
    public static final String GARDEN_USER_ROLE = "GARDEN_USER";

    private final long id;
    private final String username;
    private final String password;
    private final String name;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(GARDEN_USER_ROLE));
    }
}
