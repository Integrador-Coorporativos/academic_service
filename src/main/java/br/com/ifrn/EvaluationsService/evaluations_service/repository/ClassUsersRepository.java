package br.com.ifrn.EvaluationsService.evaluations_service.repository;

import br.com.ifrn.EvaluationsService.evaluations_service.models.ClassUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassUsersRepository extends JpaRepository<ClassUsers, Integer> {
}
