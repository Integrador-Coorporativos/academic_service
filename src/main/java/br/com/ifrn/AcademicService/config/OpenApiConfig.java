package br.com.ifrn.AcademicService.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("SADT - Evaluations Service")
                        .version("1.0.0")
                        .description("""
                            Serviço responsável por gerenciar avaliações de turmas, desempenho de alunos e importação de planilhas contendo dados acadêmicos. 
                            Faz parte de uma arquitetura modular e integra-se a outros serviços educacionais.
                            
                            """)
                        .contact(new Contact()
                                .name("Eduardo Lima")
                                .email("ferreira.lima1@escolar.ifrn.edu.br")
                                .url("https://github.com/eduardoferreiralima"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor Local de Desenvolvimento")
                ))
                .tags(List.of(
                        new Tag().name("Avaliações").description("Endpoints de gerenciamento de avaliações de turma"),
                        new Tag().name("Desempenho").description("Operações relacionadas a Desempenho"),
                        new Tag().name("Classes").description("Operações relacionadas a Turmas"),
                        new Tag().name("Courses").description("Operações relacionadas a Cursos"),
                        new Tag().name("Comments").description("Operações relacionadas a Comentários")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentação do Projeto")
                        .url("https://github.com/Integrador-Coorporativos/evaluations-service"));
    }
}
