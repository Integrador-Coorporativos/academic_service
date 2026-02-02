package br.com.ifrn.AcademicService.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StepName {

    PRIMEIRO("1° Bimestre"),
    SEGUNDO("2° Bimestre"),
    TERCEIRO("3° Bimestre"),
    QUARTO("4° Bimestre");

    private final String descricao;

    StepName(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static StepName fromValue(String value) {
        for (StepName status : StepName.values()) {
            if (status.descricao.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + value);
    }
}
