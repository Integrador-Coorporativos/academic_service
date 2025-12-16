package br.com.ifrn.EvaluationsService.evaluations_service;

import br.com.ifrn.EvaluationsService.evaluations_service.models.ClassComments;
import br.com.ifrn.EvaluationsService.evaluations_service.models.Classes;
import br.com.ifrn.EvaluationsService.evaluations_service.repository.ClassCommentsRepository;
import br.com.ifrn.EvaluationsService.evaluations_service.services.ClassCommentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClassCommentsServiceTest {

    @Mock
    private ClassCommentsRepository commentsRepository;

    @InjectMocks
    private ClassCommentsService commentService;

    private ClassComments comment;
    private Classes classe;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        classe = new Classes();
        classe.setId(1);
        classe.setName("Turma A");

        comment = new ClassComments();
        comment.setProfessorId(10);
        comment.setComment("Excelente participação");
        comment.setClasse(classe); // relacionamento com Classes
    }

    // ==============================
    // Testes Unitários
    // ==============================

    @Test
    void testCreate() {
        when(commentsRepository.save(any(ClassComments.class))).thenReturn(comment);

        ClassComments created = commentService.create(comment);

        assertNotNull(created);
        assertEquals("Excelente participação", created.getComment());
        assertEquals(10, created.getProfessorId());
        assertEquals("Turma A", created.getClasse().getName());
    }

    @Test
    void testGetByTurma() {
        // Mocka o método findAll que é utilizado no serviço
        when(commentsRepository.findAll()).thenReturn(List.of(comment));

        List<ClassComments> list = commentService.getByTurma(1);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("Excelente participação", list.get(0).getComment());
        assertEquals(10, list.get(0).getProfessorId());
        assertEquals("Turma A", list.get(0).getClasse().getName());
        verify(commentsRepository, times(1)).findAll();
    }

    // ==============================
// Testes de Valor Limite
// ==============================

    @Test
    void testGetByTurmaNonExistentId() {
        when(commentsRepository.findAll()).thenReturn(List.of(comment));

        List<ClassComments> list = commentService.getByTurma(999); // turma inexistente
        assertTrue(list.isEmpty(), "Turma inexistente deve retornar lista vazia");
    }

    @Test
    void testCreateWithEmptyCommentShouldFail() {
        comment.setComment(""); // comentário vazio

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.create(comment);
        });

        assertEquals("Comentário não pode ser vazio", exception.getMessage());
    }

    @Test
    void testCreateWithNullCommentShouldFail() {
        comment.setComment(null); // comentário nulo

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.create(comment);
        });

        assertEquals("Comentário não pode ser nulo", exception.getMessage());
    }

    @Test
    void testCreateWithMaxLengthComment() {
        comment.setComment("C".repeat(255)); // limite máximo
        when(commentsRepository.save(any(ClassComments.class))).thenReturn(comment);

        ClassComments created = commentService.create(comment);
        assertEquals(255, created.getComment().length(), "Comentário de 255 caracteres deve ser aceito");
    }

    @Test
    void testCreateWithTooLongCommentShouldFail() {
        comment.setComment("C".repeat(256)); // acima do limite

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.create(comment);
        });

        assertEquals("Comentário não pode exceder 255 caracteres", exception.getMessage());
    }

    @Test
    void testCreateWithMaxProfessorId() {
        comment.setProfessorId(Integer.MAX_VALUE);
        when(commentsRepository.save(any(ClassComments.class))).thenReturn(comment);

        ClassComments created = commentService.create(comment);
        assertEquals(Integer.MAX_VALUE, created.getProfessorId(), "Professor ID máximo deve ser aceito");
    }

    @Test
    void testCreateWithMinProfessorIdShouldFail() {
        comment.setProfessorId(0);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.create(comment);
        });

        assertEquals("Professor ID deve ser maior que zero", exception.getMessage());
    }
}
