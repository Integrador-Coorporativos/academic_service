package br.com.ifrn.AcademicService.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

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
    private String status;
    private LocalDate lastUpdate;

}
