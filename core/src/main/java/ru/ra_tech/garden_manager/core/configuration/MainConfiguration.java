package ru.ra_tech.garden_manager.core.configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ra_tech.garden_manager.core.security.JwtProvider;
import ru.ra_tech.garden_manager.core.services.AuthService;
import ru.ra_tech.garden_manager.core.services.GardenService;
import ru.ra_tech.garden_manager.core.services.UserRoleService;
import ru.ra_tech.garden_manager.core.services.UserService;
import ru.ra_tech.garden_manager.database.Transactional;
import ru.ra_tech.garden_manager.database.configuration.DatabaseConfiguration;
import ru.ra_tech.garden_manager.database.repositories.auth.AuthUserRepository;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenRepository;
import ru.ra_tech.garden_manager.database.repositories.user.UserRepository;
import ru.ra_tech.garden_manager.database.repositories.user_role.UserRoleRepository;

@Configuration
@Import(DatabaseConfiguration.class)
@EnableAspectJAutoProxy
@Slf4j
public class MainConfiguration {
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
    public GardenService gardenService(GardenRepository gardenRepository, Transactional transactional) {
        return new GardenService(gardenRepository, transactional);
    }

    @Bean
    public UserRoleService userRoleService(UserRoleRepository repo) {
        return new UserRoleService(repo);
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> registryCustomizer(AppMonitoringProps props) {
        log.info("Loaded application monitoring props: {}", props);

        return registry -> registry.config().commonTags(
                "app.name", props.appName(),
                "app.version", props.appVersion(),
                "pod.name", props.podName(),
                "pod.namespace", props.podNamespace(),
                "pod.node.name", props.nodeName()
        );
    }
}
