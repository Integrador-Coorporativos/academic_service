package br.com.ifrn.EvaluationsService.evaluations_service.mapper;


import br.com.ifrn.EvaluationsService.evaluations_service.dto.ImporterDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.request.RequestStudentPerformanceDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.models.StudentPerformance;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentPerformanceMapper {
    StudentPerformance toEntity (RequestStudentPerformanceDTO dto);

    RequestStudentPerformanceDTO toDto (StudentPerformance entity);

    List<StudentPerformance> toEntityList (List<RequestStudentPerformanceDTO> dto);

    ResponseStudentPerformanceDTO toResponseDto (StudentPerformance entity);

    StudentPerformance toEntityByImporterDto (ImporterDTO dto);
}
