package br.com.ifrn.AcademicService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RequestCourseDTO {
    @Size(min = 1, max = 100)
    @NotBlank
    private String name;

    @Size(min = 1, max = 100)
    @NotBlank
    private String description;
}