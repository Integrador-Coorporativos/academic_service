package br.com.ifrn.AcademicService;

import br.com.ifrn.AcademicService.dto.request.RequestCourseDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseCourseDTO;
import br.com.ifrn.AcademicService.mapper.CoursesMapper;
import br.com.ifrn.AcademicService.models.Courses;
import br.com.ifrn.AcademicService.repository.CoursesRepository;
import br.com.ifrn.AcademicService.services.CoursesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoursesServiceTest {

    @Mock
    private CoursesRepository coursesRepository;

    @Mock
    private CoursesMapper coursesMapper;

    @InjectMocks
    private CoursesService coursesService;

    private Courses course;
    private ResponseCourseDTO responseDTO;

    @BeforeEach
    void setUp() {
        course = new Courses();
        course.setId(1);
        course.setName("Análise de Sistemas");

        responseDTO = new ResponseCourseDTO();
        responseDTO.setId(1);
        responseDTO.setName("Análise de Sistemas");
    }

    @Test
    void testCreate() {
        RequestCourseDTO request = new RequestCourseDTO();
        request.setName("Análise de Sistemas");

        when(coursesRepository.findByName("Análise de Sistemas")).thenReturn(null);
        when(coursesRepository.save(any(Courses.class))).thenReturn(course);

        when(coursesMapper.toResponseCourseDTO(any(Courses.class))).thenReturn(responseDTO);

        ResponseCourseDTO created = coursesService.create(request);

        assertNotNull(created);
        assertEquals("Análise de Sistemas", created.getName());
        verify(coursesRepository).save(any(Courses.class));
    }

    @Test
    void testGetAll() {
        when(coursesRepository.findAll()).thenReturn(List.of(course));

        when(coursesMapper.toResponseCourseDTO(anyList()))
                .thenReturn(List.of(responseDTO));

        List<ResponseCourseDTO> all = coursesService.getAll();

        assertEquals(1, all.size());
        assertEquals("Análise de Sistemas", all.get(0).getName());
    }

    @Test
    void testGetById() {
        when(coursesRepository.findById(1)).thenReturn(Optional.of(course));
        when(coursesMapper.toResponseCourseDTO(course)).thenReturn(responseDTO);

        ResponseCourseDTO result = coursesService.getById(1);

        assertNotNull(result);
        assertEquals("Análise de Sistemas", result.getName());
    }

    @Test
    void testUpdate() {
        Courses courseInDb = new Courses();
        courseInDb.setId(1);
        courseInDb.setName("Nome Antigo");

        RequestCourseDTO request = new RequestCourseDTO();
        request.setName("Informática");

        ResponseCourseDTO updatedResponse = new ResponseCourseDTO();
        updatedResponse.setId(1);
        updatedResponse.setName("Informática");

        when(coursesRepository.findById(1)).thenReturn(Optional.of(courseInDb));
        when(coursesRepository.save(any(Courses.class))).thenAnswer(i -> i.getArgument(0));

        when(coursesMapper.toResponseCourseDTO(any(Courses.class))).thenReturn(updatedResponse);

        ResponseCourseDTO result = coursesService.update(1, request);

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
        verify(coursesRepository, never()).deleteById(anyInt());
    }

    @Test
    void testCreateWithEmptyNameShouldFail() {
        RequestCourseDTO request = new RequestCourseDTO();
        request.setName("");

        assertThrows(IllegalArgumentException.class, () -> coursesService.create(request));
        verify(coursesRepository, never()).save(any());
    }

    @Test
    void testCreateInvalidCourseShouldNotCallSave() {
        RequestCourseDTO request = new RequestCourseDTO();
        request.setName(null);

        assertThrows(IllegalArgumentException.class, () -> coursesService.create(request));
        verify(coursesRepository, never()).save(any());
    }

    @Test
    void testUpdateWithNullNameShouldFail() {
        RequestCourseDTO request = new RequestCourseDTO();
        request.setName(null);

        assertThrows(IllegalArgumentException.class, () -> coursesService.update(1, request));
        verifyNoInteractions(coursesRepository);
    }

    @Test
    void testUpdateWithEmptyNameShouldFail() {
        RequestCourseDTO request = new RequestCourseDTO();
        request.setName("");

        assertThrows(IllegalArgumentException.class, () -> coursesService.update(1, request));
        verifyNoInteractions(coursesRepository);
    }

    @Test
    void testUpdateWithVeryLongNameShouldFail() {
        RequestCourseDTO request = new RequestCourseDTO();
        request.setName("A".repeat(256));

        assertThrows(IllegalArgumentException.class, () -> coursesService.update(1, request));
        verifyNoInteractions(coursesRepository);
    }

    @Test
    void testUpdateWhenCourseDoesNotExist() {
        when(coursesRepository.findById(1)).thenReturn(Optional.empty());

        RequestCourseDTO request = new RequestCourseDTO();
        request.setName("Qualquer Nome");

        assertThrows(RuntimeException.class, () -> coursesService.update(1, request));
        verify(coursesRepository, never()).save(any());
    }

    @Test
    void testFindOrCreateByNameWhenExists() {
        when(coursesRepository.findByName("Análise de Sistemas")).thenReturn(course);

        Courses result = coursesService.findOrCreateByName("Análise de Sistemas");

        assertEquals(course, result);
        verify(coursesRepository, never()).save(any());
    }

    @Test
    void testFindOrCreateByNameWhenNotExists() {
        when(coursesRepository.findByName("ADS")).thenReturn(null);
        when(coursesRepository.save(any(Courses.class))).thenReturn(course);

        Courses result = coursesService.findOrCreateByName("ADS");

        assertNotNull(result);
        verify(coursesRepository).save(any(Courses.class));
    }
}
