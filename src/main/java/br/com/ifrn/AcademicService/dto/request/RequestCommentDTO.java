package br.com.ifrn.AcademicService.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RequestCommentDTO {
    @NotNull
    @Size(min = 1, max = 1000)
    private String comment;
}
