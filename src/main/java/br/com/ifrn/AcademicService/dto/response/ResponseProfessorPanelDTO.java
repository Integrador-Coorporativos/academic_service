package br.com.ifrn.AcademicService.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class ResponseProfessorPanelDTO implements Serializable {
    private String Name;
    private String Email;
    private String registration;
    private Long quantityStudents;
    private Long quantityClass;
}
