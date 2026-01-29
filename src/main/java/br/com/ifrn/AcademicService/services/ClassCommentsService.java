package br.com.ifrn.AcademicService.services;

import br.com.ifrn.AcademicService.models.ClassComments;
import br.com.ifrn.AcademicService.repository.ClassCommentsRepository;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class ClassCommentsService {
    @Autowired
    private ClassCommentsRepository commentRepository;

    @Autowired
    private ClassesRepository classesRepository;

    @Cacheable(value = "commentsCache", key = "#turmaId")
    public List<ClassComments> getByTurma(Integer turmaId) {
        return commentRepository.findByClasseId(turmaId);
    }

    @CacheEvict(value = "commentsCache", allEntries = true)
    public ClassComments create(ClassComments comment) {
        if (comment.getComment() == null) {
            throw new IllegalArgumentException("Comentário não pode ser nulo");
        }
        if (comment.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("Comentário não pode ser vazio");
        }
        if (comment.getComment().length() > 255) {
            throw new IllegalArgumentException("Comentário não pode exceder 255 caracteres");
        }
        if (comment.getProfessorId() <= 0) {
            throw new IllegalArgumentException("Professor ID deve ser maior que zero");
        }
        return commentRepository.save(comment);
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
