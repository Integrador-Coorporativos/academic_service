package br.com.ifrn.AcademicService.dto.response;

import br.com.ifrn.AcademicService.models.enums.Status;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class ResponseStudentPerformanceDTO implements Serializable {
    private Integer id;
    private String studentId;
    private String classId;
    private float averageScore;
    private float attendenceRate;
    private Integer failedSubjects;
    private Status status;
    private Integer totalLowGrades;
}
