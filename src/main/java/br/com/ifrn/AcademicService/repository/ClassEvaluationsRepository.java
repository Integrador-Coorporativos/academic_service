package br.com.ifrn.AcademicService.repository;

import br.com.ifrn.AcademicService.models.enums.StepName;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.ifrn.AcademicService.models.ClassEvaluations;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ClassEvaluationsRepository extends JpaRepository<ClassEvaluations, Integer> {
    List<ClassEvaluations> findByClassId(String classId);
    @Query("SELECT AVG(c.frequencyScore) as avgFrequency, AVG(c.unifirmScore) as avgUniform, " +
            "AVG(c.behaviorScore) as avgBehavior, AVG(c.participationScore) as avgParticipation, " +
            "AVG(c.performanceScore) as avgPerformance, AVG(c.cellPhoneUseScore) as avgCellPhone, " +
            "AVG(c.averageScore) as avgTotal " +
            "FROM ClassEvaluations e JOIN e.criteria c WHERE e.classId = :classId")
    EvaluationMetricsProjection findRawMetricsByClassId(String classId);

    @Query("""
    SELECT 
        AVG(c.frequencyScore) as avgFrequency, 
        AVG(c.unifirmScore) as avgUniform, 
        AVG(c.behaviorScore) as avgBehavior, 
        AVG(c.participationScore) as avgParticipation, 
        AVG(c.performanceScore) as avgPerformance, 
        AVG(c.cellPhoneUseScore) as avgCellPhone, 
        AVG(c.averageScore) as avgTotal 
    FROM ClassEvaluations e JOIN e.criteria c WHERE e.classId = :classId AND YEAR(e.date) = :year
    """)
    EvaluationMetricsProjection findRawMetricsByClassIdAndYear(
            @Param("classId") String classId,
            @Param("year") Integer year
    );

    @Query("""
SELECT 
    AVG(c.frequencyScore) as avgFrequency, 
    AVG(c.unifirmScore) as avgUniform, 
    AVG(c.behaviorScore) as avgBehavior, 
    AVG(c.participationScore) as avgParticipation, 
    AVG(c.performanceScore) as avgPerformance, 
    AVG(c.cellPhoneUseScore) as avgCellPhone, 
    AVG(c.averageScore) as avgTotal 
FROM ClassEvaluations e 
JOIN e.criteria c 
JOIN e.evaluationPeriod p 
WHERE e.classId = :classId 
  AND p.referenceYear = :year 
  AND p.stepName = :step
""")
    EvaluationMetricsProjection findMetricsByClassAndYearAndStep(
            @Param("classId") String classId,
            @Param("year") Integer year,
            @Param("step") StepName step
    );
}
