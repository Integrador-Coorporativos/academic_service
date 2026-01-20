package br.com.ifrn.AcademicService;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import com.redis.testcontainers.RedisContainer;
import java.time.Duration;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Container
    protected static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis:7.2-alpine"))
                    .withStartupTimeout(Duration.ofMinutes(2));

    @Container
    protected static final MinIOContainer MINIO_CONTAINER =
            new MinIOContainer(DockerImageName.parse("minio/minio:RELEASE.2023-09-04T19-57-37Z"))
                    .withUserName("testuser")
                    .withPassword("testpassword")
                    .withStartupTimeout(Duration.ofMinutes(2));

    @Container
    protected static final RabbitMQContainer RABBITMQ_CONTAINER =
            new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.12-management-alpine"))
                    .withStartupTimeout(Duration.ofMinutes(2));

    @Container
    protected static final GenericContainer<?> KEYCLOAK_CONTAINER =
            new GenericContainer<>(DockerImageName.parse("quay.io/keycloak/keycloak:22.0.0"))
                    .withEnv("KEYCLOAK_ADMIN", "admin")
                    .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
                    .withCommand("start-dev")
                    .withExposedPorts(8081)
                    .withStartupTimeout(Duration.ofMinutes(3));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        // Redis
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", REDIS_CONTAINER::getFirstMappedPort);

        // MinIO
        registry.add("minio.endpoint", MINIO_CONTAINER::getS3URL);
        registry.add("minio.access-key", MINIO_CONTAINER::getUserName);
        registry.add("minio.secret-key", MINIO_CONTAINER::getPassword);

        // RabbitMQ
        registry.add("spring.rabbitmq.host", RABBITMQ_CONTAINER::getHost);
        registry.add("spring.rabbitmq.port", RABBITMQ_CONTAINER::getAmqpPort);
        registry.add("spring.rabbitmq.username", RABBITMQ_CONTAINER::getAdminUsername);
        registry.add("spring.rabbitmq.password", RABBITMQ_CONTAINER::getAdminPassword);

        // Keycloak
        String keycloakUrl = String.format(
                "http://%s:%d",
                KEYCLOAK_CONTAINER.getHost(),
                KEYCLOAK_CONTAINER.getMappedPort(8081)
        );

        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakUrl + "/realms/master"
        );

        registry.add("keycloak.auth-server-url", () -> keycloakUrl);
    }
}
