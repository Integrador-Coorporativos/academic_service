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
import br.com.ifrn.AcademicService.repository.ClassEvaluationsRepository;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import br.com.ifrn.AcademicService.repository.DashboardMetricsProjection;
import br.com.ifrn.AcademicService.repository.StudentPerformanceRepository;
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

        // 1. Obtém estatísticas básicas e a lista de turmas do professor
        ProfessorStatsView stats = classesRepository.countProfessorStats(professorId);
        List<Classes> classes = classesRepository.findClassesByProfessor(professorId);

        // Evita NullPointerException se o professor não tiver turmas
        if (classes == null || classes.isEmpty()) {
            return dashboardMetricsDTO;
        }

        dashboardMetricsDTO.setTotalStudents(stats != null ? stats.getTotalAlunos() : 0);

        double totalIraAcumulado = 0;
        double totalGood = 0;
        double totalAlert = 0;
        double totalCritical = 0;
        int turmasProcessadas = 0;

        // 2. Itera sobre as turmas para agregar os dados de performance
        for (Classes classe : classes) {
            DashboardMetricsProjection projection = studentPerformanceRepository.getRawMetricsByClassId(classe.getClassId());

            // Apenas processa turmas que possuem dados de alunos e performance calculada
            if (projection != null && projection.getTotalStudents() != null && projection.getTotalStudents() > 0) {
                totalIraAcumulado += (projection.getGeneralAverage() != null ? projection.getGeneralAverage() : 0);
                totalGood += (projection.getGoodPct() != null ? projection.getGoodPct() : 0);
                totalAlert += (projection.getAlertPct() != null ? projection.getAlertPct() : 0);
                totalCritical += (projection.getCriticalPct() != null ? projection.getCriticalPct() : 0);
                turmasProcessadas++;
            }
        }

        // 3. Consolida as médias e aplica arredondamento para 2 casas decimais
        if (turmasProcessadas > 0) {
            dashboardMetricsDTO.setGeneralAverage(round(totalIraAcumulado / turmasProcessadas));
            dashboardMetricsDTO.setStudentsGoodStatusPercentage(round(totalGood / turmasProcessadas));
            dashboardMetricsDTO.setStudentsAlertStatusPercentage(round(totalAlert / turmasProcessadas));
            dashboardMetricsDTO.setStudentsCriticalStatusPercentage(round(totalCritical / turmasProcessadas));

            // Regra de Negócio: Aprovação é a soma dos status Ótimo (Verde) e Bom (Amarelo)
            dashboardMetricsDTO.setApprovalRate(
                    round(dashboardMetricsDTO.getStudentsGoodStatusPercentage() + dashboardMetricsDTO.getStudentsAlertStatusPercentage())
            );
            dashboardMetricsDTO.setFailureRate(dashboardMetricsDTO.getStudentsCriticalStatusPercentage());
        }

        Optional<EvaluationPeriod> evaluationPeriod = painelControleService.getActivePeriod();
        dashboardMetricsDTO.setPeriodName(evaluationPeriod.get().getStepName().getDescricao() + " - " + evaluationPeriod.get().getReferenceYear());

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
