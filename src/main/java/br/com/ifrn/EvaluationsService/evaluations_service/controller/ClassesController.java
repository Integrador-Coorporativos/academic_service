package br.com.ifrn.EvaluationsService.evaluations_service.controller;

import br.com.ifrn.EvaluationsService.evaluations_service.controller.docs.ClassesControllerDocs;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.request.RequestClassDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.models.Classes;
import br.com.ifrn.EvaluationsService.evaluations_service.models.Courses;
import br.com.ifrn.EvaluationsService.evaluations_service.services.ClassesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classes")
@Tag(name = "Classes", description = "Operações relacionadas a Turmas")
public class ClassesController implements ClassesControllerDocs {

    @Autowired
    private ClassesService classesService;

    @GetMapping
    public ResponseEntity<List<Classes>> getAll() {
        List<Classes> classesList = classesService.getAll();
        if (classesList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(classesList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classes> getById(@PathVariable Integer id) {
        Optional<Classes> classes = classesService.getById(id);
        return classes.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping
    public ResponseEntity<Classes> create(@RequestParam Integer courseId, @RequestBody RequestClassDTO classDTO) {
        Courses curso = classesService.getById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado!")).getCourse();

        Classes createdClasses = new Classes();
        createdClasses.setCourse(curso);
        createdClasses.setSemester(classDTO.getSemester());
        createdClasses.setName(classDTO.getName());
        createdClasses.setGradleLevel(classDTO.getGradleLevel());
        createdClasses.setShift(classDTO.getShift());

        return ResponseEntity.status(HttpStatus.CREATED).body(classesService.create(createdClasses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Classes> update(@PathVariable Integer id, @RequestBody Classes classes) {
        Optional<Classes> updatedClasses = Optional.ofNullable(classesService.update(id, classes));
        return updatedClasses.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        boolean deleted = classesService.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
