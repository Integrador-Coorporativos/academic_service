package br.com.ifrn.AcademicService.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class RequestCommentDTO implements Serializable {
    @NotNull
    @Size(min = 1, max = 1000)
    private String comment;
}
