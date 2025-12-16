package br.com.ifrn.EvaluationsService.evaluations_service.controller;

import br.com.ifrn.EvaluationsService.evaluations_service.controller.docs.CoursesControllerDocs;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.request.RequestCourseDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.models.Courses;
import br.com.ifrn.EvaluationsService.evaluations_service.services.CoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CoursesController implements CoursesControllerDocs {

    @Autowired
    private CoursesService courseService;

    @GetMapping
    public ResponseEntity<List<Courses>> getAll() {
        List<Courses> coursesList = courseService.getAll();
        if (coursesList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(coursesList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Courses> getById(@PathVariable Integer id) {
        Optional<Courses> course = courseService.getById(id);
        return course.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping
    public ResponseEntity<Courses> create(@RequestBody RequestCourseDTO courseDTO) {
        Courses createdCourse = new Courses();
        createdCourse.setName(courseDTO.getName());
        createdCourse.setDescription(courseDTO.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.create(createdCourse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Courses> update(@PathVariable Integer id, @RequestBody Courses course) {
        Optional<Courses> updatedCourse = Optional.ofNullable(courseService.update(id, course));
        return updatedCourse.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
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
