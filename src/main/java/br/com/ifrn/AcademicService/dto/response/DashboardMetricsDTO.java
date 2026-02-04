package br.com.ifrn.AcademicService.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardMetricsDTO {
    // Cabeçalho
    private String periodName;
    private Long totalStudents;

    // Resumo Geral
    private Double generalAverage;
    private Double approvalRate;
    private Double failureRate;

    // Fatias da Pizza (AcademicPieCard)
    private Double studentsGoodStatusPercentage;
    private Double studentsAlertStatusPercentage;
    private Double studentsCriticalStatusPercentage;
}
