package br.com.ifrn.AcademicService;

import br.com.ifrn.AcademicService.config.keycloak.KeycloakAdminConfig;
import br.com.ifrn.AcademicService.dto.ProfessorStatsView;
import br.com.ifrn.AcademicService.dto.StartPeriodDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseProfessorPanelDTO;
import br.com.ifrn.AcademicService.dto.response.StudentDataDTO;
import br.com.ifrn.AcademicService.exception.BusinessRuleException;
import br.com.ifrn.AcademicService.mapper.StudentPerformanceMapper;
import br.com.ifrn.AcademicService.models.EvaluationPeriod;
import br.com.ifrn.AcademicService.models.StudentPerformance;
import br.com.ifrn.AcademicService.models.enums.StepName;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import br.com.ifrn.AcademicService.repository.EvaluationPeriodRepository;
import br.com.ifrn.AcademicService.repository.StudentPerformanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PainelControleServiceTest {

    @Mock
    private KeycloakAdminConfig keycloakAdmin;

    @Mock
    private ClassesRepository classesRepository;

    @Mock
    private StudentPerformanceRepository studentPerformanceRepository;

    @Mock
    private StudentPerformanceMapper studentPerformanceMapper;

    @Mock
    private EvaluationPeriodRepository repository;

    @InjectMocks
    private br.com.ifrn.AcademicService.services.PainelControleService service;

    private UserRepresentation teacher;
    private UserRepresentation student;

    @BeforeEach
    void setUp() {
        teacher = new UserRepresentation();
        teacher.setId("t-1");
        teacher.setFirstName("Prof A");
        teacher.setEmail("prof@ifrn.br");
        teacher.setUsername("MAT123");

        student = new UserRepresentation();
        student.setId("s-1");
        student.setFirstName("Aluno A");
        student.setEmail("aluno@ifrn.br");
        student.setUsername("20260001");
    }

    @Test
    void getAllProfessorsShouldMapStatsAndReturnList() {
        when(keycloakAdmin.findUsersGroup("Teachers")).thenReturn(List.of(teacher));

        ProfessorStatsView stats = mock(ProfessorStatsView.class);
        when(stats.getTotalTurmas()).thenReturn(3L);
        when(stats.getTotalAlunos()).thenReturn(90L);

        when(classesRepository.countProfessorStats("t-1")).thenReturn(stats);

        List<ResponseProfessorPanelDTO> result = service.getAllProfessors();

        assertEquals(1, result.size());
        ResponseProfessorPanelDTO dto = result.get(0);

        assertEquals("Prof A", dto.getName());
        assertEquals("prof@ifrn.br", dto.getEmail());
        assertEquals("MAT123", dto.getRegistration());
        assertEquals(3L, dto.getQuantityClass());
        assertEquals(90L, dto.getQuantityStudents());

        verify(keycloakAdmin).findUsersGroup("Teachers");
        verify(classesRepository).countProfessorStats("t-1");
    }

    @Test
    void getAllStudentsWhenPerformanceExistsShouldUseMapperAndFillUserFields() {
        when(keycloakAdmin.findUsersGroup("Students")).thenReturn(List.of(student));

        StudentPerformance perf = new StudentPerformance();
        perf.setStudentId("s-1");
        when(studentPerformanceRepository.findByStudentId("s-1")).thenReturn(Optional.of(perf));

        StudentDataDTO mapped = new StudentDataDTO();
        mapped.setStudentId("s-1");
        when(studentPerformanceMapper.toStudentDataDTO(perf)).thenReturn(mapped);

        List<StudentDataDTO> result = service.getAllStudents();

        assertEquals(1, result.size());
        StudentDataDTO dto = result.get(0);

        assertEquals("s-1", dto.getStudentId());
        assertEquals("Aluno A", dto.getName());
        assertEquals("aluno@ifrn.br", dto.getEmail());
        assertEquals("20260001", dto.getRegistration());

        verify(keycloakAdmin).findUsersGroup("Students");
        verify(studentPerformanceRepository).findByStudentId("s-1");
        verify(studentPerformanceMapper).toStudentDataDTO(perf);
        verifyNoMoreInteractions(classesRepository, repository);
    }

    @Test
    void getAllStudentsWhenPerformanceNotExistsShouldCreateDtoAndFillUserFields() {
        when(keycloakAdmin.findUsersGroup("Students")).thenReturn(List.of(student));
        when(studentPerformanceRepository.findByStudentId("s-1")).thenReturn(Optional.empty());

        List<StudentDataDTO> result = service.getAllStudents();

        assertEquals(1, result.size());
        StudentDataDTO dto = result.get(0);

        assertEquals("s-1", dto.getStudentId());
        assertEquals("Aluno A", dto.getName());
        assertEquals("aluno@ifrn.br", dto.getEmail());
        assertEquals("20260001", dto.getRegistration());

        verify(keycloakAdmin).findUsersGroup("Students");
        verify(studentPerformanceRepository).findByStudentId("s-1");
        verifyNoInteractions(studentPerformanceMapper);
        verifyNoMoreInteractions(classesRepository, repository);
    }

    @Test
    void startNewPeriodWhenAlreadyExistsShouldThrow() {
        StepName step = StepName.PRIMEIRO;

        StartPeriodDTO dto = mock(StartPeriodDTO.class);
        when(dto.getStepName()).thenReturn(step);
        when(dto.getYear()).thenReturn(2026);

        when(repository.existsByStepNameAndReferenceYear(step, 2026)).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.startNewPeriod(dto));

        assertTrue(ex.getMessage().contains("já foi criado anteriormente"));

        verify(repository).existsByStepNameAndReferenceYear(step, 2026);
        verify(repository, never()).deactivateAllActivePeriods();
        verify(repository, never()).save(any());
    }

    @Test
    void startNewPeriodWhenNotExistsShouldDeactivateAndSave() {
        StepName step = StepName.PRIMEIRO;

        StartPeriodDTO dto = mock(StartPeriodDTO.class);
        when(dto.getStepName()).thenReturn(step);
        when(dto.getYear()).thenReturn(2026);

        when(repository.existsByStepNameAndReferenceYear(step, 2026)).thenReturn(false);
        when(repository.save(any(EvaluationPeriod.class))).thenAnswer(inv -> inv.getArgument(0));

        service.startNewPeriod(dto);

        verify(repository).deactivateAllActivePeriods();

        ArgumentCaptor<EvaluationPeriod> captor = ArgumentCaptor.forClass(EvaluationPeriod.class);
        verify(repository).save(captor.capture());

        EvaluationPeriod saved = captor.getValue();
        assertEquals(step, saved.getStepName());
        assertEquals(2026, saved.getReferenceYear());
        assertTrue(saved.isActive());

        assertNotNull(saved.getStartDate());
        assertNotNull(saved.getDeadline());
        assertTrue(saved.getDeadline().isAfter(saved.getStartDate()));

        verify(repository).existsByStepNameAndReferenceYear(step, 2026);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void autoCloseExpiredPeriodWhenNoActivePeriodShouldDoNothing() {
        when(repository.findByActiveTrue()).thenReturn(Optional.empty());

        service.autoCloseExpiredPeriod();

        verify(repository).findByActiveTrue();
        verify(repository, never()).save(any());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void autoCloseExpiredPeriodWhenNotExpiredShouldNotSave() {
        EvaluationPeriod period = new EvaluationPeriod();
        period.setActive(true);
        period.setDeadline(LocalDateTime.now().plusHours(2));

        when(repository.findByActiveTrue()).thenReturn(Optional.of(period));

        service.autoCloseExpiredPeriod();

        assertTrue(period.isActive());
        verify(repository).findByActiveTrue();
        verify(repository, never()).save(any());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void autoCloseExpiredPeriodWhenExpiredShouldDeactivateAndSave() {
        EvaluationPeriod period = new EvaluationPeriod();
        period.setActive(true);
        period.setDeadline(LocalDateTime.now().minusMinutes(1));

        when(repository.findByActiveTrue()).thenReturn(Optional.of(period));
        when(repository.save(any(EvaluationPeriod.class))).thenAnswer(inv -> inv.getArgument(0));

        service.autoCloseExpiredPeriod();

        assertFalse(period.isActive());
        verify(repository).findByActiveTrue();
        verify(repository).save(period);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getActivePeriodShouldDelegateToRepository() {
        EvaluationPeriod period = new EvaluationPeriod();
        when(repository.findFirstByOrderByActiveDescStartDateDesc()).thenReturn(Optional.of(period));

        Optional<EvaluationPeriod> result = service.getActivePeriod();

        assertTrue(result.isPresent());
        assertSame(period, result.get());
        verify(repository).findFirstByOrderByActiveDescStartDateDesc();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void manuallyEndCurrentPeriodWhenNoneActiveShouldThrow() {
        when(repository.findByActiveTrue()).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.manuallyEndCurrentPeriod());

        assertEquals("Não existe nenhum período de avaliação ativo no momento.", ex.getMessage());
        verify(repository).findByActiveTrue();
        verify(repository, never()).save(any());
    }

    @Test
    void manuallyEndCurrentPeriodWhenActiveShouldDeactivateSetDeadlineAndSave() {
        EvaluationPeriod period = new EvaluationPeriod();
        period.setActive(true);
        period.setDeadline(LocalDateTime.now().plusDays(1));

        when(repository.findByActiveTrue()).thenReturn(Optional.of(period));
        when(repository.save(any(EvaluationPeriod.class))).thenAnswer(inv -> inv.getArgument(0));

        service.manuallyEndCurrentPeriod();

        assertFalse(period.isActive());
        assertNotNull(period.getDeadline());
        verify(repository).findByActiveTrue();
        verify(repository).save(period);
    }

    @Test
    void verifyActivePeriodWhenNoActiveShouldThrowBusinessRuleException() {
        when(repository.findFirstByOrderByActiveDescStartDateDesc()).thenReturn(Optional.empty());

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.verifyActivePeriod());

        assertEquals("O período de avaliações está fechado no momento!", ex.getMessage());
        verify(repository).findFirstByOrderByActiveDescStartDateDesc();
        verify(repository, never()).findByActiveTrue();
        verify(repository, never()).save(any());
    }

    @Test
    void verifyActivePeriodWhenExpiredShouldEndPeriodAndThrow() {
        EvaluationPeriod expired = mock(EvaluationPeriod.class);
        when(expired.isExpired()).thenReturn(true);

        when(repository.findFirstByOrderByActiveDescStartDateDesc()).thenReturn(Optional.of(expired));

        EvaluationPeriod active = new EvaluationPeriod();
        active.setActive(true);
        active.setDeadline(LocalDateTime.now().minusMinutes(1));
        when(repository.findByActiveTrue()).thenReturn(Optional.of(active));
        when(repository.save(any(EvaluationPeriod.class))).thenAnswer(inv -> inv.getArgument(0));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> service.verifyActivePeriod());

        assertEquals("O prazo para esta etapa expirou!", ex.getMessage());
        assertFalse(active.isActive());

        verify(repository).findFirstByOrderByActiveDescStartDateDesc();
        verify(repository).findByActiveTrue();
        verify(repository).save(active);
    }

    @Test
    void verifyActivePeriodWhenNotExpiredShouldDoNothing() {
        EvaluationPeriod period = mock(EvaluationPeriod.class);
        when(period.isExpired()).thenReturn(false);

        when(repository.findFirstByOrderByActiveDescStartDateDesc()).thenReturn(Optional.of(period));

        assertDoesNotThrow(() -> service.verifyActivePeriod());

        verify(repository).findFirstByOrderByActiveDescStartDateDesc();
        verify(repository, never()).findByActiveTrue();
        verify(repository, never()).save(any());
    }
}
