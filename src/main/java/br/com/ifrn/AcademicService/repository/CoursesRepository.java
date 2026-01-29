package br.com.ifrn.AcademicService.repository;

import br.com.ifrn.AcademicService.models.Courses;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursesRepository extends JpaRepository<Courses, Integer> {
    Courses findByName(String name);

//    int countClassesByCourseId(@Param("courseId")int courseId);
}
