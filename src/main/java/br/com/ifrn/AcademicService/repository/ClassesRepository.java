package br.com.ifrn.AcademicService.repository;

import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.models.Courses;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassesRepository extends JpaRepository<Classes, Integer> {
    Classes findByClassId(String classId);

    List<Classes> findByCourse(Courses course);
}
