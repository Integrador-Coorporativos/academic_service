package br.com.ifrn.EvaluationsService.evaluations_service.repository;

import br.com.ifrn.EvaluationsService.evaluations_service.models.EvaluationsCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationsCriteriaRepository extends JpaRepository<EvaluationsCriteria, Integer> {
}
