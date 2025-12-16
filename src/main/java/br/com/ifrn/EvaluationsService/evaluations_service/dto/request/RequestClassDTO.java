package br.com.ifrn.EvaluationsService.evaluations_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RequestClassDTO {

    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @NotNull
    @Size(min = 1, max = 100)
    private String semester;

    @NotNull
    @Size(min = 1, max = 100)
    private int gradleLevel;
    @NotNull
    @Size(min = 1, max = 100)
    private String shift;
}
