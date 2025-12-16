package br.com.ifrn.EvaluationsService.evaluations_service.dto.response;


import br.com.ifrn.EvaluationsService.evaluations_service.models.ClassComments;
import br.com.ifrn.EvaluationsService.evaluations_service.models.Courses;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ResponseClassDTO {
    private int id;
    private String name;
    private String semester;
    private int gradleLevel;
    private String shift;
    private Courses course;
    private List<ClassComments> comments;
}
