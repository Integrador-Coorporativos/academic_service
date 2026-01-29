package br.com.ifrn.AcademicService;

import br.com.ifrn.AcademicService.models.ClassComments;
import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.repository.ClassCommentsRepository;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import br.com.ifrn.AcademicService.services.ClassCommentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassCommentsServiceTest {

    @Mock
    private ClassCommentsRepository commentsRepository;

    @Mock
    private ClassesRepository classesRepository;

    @InjectMocks
    private ClassCommentsService commentService;

    private ClassComments comment;
    private Classes classe;

    @BeforeEach
    void setUp() {
        classe = new Classes();
        classe.setId(1);
        classe.setName("Turma A");

        comment = new ClassComments();
        comment.setId(1);
        comment.setProfessorId(10);
        comment.setComment("Excelente participação");
        comment.setCreatedAt(LocalDate.now());
        comment.setUpdatedAt(LocalDate.now());
        comment.setClasse(classe);
    }

    @Test
    void getByTurma_deveRetornarLista() {
        when(commentsRepository.findByClasseId(1)).thenReturn(List.of(comment));

        List<ClassComments> list = commentService.getByTurma(1);

        assertEquals(1, list.size());
        assertEquals("Excelente participação", list.get(0).getComment());
        verify(commentsRepository, times(1)).findByClasseId(1);
        verifyNoMoreInteractions(commentsRepository);
    }

    @Test
    void getByTurma_turmaInexistente_deveRetornarListaVazia() {
        when(commentsRepository.findByClasseId(999)).thenReturn(List.of());

        List<ClassComments> list = commentService.getByTurma(999);

        assertNotNull(list);
        assertTrue(list.isEmpty());
        verify(commentsRepository).findByClasseId(999);
        verifyNoMoreInteractions(commentsRepository);
    }

    @Test
    void create_deveSalvarQuandoValido() {
        when(commentsRepository.save(any(ClassComments.class))).thenReturn(comment);

        ClassComments created = commentService.create(comment);

        assertNotNull(created);
        assertEquals("Excelente participação", created.getComment());
        assertEquals(10, created.getProfessorId());
        verify(commentsRepository, times(1)).save(any(ClassComments.class));
        verifyNoMoreInteractions(commentsRepository);
    }

    @Test
    void create_commentNulo_deveFalhar() {
        comment.setComment(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> commentService.create(comment));

        assertEquals("Comentário não pode ser nulo", ex.getMessage());
        verify(commentsRepository, never()).save(any());
    }

    @Test
    void create_commentVazio_deveFalhar() {
        comment.setComment("");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> commentService.create(comment));

        assertEquals("Comentário não pode ser vazio", ex.getMessage());
        verify(commentsRepository, never()).save(any());
    }

    @Test
    void create_commentCom1Caractere_devePassar() {
        comment.setComment("A");
        when(commentsRepository.save(any(ClassComments.class))).thenReturn(comment);

        ClassComments created = commentService.create(comment);

        assertEquals(1, created.getComment().length());
        verify(commentsRepository).save(any(ClassComments.class));
    }

    @Test
    void create_commentCom255_devePassar() {
        comment.setComment("C".repeat(255));
        when(commentsRepository.save(any(ClassComments.class))).thenReturn(comment);

        ClassComments created = commentService.create(comment);

        assertEquals(255, created.getComment().length());
        verify(commentsRepository).save(any(ClassComments.class));
    }

    @Test
    void create_commentCom256_deveFalhar() {
        comment.setComment("C".repeat(256));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> commentService.create(comment));

        assertEquals("Comentário não pode exceder 255 caracteres", ex.getMessage());
        verify(commentsRepository, never()).save(any());
    }

    @Test
    void create_professorIdZero_deveFalhar() {
        comment.setProfessorId(0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> commentService.create(comment));

        assertEquals("Professor ID deve ser maior que zero", ex.getMessage());
        verify(commentsRepository, never()).save(any());
    }

    @Test
    void create_professorIdNegativo_deveFalhar() {
        comment.setProfessorId(-1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> commentService.create(comment));

        assertEquals("Professor ID deve ser maior que zero", ex.getMessage());
        verify(commentsRepository, never()).save(any());
    }

    @Test
    void update_quandoExiste_deveAtualizarCamposESalvar() {
        ClassComments existing = new ClassComments();
        existing.setId(1);
        existing.setProfessorId(10);
        existing.setComment("Antigo");
        existing.setUpdatedAt(LocalDate.of(2026, 1, 1));
        existing.setClasse(classe);

        ClassComments incoming = new ClassComments();
        incoming.setId(1);
        incoming.setComment("Novo comentário");
        incoming.setUpdatedAt(LocalDate.of(2026, 1, 27));

        when(commentsRepository.findById(1)).thenReturn(Optional.of(existing));
        when(commentsRepository.save(any(ClassComments.class))).thenAnswer(inv -> inv.getArgument(0));

        ClassComments updated = commentService.update(incoming);

        assertEquals("Novo comentário", updated.getComment());
        assertEquals(LocalDate.of(2026, 1, 27), updated.getUpdatedAt());

        ArgumentCaptor<ClassComments> captor = ArgumentCaptor.forClass(ClassComments.class);
        verify(commentsRepository).findById(1);
        verify(commentsRepository).save(captor.capture());
        assertEquals("Novo comentário", captor.getValue().getComment());
        verifyNoMoreInteractions(commentsRepository);
    }

    @Test
    void update_quandoNaoExiste_deveRetornar404() {
        when(commentsRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> commentService.update(comment));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        // mensagem do service está "Ccomentário..." (com C duplicado). O teste garante o comportamento atual:
        assertEquals("Ccomentário não encontrado!", ex.getReason());

        verify(commentsRepository).findById(1);
        verify(commentsRepository, never()).save(any());
    }

    @Test
    void delete_deveChamarRepository() {
        doNothing().when(commentsRepository).deleteById(1);

        commentService.delete(1);

        verify(commentsRepository).deleteById(1);
        verifyNoMoreInteractions(commentsRepository);
    }
}
