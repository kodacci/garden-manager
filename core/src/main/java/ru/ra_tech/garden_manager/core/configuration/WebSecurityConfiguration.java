package ru.ra_tech.garden_manager.core.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.ra_tech.garden_manager.core.security.JwtAuthenticationProcessingFilter;
import ru.ra_tech.garden_manager.core.security.JwtAuthenticationProvider;
import ru.ra_tech.garden_manager.core.security.JwtProvider;
import ru.ra_tech.garden_manager.core.services.CustomUserDetailsService;
import ru.ra_tech.garden_manager.database.repositories.api.AuthUserRepository;
import ru.ra_tech.garden_manager.database.repositories.auth.AuthUserRepositoryImpl;

import java.time.Duration;

@Configuration
public class WebSecurityConfiguration {
    @Bean
    public AuthenticationManager authenticationManager(
            PasswordEncoder encoder,
            UserDetailsService service,
            AuthUserRepositoryImpl userRepo,
            JwtProvider jwtProvider
    ) {
        val dao = new DaoAuthenticationProvider(service);
        dao.setPasswordEncoder(encoder);

        return new ProviderManager(dao, new JwtAuthenticationProvider(userRepo, jwtProvider));
    }

    @Bean
    public AuthUserRepository authUserRepository(DSLContext dsl) {
        return new AuthUserRepositoryImpl(dsl);
    }

    @Bean
    public UserDetailsService userDetailsService(AuthUserRepository repository) {
        return new CustomUserDetailsService(repository);
    }

    @Bean
    public JwtProvider jwtAuthenticationProvider(AppSecurityProps securityProps) {
        return new JwtProvider(
                Duration.ofSeconds(securityProps.refreshTokenExpTimeoutSec()),
                Duration.ofSeconds(securityProps.accessTokenExpTimeoutSec())
        );
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtFilter(
            AuthenticationManager authManager
    ) {
        return new JwtAuthenticationProcessingFilter(authManager, new ObjectMapper());
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAuthenticationProcessingFilter jwtFilter
    ) {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                    auth -> auth
                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                            .permitAll()
                            .requestMatchers("/api/*/auth/**", "/api/v1/users")
                            .permitAll()
                            .requestMatchers("/actuator/**")
                            .permitAll()
                            .requestMatchers("/api/**")
                            .hasAuthority("GARDEN_USER")
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
