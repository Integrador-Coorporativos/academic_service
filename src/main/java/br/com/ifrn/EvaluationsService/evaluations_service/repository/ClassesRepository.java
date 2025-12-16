package br.com.ifrn.EvaluationsService.evaluations_service.repository;

import br.com.ifrn.EvaluationsService.evaluations_service.models.Classes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassesRepository extends JpaRepository<Classes, Integer> {
    Classes findByClassId(String classId);
}
