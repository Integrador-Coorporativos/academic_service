package br.com.ifrn.EvaluationsService.evaluations_service.services;

import br.com.ifrn.EvaluationsService.evaluations_service.dto.request.RequestStudentPerformanceDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.mapper.StudentPerformanceMapper;
import br.com.ifrn.EvaluationsService.evaluations_service.messaging.dto.ConsumerMessageDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.models.StudentPerformance;
import br.com.ifrn.EvaluationsService.evaluations_service.repository.StudentPerformanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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

    public ResponseStudentPerformanceDTO createStudentPerformanceByConsumerMessageDTO(ConsumerMessageDTO consumerMessageDTO) {
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

    public List<ResponseStudentPerformanceDTO> getAllStudentPerformance() {
        List<StudentPerformance> studentPerformanceList = studentPerformanceRepository.findAll();
        return studentPerformanceList.stream()
                .map(mapper::toResponseDto)
                .toList();
    }


}
