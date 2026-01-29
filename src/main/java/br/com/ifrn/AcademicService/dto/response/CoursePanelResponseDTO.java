package br.com.ifrn.AcademicService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Getter @Setter
@AllArgsConstructor
public class CoursePanelResponseDTO {
    private int courseId;
    private String courseName;
    private int totalClasses;
    private int totalStudents;
}
