package br.com.ifrn.AcademicService.dto.response;

import br.com.ifrn.AcademicService.models.EvaluationsCriteria;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;


@Getter @Setter
public class ResponseClassEvaluationsDTO implements Serializable {
    private Integer id;
    private String classId;
    private Integer professorId;
    private LocalDate date;
    private float averageScore;
    private EvaluationsCriteria criteria;
}
