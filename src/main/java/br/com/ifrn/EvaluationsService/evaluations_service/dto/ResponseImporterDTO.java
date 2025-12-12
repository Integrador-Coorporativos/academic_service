package br.com.ifrn.EvaluationsService.evaluations_service.dto;

import br.com.ifrn.EvaluationsService.evaluations_service.dto.response.ResponseStudentPerformanceDTO;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResponseImporterDTO {
//deve retornar os objetos criados
    ResponseStudentPerformanceDTO studentPerformance;
}
