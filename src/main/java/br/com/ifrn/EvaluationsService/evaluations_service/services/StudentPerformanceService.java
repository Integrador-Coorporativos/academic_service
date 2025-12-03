package br.com.ifrn.EvaluationsService.evaluations_service.services;

import br.com.ifrn.EvaluationsService.evaluations_service.dto.ImporterDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.request.RequestStudentPerformanceDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.mapper.StudentPerformanceMapper;
import br.com.ifrn.EvaluationsService.evaluations_service.models.StudentPerformance;
import br.com.ifrn.EvaluationsService.evaluations_service.repository.StudentPerformanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
public class StudentPerformanceService {

    @Autowired
    StudentPerformanceRepository  studentPerformanceRepository;

    @Autowired
    StudentPerformanceMapper mapper;

    public ResponseStudentPerformanceDTO getStudentPerformanceById(Integer id) throws EntityNotFoundException {

        StudentPerformance studentPerformance = studentPerformanceRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Not found Student Performance by Id: " + id));
        ResponseStudentPerformanceDTO dto = mapper.toResponseDto(studentPerformance);
        return dto;
    }

    public ResponseStudentPerformanceDTO createStudentPerformance(RequestStudentPerformanceDTO requestDTO) {
        StudentPerformance studentPerformance = mapper.toEntity(requestDTO);
        studentPerformance = studentPerformanceRepository.save(studentPerformance);
        ResponseStudentPerformanceDTO responseDto = mapper.toResponseDto(studentPerformance);
        return responseDto;
    }

    public ResponseStudentPerformanceDTO updateStudentPerformance(Integer id, RequestStudentPerformanceDTO dto) {
        return new ResponseStudentPerformanceDTO();
    }

    public ResponseStudentPerformanceDTO getClassEvaluationsById(Integer id) {
        return null;
    }

    public ResponseStudentPerformanceDTO createStudentPerformanceByImporterDTO(ImporterDTO  importerDTO) {
        //estrutura inicial sem qualquer validação e com dados mockcados
        StudentPerformance studentPerformance = mapper.toEntityByImporterDto(importerDTO);
        studentPerformance = studentPerformanceRepository.save(studentPerformance);
        ResponseStudentPerformanceDTO responseDto = mapper.toResponseDto(studentPerformance);
        return responseDto;
    }

}
