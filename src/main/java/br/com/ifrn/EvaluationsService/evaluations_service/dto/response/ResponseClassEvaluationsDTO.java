package br.com.ifrn.EvaluationsService.evaluations_service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter @Setter
public class ResponseClassEvaluationsDTO {
    private Integer id;
    private Integer class_id;
    private Integer professor_id;
    private LocalDate date;
    private float averageScore;
}
