package br.com.ifrn.AcademicService.models;

import br.com.ifrn.AcademicService.models.enums.StepName;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "step_name", nullable = false)
    @Enumerated(EnumType.STRING)
    private StepName stepName; // Ex: "1ª Etapa", "2º Bimestre"

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime deadline; // O "prazo final"

    @Column(nullable = false)
    private Integer referenceYear; // Ano letivo (ex: 2026)

    @Column(nullable = false)
    private boolean active; // Define se as turmas podem ser avaliadas agora

    // Método utilitário para verificar se expirou sem depender do scheduler
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.deadline);
    }
}