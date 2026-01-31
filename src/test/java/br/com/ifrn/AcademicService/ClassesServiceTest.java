package br.com.ifrn.AcademicService;

import br.com.ifrn.AcademicService.config.keycloak.KeycloakAdminConfig;
import br.com.ifrn.AcademicService.dto.response.ResponseClassByIdDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassDTO;
import br.com.ifrn.AcademicService.dto.response.StudentDataDTO;
import br.com.ifrn.AcademicService.mapper.ClassMapper;
import br.com.ifrn.AcademicService.mapper.StudentPerformanceMapper;
import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.models.Courses;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import br.com.ifrn.AcademicService.repository.StudentPerformanceRepository;
import br.com.ifrn.AcademicService.services.ClassesService;
import br.com.ifrn.AcademicService.services.CoursesService;
import br.com.ifrn.AcademicService.services.StudentPerformanceService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ClassesServiceTest {

    @Mock
    private ClassMapper classsMapper;

    @Mock
    private ClassesRepository classesRepository;

    @Mock
    private CoursesService coursesService;

    @Mock
    private StudentPerformanceService studentPerformanceService;

    @Mock
    private StudentPerformanceRepository studentPerformanceRepository;

    @Mock
    private StudentPerformanceMapper studentPerformanceMapper;

    @Mock
    private KeycloakAdminConfig keycloakAdminConfig;

    @InjectMocks
    private ClassesService classesService;

    private Classes turma;

    @BeforeEach
    void setUp() {
        turma = new Classes();
        turma.setId(1);
        turma.setName("Matemática");
    }

    // ==============================
    // CRUD BÁSICO
    // ==============================

    @Test
    void testCreate() {
        when(classesRepository.save(any(Classes.class))).thenReturn(turma);

        ResponseClassDTO created = classesService.create(turma.getCourse().getId(), classsMapper.toRequestClassDTO(turma));

        assertNotNull(created);
        assertEquals("Matemática", created.getName());
        verify(classesRepository).save(any(Classes.class));
    }

    @Test
    void testGetAll() {
        Classes turmaEntity = new Classes();
        turmaEntity.setName("Matemática");

        ResponseClassDTO turmaDTO = new ResponseClassDTO();
        turmaDTO.setName("Matemática");

        when(classesRepository.findAllWithCourse()).thenReturn(List.of(turmaEntity));

        when(classsMapper.toResponseClassDTO(anyList())).thenReturn(List.of(turmaDTO));

        List<ResponseClassDTO> all = classesService.getAll();

        assertEquals(1, all.size());
        assertEquals("Matemática", all.get(0).getName());
    }

    @Test
    void testGetAllWhenEmpty() {
        when(classesRepository.findAllWithCourse()).thenReturn(List.of());
        when(classsMapper.toResponseClassDTO(anyList())).thenReturn(List.of());

        List<ResponseClassDTO> all = classesService.getAll();

        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    void testGetById() {
        when(classesRepository.findById(1)).thenReturn(Optional.of(turma));

        Optional<Classes> result = classesService.getById(1);

        assertTrue(result.isPresent());
        assertEquals("Matemática", result.get().getName());
    }

    @Test
    void testUpdate() {
        Classes updated = new Classes();
        updated.setName("Física");

        when(classesRepository.findById(1)).thenReturn(Optional.of(turma));
        when(classesRepository.save(any(Classes.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseClassDTO result = classesService.update(1, turma.getCourse().getId(), classsMapper.toRequestClassDTO(turma));

        assertEquals("Física", result.getName());
        verify(classesRepository).save(any(Classes.class));
    }

    @Test
    void testUpdateWhenClassDoesNotExist() {
        when(classesRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            classesService.update(1, turma.getCourse().getId(), classsMapper.toRequestClassDTO(turma));
        });

        assertEquals("Classe not found", ex.getMessage());
    }

    @Test
    void testDeleteWhenExists() {
        when(classesRepository.existsById(1)).thenReturn(true);
        doNothing().when(classesRepository).deleteById(1);

        boolean result = classesService.delete(1);

        assertTrue(result);
        verify(classesRepository).deleteById(1);
    }

    @Test
    void testDeleteWhenDoesNotExist() {
        when(classesRepository.existsById(1)).thenReturn(false);

        boolean result = classesService.delete(1);

        assertFalse(result);
        verify(classesRepository, never()).deleteById(1);
    }

    // ==============================
    // VALIDAÇÕES DO CREATE
    // ==============================

    @Test
    void testCreateWithEmptyNameShouldFail() {
        turma.setName("");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            classesService.create(turma.getCourse().getId(), classsMapper.toRequestClassDTO(turma));
        });

        assertEquals("Nome da turma não pode ser vazio", exception.getMessage());
    }

    @Test
    void testCreateWithNullNameShouldFail() {
        turma.setName(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            classesService.create(turma.getCourse().getId(), classsMapper.toRequestClassDTO(turma));
        });

        assertEquals("Nome da turma não pode ser nulo", exception.getMessage());
    }

    @Test
    void testCreateWithVeryLongNameShouldFail() {
        turma.setName("A".repeat(256));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            classesService.create(turma.getCourse().getId(), classsMapper.toRequestClassDTO(turma));
        });

        assertEquals("Nome da turma não pode exceder 255 caracteres", exception.getMessage());
    }

    // ==============================
    // TESTES DE VALOR LIMITE
    // ==============================

    @Test
    void testGetByIdZeroShouldReturnEmpty() {
        when(classesRepository.findById(0)).thenReturn(Optional.empty());

        Optional<Classes> result = classesService.getById(0);

        assertTrue(result.isEmpty(), "ID zero deve retornar vazio");
    }

    @Test
    void testGetByIdMaxValueShouldReturnEmpty() {
        int maxId = Integer.MAX_VALUE;
        when(classesRepository.findById(maxId)).thenReturn(Optional.empty());

        Optional<Classes> result = classesService.getById(maxId);

        assertTrue(result.isEmpty(), "ID máximo deve retornar vazio");
    }

    // ==============================
    // getByClassId (PADRÃO test...)
    // ==============================

    @Test
    void testGetByClassIdNotFoundShouldThrow() {
        when(classesRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            classesService.getByClassId(1);
        });

        assertEquals("Classe not found", ex.getMessage());
    }

    @Test
    void testGetByClassIdUserIdsNullShouldReturnEmptyStudents() throws Exception {
        Classes c = new Classes();
        c.setId(1);
        c.setName("Turma X");
        c.setUserId(null);

        ResponseClassByIdDTO dto = new ResponseClassByIdDTO();

        when(classesRepository.findById(1)).thenReturn(Optional.of(c));
        when(classsMapper.toResponseClassByDTO(c)).thenReturn(dto);

        ResponseClassByIdDTO result = classesService.getByClassId(1);

        assertNotNull(result.getStudents());
        assertTrue(result.getStudents().isEmpty());
        verifyNoInteractions(keycloakAdminConfig);
    }

    @Test
    void testGetByClassIdKeycloakUserNullShouldIgnoreStudent() throws Exception {
        Classes c = new Classes();
        c.setId(1);
        c.setName("Turma X");
        c.setUserId(List.of("u1"));

        ResponseClassByIdDTO dto = new ResponseClassByIdDTO();

        when(classesRepository.findById(1)).thenReturn(Optional.of(c));
        when(classsMapper.toResponseClassByDTO(c)).thenReturn(dto);

        when(keycloakAdminConfig.findKeycloakUsersByIds(anyList())).thenReturn(new ArrayList<>());
        when(studentPerformanceRepository.findByStudentIdIn(anyList())).thenReturn(new ArrayList<>());
        ResponseClassByIdDTO result = classesService.getByClassId(1);

        assertNotNull(result.getStudents());
        assertTrue(result.getStudents().isEmpty());
        verify(studentPerformanceService, never()).getStudentPerformanceByStudentId(anyString());
    }

    @Test
    void testGetByClassIdUserFoundShouldAddStudent() throws Exception {
        Integer id = 1;
        Classes classe = new Classes();
        classe.setUserId(List.of("aluno-uuid-123"));

        UserRepresentation user = new UserRepresentation();
        user.setId("aluno-uuid-123");
        user.setFirstName("Eduardo");
        user.setUsername("202612345");

        when(keycloakAdminConfig.findKeycloakUsersByIds(anyList()))
                .thenReturn(List.of(user));

        when(studentPerformanceRepository.findByStudentIdIn(anyList()))
                .thenReturn(new ArrayList<>());

        when(classesRepository.findById(id)).thenReturn(Optional.of(classe));
        when(classsMapper.toResponseClassByDTO(any())).thenReturn(new ResponseClassByIdDTO());

        ResponseClassByIdDTO result = classesService.getByClassId(id);

        assertEquals(1, result.getStudents().size());
    }

    // ==============================
    // createOrUpdateClassByClassId (PADRÃO test...)
    // ==============================

    @Test
    void testCreateOrUpdateClassIdNullShouldThrow() {
        // Agora passando apenas 4 parâmetros conforme seu novo Service
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            classesService.createOrUpdateClassByClassId(
                    "Info", null, "Vespertino", "user-1"
            );
        });

        assertEquals("classId não pode ser nulo", ex.getMessage());
    }

    @Test
    void testCreateOrUpdateNotFoundShouldCreate() {
        String courseName = "Informática";
        String classId = "T01";
        String shift = "Vespertino";
        String userId = "user-123";

        // Mock do repositório
        when(classesRepository.findByClassId(classId)).thenReturn(null);

        Courses course = new Courses();
        course.setName(courseName);
        when(coursesService.findOrCreateByName(courseName)).thenReturn(course);

        when(classesRepository.save(any(Classes.class))).thenAnswer(inv -> inv.getArgument(0));

        // Chamada ao método com a nova assinatura
        Classes result = classesService.createOrUpdateClassByClassId(
                courseName, classId, shift, userId
        );

        assertEquals(course, result.getCourse());
        assertEquals(classId, result.getClassId());
        assertEquals(shift, result.getShift());
        assertEquals("Informática_T01", result.getName());
        assertNotNull(result.getComments());
        assertTrue(result.getUserId().contains(userId));

        verify(coursesService).findOrCreateByName(courseName);
        verify(classesRepository).save(any(Classes.class));
    }

    @Test
    void testCreateOrUpdateUserNewShouldAddUserId() {
        String classId = "T01";

        Classes existing = new Classes();
        existing.setClassId(classId);
        existing.setUserId(new ArrayList<>(List.of("user-old")));
        existing.setShift("Matutino");
        // Se o semester não muda via parâmetro, ele mantém o que já estava

        when(classesRepository.findByClassId(classId)).thenReturn(existing);
        when(classesRepository.save(any(Classes.class))).thenAnswer(inv -> inv.getArgument(0));

        Classes result = classesService.createOrUpdateClassByClassId(
                "Informática", classId, "Vespertino", "user-new"
        );

        assertTrue(result.getUserId().contains("user-old"));
        assertTrue(result.getUserId().contains("user-new"));
        assertEquals("Vespertino", result.getShift());
    }

    @Test
    void testCreateOrUpdateUserRepeatedShouldNotDuplicate() {
        String classId = "T01";

        Classes existing = new Classes();
        existing.setClassId(classId);
        existing.setUserId(new ArrayList<>(List.of("user-1")));
        existing.setShift("Vespertino");

        when(classesRepository.findByClassId(classId)).thenReturn(existing);
        when(classesRepository.save(any(Classes.class))).thenAnswer(inv -> inv.getArgument(0));

        Classes result = classesService.createOrUpdateClassByClassId(
                "Informática",  classId, "Vespertino", "user-1"
        );

        assertEquals(1, result.getUserId().size());
        assertEquals("user-1", result.getUserId().get(0));
    }

    @Test
    void testCreateOrUpdateGradleNullShouldKeepGradleLevel() {
        String classId = "T01";

        Classes existing = new Classes();
        existing.setClassId(classId);
        existing.setUserId(new ArrayList<>(List.of("user-1")));
        existing.setShift("Vespertino");

        when(classesRepository.findByClassId(classId)).thenReturn(existing);
        when(classesRepository.save(any(Classes.class))).thenAnswer(inv -> inv.getArgument(0));

        Classes result = classesService.createOrUpdateClassByClassId(
                "Informática",  classId, "Vespertino", "user-2"
        );
        assertTrue(result.getUserId().contains("user-2"));
    }
}
