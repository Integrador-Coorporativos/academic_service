package br.com.ifrn.AcademicService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartPeriodDTO implements Serializable {
    @NotBlank(message = "O nome da etapa é obrigatório")
    @JsonProperty("stepName")
    private String stepName;
    @Min(value = 2025, message = "Ano inválido")
    @JsonProperty("year")
    private Integer year;
}
