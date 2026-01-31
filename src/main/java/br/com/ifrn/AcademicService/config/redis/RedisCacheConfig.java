package br.com.ifrn.AcademicService.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
@EnableCaching
@Configuration
public class RedisCacheConfig {

    @Autowired
    RedisPropertiesConfig redisENV;

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisENV.host());
        config.setPort(redisENV.port());
        config.setUsername(redisENV.username());
        config.setPassword(RedisPassword.of(redisENV.password()));

        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // StudentPerformance caches
        cacheConfigurations.put("studentPerformanceCache", defaultCacheConfig.entryTtl(Duration.ofMinutes(20)));
        cacheConfigurations.put("studentPerformanceCacheAll", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));

        // Courses caches
        cacheConfigurations.put("coursesCache", defaultCacheConfig.entryTtl(Duration.ofMinutes(20)));
        cacheConfigurations.put("coursesCacheAll", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));

        // Classes caches
        cacheConfigurations.put("classesCache", defaultCacheConfig.entryTtl(Duration.ofMinutes(20)));
        cacheConfigurations.put("classesCacheAll", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));

        // ClassComments caches
        cacheConfigurations.put("commentsCache", defaultCacheConfig.entryTtl(Duration.ofMinutes(10)));

        // ClassEvaluations caches
        cacheConfigurations.put("evaluationsCache", defaultCacheConfig.entryTtl(Duration.ofMinutes(20)));
        cacheConfigurations.put("evaluationsCacheAll", defaultCacheConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put("evaluationsCacheByClass", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
