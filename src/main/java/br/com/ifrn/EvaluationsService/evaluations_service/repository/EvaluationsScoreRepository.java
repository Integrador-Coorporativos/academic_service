package br.com.ifrn.EvaluationsService.evaluations_service.repository;

import br.com.ifrn.EvaluationsService.evaluations_service.models.EvaluationsScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationsScoreRepository extends JpaRepository<EvaluationsScore, Integer> {
}
