package br.com.ifrn.AcademicService.dto;

import br.com.ifrn.AcademicService.dto.response.ResponseStudentPerformanceDTO;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResponseImporterDTO {
//deve retornar os objetos criados
    ResponseStudentPerformanceDTO studentPerformance;
}
