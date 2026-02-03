package br.com.ifrn.AcademicService.services;

import br.com.ifrn.AcademicService.dto.request.RequestClassEvaluationsDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassEvaluationsDTO;
import br.com.ifrn.AcademicService.exception.BusinessRuleException;
import br.com.ifrn.AcademicService.mapper.EvaluationsMapper;
import br.com.ifrn.AcademicService.models.ClassEvaluations;
import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.models.EvaluationPeriod;
import br.com.ifrn.AcademicService.models.EvaluationsCriteria;
import br.com.ifrn.AcademicService.repository.ClassEvaluationsRepository;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
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


    @Cacheable(value = "evaluationsCacheAll")
    public List<ResponseClassEvaluationsDTO> getAllEvaluations() {

        List<ClassEvaluations> classEvaluations = classEvaluationsRepository.findAll();

        // Converte para ArrayList mutável
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

    @CacheEvict(value = {"evaluationsCacheAll", "evaluationsCacheByClass"}, key = "#id")
    public ResponseClassEvaluationsDTO createEvaluation(RequestClassEvaluationsDTO dto, Integer id, String professorId) {
        painelControleService.verifyActivePeriod();
        Classes classes = classesRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Class not found"));
        EvaluationPeriod activePeriod = painelControleService.getActivePeriod()
                .orElseThrow(() -> new IllegalStateException("Nenhum período de avaliação ativo encontrado"));
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

    @CacheEvict(value = {"evaluationsCacheAll", "evaluationsCache", "evaluationsCacheByClass"}, key = "#id")
    public ResponseClassEvaluationsDTO updateEvaluation(Integer id, RequestClassEvaluationsDTO dto) {
        painelControleService.verifyActivePeriod();
        ClassEvaluations entity = classEvaluationsRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("ClassEvaluations not found with id: " + id));

        evaluationsMapper.updateEntityFromDto(dto, entity);
        entity = classEvaluationsRepository.save(entity);
        ResponseClassEvaluationsDTO responseDTO = evaluationsMapper.toResponseClassEvaluationsDTO(entity);

        return responseDTO;
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
