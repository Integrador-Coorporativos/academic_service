package br.com.ifrn.AcademicService.config.keycloak;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak")
public record KeycloakPropertiesConfig(
        String clientSecret,
        String serverUrl,
        String adminUser,
        String adminPassword,
        String realm
) {}
