package br.com.ifrn.AcademicService.dto;

public record DisciplineDetailDTO(
        String diario,
        String disciplina,
        String carga_horaria,
        String total_aulas,
        String total_faltas,
        String frequencia,
        String situacao,
        String mfd
) {}
