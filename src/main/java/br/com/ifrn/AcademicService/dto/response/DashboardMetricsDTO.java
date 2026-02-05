package br.com.ifrn.AcademicService.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

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

    private double frequencyScore; //frequencia
    private double unifirmScore; //fardamento
    private double behaviorScore; //comportamento
    private double participationScore; //participação
    private double performanceScore; //desempenho
    private double cellPhoneUseScore; //uso de celular

}
