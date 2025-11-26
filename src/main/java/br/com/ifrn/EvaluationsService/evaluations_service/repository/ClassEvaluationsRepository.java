package br.com.ifrn.EvaluationsService.evaluations_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.ifrn.EvaluationsService.evaluations_service.models.ClassEvaluations;

import java.util.List;

public interface ClassEvaluationsRepository extends JpaRepository<ClassEvaluations, Integer> {
    List<ClassEvaluations> findByClassId(Integer classId);
}
