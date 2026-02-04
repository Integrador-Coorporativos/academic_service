package br.com.ifrn.AcademicService.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StepName {
    PRIMEIRO(1, "1° Bimestre"),
    SEGUNDO(2, "2° Bimestre"),
    TERCEIRO(3, "3° Bimestre"),
    QUARTO(4, "4° Bimestre");

    private final int id;
    private final String descricao;

    StepName(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    // Método para converter o número (1, 2, 3...) em Enum
    public static StepName fromId(int id) {
        for (StepName step : values()) {
            if (step.id == id) return step;
        }
        throw new IllegalArgumentException("Bimestre inválido: " + id);
    }

    @JsonCreator
    public static StepName fromValue(String value) {
        for (StepName step : values()) {
            // Agora ele aceita tanto "1° Bimestre" quanto "PRIMEIRO"
            if (step.descricao.equalsIgnoreCase(value) || step.name().equalsIgnoreCase(value)) {
                return step;
            }
        }
        throw new IllegalArgumentException("Valor inválido: " + value);
    }
}