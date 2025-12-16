package br.com.ifrn.EvaluationsService.evaluations_service.repository;

import br.com.ifrn.EvaluationsService.evaluations_service.models.Courses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursesRepository extends JpaRepository<Courses, Integer> {
    Courses findByName(String name);
}
