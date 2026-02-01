package br.com.ifrn.AcademicService.repository;

import br.com.ifrn.AcademicService.models.StudentPerformance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentPerformanceRepository extends JpaRepository<StudentPerformance, Integer> {
    StudentPerformance findStudentPerformanceByStudentId(String studentId);
    List<StudentPerformance> findByStudentIdIn(List<String> studentIds);
    Optional<StudentPerformance> findByStudentId(String studentId);
}
