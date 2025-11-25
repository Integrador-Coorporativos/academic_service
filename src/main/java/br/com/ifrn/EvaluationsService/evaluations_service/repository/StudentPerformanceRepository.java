package br.com.ifrn.EvaluationsService.evaluations_service.repository;

import br.com.ifrn.EvaluationsService.evaluations_service.models.ClassEvaluations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentPerformanceRepository extends JpaRepository<ClassEvaluations, Integer> {
}
