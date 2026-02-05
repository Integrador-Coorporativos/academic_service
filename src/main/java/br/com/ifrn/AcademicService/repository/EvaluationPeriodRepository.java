package br.com.ifrn.AcademicService.repository;

import br.com.ifrn.AcademicService.models.EvaluationPeriod;
import br.com.ifrn.AcademicService.models.enums.StepName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluationPeriodRepository extends JpaRepository<EvaluationPeriod, Long> {

    // Busca o período que está rolando agora
    Optional<EvaluationPeriod> findByActiveTrue();
    Optional<EvaluationPeriod> findFirstByOrderByActiveDescStartDateDesc();

    // Desativa todos os períodos (usado antes de começar um novo)
    @Modifying
    @Query("UPDATE EvaluationPeriod e SET e.active = false WHERE e.active = true")
    void deactivateAllActivePeriods();
    boolean existsByStepNameAndReferenceYear(StepName stepName, Integer referenceYear);
}