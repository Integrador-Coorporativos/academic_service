package br.com.ifrn.AcademicService.repository;

public interface DashboardMetricsProjection {
    Long getTotalStudents();
    Double getGeneralAverage();
    Double getApprovalRate();
    Double getFailureRate();
    Double getGoodPct();
    Double getAlertPct();
    Double getCriticalPct();
}