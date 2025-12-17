package br.com.ifrn.AcademicService.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ResponseCommentDTO implements Serializable {
    private int id;
    private String comment;
}
