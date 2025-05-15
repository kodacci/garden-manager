package ru.ra_tech.garden_manager.core.configuration;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ra_tech.garden_manager.core.security.JwtProvider;
import ru.ra_tech.garden_manager.core.services.api.AuthService;
import ru.ra_tech.garden_manager.core.services.api.UserRoleService;
import ru.ra_tech.garden_manager.core.services.impl.AuthServiceImpl;
import ru.ra_tech.garden_manager.core.services.api.GardenService;
import ru.ra_tech.garden_manager.core.services.api.UserService;
import ru.ra_tech.garden_manager.core.services.impl.GardenServiceImpl;
import ru.ra_tech.garden_manager.core.services.impl.UserRoleServiceImpl;
import ru.ra_tech.garden_manager.core.services.impl.UserServiceImpl;
import ru.ra_tech.garden_manager.database.Transactional;
import ru.ra_tech.garden_manager.database.configuration.DatabaseConfiguration;
import ru.ra_tech.garden_manager.database.repositories.api.AuthUserRepository;
import ru.ra_tech.garden_manager.database.repositories.api.GardenRepository;
import ru.ra_tech.garden_manager.database.repositories.api.UserRepository;
import ru.ra_tech.garden_manager.database.repositories.api.UserRoleRepository;

@Configuration
@Import(DatabaseConfiguration.class)
@EnableAspectJAutoProxy
@Slf4j
@ComponentScan({
        "ru.ra_tech.garden_manager.core.controllers",
        "ru.ra_tech.garden_manager.core.filter"
})
public class MainConfiguration {
    @Bean
    public UserService userService(UserRepository repo, PasswordEncoder passwordEncoder) {
        return new UserServiceImpl(repo, passwordEncoder);
    }

    @Bean
    public AuthService authService(
            JwtProvider jwtProvider,
            AuthenticationManager authManager,
            AuthUserRepository repo
    ) {
        return new AuthServiceImpl(jwtProvider, authManager, repo);
    }

    @Bean
    public GardenService gardenService(GardenRepository gardenRepository, Transactional transactional) {
        return new GardenServiceImpl(gardenRepository, transactional);
    }

    @Bean
    public UserRoleService userRoleService(UserRoleRepository repo) {
        return new UserRoleServiceImpl(repo);
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public CountedAspect countedAspect(MeterRegistry registry) { return new CountedAspect(registry); }

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
