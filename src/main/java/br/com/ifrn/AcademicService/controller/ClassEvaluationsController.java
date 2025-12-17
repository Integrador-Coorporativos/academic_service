package br.com.ifrn.AcademicService.controller;

import br.com.ifrn.AcademicService.controller.docs.ClassEvaluationsControllerDocs;
import br.com.ifrn.AcademicService.dto.request.RequestClassEvaluationsDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassEvaluationsDTO;
import br.com.ifrn.AcademicService.services.ClassEvaluationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class ClassEvaluationsController implements ClassEvaluationsControllerDocs {

    @Autowired
    private ClassEvaluationsService classEvaluationsService;

    @GetMapping
    public ResponseEntity<List<ResponseClassEvaluationsDTO>> getAllEvaluations() {
        return ResponseEntity.ok().body(classEvaluationsService.getAllEvaluations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseClassEvaluationsDTO> getEvaluationById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(classEvaluationsService.getEvaluationById(id));
    }

    @GetMapping("/class/{id}")
    public ResponseEntity<List<ResponseClassEvaluationsDTO>> getEvaluationsByClassId(@PathVariable Integer id) {
        return ResponseEntity.ok().body(classEvaluationsService.getEvaluationsByClassId(id));
    }

    @PostMapping
    public ResponseEntity<ResponseClassEvaluationsDTO> createEvaluation(@RequestBody RequestClassEvaluationsDTO dto, @RequestParam String classId, @RequestParam Integer professorId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(classEvaluationsService.createEvaluation(dto,  classId, professorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseClassEvaluationsDTO> updateEvaluation(@PathVariable Integer id, @RequestBody RequestClassEvaluationsDTO dto) {
        return ResponseEntity.ok().body(classEvaluationsService.updateEvaluation(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvaluation(@PathVariable Integer id) {
        classEvaluationsService.deleteEvaluation(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
