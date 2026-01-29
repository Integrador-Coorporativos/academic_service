package br.com.ifrn.AcademicService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClassPanelResponseDTO {

    private int id;
    private String name;
    private String shift;

    private int courseId;
    private String courseName;
}
