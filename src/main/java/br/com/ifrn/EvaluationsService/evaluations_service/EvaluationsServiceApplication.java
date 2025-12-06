package br.com.ifrn.EvaluationsService.evaluations_service;

import br.com.ifrn.EvaluationsService.evaluations_service.file.objectstorage.MinioPropertiesConfig;
import br.com.ifrn.EvaluationsService.evaluations_service.keycloak.KeycloakPropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({
        KeycloakPropertiesConfig.class,
        MinioPropertiesConfig.class
})
@SpringBootApplication
public class EvaluationsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvaluationsServiceApplication.class, args);
	}

}
