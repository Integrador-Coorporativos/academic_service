package br.com.ifrn.AcademicService;

import br.com.ifrn.AcademicService.dto.request.RequestCourseDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseCourseDTO;
import br.com.ifrn.AcademicService.mapper.CoursesMapper;
import br.com.ifrn.AcademicService.models.Courses;
import br.com.ifrn.AcademicService.repository.CoursesRepository;
import br.com.ifrn.AcademicService.services.CoursesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class CoursesServiceTest {

    @Mock
    private CoursesRepository coursesRepository;

    @InjectMocks
    private CoursesService coursesService;

    @Autowired
    CoursesMapper coursesMapper;

    private Courses course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        course = new Courses();
        course.setId(1);
        course.setName("Análise de Sistemas");
    }

    @Test
    void testCreate() {
        when(coursesRepository.findByName(course.getName())).thenReturn(null);
        when(coursesRepository.save(any(Courses.class))).thenReturn(course);
        RequestCourseDTO  requestCourseDTO = coursesMapper.toRequestCourseDTO(course);

        ResponseCourseDTO created = coursesService.create(requestCourseDTO);

        assertNotNull(created);
        assertEquals("Análise de Sistemas", created.getName());
    }

    @Test
    void testGetAll() {
        when(coursesRepository.findAll()).thenReturn(List.of(course));

        List<ResponseCourseDTO> all = coursesService.getAll();

        assertEquals(1, all.size());
    }

    @Test
    void testGetById() {
        when(coursesRepository.findById(1)).thenReturn(Optional.of(course));

        ResponseCourseDTO result = coursesService.getById(1);

        assertTrue(result != null);
    }

    @Test
    void testUpdate() {
        // GIVEN
        Courses updatedDetails = new Courses();
        updatedDetails.setName("Informática"); // Nome válido, não lança erro!
        RequestCourseDTO requestCourseDTO = coursesMapper.toRequestCourseDTO(updatedDetails);

        // "turma" ou "course" (o que está no banco)
        Courses courseInDb = new Courses();
        courseInDb.setId(1);
        courseInDb.setName("Nome Antigo");

        when(coursesRepository.findById(1)).thenReturn(Optional.of(courseInDb));
        when(coursesRepository.save(any(Courses.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        ResponseCourseDTO result = coursesService.update(1, requestCourseDTO);

        // THEN
        assertEquals("Informática", result.getName());
        verify(coursesRepository).save(any(Courses.class));
    }

    @Test
    void testDeleteShouldReturnTrueWhenExists() {
        when(coursesRepository.existsById(1)).thenReturn(true);

        boolean result = coursesService.delete(1);

        assertTrue(result);
        verify(coursesRepository).deleteById(1);
    }

    @Test
    void testDeleteShouldThrowWhenNotExists() {
        when(coursesRepository.existsById(1)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> coursesService.delete(1));
        verify(coursesRepository, never()).deleteById(any());
    }

    @Test
    void testCreateWithEmptyNameShouldFail() {
        course.setName("");

        assertThrows(IllegalArgumentException.class,
                () -> coursesService.create(coursesMapper.toRequestCourseDTO(course)));
    }

    @Test
    void testCreateInvalidCourseShouldNotCallSave() {
        course.setName(null);

        assertThrows(IllegalArgumentException.class,
                () -> coursesService.create(coursesMapper.toRequestCourseDTO(course)));

        verify(coursesRepository, never()).save(any());
    }

    @Test
    void testUpdateWithNullNameShouldFail() {
        course.setName(null);

        assertThrows(IllegalArgumentException.class,
                () -> coursesService.update(1, coursesMapper.toRequestCourseDTO(course)));
    }

    @Test
    void testUpdateWithEmptyNameShouldFail() {
        course.setName("");

        assertThrows(IllegalArgumentException.class,
                () -> coursesService.update(1, coursesMapper.toRequestCourseDTO(course)));
    }

    @Test
    void testUpdateWithVeryLongNameShouldFail() {
        course.setName("A".repeat(256));

        assertThrows(IllegalArgumentException.class,
                () -> coursesService.update(1, coursesMapper.toRequestCourseDTO(course)));
    }

    @Test
    void testUpdateWhenCourseDoesNotExist() {
        when(coursesRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> coursesService.update(1, coursesMapper.toRequestCourseDTO(course)));
    }

    @Test
    void testFindOrCreateByNameWhenExists() {
        when(coursesRepository.findByName("Análise de Sistemas"))
                .thenReturn(course);

        Courses result = coursesService.findOrCreateByName("Análise de Sistemas");

        assertEquals(course, result);
        verify(coursesRepository, never()).save(any());
    }

    @Test
    void testFindOrCreateByNameWhenNotExists() {
        when(coursesRepository.findByName("ADS")).thenReturn(null);
        when(coursesRepository.save(any())).thenReturn(course);

        Courses result = coursesService.findOrCreateByName("ADS");

        assertNotNull(result);
        verify(coursesRepository).save(any());
    }
}
