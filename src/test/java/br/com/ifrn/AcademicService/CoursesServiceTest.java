package br.com.ifrn.AcademicService;

import br.com.ifrn.AcademicService.repository.CoursesRepository;
import br.com.ifrn.AcademicService.services.CoursesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;
import br.com.ifrn.AcademicService.models.Courses;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class CoursesServiceTest{

    @Mock
    private CoursesRepository coursesRepository;

    @InjectMocks
    private CoursesService coursesService;

    private Courses course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        course = new Courses();
        course.setId(1);
        course.setName("Análise de Sistemas");
        course.setDescription("Curso de Análise de Sistemas");
    }

    // ==============================
    // Testes Unitários
    // ==============================

    @Test
    void testCreate() {
        when(coursesRepository.save(any(Courses.class))).thenReturn(course);
        Courses created = coursesService.create(course);
        assertNotNull(created);
        assertEquals("Análise de Sistemas", created.getName());
    }

    @Test
    void testGetAll() {
        when(coursesRepository.findAll()).thenReturn(List.of(course));
        List<Courses> all = coursesService.getAll();
        assertEquals(1, all.size());
        assertEquals("Análise de Sistemas", all.get(0).getName());
    }

    @Test
    void testGetById() {
        when(coursesRepository.findById(1)).thenReturn(Optional.of(course));
        Optional<Courses> result = coursesService.getById(1);
        assertTrue(result.isPresent());
        assertEquals("Análise de Sistemas", result.get().getName());
    }

    @Test
    void testUpdate() {
        Courses updated = new Courses();
        updated.setName("Informática");
        updated.setDescription("Curso de Informática");

        when(coursesRepository.findById(1)).thenReturn(Optional.of(course));
        when(coursesRepository.save(any(Courses.class))).thenReturn(updated);

        Courses result = coursesService.update(1, updated);
        assertEquals("Informática", result.getName());
        assertEquals("Curso de Informática", result.getDescription());
    }

    @Test
    void testDelete() {
        when(coursesRepository.existsById(1)).thenReturn(true);
        doNothing().when(coursesRepository).deleteById(1);
        assertDoesNotThrow(() -> coursesService.delete(1));
        verify(coursesRepository, times(1)).deleteById(1);
    }

    // ==============================
    // Testes de Valor Limite
    // ==============================
    @Test
    void testGetByIdZeroShouldReturnEmpty() {
        when(coursesRepository.findById(0)).thenReturn(Optional.empty());
        Optional<Courses> result = coursesService.getById(0);
        assertTrue(result.isEmpty(), "ID zero deve retornar vazio");
    }

    @Test
    void testGetByIdMaxValueShouldReturnEmpty() {
        int maxId = Integer.MAX_VALUE;
        when(coursesRepository.findById(maxId)).thenReturn(Optional.empty());
        Optional<Courses> result = coursesService.getById(maxId);
        assertTrue(result.isEmpty(), "ID máximo deve retornar vazio");
    }

    @Test
    void testCreateWithEmptyNameShouldFail() {
        course.setName("");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            coursesService.create(course);
        });
        assertEquals("Nome do curso não pode ser vazio", exception.getMessage());
    }

    @Test
    void testCreateWithNullNameShouldFail() {
        course.setName(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            coursesService.create(course);
        });
        assertEquals("Nome do curso não pode ser nulo", exception.getMessage());
    }

    @Test
    void testCreateWithVeryLongNameShouldFail() {
        course.setName("A".repeat(256));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            coursesService.create(course);
        });
        assertEquals("Nome do curso não pode exceder 255 caracteres", exception.getMessage());
    }

    @Test
    void testCreateWithVeryLongDescriptionShouldFail() {
        course.setDescription("D".repeat(501));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            coursesService.create(course);
        });
        assertEquals("Descrição do curso não pode exceder 500 caracteres", exception.getMessage());
    }

    // ==============================
    // TESTES ESTRUTURAIS
    // ==============================

    @Test
    void testUpdateWhenCourseDoesNotExist() {
        when(coursesRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            coursesService.update(1, course);
        });
    }

    @Test
    void testCreateInvalidCourseShouldNotCallSave() {
        course.setName(null);

        assertThrows(IllegalArgumentException.class, () -> {
            coursesService.create(course);
        });

        verify(coursesRepository, never()).save(any());
    }

}
