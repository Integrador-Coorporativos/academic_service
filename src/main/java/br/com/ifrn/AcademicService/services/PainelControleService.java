package br.com.ifrn.AcademicService.services;

import br.com.ifrn.AcademicService.config.keycloak.KeycloakAdminConfig;
import br.com.ifrn.AcademicService.dto.ProfessorStatsView;
import br.com.ifrn.AcademicService.dto.StartPeriodDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseProfessorPanelDTO;
import br.com.ifrn.AcademicService.dto.response.StudentDataDTO;
import br.com.ifrn.AcademicService.exception.BusinessRuleException;
import br.com.ifrn.AcademicService.mapper.StudentPerformanceMapper;
import br.com.ifrn.AcademicService.models.EvaluationPeriod;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import br.com.ifrn.AcademicService.repository.EvaluationPeriodRepository;
import br.com.ifrn.AcademicService.repository.StudentPerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PainelControleService {
    @Autowired
    KeycloakAdminConfig keycloakAdmin;

    @Autowired
    ClassesRepository classesRepository;

    @Autowired
    StudentPerformanceRepository studentPerformanceRepository;

    @Autowired
    StudentPerformanceMapper  studentPerformanceMapper;

    @Autowired
    private EvaluationPeriodRepository repository;

    public List<ResponseProfessorPanelDTO> getAllProfessors() {
        return keycloakAdmin.findUsersGroup("Teachers")
                .stream()
                .map(user -> {
                    ProfessorStatsView stats = classesRepository.countProfessorStats(user.getId());
                    ResponseProfessorPanelDTO professor = new ResponseProfessorPanelDTO();
                    professor.setName(user.getFirstName());
                    professor.setEmail(user.getEmail());
                    professor.setRegistration(user.getUsername());
                    professor.setQuantityClass(stats.getTotalTurmas());
                    professor.setQuantityStudents(stats.getTotalAlunos());
                    return professor;
                })
                .toList();
    }

    public List<StudentDataDTO> getAllStudents() {
        return keycloakAdmin.findUsersGroup("Students")
                .stream()
                .map(user -> {
                    StudentDataDTO student = studentPerformanceRepository
                            .findByStudentId(user.getId())
                            .map(studentPerformanceMapper::toStudentDataDTO)
                            .orElseGet(() -> {
                                StudentDataDTO dto = new StudentDataDTO();
                                dto.setStudentId(user.getId());
                                return dto;
                            });

                    student.setName(user.getFirstName());
                    student.setEmail(user.getEmail());
                    student.setRegistration(user.getUsername());

                    return student;
                })
                .toList();
    }

    @Transactional
    public void startNewPeriod(StartPeriodDTO data) {
        boolean alreadyExists = repository.existsByStepNameAndReferenceYear(
                data.getStepName(),
                data.getYear()
        );

        if (alreadyExists) {
            throw new BusinessRuleException("O ciclo " + data.getStepName().getDescricao() +
                    " para o ano " + data.getYear() + " já foi criado anteriormente.");
        }
        repository.deactivateAllActivePeriods();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime deadline = start.plusWeeks(1);
        EvaluationPeriod newPeriod = new EvaluationPeriod();
        newPeriod.setStepName(data.getStepName());
        newPeriod.setStartDate(start);
        newPeriod.setDeadline(deadline);
        newPeriod.setReferenceYear(data.getYear());
        newPeriod.setActive(true);
        repository.save(newPeriod);
    }

    // Método que o Scheduler vai chamar ou o Front para validar
    @Scheduled(cron = "0 0 * * * *") // Roda de hora em hora
    @Transactional
    public void autoCloseExpiredPeriod() {
        repository.findByActiveTrue().ifPresent(period -> {
            if (LocalDateTime.now().isAfter(period.getDeadline())) {
                period.setActive(false);
                repository.save(period);
                System.out.println("Período " + period.getStepName() + " encerrado automaticamente.");
            }
        });
    }
    public Optional<EvaluationPeriod> getActivePeriod() {
        return repository.findFirstByOrderByActiveDescStartDateDesc();
    }

    @Transactional
    public void manuallyEndCurrentPeriod() {
        EvaluationPeriod activePeriod = repository.findByActiveTrue()
                .orElseThrow(() -> new RuntimeException("Não existe nenhum período de avaliação ativo no momento."));
        activePeriod.setActive(false);
        activePeriod.setDeadline(LocalDateTime.now());
        repository.save(activePeriod);
    }

    public void verifyActivePeriod() {
        EvaluationPeriod activePeriod = getActivePeriod()
                .orElseThrow(() -> new BusinessRuleException("O período de avaliações está fechado no momento!"));
        if (activePeriod.isExpired()) {
            manuallyEndCurrentPeriod();
            throw new BusinessRuleException("O prazo para esta etapa expirou!");
        }
    }

}

