package ru.ra_tech.garden_manager.core.configuration;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ra_tech.garden_manager.core.security.JwtProvider;
import ru.ra_tech.garden_manager.core.services.AuthService;
import ru.ra_tech.garden_manager.core.services.GardenService;
import ru.ra_tech.garden_manager.core.services.UserService;
import ru.ra_tech.garden_manager.database.repositories.auth.AuthUserRepository;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenRepository;
import ru.ra_tech.garden_manager.database.repositories.user.UserRepository;

@Configuration
public class MainConfiguration {
    @Bean
    public UserRepository userRepository(DSLContext dsl) {
        return new UserRepository(dsl);
    }

    @Bean
    public UserService userService(UserRepository repo, PasswordEncoder passwordEncoder) {
        return new UserService(repo, passwordEncoder);
    }

    @Bean
    public AuthService authService(
            JwtProvider jwtProvider,
            AuthenticationManager authManager,
            AuthUserRepository repo
    ) {
        return new AuthService(jwtProvider, authManager, repo);
    }

    @Bean
    public GardenRepository gardenRepository(DSLContext dsl) {
        return new GardenRepository(dsl);
    }

    @Bean
    public GardenService gardenService(GardenRepository gardenRepository) {
        return new GardenService(gardenRepository);
    }
}
