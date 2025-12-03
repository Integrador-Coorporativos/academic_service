package br.com.ifrn.EvaluationsService.evaluations_service.services;

import br.com.ifrn.EvaluationsService.evaluations_service.dto.request.RequestClassEvaluationsDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.response.ResponseClassEvaluationsDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.mapper.EvaluationsMapper;
import br.com.ifrn.EvaluationsService.evaluations_service.models.ClassEvaluations;
import br.com.ifrn.EvaluationsService.evaluations_service.models.EvaluationsCriteria;
import br.com.ifrn.EvaluationsService.evaluations_service.repository.ClassEvaluationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClassEvaluationsService {

    @Autowired
    ClassEvaluationsRepository classEvaluationsRepository;

    @Autowired
    EvaluationsMapper evaluationsMapper;

    public List<ResponseClassEvaluationsDTO> getAllEvaluations() {

        List<ClassEvaluations> classEvaluations = classEvaluationsRepository.findAll();

        List<ResponseClassEvaluationsDTO> responseDTOList = classEvaluations.stream()
                .map(evaluationsMapper::toResponseClassEvaluationsDTO)
                .toList();

        return responseDTOList;

    }

    public ResponseClassEvaluationsDTO getEvaluationById(Integer id) {
        ClassEvaluations classEvaluations = classEvaluationsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Evaluation not found"));

        ResponseClassEvaluationsDTO responseDTO =
                evaluationsMapper.toResponseClassEvaluationsDTO(classEvaluations);

        return responseDTO;
    }

    public List<ResponseClassEvaluationsDTO> getEvaluationsByClassId(Integer id) {
        List<ClassEvaluations> classEvaluations = classEvaluationsRepository.findByClassId(id);
        List<ResponseClassEvaluationsDTO> responseDTO = classEvaluations
                .stream()
                .map(evaluationsMapper::toResponseClassEvaluationsDTO)
                .toList();
        return responseDTO;
    }

    public ResponseClassEvaluationsDTO createEvaluation(RequestClassEvaluationsDTO dto, Integer classId, Integer professorId) {
        EvaluationsCriteria evaluations = evaluationsMapper.toEvaluationsCriteria(dto);
        ClassEvaluations classEvaluations = new  ClassEvaluations();
        classEvaluations.setClassId(classId);//precisa adicionar validação de existencia de turma
        classEvaluations.setProfessorId(professorId);//precisa adicionar validação de existencia do professor
        classEvaluations.setCriteria(evaluations);
        classEvaluations.setDate(LocalDate.now());
        evaluations.setAverageScore(calcAverageScore(dto));
        classEvaluations = classEvaluationsRepository.save(classEvaluations);
        return evaluationsMapper.toResponseClassEvaluationsDTO(classEvaluations);
    }

    public ResponseClassEvaluationsDTO updateEvaluation(Integer id, RequestClassEvaluationsDTO dto) {
        ClassEvaluations entity = classEvaluationsRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("ClassEvaluations not found with id: " + id));

        evaluationsMapper.updateEntityFromDto(dto, entity);
        entity = classEvaluationsRepository.save(entity);
        ResponseClassEvaluationsDTO responseDTO = evaluationsMapper.toResponseClassEvaluationsDTO(entity);

        return responseDTO;
    }

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
