package br.com.ifrn.AcademicService.dto.response;

import br.com.ifrn.AcademicService.models.EvaluationsCriteria;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDate;

@Getter @Setter
public class ResponseClassEvaluationsDTO implements Serializable {
    private Integer id;
    private String classId;
    private Integer professorId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    private float averageScore;
    private EvaluationsCriteria criteria;
}
