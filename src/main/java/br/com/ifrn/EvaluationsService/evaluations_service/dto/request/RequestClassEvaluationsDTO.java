package br.com.ifrn.EvaluationsService.evaluations_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter @Setter
public class RequestClassEvaluationsDTO {
    private Integer classId;
    private Integer professorId;
    private LocalDate date;
    private float averageScore;
}
