package br.com.ifrn.AcademicService.repository;

import br.com.ifrn.AcademicService.models.Classes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassesRepository extends JpaRepository<Classes, Integer> {
    Classes findByClassId(String classId);
}
