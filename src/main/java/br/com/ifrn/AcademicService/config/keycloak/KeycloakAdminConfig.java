package br.com.ifrn.AcademicService.config.keycloak;

import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KeycloakAdminConfig {

    @Autowired
    KeycloakPropertiesConfig envKeycloak;

    public Keycloak createKeycloakAdminClient() {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(envKeycloak.serverUrl())
                .realm("master") // realm onde o admin user existe
                .clientId("admin-cli") // client para admin
                .clientSecret(envKeycloak.clientSecret()) // se necessário
                .username(envKeycloak.adminUser()) // usuário admin
                .password(envKeycloak.adminPassword())
                .grantType(OAuth2Constants.PASSWORD)
                .build();
        return keycloak;
    }

    public Response createKeycloakUser(String username, String name) {
        Keycloak keycloak = createKeycloakAdminClient();

        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEnabled(true);
        user.setFirstName(name);

        Response response = keycloak.realm(envKeycloak.realm()).users().create(user);
        return response;

    }


    public UserRepresentation findKeycloakUser(String userId) throws Exception {
        try {
            Keycloak keycloak = createKeycloakAdminClient();

            // Em vez de .search(), usamos .get(id) que foca no UUID interno
            return keycloak.realm(envKeycloak.realm())
                    .users()
                    .get(userId) // Busca direta pelo UUID
                    .toRepresentation();

        } catch (Exception e) {
            // Log para outros erros (conexão, timeout, etc)
            System.err.println("Erro ao buscar usuário no Keycloak: " + e.getMessage());
            return null;
        }
    }

}
