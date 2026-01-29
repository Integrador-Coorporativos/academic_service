package br.com.ifrn.AcademicService.config.audit;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;

public class UserRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity rev = (CustomRevisionEntity) revisionEntity;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();

            // 1. Se logado via Navegador (OidcUser)
            if (principal instanceof OidcUser oidc) {
                rev.setUserId(oidc.getSubject()); // Pega o 'sub'
            }
            // 2. Se logado via API/Postman (JWT)
            else if (principal instanceof Jwt jwt) {
                rev.setUserId(jwt.getSubject()); // Pega o 'sub'
            }
            else {
                rev.setUserId(auth.getName());
            }
        } else {
            rev.setUserId("SYSTEM");
        }
    }
}
