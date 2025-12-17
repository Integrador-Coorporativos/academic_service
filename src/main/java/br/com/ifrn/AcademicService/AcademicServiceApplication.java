package br.com.ifrn.AcademicService;

import br.com.ifrn.AcademicService.file.objectstorage.MinioPropertiesConfig;
import br.com.ifrn.AcademicService.keycloak.KeycloakPropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({
        KeycloakPropertiesConfig.class,
        MinioPropertiesConfig.class
})
@SpringBootApplication
public class AcademicServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcademicServiceApplication.class, args);
	}

}
