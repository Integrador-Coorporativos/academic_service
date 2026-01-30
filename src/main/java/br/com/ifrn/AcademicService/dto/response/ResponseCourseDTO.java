package br.com.ifrn.AcademicService.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class ResponseCourseDTO implements Serializable {
    private int id;
    private String name;
}
