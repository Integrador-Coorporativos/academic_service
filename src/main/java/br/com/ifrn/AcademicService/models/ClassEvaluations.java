package br.com.ifrn.AcademicService.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassEvaluations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    private String classId;
    private Integer professorId;
    private LocalDate date;

    @Range(min = 0, max = 5)
    private float averageScore;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "criteria_id")
    private EvaluationsCriteria criteria;

}
