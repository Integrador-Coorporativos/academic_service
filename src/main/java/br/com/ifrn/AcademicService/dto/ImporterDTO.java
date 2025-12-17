package br.com.ifrn.AcademicService.dto;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ImporterDTO {

    private String name;
    private String registration;
    private String classId;
    private String course;
    private String shift;
    private String semester;
    private float presence;
    private float average;
    private  float ira;
    private  Integer rejections;

}
