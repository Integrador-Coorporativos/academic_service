package br.com.ifrn.EvaluationsService.evaluations_service.repository;

import br.com.ifrn.EvaluationsService.evaluations_service.models.ClassComments;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClassCommentsRepository extends JpaRepository<ClassComments, Integer> {
    List<ClassComments> findByClasse_Id(Integer classeId);
}
