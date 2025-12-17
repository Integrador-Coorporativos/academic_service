package br.com.ifrn.AcademicService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.ifrn.AcademicService.models.ClassEvaluations;
import java.util.List;

public interface ClassEvaluationsRepository extends JpaRepository<ClassEvaluations, Integer> {
    List<ClassEvaluations> findByClassId(Integer classId);
}
