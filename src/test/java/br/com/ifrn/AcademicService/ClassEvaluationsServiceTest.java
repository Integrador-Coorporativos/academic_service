package br.com.ifrn.AcademicService;

import br.com.ifrn.AcademicService.dto.request.RequestClassEvaluationsDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassEvaluationsDTO;
import br.com.ifrn.AcademicService.mapper.EvaluationsMapper;
import br.com.ifrn.AcademicService.models.ClassEvaluations;
import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.models.EvaluationPeriod;
import br.com.ifrn.AcademicService.models.EvaluationsCriteria;
import br.com.ifrn.AcademicService.repository.ClassEvaluationsRepository;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import br.com.ifrn.AcademicService.repository.StudentPerformanceRepository;
import br.com.ifrn.AcademicService.services.ClassEvaluationsService;
import br.com.ifrn.AcademicService.services.PainelControleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassEvaluationsServiceTest {

    @Mock
    private ClassEvaluationsRepository classEvaluationsRepository;

    @Mock
    private EvaluationsMapper evaluationsMapper;

    @Mock
    private ClassesRepository classesRepository;

    @Mock
    private PainelControleService painelControleService;

    @Mock
    private StudentPerformanceRepository studentPerformanceRepository;

    @InjectMocks
    private ClassEvaluationsService service;

    private ClassEvaluations entity1;
    private ClassEvaluations entity2;

    @BeforeEach
    void setUp() {
        entity1 = new ClassEvaluations();
        entity2 = new ClassEvaluations();
    }

    @Test
    void getAllEvaluations_shouldMapEntitiesAndReturnMutableArrayList() {
        when(classEvaluationsRepository.findAll()).thenReturn(List.of(entity1, entity2));

        ResponseClassEvaluationsDTO dto1 = mock(ResponseClassEvaluationsDTO.class);
        ResponseClassEvaluationsDTO dto2 = mock(ResponseClassEvaluationsDTO.class);

        when(evaluationsMapper.toResponseClassEvaluationsDTO(entity1)).thenReturn(dto1);
        when(evaluationsMapper.toResponseClassEvaluationsDTO(entity2)).thenReturn(dto2);

        List<ResponseClassEvaluationsDTO> result = service.getAllEvaluations();

        assertEquals(2, result.size());
        assertTrue(result instanceof ArrayList, "Should return a mutable ArrayList");
        assertDoesNotThrow(() -> result.add(mock(ResponseClassEvaluationsDTO.class)));

        verify(classEvaluationsRepository).findAll();
        verify(evaluationsMapper).toResponseClassEvaluationsDTO(entity1);
        verify(evaluationsMapper).toResponseClassEvaluationsDTO(entity2);
        verifyNoMoreInteractions(classEvaluationsRepository, evaluationsMapper);
    }

    @Test
    void getEvaluationByIdWhenExistsShouldReturnDTO() {
        when(classEvaluationsRepository.findById(1)).thenReturn(Optional.of(entity1));

        ResponseClassEvaluationsDTO dto = mock(ResponseClassEvaluationsDTO.class);
        when(evaluationsMapper.toResponseClassEvaluationsDTO(entity1)).thenReturn(dto);

        ResponseClassEvaluationsDTO result = service.getEvaluationById(1);

        assertSame(dto, result);
        verify(classEvaluationsRepository).findById(1);
        verify(evaluationsMapper).toResponseClassEvaluationsDTO(entity1);
        verifyNoMoreInteractions(classEvaluationsRepository, evaluationsMapper);
    }

    @Test
    void getEvaluationByIdWhenNotExistsShouldThrowException() {
        when(classEvaluationsRepository.findById(99)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.getEvaluationById(99)
        );

        assertEquals("Evaluation not found", ex.getMessage());
        verify(classEvaluationsRepository).findById(99);
        verifyNoMoreInteractions(classEvaluationsRepository, evaluationsMapper);
    }

    @Test
    void getEvaluationsByClassIdShouldMapEntityList() {
        Classes classes = new Classes();
        classes.setId(10);
        classes.setClassId("classId");

        when(classesRepository.findById(10)).thenReturn(Optional.of(classes));
        when(classEvaluationsRepository.findByClassId("classId")).thenReturn(List.of(entity1, entity2));

        ResponseClassEvaluationsDTO dto1 = mock(ResponseClassEvaluationsDTO.class);
        ResponseClassEvaluationsDTO dto2 = mock(ResponseClassEvaluationsDTO.class);

        when(evaluationsMapper.toResponseClassEvaluationsDTO(entity1)).thenReturn(dto1);
        when(evaluationsMapper.toResponseClassEvaluationsDTO(entity2)).thenReturn(dto2);

        List<ResponseClassEvaluationsDTO> result = service.getEvaluationsByClassId(10);

        assertEquals(2, result.size());
        verify(classesRepository).findById(10);
        verify(classEvaluationsRepository).findByClassId("classId");
        verify(evaluationsMapper).toResponseClassEvaluationsDTO(entity1);
        verify(evaluationsMapper).toResponseClassEvaluationsDTO(entity2);
        verifyNoMoreInteractions(classEvaluationsRepository, evaluationsMapper);
    }

    @Test
    void createEvaluationShouldSaveEntityAndCalculateAverageZeroScores() {
        RequestClassEvaluationsDTO req = mock(RequestClassEvaluationsDTO.class);
        when(req.getBehaviorScore()).thenReturn(0f);
        when(req.getFrequencyScore()).thenReturn(0f);
        when(req.getUnifirmScore()).thenReturn(0f);
        when(req.getParticipationScore()).thenReturn(0f);
        when(req.getPerformanceScore()).thenReturn(0f);
        when(req.getCellPhoneUseScore()).thenReturn(0f);

        doNothing().when(painelControleService).verifyActivePeriod();
        EvaluationPeriod activePeriod = mock(EvaluationPeriod.class);
        when(painelControleService.getActivePeriod()).thenReturn(Optional.of(activePeriod));

        Classes classes = new Classes();
        classes.setId(1);
        classes.setClassId("C1");
        when(classesRepository.findById(1)).thenReturn(Optional.of(classes));

        when(classEvaluationsRepository.findByProfessorIdAndClassIdAndEvaluationPeriod(
                eq("professorid"), eq("C1"), eq(activePeriod)
        )).thenReturn(Optional.empty());

        EvaluationsCriteria criteria = mock(EvaluationsCriteria.class);
        when(evaluationsMapper.toEvaluationsCriteria(req)).thenReturn(criteria);

        when(classEvaluationsRepository.save(any(ClassEvaluations.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ResponseClassEvaluationsDTO responseDTO = mock(ResponseClassEvaluationsDTO.class);
        when(evaluationsMapper.toResponseClassEvaluationsDTO(any(ClassEvaluations.class)))
                .thenReturn(responseDTO);

        ResponseClassEvaluationsDTO result = service.createEvaluation(req, 1, "professorid");

        assertSame(responseDTO, result);

        ArgumentCaptor<ClassEvaluations> captor = ArgumentCaptor.forClass(ClassEvaluations.class);
        verify(classEvaluationsRepository).save(captor.capture());

        ClassEvaluations saved = captor.getValue();
        assertEquals("C1", saved.getClassId());
        assertEquals("professorid", saved.getProfessorId());
        assertSame(criteria, saved.getCriteria());
        assertEquals(LocalDate.now(), saved.getDate());
        assertSame(activePeriod, saved.getEvaluationPeriod());

        verify(criteria).setAverageScore(0f);
        verify(painelControleService).verifyActivePeriod();
        verify(painelControleService).getActivePeriod();
        verify(classesRepository).findById(1);
        verify(evaluationsMapper).toEvaluationsCriteria(req);
        verify(evaluationsMapper).toResponseClassEvaluationsDTO(any(ClassEvaluations.class));
    }

    @Test
    void createEvaluationShouldCalculateAverageFiveScores() {
        RequestClassEvaluationsDTO req = mock(RequestClassEvaluationsDTO.class);
        when(req.getBehaviorScore()).thenReturn(5f);
        when(req.getFrequencyScore()).thenReturn(5f);
        when(req.getUnifirmScore()).thenReturn(5f);
        when(req.getParticipationScore()).thenReturn(5f);
        when(req.getPerformanceScore()).thenReturn(5f);
        when(req.getCellPhoneUseScore()).thenReturn(5f);

        doNothing().when(painelControleService).verifyActivePeriod();
        EvaluationPeriod activePeriod = mock(EvaluationPeriod.class);
        when(painelControleService.getActivePeriod()).thenReturn(Optional.of(activePeriod));

        Classes classes = new Classes();
        classes.setId(2);
        classes.setClassId("C2");
        when(classesRepository.findById(2)).thenReturn(Optional.of(classes));

        when(classEvaluationsRepository.findByProfessorIdAndClassIdAndEvaluationPeriod(
                eq("professorid2"), eq("C2"), eq(activePeriod)
        )).thenReturn(Optional.empty());

        EvaluationsCriteria criteria = mock(EvaluationsCriteria.class);
        when(evaluationsMapper.toEvaluationsCriteria(req)).thenReturn(criteria);

        when(classEvaluationsRepository.save(any(ClassEvaluations.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(evaluationsMapper.toResponseClassEvaluationsDTO(any(ClassEvaluations.class)))
                .thenReturn(mock(ResponseClassEvaluationsDTO.class));

        service.createEvaluation(req, 2, "professorid2");

        verify(criteria).setAverageScore(5f);
    }

    @Test
    void createEvaluationWhenExistsShouldDelegateToUpdate() {
        RequestClassEvaluationsDTO req = mock(RequestClassEvaluationsDTO.class);
        when(req.getBehaviorScore()).thenReturn(1f);
        when(req.getFrequencyScore()).thenReturn(1f);
        when(req.getUnifirmScore()).thenReturn(1f);
        when(req.getParticipationScore()).thenReturn(1f);
        when(req.getPerformanceScore()).thenReturn(1f);
        when(req.getCellPhoneUseScore()).thenReturn(1f);

        doNothing().when(painelControleService).verifyActivePeriod();
        EvaluationPeriod activePeriod = mock(EvaluationPeriod.class);
        when(painelControleService.getActivePeriod()).thenReturn(Optional.of(activePeriod));

        Classes classes = new Classes();
        classes.setId(1);
        classes.setClassId("C1");
        when(classesRepository.findById(1)).thenReturn(Optional.of(classes));

        ClassEvaluations existing = new ClassEvaluations();
        existing.setId(99);
        existing.setClassId("C1");
        existing.setProfessorId("professorid");

        when(classEvaluationsRepository.findByProfessorIdAndClassIdAndEvaluationPeriod(
                eq("professorid"), eq("C1"), eq(activePeriod)
        )).thenReturn(Optional.of(existing));

        when(classEvaluationsRepository.findById(99)).thenReturn(Optional.of(existing));
        EvaluationsCriteria criteria = new EvaluationsCriteria();
        existing.setCriteria(criteria);

        when(classEvaluationsRepository.save(any(ClassEvaluations.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ResponseClassEvaluationsDTO responseDTO = mock(ResponseClassEvaluationsDTO.class);
        when(evaluationsMapper.toResponseClassEvaluationsDTO(any(ClassEvaluations.class)))
                .thenReturn(responseDTO);

        ResponseClassEvaluationsDTO result = service.createEvaluation(req, 1, "professorid");

        assertSame(responseDTO, result);
        verify(classEvaluationsRepository).findById(99);
        verify(evaluationsMapper).updateCriteriaFromDto(eq(req), any(EvaluationsCriteria.class));
        verify(classEvaluationsRepository).save(any(ClassEvaluations.class));
    }

    @Test
    void updateEvaluationWhenExistsShouldUpdateAndSave() {
        doNothing().when(painelControleService).verifyActivePeriod();

        EvaluationsCriteria criteria = new EvaluationsCriteria();
        entity1.setCriteria(criteria);

        RequestClassEvaluationsDTO req = mock(RequestClassEvaluationsDTO.class);
        when(req.getBehaviorScore()).thenReturn(0f);
        when(req.getFrequencyScore()).thenReturn(0f);
        when(req.getUnifirmScore()).thenReturn(0f);
        when(req.getParticipationScore()).thenReturn(0f);
        when(req.getPerformanceScore()).thenReturn(0f);
        when(req.getCellPhoneUseScore()).thenReturn(0f);

        when(classEvaluationsRepository.findById(1)).thenReturn(Optional.of(entity1));
        when(classEvaluationsRepository.save(any(ClassEvaluations.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ResponseClassEvaluationsDTO responseDTO = mock(ResponseClassEvaluationsDTO.class);
        when(evaluationsMapper.toResponseClassEvaluationsDTO(any(ClassEvaluations.class)))
                .thenReturn(responseDTO);

        ResponseClassEvaluationsDTO result = service.updateEvaluation(1, req);

        assertSame(responseDTO, result);
        verify(painelControleService).verifyActivePeriod();
        verify(classEvaluationsRepository).findById(1);
        verify(evaluationsMapper).updateCriteriaFromDto(req, criteria);
        verify(classEvaluationsRepository).save(entity1);
        verify(evaluationsMapper).toResponseClassEvaluationsDTO(entity1);
        verifyNoMoreInteractions(classEvaluationsRepository, evaluationsMapper);
    }

    @Test
    void updateEvaluationWhenNotExistsShouldThrowException() {
        doNothing().when(painelControleService).verifyActivePeriod();
        when(classEvaluationsRepository.findById(123)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.updateEvaluation(123, mock(RequestClassEvaluationsDTO.class))
        );

        assertEquals("ClassEvaluations not found with id: 123", ex.getMessage());
        verify(painelControleService).verifyActivePeriod();
        verify(classEvaluationsRepository).findById(123);
        verifyNoMoreInteractions(classEvaluationsRepository, evaluationsMapper);
    }

    @Test
    void deleteEvaluationWhenNotExistsShouldThrowException() {
        when(classEvaluationsRepository.existsById(10)).thenReturn(false);

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> service.deleteEvaluation(10)
        );

        assertEquals("ClassEvaluations not found with id: 10", ex.getMessage());
        verify(classEvaluationsRepository).existsById(10);
        verify(classEvaluationsRepository, never()).deleteById(anyInt());
        verifyNoMoreInteractions(classEvaluationsRepository, evaluationsMapper);
    }

    @Test
    void deleteEvaluationWhenExistsShouldDelete() {
        when(classEvaluationsRepository.existsById(10)).thenReturn(true);

        service.deleteEvaluation(10);

        verify(classEvaluationsRepository).existsById(10);
        verify(classEvaluationsRepository).deleteById(10);
        verifyNoMoreInteractions(classEvaluationsRepository, evaluationsMapper);
    }

    @Test
    void getEvaluationsByClassIdWhenClassNotFoundShouldThrow() {
        when(classesRepository.findById(999)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> service.getEvaluationsByClassId(999));

        assertEquals("Class not found", ex.getMessage());
        verify(classesRepository).findById(999);
        verifyNoMoreInteractions(classEvaluationsRepository, evaluationsMapper);
    }

    @Test
    void createEvaluationWhenNoActivePeriodShouldThrowIllegalState() {
        doNothing().when(painelControleService).verifyActivePeriod();

        Classes classes = new Classes();
        classes.setId(1);
        classes.setClassId("C1");
        when(classesRepository.findById(1)).thenReturn(Optional.of(classes));

        when(painelControleService.getActivePeriod()).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.createEvaluation(mock(RequestClassEvaluationsDTO.class), 1, "prof"));

        assertEquals("Nenhum período de avaliação ativo encontrado", ex.getMessage());
        verify(painelControleService).verifyActivePeriod();
        verify(classesRepository).findById(1);
        verify(painelControleService).getActivePeriod();
        verifyNoMoreInteractions(classEvaluationsRepository, evaluationsMapper);
    }

    @Test
    void createEvaluationWhenClassNotFoundShouldThrow() {
        doNothing().when(painelControleService).verifyActivePeriod();
        when(classesRepository.findById(1)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> service.createEvaluation(mock(RequestClassEvaluationsDTO.class), 1, "prof"));

        assertEquals("Class not found", ex.getMessage());
        verify(painelControleService).verifyActivePeriod();
        verify(classesRepository).findById(1);
        verifyNoMoreInteractions(classEvaluationsRepository, evaluationsMapper);
    }
}
