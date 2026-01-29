package br.com.ifrn.AcademicService.controller;

import br.com.ifrn.AcademicService.controller.docs.ClassCommentsControllerDocs;
import br.com.ifrn.AcademicService.dto.request.RequestCommentDTO;
import br.com.ifrn.AcademicService.models.ClassComments;
import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.services.ClassCommentsService;
import br.com.ifrn.AcademicService.services.ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/classes/{classId}/comments")

public class ClassCommentsController implements ClassCommentsControllerDocs {

    @Autowired
    private ClassCommentsService commentService;

    @Autowired
    private ClassesService classesService;

    @GetMapping
    public ResponseEntity<List<ClassComments>> getByClass(@PathVariable Integer classId) {
        List<ClassComments> comments = commentService.getByTurma(classId);
        if (comments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<ClassComments> create(@PathVariable Integer classId, @RequestParam Integer professorId, @RequestBody RequestCommentDTO commentDTO) {
        Classes classe = classesService.getById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Classe não encontrada"));

        ClassComments classComments = new ClassComments();
        classComments.setClasse(classe);
        classComments.setComment(commentDTO.getComment());
        classComments.setCreatedAt(LocalDate.EPOCH);
        classComments.setUpdatedAt(LocalDate.EPOCH);
        classComments.setProfessorId(professorId);

        ClassComments createdComment = commentService.create(classComments);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ClassComments> update(@PathVariable Integer classId, @PathVariable Integer commentId, @RequestBody ClassComments comment) {
        comment.setId(commentId);
        return ResponseEntity.ok(commentService.update(comment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Integer classId, @PathVariable Integer commentId) {

        commentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}
