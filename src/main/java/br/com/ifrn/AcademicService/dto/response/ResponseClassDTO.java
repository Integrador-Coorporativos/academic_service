package br.com.ifrn.AcademicService.dto.response;


import br.com.ifrn.AcademicService.models.ClassComments;
import br.com.ifrn.AcademicService.models.Courses;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
public class ResponseClassDTO implements Serializable {
    private int id;
    private String name;
    private String semester;
    private int gradleLevel;
    private String shift;
    private Courses course;
    private List<ClassComments> comments;
    private List<String> userId;
    private String classId;
}
