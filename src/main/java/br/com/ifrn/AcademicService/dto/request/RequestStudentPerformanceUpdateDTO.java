package br.com.ifrn.AcademicService.dto.request;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class RequestStudentPerformanceUpdateDTO implements Serializable {
    private float averageScore;
    private float attendenceRate;
    private Integer failedSubjects;
    private float ira;
    private Integer totalLowGrades;
}
