package br.com.ifrn.AcademicService.repository;

import br.com.ifrn.AcademicService.dto.ProfessorStatsView;
import br.com.ifrn.AcademicService.dto.response.ClassPanelResponseDTO;
import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.models.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClassesRepository extends JpaRepository<Classes, Integer> {
    @Query("SELECT c FROM Classes c JOIN FETCH c.course")
    List<Classes> findAllWithCourse();

    Classes findByClassId(String classId);

    List<Classes> findByCourse(Courses course);

    @Query("SELECT new br.com.ifrn.AcademicService.dto.response.ClassPanelResponseDTO(" +
            "c.id, c.name, c.shift, c.course.id, c.course.name) " +
            "FROM Classes c")
    List<ClassPanelResponseDTO> findAllForPanel();

    @Query("""
    SELECT 
        COUNT(DISTINCT c) AS totalTurmas,
        COALESCE(SUM(SIZE(c.userId)), 0) AS totalAlunos
    FROM Classes c
    WHERE :professorId MEMBER OF c.professors
    """)
    ProfessorStatsView countProfessorStats(@Param("professorId") String professorId);


    @Query("SELECT c FROM Classes c JOIN c.professors p WHERE p = :professorId")
    List<Classes> findClassesByProfessor(@Param("professorId") String professorId);

}
