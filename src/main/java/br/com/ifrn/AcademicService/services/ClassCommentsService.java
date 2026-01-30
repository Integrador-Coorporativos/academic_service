package br.com.ifrn.AcademicService.services;

import br.com.ifrn.AcademicService.config.keycloak.KeycloakAdminConfig;
import br.com.ifrn.AcademicService.dto.request.RequestCommentDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseCommentDTO;
import br.com.ifrn.AcademicService.models.ClassComments;
import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.repository.ClassCommentsRepository;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClassCommentsService {
    @Autowired
    private KeycloakAdminConfig keycloak;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private ClassCommentsRepository commentRepository;

    @Autowired
    private ClassesRepository classesRepository;

    @Cacheable(value = "commentsCache", key = "#turmaId")
    public List<ResponseCommentDTO> getByTurma(Integer turmaId) {
        List<ClassComments> classComments = commentRepository.findByClasseId(turmaId);
        List<ResponseCommentDTO> responseCommentDTO = new ArrayList<>();

        classComments.stream().forEach(classComment -> {
            ResponseCommentDTO responseCommentDTO1 = new ResponseCommentDTO();
            responseCommentDTO1.setId(classComment.getId());
            responseCommentDTO1.setComment(classComment.getComment());
            responseCommentDTO1.setCreatedAt(classComment.getCreatedAt());
            responseCommentDTO1.setProfessorName(classComment.getProfessorName());
            responseCommentDTO.add(responseCommentDTO1);

        });
        return responseCommentDTO;
    }

    @CacheEvict(value = "commentsCache", allEntries = true)
    public ClassComments create(RequestCommentDTO commentDTO, String professorId, Integer classId) {
        if (commentDTO.getComment() == null) {
            throw new IllegalArgumentException("Comentário não pode ser nulo");
        }
        if (commentDTO.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("Comentário não pode ser vazio");
        }
        if (commentDTO.getComment().length() > 255) {
            throw new IllegalArgumentException("Comentário não pode exceder 255 caracteres");
        }
        if (professorId == null || professorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Professor ID inválido!");
        }
        ClassComments classComments = new ClassComments();
        Classes classe = classesService.getById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Classe não encontrada"));
        try {
            UserRepresentation user = keycloak.findKeycloakUser(professorId);
            classComments.setClasse(classe);
            classComments.setComment(commentDTO.getComment());
            classComments.setProfessorId(professorId);
            classComments.setProfessorName(user.getFirstName());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar dados do professor", e);
        }
        return commentRepository.save(classComments);
    }

    @CacheEvict(value = "commentsCache", allEntries = true)
    public ClassComments update(ClassComments comment) {
        ClassComments classComment = commentRepository.findById(comment.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ccomentário não encontrado!"));
        classComment.setComment(comment.getComment());
        classComment.setUpdatedAt(comment.getUpdatedAt());
        return commentRepository.save(classComment);
    }

    @CacheEvict(value = "commentsCache", allEntries = true)
    public void delete(Integer id) {
        commentRepository.deleteById(id); }
}
