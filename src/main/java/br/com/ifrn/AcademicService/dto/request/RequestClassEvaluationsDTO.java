package br.com.ifrn.AcademicService.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;


@Getter @Setter
public class RequestClassEvaluationsDTO  {

    @Range(min = 0, max = 5)
    private float frequencyScore; //frequencia
    @Range(min = 0, max = 5)
    private float unifirmScore; //fardamento
    @Range(min = 0, max = 5)
    private float behaviorScore; //comportamento
    @Range(min = 0, max = 5)
    private float participationScore; //participação
    @Range(min = 0, max = 5)
    private float performanceScore; //desempenho
    @Range(min = 0, max = 5)
    private float cellPhoneUseScore; //uso de celular
}
