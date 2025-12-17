package br.com.ifrn.AcademicService.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redis")
public record RedisPropertiesConfig(
        String host,
        int port
) {}

