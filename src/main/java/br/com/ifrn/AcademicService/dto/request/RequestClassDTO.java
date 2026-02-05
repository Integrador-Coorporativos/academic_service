package br.com.ifrn.AcademicService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class RequestClassDTO implements Serializable {

    @NotNull
    @Size(min = 1, max = 100)
    private String gradleLevel;

    @NotNull
    @Size(min = 1, max = 100)
    private String shift;

    @NotNull
    @Size(min = 1, max = 100)
    private String classId;
}
