package br.com.ifrn.AcademicService.services;

import br.com.ifrn.AcademicService.dto.ProfessorStatsView;
import br.com.ifrn.AcademicService.dto.request.RequestClassEvaluationsDTO;
import br.com.ifrn.AcademicService.dto.response.DashboardMetricsDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassEvaluationsDTO;
import br.com.ifrn.AcademicService.mapper.EvaluationsMapper;
import br.com.ifrn.AcademicService.models.ClassEvaluations;
import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.models.EvaluationPeriod;
import br.com.ifrn.AcademicService.models.EvaluationsCriteria;
import br.com.ifrn.AcademicService.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClassEvaluationsService {

    @Autowired
    ClassEvaluationsRepository classEvaluationsRepository;

    @Autowired
    EvaluationsMapper evaluationsMapper;

    @Autowired
    ClassesRepository classesRepository;

    @Autowired
    PainelControleService painelControleService;

    @Autowired
    StudentPerformanceRepository  studentPerformanceRepository;


    @Cacheable(value = "evaluationsCacheAll")
    public List<ResponseClassEvaluationsDTO> getAllEvaluations() {
        List<ClassEvaluations> classEvaluations = classEvaluationsRepository.findAll();
        List<ResponseClassEvaluationsDTO> responseDTOList = classEvaluations.stream()
                .map(evaluationsMapper::toResponseClassEvaluationsDTO)
                .collect(Collectors.toCollection(ArrayList::new));
        return responseDTOList;
    }

    @Cacheable(value = "evaluationsCache", key = "#id")
    public ResponseClassEvaluationsDTO getEvaluationById(Integer id) {
        ClassEvaluations classEvaluations = classEvaluationsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Evaluation not found"));

        ResponseClassEvaluationsDTO responseDTO =
                evaluationsMapper.toResponseClassEvaluationsDTO(classEvaluations);

        return responseDTO;
    }

    @Cacheable(value = "evaluationsCacheByClass", key = "#id")
    public List<ResponseClassEvaluationsDTO> getEvaluationsByClassId(Integer id) {
        Classes classes = classesRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Class not found"));
        List<ClassEvaluations> classEvaluations = classEvaluationsRepository.findByClassId(classes.getClassId());
        List<ResponseClassEvaluationsDTO> responseDTO = classEvaluations
                .stream()
                .map(evaluationsMapper::toResponseClassEvaluationsDTO)
                .toList();
        return responseDTO;
    }

    /**
     * Calcula as métricas consolidadas do dashboard para um professor específico.
     * <p>
     * A lógica realiza a agregação de dados de todas as turmas vinculadas ao professor,
     * calculando a média aritmética das performances individuais de cada turma (média das médias).
     * Os status são baseados na distribuição percentual de alunos em níveis Ótimo, Bom e Ruim.
     * </p>
     *
     * @param professorId O identificador único do professor no sistema.
     * @return Um {@link DashboardMetricsDTO} contendo os índices de performance,
     * taxas de aprovação e distribuição acadêmica consolidada.
     */
    public DashboardMetricsDTO getEvaluationsByDashboard(String professorId) {
        DashboardMetricsDTO dashboardMetricsDTO = new DashboardMetricsDTO();

        // 1. Obtém estatísticas básicas e a lista de turmas
        ProfessorStatsView stats = classesRepository.countProfessorStats(professorId);
        List<Classes> classes = classesRepository.findClassesByProfessor(professorId);

        if (classes == null || classes.isEmpty()) {
            return dashboardMetricsDTO;
        }

        dashboardMetricsDTO.setTotalStudents(stats != null ? stats.getTotalAlunos() : 0);

        // 2. Variáveis para performance e AGORA para Critérios Comportamentais
        double totalIraAcumulado = 0, totalGood = 0, totalAlert = 0, totalCritical = 0;

        // Novas variáveis para acumular as médias comportamentais do professor
        double sumFreq = 0, sumUnif = 0, sumBehav = 0, sumPart = 0, sumPerf = 0, sumCell = 0;
        int turmasProcessadas = 0;
        int turmasComAvaliacao = 0;

        // Pega o período ativo uma única vez para usar no loop
        Optional<EvaluationPeriod> evaluationPeriod = painelControleService.getActivePeriod();

        // 3. Itera sobre as turmas
        for (Classes classe : classes) {
            // --- PARTE A: Performance dos Alunos ---
            DashboardMetricsProjection perfProj = studentPerformanceRepository.getRawMetricsByClassId(classe.getClassId());
            if (perfProj != null && perfProj.getTotalStudents() != null && perfProj.getTotalStudents() > 0) {
                totalIraAcumulado += (perfProj.getGeneralAverage() != null ? perfProj.getGeneralAverage() : 0);
                totalGood += (perfProj.getGoodPct() != null ? perfProj.getGoodPct() : 0);
                totalAlert += (perfProj.getAlertPct() != null ? perfProj.getAlertPct() : 0);
                totalCritical += (perfProj.getCriticalPct() != null ? perfProj.getCriticalPct() : 0);
                turmasProcessadas++;
            }

            // --- PARTE B: Métricas Comportamentais (A nova query!) ---
            if (evaluationPeriod.isPresent()) {
                EvaluationMetricsProjection evalProj = classEvaluationsRepository.getEvaluationMetrics(
                        classe.getClassId(),
                        evaluationPeriod.get().getReferenceYear(),
                        evaluationPeriod.get().getStepName()
                );

                if (evalProj != null && evalProj.getAvgTotal() != null) {
                    sumFreq += (evalProj.getAvgFrequency() != null ? evalProj.getAvgFrequency() : 0);
                    sumUnif += (evalProj.getAvgUniform() != null ? evalProj.getAvgUniform() : 0);
                    sumBehav += (evalProj.getAvgBehavior() != null ? evalProj.getAvgBehavior() : 0);
                    sumPart += (evalProj.getAvgParticipation() != null ? evalProj.getAvgParticipation() : 0);
                    sumPerf += (evalProj.getAvgPerformance() != null ? evalProj.getAvgPerformance() : 0);
                    sumCell += (evalProj.getAvgCellPhone() != null ? evalProj.getAvgCellPhone() : 0);
                    turmasComAvaliacao++;
                }
            }
        }

        // 4. Consolida Performance
        if (turmasProcessadas > 0) {
            dashboardMetricsDTO.setGeneralAverage(round(totalIraAcumulado / turmasProcessadas));
            dashboardMetricsDTO.setStudentsGoodStatusPercentage(round(totalGood / turmasProcessadas));
            dashboardMetricsDTO.setStudentsAlertStatusPercentage(round(totalAlert / turmasProcessadas));
            dashboardMetricsDTO.setStudentsCriticalStatusPercentage(round(totalCritical / turmasProcessadas));
            dashboardMetricsDTO.setApprovalRate(round(dashboardMetricsDTO.getStudentsGoodStatusPercentage() + dashboardMetricsDTO.getStudentsAlertStatusPercentage()));
            dashboardMetricsDTO.setFailureRate(dashboardMetricsDTO.getStudentsCriticalStatusPercentage());
        }

        // 5. Consolida Comportamento no DTO
        if (turmasComAvaliacao > 0) {
            dashboardMetricsDTO.setFrequencyScore(round(sumFreq / turmasComAvaliacao));
            dashboardMetricsDTO.setUnifirmScore(round(sumUnif / turmasComAvaliacao));
            dashboardMetricsDTO.setBehaviorScore(round(sumBehav / turmasComAvaliacao));
            dashboardMetricsDTO.setParticipationScore(round(sumPart / turmasComAvaliacao));
            dashboardMetricsDTO.setPerformanceScore(round(sumPerf / turmasComAvaliacao));
            dashboardMetricsDTO.setCellPhoneUseScore(round(sumCell / turmasComAvaliacao));
        }

        if (evaluationPeriod.isPresent()) {
            dashboardMetricsDTO.setPeriodName(evaluationPeriod.get().getStepName().getDescricao() + " - " + evaluationPeriod.get().getReferenceYear());
        }

        return dashboardMetricsDTO;
    }


    /**
     * Método auxiliar para arredondar valores double para 2 casas decimais.
     */
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }


    @Transactional
    @CacheEvict(value = {"evaluationsCacheAll", "evaluationsCacheByClass"}, key = "#id")
    public ResponseClassEvaluationsDTO createEvaluation(RequestClassEvaluationsDTO dto, Integer id, String professorId) {
        painelControleService.verifyActivePeriod();
        Classes classes = classesRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Class not found"));
        EvaluationPeriod activePeriod = painelControleService.getActivePeriod()
                .orElseThrow(() -> new IllegalStateException("Nenhum período de avaliação ativo encontrado"));
        Optional<ClassEvaluations> evalExistente = classEvaluationsRepository.findByProfessorIdAndClassIdAndEvaluationPeriod(
                professorId, classes.getClassId(), activePeriod
        );
        if (evalExistente.isPresent()) {
            return updateEvaluation(evalExistente.get().getId(), dto);
        }else {
            EvaluationsCriteria evaluations = evaluationsMapper.toEvaluationsCriteria(dto);
            evaluations.setAverageScore(calcAverageScore(dto));
            ClassEvaluations classEvaluations = new  ClassEvaluations();
            classEvaluations.setClassId(classes.getClassId());
            classEvaluations.setProfessorId(professorId);
            classEvaluations.setCriteria(evaluations);
            classEvaluations.setDate(LocalDate.now());
            classEvaluations.setEvaluationPeriod(activePeriod);
            classEvaluations = classEvaluationsRepository.save(classEvaluations);
            return evaluationsMapper.toResponseClassEvaluationsDTO(classEvaluations);
        }
    }

    @Transactional
    @CacheEvict(value = {"evaluationsCacheAll", "evaluationsCache", "evaluationsCacheByClass"}, allEntries = true)
    public ResponseClassEvaluationsDTO updateEvaluation(Integer id, RequestClassEvaluationsDTO dto) {
        painelControleService.verifyActivePeriod();
        ClassEvaluations entity = classEvaluationsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ClassEvaluations not found with id: " + id));
        EvaluationsCriteria criteria = entity.getCriteria();
        evaluationsMapper.updateCriteriaFromDto(dto, criteria);
        float novaMedia = calcAverageScore(dto);
        criteria.setAverageScore(novaMedia);
        entity.setAverageScore(novaMedia);
        entity.setDate(LocalDate.now());
        entity = classEvaluationsRepository.save(entity);
        return evaluationsMapper.toResponseClassEvaluationsDTO(entity);
    }

    @CacheEvict(value = {"evaluationsCacheAll", "evaluationsCache", "evaluationsCacheByClass"}, key = "#id")
    public void deleteEvaluation(Integer id) {
        if (!classEvaluationsRepository.existsById(id)){
            throw new NoSuchElementException("ClassEvaluations not found with id: " + id);
        }
        classEvaluationsRepository.deleteById(id);
    }


    public DashboardMetricsDTO getMetricsByClass(String classId) {
        DashboardMetricsDTO dto = new DashboardMetricsDTO();

        // 1. Busca os dados brutos da performance apenas dessa turma
        DashboardMetricsProjection projection = studentPerformanceRepository.getRawMetricsByClassId(classId);

        // Se não tiver dados ou alunos, retorna o DTO vazio ou com zeros
        if (projection == null || projection.getTotalStudents() == null || projection.getTotalStudents() == 0) {
            return dto;
        }

        // 2. Mapeia os dados da projeção direto para o DTO
        dto.setTotalStudents(projection.getTotalStudents());
        dto.setGeneralAverage(round(projection.getGeneralAverage() != null ? projection.getGeneralAverage() : 0));

        // Percentuais de status
        dto.setStudentsGoodStatusPercentage(round(projection.getGoodPct() != null ? projection.getGoodPct() : 0));
        dto.setStudentsAlertStatusPercentage(round(projection.getAlertPct() != null ? projection.getAlertPct() : 0));
        dto.setStudentsCriticalStatusPercentage(round(projection.getCriticalPct() != null ? projection.getCriticalPct() : 0));

        // 3. Aplica a Regra de Negócio de Aprovação/Reprovação
        // Aprovação = Verde (Good) + Amarelo (Alert)
        dto.setApprovalRate(round(dto.getStudentsGoodStatusPercentage() + dto.getStudentsAlertStatusPercentage()));
        dto.setFailureRate(dto.getStudentsCriticalStatusPercentage());

        // 4. Pega o período ativo (mantendo sua lógica original)
        painelControleService.getActivePeriod().ifPresent(period ->
                dto.setPeriodName(period.getStepName().getDescricao() + " - " + period.getReferenceYear())
        );

        return dto;
    }

    private float calcAverageScore(RequestClassEvaluationsDTO dto) {
        float averageScore = 0.0F;
        float n1,n2,n3,n4,n5,n6;
        n1 = dto.getBehaviorScore();
        n2 = dto.getFrequencyScore();
        n3 = dto.getUnifirmScore();
        n4 = dto.getParticipationScore();
        n5 = dto.getPerformanceScore();
        n6 = dto.getCellPhoneUseScore();

        averageScore = (n1 + n2 + n3 + n4 + n5 + n6)/6;

        return averageScore;
    }
}
