package br.com.ifrn.EvaluationsService.evaluations_service.controller;


import br.com.ifrn.EvaluationsService.evaluations_service.controller.docs.ClassEvaluationsControllerDocs;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.request.RequestClassEvaluationsDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.response.ResponseClassEvaluationsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class ClassEvaluationsController implements ClassEvaluationsControllerDocs {

    @GetMapping
    public ResponseEntity<List<ResponseClassEvaluationsDTO>> getAllEvaluations() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseClassEvaluationsDTO> getEvaluationById(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/class/{id}")
    public ResponseEntity<List<ResponseClassEvaluationsDTO>> getEvaluationsByClassId(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<ResponseClassEvaluationsDTO> createEvaluation(@RequestBody RequestClassEvaluationsDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseClassEvaluationsDTO> updateEvaluation(@PathVariable String id, @RequestBody RequestClassEvaluationsDTO dto) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvaluation(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
