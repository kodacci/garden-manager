package ru.ra_tech.garden_manager.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.ra_tech.garden_manager.core.configuration.AppMonitoringProps;

@SpringBootApplication
@EnableConfigurationProperties(AppMonitoringProps.class)
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
