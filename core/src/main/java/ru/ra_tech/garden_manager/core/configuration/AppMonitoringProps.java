package ru.ra_tech.garden_manager.core.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.monitoring")
public record AppMonitoringProps (
        String appName,
        String appVersion,
        String podName,
        String podNamespace,
        String nodeName
) {}
