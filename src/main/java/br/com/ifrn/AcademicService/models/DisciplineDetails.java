package br.com.ifrn.AcademicService.models;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class DisciplineDetails {
    private String diario;
    private String disciplina;

    @Column(name = "carga_horaria")
    private String cargaHoraria;

    @Column(name = "total_aulas")
    private Integer totalAulas;

    @Column(name = "total_faltas")
    private Integer totalFaltas;

    private Float frequencia;

    private String situacao;
    private String mfd;
}