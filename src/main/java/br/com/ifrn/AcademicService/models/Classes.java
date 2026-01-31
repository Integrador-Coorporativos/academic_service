package br.com.ifrn.AcademicService.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String shift;
    private String gradleLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnoreProperties("classes")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Courses course;

    @OneToMany(mappedBy = "classe", fetch = FetchType.LAZY)
    @JsonIgnore
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private List<ClassComments> comments;

    @ElementCollection
    @JsonIgnore
    private List<String> userId;

    private String classId; //id da turma fornecido na planilha

}
