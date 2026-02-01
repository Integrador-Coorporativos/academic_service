package br.com.ifrn.AcademicService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseclassificationsClassDTO {
    private int classId;
    private String courseName;
    private String gradleLevel; //Ano ou Período
    private String shift; //Turno
    private float frequencyScore; //frequencia
    private float unifirmScore; //fardamento
    private float behaviorScore; //comportamento
    private float participationScore; //participação
    private float performanceScore; //desempenho
    private float cellPhoneUseScore; //uso de celular
    private float averageScore; //pontuação média da avaliação
    private ResponseClassificationsRankDTO rank;
}
