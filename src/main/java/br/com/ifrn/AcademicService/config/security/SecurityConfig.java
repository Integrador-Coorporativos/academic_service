package br.com.ifrn.AcademicService.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;
import java.util.List;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Value("${SPRING_FRONTEND_URL:http://localhost:5173}")
    private String frontendUrl;

    @Value("${SPRING_SERVER_URL}")
    private String backendUrl;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        authorizeConfig -> {
                            // LIBERAR SWAGGER E DOCUMENTAÇÃO
                            authorizeConfig.requestMatchers(
                                    "/",                       // Raiz para o redirect funcionar
                                    "/api/docs/**",            // Onde está o seu Swagger UI
                                    "/v3/api-docs/**",         // Onde está o JSON (importante!)
                                    "/swagger-ui/**",          // Caminhos internos da lib
                                    "/webjars/**"              // Arquivos CSS/JS do Swagger
                            ).permitAll();
                            authorizeConfig.requestMatchers("/actuator/**").permitAll();
                            authorizeConfig.requestMatchers("/public").permitAll();
                            authorizeConfig.requestMatchers("/logout").permitAll();


                            // Para poder trabalhar os endpoints
                            authorizeConfig.requestMatchers("/api/courses/**").permitAll();
                            authorizeConfig.requestMatchers("/api/classes/**").permitAll();



                            authorizeConfig.anyRequest().authenticated();
                        })
                .oauth2Login(withDefaults())
                .oauth2ResourceServer(conf -> conf.jwt(withDefaults()))
                .build();
    }
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/api/docs");
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Lista de origens permitidas
        configuration.setAllowedOrigins(List.of(
                frontendUrl,
                backendUrl,
                "http://localhost:5173"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // 1. Pega as authorities padrão (como SCOPE_profile, SCOPE_email)
            Collection<GrantedAuthority> authorities = defaultConverter.convert(jwt);

            // 2. Pega o seu atributo customizado "type_user"
            String typeUser = jwt.getClaimAsString("type_user");

            if (typeUser != null) {
                // Adicionamos o valor (Aluno/Professor) como uma permissão
                authorities.add(new SimpleGrantedAuthority(typeUser));
            }

            return authorities;
        });
        return converter;
    }
}

