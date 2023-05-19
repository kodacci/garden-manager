package ru.ra_tech.garden_manager.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import ru.ra_tech.garden_manager.core.controllers.error_responses.UnauthorizedResponse;

import java.io.IOException;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

public class JwtAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private static final String API_URL_PATTERN = "/api/**";
    private static final String LOGIN_URL_PATTERN = "/api/*/auth/login";
    private static final String REFRESH_URL_PATTERN = "/api/*/auth/refresh";
    private static final String CREATE_USER_PATTERN = "/api/v1/users";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final RequestMatcher REQUEST_MATCHER;

    static {
        REQUEST_MATCHER = new AndRequestMatcher(
                antMatcher(API_URL_PATTERN),
                new NegatedRequestMatcher(
                        new OrRequestMatcher(antMatcher(LOGIN_URL_PATTERN), antMatcher(REFRESH_URL_PATTERN))
                ),
                new NegatedRequestMatcher(
                        new AntPathRequestMatcher(CREATE_USER_PATTERN, HttpMethod.POST.toString())
                )
        );
    }

    private static class SuccessHandler implements AuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Authentication authentication
        ) {}

        @Override
        public void onAuthenticationSuccess(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain chain,
                Authentication authentication
        ) {}
    }

    private void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        mapper.writeValue(response.getWriter(), new UnauthorizedResponse(request.getPathInfo()).getBody());
    }

    private final ObjectMapper mapper;

    public JwtAuthenticationProcessingFilter(
            AuthenticationManager authenticationManager,
            ObjectMapper mapper
    ) {
        super(REQUEST_MATCHER, authenticationManager);
        setAuthenticationSuccessHandler(new SuccessHandler());
        setAuthenticationFailureHandler(this::onAuthenticationFailure);
        this.mapper = mapper;
    }

    private @Nullable String toToken(@Nullable String header) {
        return header != null && header.startsWith(TOKEN_PREFIX)
                ? header.substring(TOKEN_PREFIX.length())
                : null;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {
        val token = toToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (token == null) {
            throw new AuthenticationException("No auth token provided") {};
        }

        return getAuthenticationManager().authenticate(new JwtAuthenticationToken(token));
    }

    @Override
    public void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain,
            Authentication authResult
    ) throws IOException, ServletException {
        super.successfulAuthentication(request, response, filterChain, authResult);
        filterChain.doFilter(request, response);
    }
}
