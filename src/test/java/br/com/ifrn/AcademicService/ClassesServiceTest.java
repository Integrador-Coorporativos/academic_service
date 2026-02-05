package br.com.ifrn.AcademicService;

import br.com.ifrn.AcademicService.config.keycloak.KeycloakAdminConfig;
import br.com.ifrn.AcademicService.dto.request.RequestClassDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassByIdDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassDTO;
import br.com.ifrn.AcademicService.dto.response.StudentDataDTO;
import br.com.ifrn.AcademicService.mapper.ClassMapper;
import br.com.ifrn.AcademicService.mapper.StudentPerformanceMapper;
import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.models.Courses;
import br.com.ifrn.AcademicService.models.StudentPerformance;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import br.com.ifrn.AcademicService.repository.CoursesRepository;
import br.com.ifrn.AcademicService.repository.StudentPerformanceRepository;
import br.com.ifrn.AcademicService.services.ClassesService;
import br.com.ifrn.AcademicService.services.CoursesService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ClassesServiceTest {

    @Mock
    private ClassMapper classsMapper;

    @Mock
    private ClassesRepository classesRepository;

    @Mock
    private CoursesRepository coursesRepository;

    @Mock
    private CoursesService coursesService;

    @Mock
    private StudentPerformanceRepository studentPerformanceRepository;

    @Mock
    private StudentPerformanceMapper studentPerformanceMapper;

    @Mock
    private KeycloakAdminConfig keycloakAdminConfig;

    @InjectMocks
    private ClassesService classesService;

    private Classes turma;
    private Courses course;

    @BeforeEach
    void setUp() {
        course = new Courses();
        course.setId(10);
        course.setName("Informática");

        turma = new Classes();
        turma.setId(1);
        turma.setClassId("T01");
        turma.setShift("Vespertino");
        turma.setName("Matemática");
        turma.setCourse(course);
        turma.setProfessors(new ArrayList<>(List.of("professorId")));
        turma.setUserId(new ArrayList<>());
        turma.setComments(new ArrayList<>());
    }

    @Test
    void testCreate() {
        Integer courseId = 10;

        RequestClassDTO req = new RequestClassDTO();
        req.setClassId("T01");
        req.setShift("Vespertino");

        Classes mapped = new Classes();
        mapped.setClassId("T01");
        mapped.setShift("Vespertino");

        Classes saved = new Classes();
        saved.setId(1);
        saved.setClassId("T01");
        saved.setShift("Vespertino");
        saved.setCourse(course);
        saved.setName("Informática_T01");

        ResponseClassDTO resp = new ResponseClassDTO();
        resp.setId(1);
        resp.setName("Informática_T01");

        when(coursesRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(classsMapper.toClassDTO(req)).thenReturn(mapped);
        when(classesRepository.save(any(Classes.class))).thenReturn(saved);
        when(classsMapper.toResponseClassDTO(saved)).thenReturn(resp);

        ResponseClassDTO created = classesService.create(courseId, req);

        assertNotNull(created);
        assertEquals("Informática_T01", created.getName());
        verify(classesRepository).save(any(Classes.class));
    }

    @Test
    void testGetAll() {
        Classes turmaEntity = new Classes();
        turmaEntity.setId(1);
        turmaEntity.setName("Matemática");
        turmaEntity.setProfessors(new ArrayList<>(List.of("professorId")));

        ResponseClassDTO turmaDTO = new ResponseClassDTO();
        turmaDTO.setId(1);
        turmaDTO.setName("Matemática");
        turmaDTO.setTeacherLinked(true);

        when(classesRepository.findAllWithCourse()).thenReturn(List.of(turmaEntity));
        when(classsMapper.toResponseClassDTO(turmaEntity)).thenReturn(turmaDTO);

        List<ResponseClassDTO> all = classesService.getAll("professorId");

        assertEquals(1, all.size());
        assertEquals("Matemática", all.get(0).getName());
        assertTrue(all.get(0).isTeacherLinked());
        verify(classesRepository).findAllWithCourse();
        verify(classsMapper).toResponseClassDTO(turmaEntity);
    }

    @Test
    void testGetAllWhenEmpty() {
        when(classesRepository.findAllWithCourse()).thenReturn(List.of());

        List<ResponseClassDTO> all = classesService.getAll("professerId");

        assertNotNull(all);
        assertTrue(all.isEmpty());
        verify(classesRepository).findAllWithCourse();
        verifyNoInteractions(classsMapper);
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
        Integer id = 1;
        Integer courseId = 10;

        RequestClassDTO details = new RequestClassDTO();
        details.setClassId("T99");
        details.setShift("Noturno");
        details.setGradleLevel(null);

        Classes turmaInDb = new Classes();
        turmaInDb.setId(id);
        turmaInDb.setCourse(course);
        turmaInDb.setClassId("T01");
        turmaInDb.setShift("Vespertino");

        Classes wrapperCourse = new Classes();
        wrapperCourse.setId(courseId);
        wrapperCourse.setCourse(course);

        Classes saved = new Classes();
        saved.setId(id);
        saved.setCourse(course);
        saved.setClassId("T99");
        saved.setShift("Noturno");
        saved.setName("Informática_T99");

        ResponseClassDTO response = new ResponseClassDTO();
        response.setId(id);
        response.setName("Informática_T99");

        when(classesRepository.findById(id)).thenReturn(Optional.of(turmaInDb));
        when(classesRepository.findById(courseId)).thenReturn(Optional.of(wrapperCourse));
        when(classesRepository.save(any(Classes.class))).thenReturn(saved);
        when(classsMapper.toResponseClassDTO(saved)).thenReturn(response);

        ResponseClassDTO result = classesService.update(id, courseId, details);

        assertEquals("Informática_T99", result.getName());
        verify(classesRepository).save(any(Classes.class));
    }

    @Test
    void testUpdateWhenClassDoesNotExist() {
        Integer id = 1;
        Integer courseId = 10;

        RequestClassDTO details = new RequestClassDTO();
        details.setClassId("T02");

        when(classesRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                classesService.update(id, courseId, details)
        );

        assertEquals("Classe not found", ex.getMessage());
        verify(classesRepository).findById(id);
        verify(classesRepository, never()).save(any());
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

    @Test
    void testGetByClassIdNotFoundShouldThrow() {
        when(classesRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                classesService.getByClassId(1)
        );

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
        verifyNoInteractions(studentPerformanceRepository);
        verifyNoInteractions(studentPerformanceMapper);
    }

    @Test
    void testGetByClassIdKeycloakUsersEmptyShouldReturnEmptyStudents() throws Exception {
        Classes c = new Classes();
        c.setId(1);
        c.setName("Turma X");
        c.setUserId(List.of("u1"));

        ResponseClassByIdDTO dto = new ResponseClassByIdDTO();

        when(classesRepository.findById(1)).thenReturn(Optional.of(c));
        when(classsMapper.toResponseClassByDTO(c)).thenReturn(dto);

        when(studentPerformanceRepository.findByStudentIdIn(anyList())).thenReturn(new ArrayList<>());
        when(keycloakAdminConfig.findKeycloakUsersByIds(anyList())).thenReturn(new ArrayList<>());

        ResponseClassByIdDTO result = classesService.getByClassId(1);

        assertNotNull(result.getStudents());
        assertTrue(result.getStudents().isEmpty());
        verifyNoMoreInteractions(studentPerformanceMapper);
    }

    @Test
    void testGetByClassIdUserFoundShouldAddStudent() throws Exception {
        Integer id = 1;

        Classes classe = new Classes();
        classe.setId(id);
        classe.setName("Turma X");
        classe.setUserId(List.of("aluno-uuid-123"));

        UserRepresentation user = new UserRepresentation();
        user.setId("aluno-uuid-123");
        user.setFirstName("Eduardo");
        user.setUsername("202612345");

        StudentPerformance perf = new StudentPerformance();
        perf.setStudentId("aluno-uuid-123");

        StudentDataDTO studentDTO = new StudentDataDTO();
        studentDTO.setStudentId("aluno-uuid-123");

        ResponseClassByIdDTO base = new ResponseClassByIdDTO();

        when(classesRepository.findById(id)).thenReturn(Optional.of(classe));
        when(classsMapper.toResponseClassByDTO(classe)).thenReturn(base);

        when(studentPerformanceRepository.findByStudentIdIn(anyList()))
                .thenReturn(List.of(perf));

        when(keycloakAdminConfig.findKeycloakUsersByIds(anyList()))
                .thenReturn(List.of(user));

        when(studentPerformanceMapper.toStudentDataDTO(perf)).thenReturn(studentDTO);

        ResponseClassByIdDTO result = classesService.getByClassId(id);

        assertNotNull(result.getStudents());
        assertEquals(1, result.getStudents().size());
        assertEquals("Eduardo", result.getStudents().get(0).getName());
        assertEquals("202612345", result.getStudents().get(0).getRegistration());
        assertEquals("aluno-uuid-123", result.getStudents().get(0).getStudentId());
    }

    @Test
    void testCreateOrUpdateClassIdNullShouldThrow() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                classesService.createOrUpdateClassByClassId("Info", null, "Vespertino", "user-1")
        );

        assertEquals("classId não pode ser nulo", ex.getMessage());
    }

    @Test
    void testCreateOrUpdateNotFoundShouldCreate() {
        String courseName = "Informática";
        String classId = "T01";
        String shift = "Vespertino";
        String userId = "user-123";

        when(classesRepository.findByClassId(classId)).thenReturn(null);

        Courses c = new Courses();
        c.setId(10);
        c.setName(courseName);

        when(coursesService.findOrCreateByName(courseName)).thenReturn(c);

        when(classesRepository.save(any(Classes.class))).thenAnswer(inv -> inv.getArgument(0));

        Classes result = classesService.createOrUpdateClassByClassId(courseName, classId, shift, userId);

        assertEquals(c, result.getCourse());
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

        when(classesRepository.findByClassId(classId)).thenReturn(existing);
        when(classesRepository.save(any(Classes.class))).thenAnswer(inv -> inv.getArgument(0));

        Classes result = classesService.createOrUpdateClassByClassId("Informática", classId, "Vespertino", "user-new");

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

        Classes result = classesService.createOrUpdateClassByClassId("Informática", classId, "Vespertino", "user-1");

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

        Classes result = classesService.createOrUpdateClassByClassId("Informática", classId, "Vespertino", "user-2");

        assertTrue(result.getUserId().contains("user-2"));
    }

    @Test
    void addProfessorToClassWhenAlreadyContainsShouldRemove() {
        Classes c = new Classes();
        c.setId(1);
        c.setProfessors(new ArrayList<>(List.of("prof1")));

        ResponseClassDTO resp = new ResponseClassDTO();
        resp.setId(1);

        when(classesRepository.findById(1)).thenReturn(Optional.of(c));
        when(classesRepository.save(any(Classes.class))).thenAnswer(inv -> inv.getArgument(0));
        when(classsMapper.toResponseClassDTO(any(Classes.class))).thenReturn(resp);

        ResponseClassDTO result = classesService.addProfessorToClass(1, "prof1");

        assertNotNull(result);
        assertFalse(c.getProfessors().contains("prof1"));
        verify(classesRepository).save(c);
    }

    @Test
    void addProfessorToClassWhenNotContainsShouldAdd() {
        Classes c = new Classes();
        c.setId(1);
        c.setProfessors(new ArrayList<>());

        ResponseClassDTO resp = new ResponseClassDTO();
        resp.setId(1);

        when(classesRepository.findById(1)).thenReturn(Optional.of(c));
        when(classesRepository.save(any(Classes.class))).thenAnswer(inv -> inv.getArgument(0));
        when(classsMapper.toResponseClassDTO(any(Classes.class))).thenReturn(resp);

        ResponseClassDTO result = classesService.addProfessorToClass(1, "prof1");

        assertNotNull(result);
        assertTrue(c.getProfessors().contains("prof1"));
        verify(classesRepository).save(c);
    }

    @Test
    void createOrUpdateExistingWhenUserIdListNullShouldInitializeAndAdd() {
        Classes existing = new Classes();
        existing.setClassId("T01");
        existing.setUserId(null); // branch importante
        existing.setShift("Vespertino");

        when(classesRepository.findByClassId("T01")).thenReturn(existing);
        when(classesRepository.save(any(Classes.class))).thenAnswer(inv -> inv.getArgument(0));

        Classes result = classesService.createOrUpdateClassByClassId("Informática", "T01", "Vespertino", "user-x");

        assertNotNull(result.getUserId());
        assertTrue(result.getUserId().contains("user-x"));
        // shift igual -> não entra no if de update
        assertEquals("Vespertino", result.getShift());
        verify(classesRepository).save(existing);
    }
}
