package br.com.ifrn.AcademicService.dto.response;

import br.com.ifrn.AcademicService.models.ClassComments;
import br.com.ifrn.AcademicService.models.Courses;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ResponseClassByIdDTO implements Serializable {
    private int id;
    private String name;
    private int gradleLevel;
    private String shift;
    private ResponseCourseDTO course;
    //private List<ResponseCommentDTO> comments;
    private String classId;
    private List<StudentDataDTO> students;
}
