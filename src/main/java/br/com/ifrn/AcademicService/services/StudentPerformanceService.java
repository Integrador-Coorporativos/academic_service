package br.com.ifrn.AcademicService.services;

import br.com.ifrn.AcademicService.dto.ImportMessageDTO;
import br.com.ifrn.AcademicService.dto.request.RequestStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.AcademicService.mapper.StudentPerformanceMapper;
import br.com.ifrn.AcademicService.models.StudentPerformance;
import br.com.ifrn.AcademicService.repository.StudentPerformanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentPerformanceService {

    @Autowired
    StudentPerformanceRepository  studentPerformanceRepository;

    @Autowired
    StudentPerformanceMapper mapper;

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
