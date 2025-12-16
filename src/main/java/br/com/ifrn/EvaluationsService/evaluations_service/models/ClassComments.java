package br.com.ifrn.EvaluationsService.evaluations_service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ClassComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int professorId;

    private String comment;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Classes classe;
}
