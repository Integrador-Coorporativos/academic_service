package br.com.ifrn.AcademicService.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.List;

@Entity
@Audited
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;



    @Column(name = "name",  nullable = false,  length = 255)
    private String name;
    private String semester;
    private int gradleLevel;
    private String shift;

    @ManyToOne
    private Courses course;


    @OneToMany(mappedBy = "classe")
    private List<ClassComments> comments;

    @ElementCollection
    private List<String> userId;

    private String classId; //id da turma fornecido na planilha

}
