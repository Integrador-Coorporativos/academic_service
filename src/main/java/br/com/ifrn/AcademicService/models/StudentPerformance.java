package br.com.ifrn.AcademicService.models;

import br.com.ifrn.AcademicService.models.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;

@Entity
//@Audited
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentPerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private String studentId;
    private String classId;
    private float averageScore;
    private float attendenceRate;
    private Integer failedSubjects;
    private float ira;
    private Integer totalLowGrades;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ElementCollection
    private List<DisciplineDetails> disciplineDetails = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void preAtualizarStatus() {
        this.status = calcularStatus();
    }

    public Status calcularStatus() {
        int reprovacoes = (failedSubjects == null) ? 0 : failedSubjects;

        // 1. Condição para "RUIM" (Prioridade - Risco Acadêmico)
        if (reprovacoes > 1 || this.attendenceRate < 75 || this.ira < 60) {
            return Status.RUIM;
        }

        // 2. Condição para "ÓTIMO"
        if (this.ira >= 80 && this.attendenceRate >= 90 && reprovacoes == 0) {
            return Status.OPTIMO;
        }

        // 3. Caso contrário
        return Status.BOM;
    }
}
