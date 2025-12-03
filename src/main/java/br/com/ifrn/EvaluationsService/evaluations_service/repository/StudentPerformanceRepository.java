package br.com.ifrn.EvaluationsService.evaluations_service.repository;

import br.com.ifrn.EvaluationsService.evaluations_service.models.StudentPerformance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentPerformanceRepository extends JpaRepository<StudentPerformance, Integer> {
}
