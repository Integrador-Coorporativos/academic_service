package br.com.ifrn.AcademicService.dto.response;


import br.com.ifrn.AcademicService.models.enums.Status;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StudentDataDTO {

    private Integer id;
    private String name;
    private String registration;
    private String studentId;
    private String classId;
    private float averageScore;
    private float attendenceRate;
    private Integer failedSubjects;
    private float ira;
    private Status status;
}
