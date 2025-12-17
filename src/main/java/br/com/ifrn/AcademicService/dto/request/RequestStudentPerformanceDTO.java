package br.com.ifrn.AcademicService.dto.request;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class RequestStudentPerformanceDTO implements Serializable {
    private String studentId;
    private String classId;
    private float averageScore;
    private float attendenceRate;
    private Integer failedSubjects;
    private String status;
    private float ira;
}
