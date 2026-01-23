package br.com.ifrn.AcademicService.config.keycloak;

import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KeycloakAdminConfig {

    @Autowired
    KeycloakPropertiesConfig envKeycloak;

    public Keycloak createKeycloakAdminClient() {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(envKeycloak.internalUrl())
                .realm(envKeycloak.adminRealm()) // realm onde o admin user existe
                .clientId(envKeycloak.adminClientId()) // client para admin
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
            return keycloak.realm(envKeycloak.realm())
                    .users()
                    .get(userId) // Busca direta pelo UUID
                    .toRepresentation();

        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário no Keycloak: " + e.getMessage());
            return null;
        }
    }

}
