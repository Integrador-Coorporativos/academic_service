package br.com.ifrn.AcademicService.controller;

import br.com.ifrn.AcademicService.controller.docs.ClassCommentsControllerDocs;
import br.com.ifrn.AcademicService.dto.request.RequestCommentDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseCommentDTO;
import br.com.ifrn.AcademicService.models.ClassComments;
import br.com.ifrn.AcademicService.services.ClassCommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes/{classId}/comments")

public class ClassCommentsController implements ClassCommentsControllerDocs {

    @Autowired
    private ClassCommentsService commentService;

    @GetMapping
    public ResponseEntity<List<ResponseCommentDTO>> getByClass(@PathVariable Integer classId) {
        List<ResponseCommentDTO> comments = commentService.getByTurma(classId);
        if (comments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<ResponseCommentDTO> create(
            @PathVariable Integer classId,
            @RequestBody RequestCommentDTO commentDTO,
            Authentication authentication ) {

        String professorId = getProfessorId(authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                commentService.create(commentDTO, professorId, classId)
        );
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ResponseCommentDTO> update(
            @PathVariable Integer commentId,
            @RequestBody RequestCommentDTO comment,
            Authentication authentication) throws IllegalAccessException {

        String professorId = getProfessorId(authentication);

        return ResponseEntity.ok(commentService.update(commentId, comment, professorId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Integer classId, @PathVariable Integer commentId) {

        commentService.delete(commentId);
        return ResponseEntity.noContent().build();
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
