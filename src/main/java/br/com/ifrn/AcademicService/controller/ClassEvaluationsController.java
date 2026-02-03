package br.com.ifrn.AcademicService.controller;

import br.com.ifrn.AcademicService.controller.docs.ClassEvaluationsControllerDocs;
import br.com.ifrn.AcademicService.dto.request.RequestClassEvaluationsDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassEvaluationsDTO;
import br.com.ifrn.AcademicService.services.ClassEvaluationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class ClassEvaluationsController implements ClassEvaluationsControllerDocs {

    @Autowired
    private ClassEvaluationsService classEvaluationsService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    @GetMapping
    public ResponseEntity<List<ResponseClassEvaluationsDTO>> getAllEvaluations() {
        return ResponseEntity.ok().body(classEvaluationsService.getAllEvaluations());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseClassEvaluationsDTO> getEvaluationById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(classEvaluationsService.getEvaluationById(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    @GetMapping("/class/{id}")
    public ResponseEntity<List<ResponseClassEvaluationsDTO>> getEvaluationsByClassId(@PathVariable Integer id) {
        return ResponseEntity.ok().body(classEvaluationsService.getEvaluationsByClassId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    public ResponseEntity<ResponseClassEvaluationsDTO> createEvaluation(
            @RequestBody RequestClassEvaluationsDTO dto,
            @RequestParam Integer id,
            Authentication authentication
    ) {
        String professorId = getProfessorId(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(classEvaluationsService.createEvaluation(dto, id, professorId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseClassEvaluationsDTO> updateEvaluation(@PathVariable Integer id, @RequestBody RequestClassEvaluationsDTO dto) {
        return ResponseEntity.ok().body(classEvaluationsService.updateEvaluation(id,dto));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvaluation(@PathVariable Integer id) {
        classEvaluationsService.deleteEvaluation(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private static String getProfessorId(Authentication authentication) {
        String professorId = null;

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            professorId = jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof OidcUser oidc) {
            professorId = oidc.getSubject();
        } else {
            professorId = authentication.getName();
        }
        return professorId;
    }
}
