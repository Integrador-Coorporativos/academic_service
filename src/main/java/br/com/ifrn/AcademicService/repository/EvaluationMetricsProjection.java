package br.com.ifrn.AcademicService.repository;

public interface EvaluationMetricsProjection {
    Float getAvgFrequency();
    Float getAvgUniform();
    Float getAvgBehavior();
    Float getAvgParticipation();
    Float getAvgPerformance();
    Float getAvgCellPhone();
    Float getAvgTotal();
}
