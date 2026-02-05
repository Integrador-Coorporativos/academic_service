package br.com.ifrn.AcademicService.controller;

import br.com.ifrn.AcademicService.controller.docs.ClassesControllerDocs;
import br.com.ifrn.AcademicService.dto.request.RequestClassDTO;
import br.com.ifrn.AcademicService.dto.response.ClassPanelResponseDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassByIdDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassDTO;
import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.services.ClassesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classes")
@Tag(name = "Classes", description = "Operações relacionadas a Turmas")
public class ClassesController implements ClassesControllerDocs {

    @Autowired
    private ClassesService classesService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR', 'ROLE_ALUNO')")
    @GetMapping
    public ResponseEntity<List<ResponseClassDTO>> getAll(Authentication authentication) {
        String professorId = getProfessorId(authentication);
        List<ResponseClassDTO> classesList = classesService.getAll(professorId);
        if (classesList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(classesList);
    }
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    @GetMapping("/my-classes")
    public ResponseEntity<List<ResponseClassDTO>> getMyClasses(Authentication authentication) {
        String professorId = getProfessorId(authentication);
        List<ResponseClassDTO> classesList = classesService.getMyClasses(professorId);
        if (classesList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(classesList);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR', 'ROLE_ALUNO')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseClassByIdDTO> getById(@PathVariable Integer id) throws Exception {
        ResponseClassByIdDTO classes = classesService.getByClassId(id);
        if (classes == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(classes);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/panel")
    public ResponseEntity<List<ClassPanelResponseDTO>> getClassesForPanel() {
        List<ClassPanelResponseDTO> panel = classesService.getClassesForPanel();
        return ResponseEntity.ok(panel);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ResponseClassDTO> create(@RequestParam Integer courseId, @RequestBody RequestClassDTO classDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classesService.create(courseId, classDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseClassDTO> update(@PathVariable Integer id, @PathVariable Integer courseId, @RequestBody RequestClassDTO classes) {
        return ResponseEntity.status(HttpStatus.OK).body(classesService.update(id, courseId, classes));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = classesService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    @PatchMapping("/{id}/professor")
    public ResponseEntity<ResponseClassDTO> addProfessorToClass(@PathVariable Integer id, Authentication authentication) {
        String professorId = getProfessorId(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(classesService. addProfessorToClass(id, professorId));
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
