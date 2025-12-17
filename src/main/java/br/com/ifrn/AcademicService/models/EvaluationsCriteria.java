package br.com.ifrn.AcademicService.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationsCriteria { //Modelo de critérios de avaliação

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
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

    @Range(min = 0, max = 5)
    private float averageScore; //pontuação média da avaliação

    private Boolean active;
}
