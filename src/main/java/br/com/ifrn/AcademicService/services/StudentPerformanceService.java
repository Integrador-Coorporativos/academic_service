package br.com.ifrn.AcademicService.services;

import br.com.ifrn.AcademicService.dto.ImportMessageDTO;
import br.com.ifrn.AcademicService.dto.request.RequestStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseclassificationsClassDTO;
import br.com.ifrn.AcademicService.mapper.StudentPerformanceMapper;
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

import java.util.ArrayList;
import java.util.List;

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
        StudentPerformance studentPerformance = mapper.toEntity(requestDTO);
        studentPerformance = studentPerformanceRepository.save(studentPerformance);
        ResponseStudentPerformanceDTO responseDto = mapper.toResponseDto(studentPerformance);
        return responseDto;
    }

    @CacheEvict(value = {"studentPerformanceCache", "studentPerformanceCacheAll"}, allEntries = true)
    public ResponseStudentPerformanceDTO updateStudentPerformance(Integer id, RequestStudentPerformanceDTO dto) {
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
            responseDto = updateStudentPerformance(studentPerformance.getId(), studentPerformanceDTO);
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
}
