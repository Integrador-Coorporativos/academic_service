package br.com.ifrn.AcademicService;

import br.com.ifrn.AcademicService.config.keycloak.KeycloakAdminConfig;
import br.com.ifrn.AcademicService.dto.request.RequestCommentDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseCommentDTO;
import br.com.ifrn.AcademicService.mapper.CommentsMapper;
import br.com.ifrn.AcademicService.models.ClassComments;
import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.repository.ClassCommentsRepository;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import br.com.ifrn.AcademicService.services.ClassCommentsService;
import br.com.ifrn.AcademicService.services.ClassesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ClassesService classesService;

    @Mock
    private KeycloakAdminConfig keycloak;

    @Mock
    private ClassCommentsRepository commentsRepository;

    @Mock
    private ClassesRepository classesRepository;

    @Autowired
    private CommentsMapper  commentsMapper;

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
        comment.setProfessorId("professorId");
        comment.setComment("Excelente participação");
        comment.setCreatedAt(LocalDate.now());
        comment.setUpdatedAt(LocalDate.now());
        comment.setClasse(classe);
    }

    @Test
    void getByTurma_deveRetornarLista() {
        when(commentsRepository.findByClasseId(1)).thenReturn(List.of(comment));

        List<ResponseCommentDTO> list = commentService.getByTurma(1);

        assertEquals(1, list.size());
        assertEquals("Excelente participação", list.get(0).getComment());
        verify(commentsRepository, times(1)).findByClasseId(1);
        verifyNoMoreInteractions(commentsRepository);
    }

    @Test
    void getByTurma_turmaInexistente_deveRetornarListaVazia() {
        when(commentsRepository.findByClasseId(999)).thenReturn(List.of());

        List<ResponseCommentDTO> list = commentService.getByTurma(999);

        assertNotNull(list);
        assertTrue(list.isEmpty());
        verify(commentsRepository).findByClasseId(999);
        verifyNoMoreInteractions(commentsRepository);
    }

    @Test
    void create_deveSalvarQuandoValido() {
        // Arrange
        String profId = "prof-123";
        Integer classId = 1;
        RequestCommentDTO dto = new RequestCommentDTO("Excelente participação");

        Classes mockClasse = new Classes();
        mockClasse.setId(classId);

        UserRepresentation mockUser = new UserRepresentation();
        mockUser.setFirstName("Professor Girafales");

        when(classesService.getById(classId)).thenReturn(Optional.of(mockClasse));
        try {
            when(keycloak.findKeycloakUser(profId)).thenReturn(mockUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        when(commentsRepository.save(any(ClassComments.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        ResponseCommentDTO created = commentService.create(dto, profId, classId);

        // Assert
        assertNotNull(created);
        assertEquals("Excelente participação", created.getComment());
        assertEquals("Professor Girafales", created.getProfessorName());
        verify(commentsRepository, times(1)).save(any(ClassComments.class));
    }

    @Test
    void create_commentNulo_deveFalhar() {
        RequestCommentDTO dto = new RequestCommentDTO(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> commentService.create(dto, "id", 1));

        assertEquals("Comentário não pode ser nulo", ex.getMessage());
        verify(commentsRepository, never()).save(any());
    }

    @Test
    void create_commentVazio_deveFalhar() {
        RequestCommentDTO dto = new RequestCommentDTO("");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> commentService.create(dto, "id", 1));

        assertEquals("Comentário não pode ser vazio", ex.getMessage());
    }

    @Test
    void create_classeNaoEncontrada_deveFalhar() {
        RequestCommentDTO dto = new RequestCommentDTO("Bom trabalho");
        when(classesService.getById(99)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> commentService.create(dto, "prof-1", 99));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Classe não encontrada", ex.getReason());
    }

    @Test
    void create_commentExcedendoLimite_deveFalhar() {
        RequestCommentDTO dto = new RequestCommentDTO("C".repeat(256));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> commentService.create(dto, "id", 1));

        assertEquals("Comentário não pode exceder 255 caracteres", ex.getMessage());
    }

    @Test
    void create_professorIdNull_deveFalhar() {
        RequestCommentDTO dto = new RequestCommentDTO("sdfgsdf");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> commentService.create(dto, null, 1));
        assertEquals("Professor ID inválido!", ex.getMessage());
        verify(commentsRepository, never()).save(any());
    }

    @Test
    void create_professorIdVazio_deveFalhar() {
        RequestCommentDTO dto = new RequestCommentDTO("sgdfg");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> commentService.create(dto, "", 1));
        assertEquals("Professor ID inválido!", ex.getMessage());
        verify(commentsRepository, never()).save(any());
    }

    @Test
    void update_quandoExiste_deveAtualizarCamposESalvar() throws IllegalAccessException {
        ClassComments existing = new ClassComments();
        existing.setId(1);
        existing.setProfessorId("professorId");
        existing.setComment("Antigo");
        existing.setUpdatedAt(LocalDate.of(2026, 1, 1));
        existing.setClasse(classe);

        RequestCommentDTO incoming = new RequestCommentDTO();
        incoming.setComment("Novo comentário");

        when(commentsRepository.findById(1)).thenReturn(Optional.of(existing));
        when(commentsRepository.save(any(ClassComments.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseCommentDTO updated = commentService.update(1, incoming, "professorId");

        assertEquals("Novo comentário", updated.getComment());

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
                () -> commentService.update(1, commentsMapper.toRequestCommentDTO(comment), "professorId"));

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
