package br.com.ifrn.EvaluationsService.evaluations_service.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RequestStudentPerformanceDTO {
    private String studentId;
    private String classId;
    private float averageScore;
    private float attendenceRate;
    private Integer failedSubjects;
    private String status;
    private float ira;
}
