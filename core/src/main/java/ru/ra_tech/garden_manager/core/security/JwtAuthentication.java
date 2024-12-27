package ru.ra_tech.garden_manager.core.security;

import io.vavr.collection.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serial;
import java.util.Collection;
import java.util.Date;

public class JwtAuthentication implements Authentication {
    @Serial
    private static final long serialVersionUID = 2024_01_06_22_00_00L;

    private final JwtPrincipal principal;
    private final Date expiration;
    private boolean isAuthenticated;
    private final List<GrantedAuthority> authorities;

    public JwtAuthentication(JwtPrincipal principal, Date expiration, String role) {
        this.principal = principal;
        this.expiration = expiration;
        isAuthenticated = true;
        authorities = List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.asJava();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated && expiration.after(new Date());
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Manually setting authenticated to true is illegal");
        }

        this.isAuthenticated = false;
    }

    @Override
    public String getName() {
        return principal.name();
    }
}
