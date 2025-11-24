package br.com.ifrn.EvaluationsService.evaluations_service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationsScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "evaluation_id")
    private ClassEvaluations evaluation_id;

    @ManyToOne
    @JoinColumn(name = "criteria_id")
    private EvaluationsCriteria criteria_id;

    private float score;
}
