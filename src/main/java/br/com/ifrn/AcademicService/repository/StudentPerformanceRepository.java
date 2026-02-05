package br.com.ifrn.AcademicService.repository;

import br.com.ifrn.AcademicService.models.StudentPerformance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentPerformanceRepository extends JpaRepository<StudentPerformance, Integer> {
    StudentPerformance findStudentPerformanceByStudentId(String studentId);
    List<StudentPerformance> findByStudentIdIn(List<String> studentIds);
    Optional<StudentPerformance> findByStudentId(String studentId);

    @Query("""
    SELECT 
        COUNT(s.id) AS totalStudents, 
        AVG(CAST(s.ira AS double)) AS generalAverage,
        (SUM(CASE WHEN s.status = 'OPTIMO' OR s.status = 'BOM' THEN 1.0 ELSE 0.0 END) * 100.0 / COUNT(s.id)) AS approvalRate,
        (SUM(CASE WHEN s.status = 'RUIM' THEN 1.0 ELSE 0.0 END) * 100.0 / COUNT(s.id)) AS failureRate,
        (SUM(CASE WHEN s.status = 'OPTIMO' THEN 1.0 ELSE 0.0 END) * 100.0 / COUNT(s.id)) AS goodPct,
        (SUM(CASE WHEN s.status = 'BOM' THEN 1.0 ELSE 0.0 END) * 100.0 / COUNT(s.id)) AS alertPct,
        (SUM(CASE WHEN s.status = 'RUIM' THEN 1.0 ELSE 0.0 END) * 100.0 / COUNT(s.id)) AS criticalPct
    FROM StudentPerformance s
    WHERE s.classId = :classId
""")
    DashboardMetricsProjection getRawMetricsByClassId(@Param("classId") String classId);
}
