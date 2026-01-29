package br.com.ifrn.AcademicService;

import br.com.ifrn.AcademicService.dto.ImportMessageDTO;
import br.com.ifrn.AcademicService.dto.request.RequestStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.AcademicService.mapper.StudentPerformanceMapper;
import br.com.ifrn.AcademicService.models.StudentPerformance;
import br.com.ifrn.AcademicService.services.StudentPerformanceService;
import br.com.ifrn.AcademicService.repository.StudentPerformanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentPerformanceServiceTest {

    @Mock
    private StudentPerformanceRepository repository;

    @Mock
    private StudentPerformanceMapper mapper;

    @InjectMocks
    private StudentPerformanceService service;

    private StudentPerformance entity;

    @BeforeEach
    void setUp() {
        entity = new StudentPerformance();
        entity.setId(1);
    }

    @Test
    void getStudentPerformanceByStudentIdWhenExistsShouldReturnEntity() {
        when(repository.findStudentPerformanceByStudentId("S1")).thenReturn(entity);

        StudentPerformance result = service.getStudentPerformanceByStudentId("S1");

        assertSame(entity, result);
        verify(repository).findStudentPerformanceByStudentId("S1");
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void getStudentPerformanceByStudentIdWhenNotExistsShouldThrow() {
        when(repository.findStudentPerformanceByStudentId("S404")).thenReturn(null);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.getStudentPerformanceByStudentId("S404"));

        assertEquals("Student Performance not found", ex.getMessage());
        verify(repository).findStudentPerformanceByStudentId("S404");
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void getStudentPerformanceByIdWhenExistsShouldReturnDto() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));

        ResponseStudentPerformanceDTO dto = mock(ResponseStudentPerformanceDTO.class);
        when(mapper.toResponseDto(entity)).thenReturn(dto);

        ResponseStudentPerformanceDTO result = service.getStudentPerformanceById(1);

        assertSame(dto, result);
        verify(repository).findById(1);
        verify(mapper).toResponseDto(entity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void getStudentPerformanceByIdWhenNotExistsShouldThrow() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.getStudentPerformanceById(99));

        assertEquals("Not found Student Performance by Id: 99", ex.getMessage());
        verify(repository).findById(99);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void createStudentPerformanceShouldMapSaveAndReturnDto() {
        RequestStudentPerformanceDTO req = mock(RequestStudentPerformanceDTO.class);

        StudentPerformance mapped = new StudentPerformance();
        when(mapper.toEntity(req)).thenReturn(mapped);

        when(repository.save(mapped)).thenReturn(entity);

        ResponseStudentPerformanceDTO resp = mock(ResponseStudentPerformanceDTO.class);
        when(mapper.toResponseDto(entity)).thenReturn(resp);

        ResponseStudentPerformanceDTO result = service.createStudentPerformance(req);

        assertSame(resp, result);
        verify(mapper).toEntity(req);
        verify(repository).save(mapped);
        verify(mapper).toResponseDto(entity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void updateStudentPerformanceWhenExistsShouldUpdateSaveAndReturnDto() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));

        RequestStudentPerformanceDTO req = mock(RequestStudentPerformanceDTO.class);

        // save é chamado 2x no código atual
        when(repository.save(entity)).thenReturn(entity);

        ResponseStudentPerformanceDTO resp = mock(ResponseStudentPerformanceDTO.class);
        when(mapper.toResponseDto(entity)).thenReturn(resp);

        ResponseStudentPerformanceDTO result = service.updateStudentPerformance(1, req);

        assertSame(resp, result);
        verify(repository).findById(1);
        verify(mapper).updateEntityFromDto(req, entity);
        verify(repository, times(2)).save(entity);
        verify(mapper).toResponseDto(entity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void updateStudentPerformanceWhenNotExistsShouldThrow() {
        when(repository.findById(123)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.updateStudentPerformance(123, mock(RequestStudentPerformanceDTO.class)));

        assertEquals("Not found Student Performance by Id: 123", ex.getMessage());
        verify(repository).findById(123);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void createStudentPerformanceByConsumerMessageDTOWhenStudentNotExistsShouldCreate() {
        ImportMessageDTO msg = mock(ImportMessageDTO.class);

        RequestStudentPerformanceDTO req = mock(RequestStudentPerformanceDTO.class);
        when(req.getStudentId()).thenReturn("S1");
        when(mapper.toRequestStudentPerformanceByConsumerMessageDto(msg)).thenReturn(req);

        when(repository.findStudentPerformanceByStudentId("S1")).thenReturn(null);

        // spy para garantir que chamou o método certo
        StudentPerformanceService spy = spy(service);
        doReturn(mock(ResponseStudentPerformanceDTO.class))
                .when(spy).createStudentPerformance(req);

        ResponseStudentPerformanceDTO result = spy.createStudentPerformanceByConsumerMessageDTO(msg);

        assertNotNull(result);
        verify(mapper).toRequestStudentPerformanceByConsumerMessageDto(msg);
        verify(repository).findStudentPerformanceByStudentId("S1");
        verify(spy).createStudentPerformance(req);
        verify(spy, never()).updateStudentPerformance(anyInt(), any());
    }

    @Test
    void createStudentPerformanceByConsumerMessageDTOWhenStudentExistsShouldUpdate() {
        ImportMessageDTO msg = mock(ImportMessageDTO.class);

        RequestStudentPerformanceDTO req = mock(RequestStudentPerformanceDTO.class);
        when(req.getStudentId()).thenReturn("S1");
        when(mapper.toRequestStudentPerformanceByConsumerMessageDto(msg)).thenReturn(req);

        StudentPerformance existing = new StudentPerformance();
        existing.setId(55);
        when(repository.findStudentPerformanceByStudentId("S1")).thenReturn(existing);

        StudentPerformanceService spy = spy(service);
        doReturn(mock(ResponseStudentPerformanceDTO.class))
                .when(spy).updateStudentPerformance(55, req);

        ResponseStudentPerformanceDTO result = spy.createStudentPerformanceByConsumerMessageDTO(msg);

        assertNotNull(result);
        verify(mapper).toRequestStudentPerformanceByConsumerMessageDto(msg);
        verify(repository).findStudentPerformanceByStudentId("S1");
        verify(spy).updateStudentPerformance(55, req);
        verify(spy, never()).createStudentPerformance(any());
    }

    @Test
    void getAllStudentPerformanceWhenEmptyShouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        List<ResponseStudentPerformanceDTO> result = service.getAllStudentPerformance();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findAll();
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void getAllStudentPerformanceShouldMapList() {
        StudentPerformance e1 = new StudentPerformance();
        StudentPerformance e2 = new StudentPerformance();
        when(repository.findAll()).thenReturn(List.of(e1, e2));

        ResponseStudentPerformanceDTO d1 = mock(ResponseStudentPerformanceDTO.class);
        ResponseStudentPerformanceDTO d2 = mock(ResponseStudentPerformanceDTO.class);
        when(mapper.toResponseDto(e1)).thenReturn(d1);
        when(mapper.toResponseDto(e2)).thenReturn(d2);

        List<ResponseStudentPerformanceDTO> result = service.getAllStudentPerformance();

        assertEquals(2, result.size());
        verify(repository).findAll();
        verify(mapper).toResponseDto(e1);
        verify(mapper).toResponseDto(e2);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void getClassEvaluationsByIdShouldReturnNull() {
        assertNull(service.getClassEvaluationsById(1));
    }
}
