package br.com.ifrn.AcademicService.services;

import br.com.ifrn.AcademicService.dto.ImportMessageDTO;
import br.com.ifrn.AcademicService.dto.request.RequestStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.request.RequestStudentPerformanceUpdateDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassificationsRankDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseclassificationsClassDTO;
import br.com.ifrn.AcademicService.mapper.StudentPerformanceMapper;
import br.com.ifrn.AcademicService.models.ClassEvaluations;
import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.models.StudentPerformance;
import br.com.ifrn.AcademicService.models.enums.Status;
import br.com.ifrn.AcademicService.repository.ClassEvaluationsRepository;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import br.com.ifrn.AcademicService.repository.EvaluationMetricsProjection;
import br.com.ifrn.AcademicService.repository.StudentPerformanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Service
public class StudentPerformanceService {

    @Autowired
    StudentPerformanceRepository  studentPerformanceRepository;

    @Autowired
    StudentPerformanceMapper mapper;

    @Autowired
    ClassesRepository classesRepository;

    @Autowired
    ClassEvaluationsRepository classEvaluationsRepository;


    @Cacheable(value = "studentPerformanceCache", key = "#studentid")
    public StudentPerformance getStudentPerformanceByStudentId(String studentid) throws EntityNotFoundException {

        StudentPerformance studentPerformance = studentPerformanceRepository.findStudentPerformanceByStudentId(studentid);
        if(studentPerformance == null){
            throw new EntityNotFoundException("Student Performance not found");
        }
        return studentPerformance;
    }

    public List<ResponseclassificationsClassDTO> getClassifications() {
        List<Classes> classes = classesRepository.findAll();
        List<ResponseclassificationsClassDTO> dtos = new ArrayList<>();

        classes.forEach(classe -> {
            // Busca as médias. O banco pode retornar null se não houver avaliações.
            EvaluationMetricsProjection metrics = classEvaluationsRepository.findRawMetricsByClassId(classe.getClassId());

            ResponseclassificationsClassDTO dto = new ResponseclassificationsClassDTO();
            dto.setClassId(classe.getId());
            dto.setCourseName(classe.getCourse().getName());
            dto.setShift(classe.getShift());
            dto.setGradleLevel("2°"); //Atenção: Ajustar quando refatorar com a planilha

            if (metrics != null && metrics.getAvgFrequency() != null) {
                dto.setFrequencyScore(metrics.getAvgFrequency());
                dto.setUnifirmScore(metrics.getAvgUniform());
                dto.setBehaviorScore(metrics.getAvgBehavior());
                dto.setParticipationScore(metrics.getAvgParticipation());
                dto.setPerformanceScore(metrics.getAvgPerformance());
                dto.setCellPhoneUseScore(metrics.getAvgCellPhone());
                dto.setAverageScore(metrics.getAvgTotal());
            } else {
                dto.setFrequencyScore(0.0f);
                dto.setUnifirmScore(0.0f);
                dto.setBehaviorScore(0.0f);
                dto.setParticipationScore(0.0f);
                dto.setPerformanceScore(0.0f);
                dto.setCellPhoneUseScore(0.0f);
                dto.setAverageScore(0.0f);
            }
            dtos.add(dto);
        });

        return dtos;
    }

    public ResponseclassificationsClassDTO getClassificationByClassId(Integer classId, Integer year) throws EntityNotFoundException {
        Classes classe = classesRepository.findById(classId).orElseThrow(() -> new EntityNotFoundException("Classe not found"));
        // Busca as médias. O banco pode retornar null se não houver avaliações.
        EvaluationMetricsProjection metrics = classEvaluationsRepository.findRawMetricsByClassIdAndYear(classe.getClassId(), year);

        ResponseclassificationsClassDTO dto = new ResponseclassificationsClassDTO();
        dto.setClassId(classe.getId());
        dto.setCourseName(classe.getCourse().getName());
        dto.setShift(classe.getShift());
        dto.setGradleLevel(classe.getGradleLevel());

        if (metrics != null && metrics.getAvgFrequency() != null) {
            dto.setFrequencyScore(metrics.getAvgFrequency());
            dto.setUnifirmScore(metrics.getAvgUniform());
            dto.setBehaviorScore(metrics.getAvgBehavior());
            dto.setParticipationScore(metrics.getAvgParticipation());
            dto.setPerformanceScore(metrics.getAvgPerformance());
            dto.setCellPhoneUseScore(metrics.getAvgCellPhone());
            dto.setAverageScore(metrics.getAvgTotal());
        } else {
            dto.setFrequencyScore(0.0f);
            dto.setUnifirmScore(0.0f);
            dto.setBehaviorScore(0.0f);
            dto.setParticipationScore(0.0f);
            dto.setPerformanceScore(0.0f);
            dto.setCellPhoneUseScore(0.0f);
            dto.setAverageScore(0.0f);
        }
        List<ResponseClassificationsRankDTO> allRankings = getClassesWithRankings(year);
        ResponseClassificationsRankDTO rank = allRankings.stream()
                .filter(dto2 -> {
                    return dto2.getClassid().equals(classId);
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Turma não encontrada ou sem avaliações para o ranking"));
        dto.setRank(rank);
        return dto;
    }

    @Cacheable(value = "studentPerformanceCache", key = "#id")
    public ResponseStudentPerformanceDTO getStudentPerformanceById(Integer id) throws EntityNotFoundException {

        StudentPerformance studentPerformance = studentPerformanceRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Not found Student Performance by Id: " + id));
        ResponseStudentPerformanceDTO dto = mapper.toResponseDto(studentPerformance);
        return dto;
    }

    @CacheEvict(value = "studentPerformanceCacheAll", allEntries = true)
    public ResponseStudentPerformanceDTO createStudentPerformance(RequestStudentPerformanceDTO requestDTO) {
        StudentPerformance studentPerformance = studentPerformanceRepository.findStudentPerformanceByStudentId(requestDTO.getStudentId());
        if (studentPerformance != null) {
            throw  new EntityNotFoundException("Já existe uma avaliação cadastrada para esse aluno!");
        }
        studentPerformance = mapper.toEntity(requestDTO);
        studentPerformance = studentPerformanceRepository.save(studentPerformance);
        ResponseStudentPerformanceDTO responseDto = mapper.toResponseDto(studentPerformance);
        return responseDto;
    }

    @CacheEvict(value = {"studentPerformanceCache", "studentPerformanceCacheAll"}, allEntries = true)
    public ResponseStudentPerformanceDTO updateStudentPerformance(Integer id, RequestStudentPerformanceUpdateDTO dto) {
        StudentPerformance studentPerformance = studentPerformanceRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Not found Student Performance by Id: " + id));
        mapper.updateEntityFromDto(dto, studentPerformance);
        studentPerformance = studentPerformanceRepository.save(studentPerformance);
        ResponseStudentPerformanceDTO responseDto = mapper.toResponseDto(studentPerformanceRepository.save(studentPerformance));
        return responseDto;
    }

    public ResponseStudentPerformanceDTO getClassEvaluationsById(Integer id) {
        return null;
    }

    @CacheEvict(value = "studentPerformanceCacheAll", allEntries = true)
    public ResponseStudentPerformanceDTO createStudentPerformanceByConsumerMessageDTO(ImportMessageDTO consumerMessageDTO) {
        //estrutura inicial
        ResponseStudentPerformanceDTO responseDto =  new ResponseStudentPerformanceDTO();
        RequestStudentPerformanceDTO studentPerformanceDTO = mapper.toRequestStudentPerformanceByConsumerMessageDto(consumerMessageDTO);
        StudentPerformance studentPerformance = studentPerformanceRepository.findStudentPerformanceByStudentId(studentPerformanceDTO.getStudentId());
        if (studentPerformance == null) {
            responseDto = createStudentPerformance(studentPerformanceDTO);
        }else{
            responseDto = updateStudentPerformance(studentPerformance.getId(), mapper.toRequestStudentPerformanceUpdateDto(studentPerformanceDTO));
        }
        return responseDto;
    }

    @Cacheable(value = "studentPerformanceCacheAll")
    public List<ResponseStudentPerformanceDTO> getAllStudentPerformance() {
        List<StudentPerformance> studentPerformanceList = studentPerformanceRepository.findAll();
        return studentPerformanceList.stream()
                .map(mapper::toResponseDto)
                .toList();
    }


    public List<ResponseClassificationsRankDTO> getClassesWithRankings(Integer year) {
        // 1. Busca os dados brutos
        List<Classes> allEntities = classesRepository.findAll();
        List<ClassEvaluations> classEvaluations = classEvaluationsRepository.findAll();

        // 2. Agrupa as avaliações por ID da turma para evitar buscas N+1
        Map<String, List<ClassEvaluations>> evaluationsByClass = classEvaluations.stream()
                .collect(Collectors.groupingBy(ClassEvaluations::getClassId));

        // 3. Mapeia as turmas para a nossa "mochila" temporária (ScoreHelper)
        List<ScoreHelper> helpers = allEntities.stream()
                .map(t -> new ScoreHelper(t, evaluationsByClass.getOrDefault(t.getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        // 4. Aplica os Rankings usando a função auxiliar genérica
        aplicarRank(helpers, h -> h.freq, ResponseClassificationsRankDTO::setFrequencyRank);
        aplicarRank(helpers, h -> h.unif, ResponseClassificationsRankDTO::setUnifirmRank);
        aplicarRank(helpers, h -> h.behav, ResponseClassificationsRankDTO::setBehaviorRank);
        aplicarRank(helpers, h -> h.partic, ResponseClassificationsRankDTO::setParticipationRank);
        aplicarRank(helpers, h -> h.perf, ResponseClassificationsRankDTO::setPerformanceRank);
        aplicarRank(helpers, h -> h.cell, ResponseClassificationsRankDTO::setCellPhoneUseRank);
        aplicarRank(helpers, h -> h.avg, ResponseClassificationsRankDTO::setAverageRank);

        // 5. Extrai apenas os DTOs preenchidos para retornar ao controller
        return helpers.stream()
                .map(h -> h.dto)
                .collect(Collectors.toList());
    }

    /**
     * FUNÇÃO AUXILIAR: Ordena a lista pelo score extraído e carimba a posição no DTO.
     */
    private void aplicarRank(List<ScoreHelper> lista,
                             ToDoubleFunction<ScoreHelper> scoreExtractor,
                             BiConsumer<ResponseClassificationsRankDTO, Integer> rankSetter) {

        // Ordena do maior score para o menor (reversed)
        lista.sort(Comparator.comparingDouble(scoreExtractor).reversed());

        for (int i = 0; i < lista.size(); i++) {
            // Pega o DTO que está dentro do helper e define sua posição (i + 1)
            rankSetter.accept(lista.get(i).dto, i + 1);
        }
    }

    /**
     * CLASSE AUXILIAR (MOCHILA): Armazena o DTO e os scores temporários para cálculo de ranking.
     * Definida como private static para ser acessível apenas dentro deste Service.
     */
    private static class ScoreHelper {
        ResponseClassificationsRankDTO dto;
        double freq, unif, behav, partic, perf, cell, avg;

        ScoreHelper(Classes t, List<ClassEvaluations> evals) {
            this.dto = new ResponseClassificationsRankDTO();
            this.dto.setClassid(t.getId());

            if (evals != null && !evals.isEmpty()) {
                double n = (double) evals.size();
                // Somamos os scores através do relacionamento Criteria e dividimos pela quantidade
                this.freq = evals.stream().mapToDouble(e -> e.getCriteria().getFrequencyScore()).sum() / n;
                this.unif = evals.stream().mapToDouble(e -> e.getCriteria().getUnifirmScore()).sum() / n;
                this.behav = evals.stream().mapToDouble(e -> e.getCriteria().getBehaviorScore()).sum() / n;
                this.partic = evals.stream().mapToDouble(e -> e.getCriteria().getParticipationScore()).sum() / n;
                this.perf = evals.stream().mapToDouble(e -> e.getCriteria().getPerformanceScore()).sum() / n;
                this.cell = evals.stream().mapToDouble(e -> e.getCriteria().getCellPhoneUseScore()).sum() / n;
                this.avg = evals.stream().mapToDouble(e -> e.getCriteria().getAverageScore()).sum() / n;
            } else {
                // Caso não existam avaliações, os scores permanecem 0.0
                this.freq = this.unif = this.behav = this.partic = this.perf = this.cell = this.avg = 0.0;
            }
        }
    }
}

