package br.com.ifrn.AcademicService.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    OPTIMO("Ótimo"),
    BOM("Bom"),
    RUIM("Ruim");

    private final String descricao;

    Status(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue // Faz com que o JSON retorne "Ótimo" em vez de "OPTIMO"
    public String getDescricao() {
        return descricao;
    }
}
