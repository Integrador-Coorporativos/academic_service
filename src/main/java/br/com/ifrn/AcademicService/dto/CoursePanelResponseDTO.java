package br.com.ifrn.AcademicService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CoursePanelResponseDTO {
    private Integer id;
    private String course;
    private Integer quantClasses;
    private Integer quantStudent;
    private String shift;
}
