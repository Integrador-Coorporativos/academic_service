package br.com.ifrn.AcademicService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseClassificationsRankDTO implements Serializable {
    private Integer Classid;
    private float frequencyRank; //frequencia
    private float unifirmRank; //fardamento
    private float behaviorRank; //comportamento
    private float participationRank; //participação
    private float performanceRank; //desempenho
    private float cellPhoneUseRank; //uso de celular
    private float averageRank; //pontuação média da avaliação
}
