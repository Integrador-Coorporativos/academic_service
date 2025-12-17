package br.com.ifrn.AcademicService.repository;

import br.com.ifrn.AcademicService.models.StudentPerformance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentPerformanceRepository extends JpaRepository<StudentPerformance, Integer> {
    StudentPerformance findStudentPerformanceByStudentId(String studentId);
}
