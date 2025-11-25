package br.com.ifrn.EvaluationsService.evaluations_service.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResponseStudentPerformanceDTO {
    private Integer id;
    private Integer studentId;
    private Integer classId;
    private float averageScore;
    private float attendenceRate;
    private Integer failedSubjects;
    private String status;
}
