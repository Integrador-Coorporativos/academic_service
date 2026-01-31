package br.com.ifrn.AcademicService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestCommentDTO implements Serializable {
    @NotBlank(message = "Comentário não pode ser vazio ou nulo")
    @Size(min = 1, max = 255, message = "Comentário não pode exceder 255 caracteres")
    private String comment;
}
