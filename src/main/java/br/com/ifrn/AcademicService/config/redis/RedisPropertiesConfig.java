package br.com.ifrn.AcademicService.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Alteramos o prefixo para o padrão do Spring Data Redis
@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisPropertiesConfig(
        String host,
        int port,
        String username,
        String password
) {}

