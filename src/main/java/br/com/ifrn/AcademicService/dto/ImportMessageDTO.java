package br.com.ifrn.AcademicService.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class ImportMessageDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    private String userId;
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
