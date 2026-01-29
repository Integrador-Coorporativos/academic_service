package br.com.ifrn.AcademicService;

import br.com.ifrn.AcademicService.config.redis.RedisPropertiesConfig;
import br.com.ifrn.AcademicService.config.minio.MinioPropertiesConfig;
import br.com.ifrn.AcademicService.config.keycloak.KeycloakPropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({
        KeycloakPropertiesConfig.class,
        MinioPropertiesConfig.class,
        RedisPropertiesConfig.class
})
@SpringBootApplication
public class AcademicServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcademicServiceApplication.class, args);
	}

}
