package ru.ra_tech.garden_manager.core.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.security")
public record AppSecurityProps(
        int refreshTokenExpTimeoutSec,
        int accessTokenExpTimeoutSec
) {}
