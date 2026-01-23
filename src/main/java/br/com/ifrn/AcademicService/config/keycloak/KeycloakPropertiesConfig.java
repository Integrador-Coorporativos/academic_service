package br.com.ifrn.AcademicService.config.keycloak;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.keycloak")
public record KeycloakPropertiesConfig(
        String clientSecret,
        String serverUrl,
        String internalUrl,
        String adminUser,
        String adminPassword,
        String adminClientId,
        String realm,
        String adminRealm
) {}
