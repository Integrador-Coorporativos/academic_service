package br.com.ifrn.AcademicService;

import br.com.ifrn.AcademicService.dto.ImportMessageDTO;
import br.com.ifrn.AcademicService.dto.request.RequestStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.request.RequestStudentPerformanceUpdateDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassificationsRankDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseclassificationsClassDTO;
import br.com.ifrn.AcademicService.models.ClassEvaluations;
import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.models.Courses;
import br.com.ifrn.AcademicService.models.EvaluationsCriteria;
import br.com.ifrn.AcademicService.models.StudentPerformance;
import br.com.ifrn.AcademicService.models.enums.StepName;
import br.com.ifrn.AcademicService.repository.*;
import br.com.ifrn.AcademicService.mapper.StudentPerformanceMapper;
import br.com.ifrn.AcademicService.services.StudentPerformanceService;
import jakarta.persistence.EntityNotFoundException;
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
class StudentPerformanceServiceTest {

    @Mock
    private StudentPerformanceRepository studentPerformanceRepository;

    @Mock
    private EvaluationPeriodRepository evaluationPeriodRepository;

    @Mock
    private StudentPerformanceMapper mapper;

    @Mock
    private ClassesRepository classesRepository;

    @Mock
    private ClassEvaluationsRepository classEvaluationsRepository;

    @InjectMocks
    private StudentPerformanceService service;

    private StudentPerformance entity;

    @BeforeEach
    void setUp() {
        entity = new StudentPerformance();
        entity.setId(1);
        entity.setStudentId("S1");
    }

    @Test
    void getStudentPerformanceByStudentIdWhenExistsShouldReturnEntity() {
        when(studentPerformanceRepository.findStudentPerformanceByStudentId("S1")).thenReturn(entity);

        StudentPerformance result = service.getStudentPerformanceByStudentId("S1");

        assertSame(entity, result);
        verify(studentPerformanceRepository).findStudentPerformanceByStudentId("S1");
        verifyNoMoreInteractions(studentPerformanceRepository, mapper, classesRepository, classEvaluationsRepository);
    }

    @Test
    void getStudentPerformanceByStudentIdWhenNotExistsShouldThrow() {
        when(studentPerformanceRepository.findStudentPerformanceByStudentId("S404")).thenReturn(null);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.getStudentPerformanceByStudentId("S404"));

        assertEquals("Student Performance not found", ex.getMessage());
        verify(studentPerformanceRepository).findStudentPerformanceByStudentId("S404");
        verifyNoMoreInteractions(studentPerformanceRepository, mapper, classesRepository, classEvaluationsRepository);
    }

    @Test
    void getStudentPerformanceByIdWhenExistsShouldReturnDto() {
        when(studentPerformanceRepository.findById(1)).thenReturn(Optional.of(entity));

        ResponseStudentPerformanceDTO dto = mock(ResponseStudentPerformanceDTO.class);
        when(mapper.toResponseDto(entity)).thenReturn(dto);

        ResponseStudentPerformanceDTO result = service.getStudentPerformanceById(1);

        assertSame(dto, result);
        verify(studentPerformanceRepository).findById(1);
        verify(mapper).toResponseDto(entity);
        verifyNoMoreInteractions(studentPerformanceRepository, mapper, classesRepository, classEvaluationsRepository);
    }

    @Test
    void getStudentPerformanceByIdWhenNotExistsShouldThrow() {
        when(studentPerformanceRepository.findById(99)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.getStudentPerformanceById(99));

        assertEquals("Not found Student Performance by Id: 99", ex.getMessage());
        verify(studentPerformanceRepository).findById(99);
        verifyNoMoreInteractions(studentPerformanceRepository, mapper, classesRepository, classEvaluationsRepository);
    }

    @Test
    void createStudentPerformanceShouldMapSaveAndReturnDto() {
        RequestStudentPerformanceDTO req = mock(RequestStudentPerformanceDTO.class);
        when(req.getStudentId()).thenReturn("S1");

        when(studentPerformanceRepository.findStudentPerformanceByStudentId("S1")).thenReturn(null);

        StudentPerformance mapped = new StudentPerformance();
        when(mapper.toEntity(req)).thenReturn(mapped);

        when(studentPerformanceRepository.save(mapped)).thenReturn(entity);

        ResponseStudentPerformanceDTO resp = mock(ResponseStudentPerformanceDTO.class);
        when(mapper.toResponseDto(entity)).thenReturn(resp);

        ResponseStudentPerformanceDTO result = service.createStudentPerformance(req);

        assertSame(resp, result);
        verify(studentPerformanceRepository).findStudentPerformanceByStudentId("S1");
        verify(mapper).toEntity(req);
        verify(studentPerformanceRepository).save(mapped);
        verify(mapper).toResponseDto(entity);
        verifyNoMoreInteractions(studentPerformanceRepository, mapper, classesRepository, classEvaluationsRepository);
    }

    @Test
    void createStudentPerformanceWhenAlreadyExistsShouldThrow() {
        RequestStudentPerformanceDTO req = mock(RequestStudentPerformanceDTO.class);
        when(req.getStudentId()).thenReturn("S1");

        when(studentPerformanceRepository.findStudentPerformanceByStudentId("S1")).thenReturn(new StudentPerformance());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.createStudentPerformance(req));

        assertEquals("Já existe uma avaliação cadastrada para esse aluno!", ex.getMessage());
        verify(studentPerformanceRepository).findStudentPerformanceByStudentId("S1");
        verifyNoMoreInteractions(studentPerformanceRepository, mapper, classesRepository, classEvaluationsRepository);
    }

    @Test
    void updateStudentPerformanceWhenExistsShouldUpdateSaveAndReturnDto() {
        when(studentPerformanceRepository.findById(1)).thenReturn(Optional.of(entity));

        RequestStudentPerformanceUpdateDTO req = mock(RequestStudentPerformanceUpdateDTO.class);

        when(studentPerformanceRepository.save(entity)).thenReturn(entity);

        ResponseStudentPerformanceDTO resp = mock(ResponseStudentPerformanceDTO.class);
        when(mapper.toResponseDto(entity)).thenReturn(resp);

        ResponseStudentPerformanceDTO result = service.updateStudentPerformance(1, req);

        assertSame(resp, result);
        verify(studentPerformanceRepository).findById(1);
        verify(mapper).updateEntityFromDto(req, entity);
        verify(studentPerformanceRepository, times(2)).save(entity);
        verify(mapper).toResponseDto(entity);
        verifyNoMoreInteractions(studentPerformanceRepository, mapper, classesRepository, classEvaluationsRepository);
    }

    @Test
    void updateStudentPerformanceWhenNotExistsShouldThrow() {
        when(studentPerformanceRepository.findById(123)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.updateStudentPerformance(123, mock(RequestStudentPerformanceUpdateDTO.class)));

        assertEquals("Not found Student Performance by Id: 123", ex.getMessage());
        verify(studentPerformanceRepository).findById(123);
        verifyNoMoreInteractions(studentPerformanceRepository, mapper, classesRepository, classEvaluationsRepository);
    }

    @Test
    void createStudentPerformanceByConsumerMessageDTOWhenStudentNotExistsShouldCreate() {
        ImportMessageDTO msg = mock(ImportMessageDTO.class);

        RequestStudentPerformanceDTO req = mock(RequestStudentPerformanceDTO.class);
        when(req.getStudentId()).thenReturn("S1");
        when(mapper.toRequestStudentPerformanceByConsumerMessageDto(msg)).thenReturn(req);

        when(studentPerformanceRepository.findStudentPerformanceByStudentId("S1")).thenReturn(null);

        StudentPerformanceService spy = spy(service);
        doReturn(mock(ResponseStudentPerformanceDTO.class))
                .when(spy).createStudentPerformance(req);

        ResponseStudentPerformanceDTO result = spy.createStudentPerformanceByConsumerMessageDTO(msg);

        assertNotNull(result);
        verify(mapper).toRequestStudentPerformanceByConsumerMessageDto(msg);
        verify(studentPerformanceRepository).findStudentPerformanceByStudentId("S1");
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
        existing.setStudentId("S1");
        when(studentPerformanceRepository.findStudentPerformanceByStudentId("S1")).thenReturn(existing);

        RequestStudentPerformanceUpdateDTO updateReq = mock(RequestStudentPerformanceUpdateDTO.class);
        when(mapper.toRequestStudentPerformanceUpdateDto(req)).thenReturn(updateReq);

        StudentPerformanceService spy = spy(service);
        doReturn(mock(ResponseStudentPerformanceDTO.class))
                .when(spy).updateStudentPerformance(55, updateReq);

        ResponseStudentPerformanceDTO result = spy.createStudentPerformanceByConsumerMessageDTO(msg);

        assertNotNull(result);
        verify(mapper).toRequestStudentPerformanceByConsumerMessageDto(msg);
        verify(studentPerformanceRepository).findStudentPerformanceByStudentId("S1");
        verify(mapper).toRequestStudentPerformanceUpdateDto(req);
        verify(spy).updateStudentPerformance(55, updateReq);
        verify(spy, never()).createStudentPerformance(any());
    }

    @Test
    void getAllStudentPerformanceWhenEmptyShouldReturnEmptyList() {
        when(studentPerformanceRepository.findAll()).thenReturn(List.of());

        List<ResponseStudentPerformanceDTO> result = service.getAllStudentPerformance();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(studentPerformanceRepository).findAll();
        verifyNoMoreInteractions(studentPerformanceRepository, mapper, classesRepository, classEvaluationsRepository);
    }

    @Test
    void getAllStudentPerformanceShouldMapList() {
        StudentPerformance e1 = new StudentPerformance();
        StudentPerformance e2 = new StudentPerformance();
        when(studentPerformanceRepository.findAll()).thenReturn(List.of(e1, e2));

        ResponseStudentPerformanceDTO d1 = mock(ResponseStudentPerformanceDTO.class);
        ResponseStudentPerformanceDTO d2 = mock(ResponseStudentPerformanceDTO.class);
        when(mapper.toResponseDto(e1)).thenReturn(d1);
        when(mapper.toResponseDto(e2)).thenReturn(d2);

        List<ResponseStudentPerformanceDTO> result = service.getAllStudentPerformance();

        assertEquals(2, result.size());
        verify(studentPerformanceRepository).findAll();
        verify(mapper).toResponseDto(e1);
        verify(mapper).toResponseDto(e2);
        verifyNoMoreInteractions(studentPerformanceRepository, mapper, classesRepository, classEvaluationsRepository);
    }

    @Test
    void getClassEvaluationsByIdShouldReturnNull() {
        assertNull(service.getClassEvaluationsById(1));
    }

    @Test
    void getClassificationsWhenMetricsNullShouldZeroAllScores() {
        Courses course = new Courses();
        course.setName("Informática");

        Classes c = new Classes();
        c.setId(1);
        c.setClassId("C1");
        c.setShift("Vespertino");
        c.setCourse(course);

        when(classesRepository.findAll()).thenReturn(List.of(c));
        when(classEvaluationsRepository.findRawMetricsByClassId("C1")).thenReturn(null);

        List<ResponseclassificationsClassDTO> result = service.getClassifications();

        assertEquals(1, result.size());
        ResponseclassificationsClassDTO dto = result.get(0);
        assertEquals(1, dto.getClassId());
        assertEquals("Informática", dto.getCourseName());
        assertEquals("Vespertino", dto.getShift());

        assertEquals(0.0f, dto.getFrequencyScore());
        assertEquals(0.0f, dto.getUnifirmScore());
        assertEquals(0.0f, dto.getBehaviorScore());
        assertEquals(0.0f, dto.getParticipationScore());
        assertEquals(0.0f, dto.getPerformanceScore());
        assertEquals(0.0f, dto.getCellPhoneUseScore());
        assertEquals(0.0f, dto.getAverageScore());

        verify(classesRepository).findAll();
        verify(classEvaluationsRepository).findRawMetricsByClassId("C1");
    }

    @Test
    void getClassificationsWhenMetricsPresentShouldFillScores() {
        Courses course = new Courses();
        course.setName("ADS");

        Classes c = new Classes();
        c.setId(1);
        c.setClassId("C1");
        c.setShift("Matutino");
        c.setCourse(course);

        EvaluationMetricsProjection metrics = mock(EvaluationMetricsProjection.class);
        when(metrics.getAvgFrequency()).thenReturn(1.0f);
        when(metrics.getAvgUniform()).thenReturn(2.0f);
        when(metrics.getAvgBehavior()).thenReturn(3.0f);
        when(metrics.getAvgParticipation()).thenReturn(4.0f);
        when(metrics.getAvgPerformance()).thenReturn(5.0f);
        when(metrics.getAvgCellPhone()).thenReturn(2.5f);
        when(metrics.getAvgTotal()).thenReturn(3.0f);

        when(classesRepository.findAll()).thenReturn(List.of(c));
        when(classEvaluationsRepository.findRawMetricsByClassId("C1")).thenReturn(metrics);

        List<ResponseclassificationsClassDTO> result = service.getClassifications();

        assertEquals(1, result.size());
        ResponseclassificationsClassDTO dto = result.get(0);

        assertEquals(1.0f, dto.getFrequencyScore());
        assertEquals(2.0f, dto.getUnifirmScore());
        assertEquals(3.0f, dto.getBehaviorScore());
        assertEquals(4.0f, dto.getParticipationScore());
        assertEquals(5.0f, dto.getPerformanceScore());
        assertEquals(2.5f, dto.getCellPhoneUseScore());
        assertEquals(3.0f, dto.getAverageScore());

        verify(classesRepository).findAll();
        verify(classEvaluationsRepository).findRawMetricsByClassId("C1");
    }

    @Test
    void getClassificationByClassIdShouldResetScoresWhenNoBimestreMetrics() {
        Courses course = new Courses();
        course.setName("ADS");

        Classes classe = new Classes();
        classe.setId(1);
        classe.setClassId("C1");
        classe.setShift("Vespertino");
        classe.setGradleLevel("2°");
        classe.setCourse(course);

        when(classesRepository.findById(1)).thenReturn(Optional.of(classe));

        when(classEvaluationsRepository.findMetricsByClassAndYearAndStep(eq("C1"), eq(2026), any(StepName.class)))
                .thenReturn(null);

        when(classesRepository.findAll()).thenReturn(List.of(classe));
        when(classEvaluationsRepository.findAll()).thenReturn(List.of());

        ResponseclassificationsClassDTO dto = service.getClassificationByClassId(1, 2026, 1);

        assertNotNull(dto);
        assertEquals(0.0f, dto.getAverageScore());
        assertNotNull(dto.getRank());
        verify(classesRepository).findById(1);
        verify(classEvaluationsRepository).findMetricsByClassAndYearAndStep(eq("C1"), eq(2026), any(StepName.class));
    }

    @Test
    void getClassesWithRankingsShouldSetRanksForClassesWithAndWithoutEvaluations() {
        Classes c1 = new Classes();
        c1.setId(1);

        Classes c2 = new Classes();
        c2.setId(2);

        when(classesRepository.findAll()).thenReturn(List.of(c1, c2));

        EvaluationsCriteria crit = new EvaluationsCriteria();
        crit.setFrequencyScore(5f);
        crit.setUnifirmScore(5f);
        crit.setBehaviorScore(5f);
        crit.setParticipationScore(5f);
        crit.setPerformanceScore(5f);
        crit.setCellPhoneUseScore(5f);
        crit.setAverageScore(5f);

        ClassEvaluations eval = new ClassEvaluations();
        eval.setClassId("IGNORADO_POR_BUG");
        eval.setCriteria(crit);

        when(classEvaluationsRepository.findAll()).thenReturn(List.of(eval));

        List<ResponseClassificationsRankDTO> ranks = service.getClassesWithRankings(2026);

        assertEquals(2, ranks.size());

        for (ResponseClassificationsRankDTO r : ranks) {
            assertTrue(r.getAverageRank() >= 1 && r.getAverageRank() <= 2);
            assertTrue(r.getFrequencyRank() >= 1 && r.getFrequencyRank() <= 2);
        }

        verify(classesRepository).findAll();
        verify(classEvaluationsRepository).findAll();
    }
}
