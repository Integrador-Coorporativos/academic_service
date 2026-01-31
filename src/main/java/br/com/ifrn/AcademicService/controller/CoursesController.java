package br.com.ifrn.AcademicService.controller;

import br.com.ifrn.AcademicService.controller.docs.CoursesControllerDocs;
import br.com.ifrn.AcademicService.dto.request.RequestCourseDTO;
import br.com.ifrn.AcademicService.dto.response.CoursePanelResponseDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseCourseDTO;
import br.com.ifrn.AcademicService.models.Courses;
import br.com.ifrn.AcademicService.services.CoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CoursesController implements CoursesControllerDocs {

    @Autowired
    private CoursesService courseService;

    @GetMapping
    public ResponseEntity<List<ResponseCourseDTO>> getAll() {
        List<ResponseCourseDTO> coursesList = courseService.getAll();
        return ResponseEntity.ok(coursesList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseCourseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(courseService.getById(id));
    }

    @GetMapping("/panel")
    public ResponseEntity<List<CoursePanelResponseDTO>> getCoursesPanel() {
        List<CoursePanelResponseDTO> panel = courseService.getCoursesPanel();
        return ResponseEntity.ok(panel);
    }

    @PostMapping
    public ResponseEntity<ResponseCourseDTO> create(@RequestBody RequestCourseDTO courseDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(courseDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseCourseDTO> update(@PathVariable Integer id, @RequestBody RequestCourseDTO courseDTO) {
        return ResponseEntity.ok(courseService.update(id, courseDTO));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = courseService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
