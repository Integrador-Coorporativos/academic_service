package br.com.ifrn.EvaluationsService.evaluations_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter @Setter
public class RequestClassEvaluationsDTO {
    private Integer class_id;
    private Integer professor_id;
    private LocalDate date;
    private float averageScore;
}
