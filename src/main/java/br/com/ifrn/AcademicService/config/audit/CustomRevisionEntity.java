package br.com.ifrn.AcademicService.config.audit;

import jakarta.persistence.*;
import org.hibernate.envers.*;
import java.io.Serializable;

@Entity
@Table(name = "revinfo")
@RevisionEntity(UserRevisionListener.class)
public class CustomRevisionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rev")
    @RevisionNumber
    private int id;

    @RevisionTimestamp
    @Column(name = "revtstmp")
    private long timestamp;

    @Column(name = "user_id")
    private String userId; // Seu campo customizado para o 'sub' do Keycloak

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}