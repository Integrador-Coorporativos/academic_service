package br.com.ifrn.AcademicService.config.keycloak;

import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public List<UserRepresentation> findKeycloakUsersByIds(List<String> ids) {
        Keycloak keycloak = createKeycloakAdminClient();
        var usersResource = keycloak.realm(envKeycloak.realm()).users();
        return ids.stream()
                .map(id -> {
                    try {
                        return usersResource.get(id).toRepresentation();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<UserRepresentation> findUsersGroup(String nomeGrupo) {
        Keycloak keycloak = createKeycloakAdminClient();
        String grupoId = keycloak.realm(envKeycloak.realm())
                .groups()
                .groups().stream()
                .filter(g -> g.getName().equalsIgnoreCase(nomeGrupo))
                .findFirst()
                .orElseThrow()
                .getId();

        // 2. Retornamos os membros desse grupo
        return keycloak.realm(envKeycloak.realm())
                .groups()
                .group(grupoId)
                .members();
    }
}
