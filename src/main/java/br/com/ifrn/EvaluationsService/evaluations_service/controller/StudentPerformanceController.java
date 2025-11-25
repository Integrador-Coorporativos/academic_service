package br.com.ifrn.EvaluationsService.evaluations_service.controller;

import br.com.ifrn.EvaluationsService.evaluations_service.controller.docs.StudentPerformanceControllerDocs;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.response.ResponseClassEvaluationsDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.response.ResponseStudentPerformanceDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/performance")
public class StudentPerformanceController implements StudentPerformanceControllerDocs {

    @GetMapping("/student/{id}")
    public ResponseEntity<ResponseStudentPerformanceDTO> getStudentPerformanceById(@PathVariable String id) {
        return ResponseEntity.ok().body(new ResponseStudentPerformanceDTO());
    }
    @GetMapping("/class/{id}")
    public ResponseEntity<ResponseClassEvaluationsDTO> getClassEvaluationsById(@PathVariable String id) {
        return ResponseEntity.ok().body(new ResponseClassEvaluationsDTO());
    }
    @PostMapping("/class/{id}")
    public ResponseEntity<ResponseClassEvaluationsDTO> createClassEvaluations(@PathVariable String id) {
        return ResponseEntity.ok().body(new ResponseClassEvaluationsDTO());
    }
    @PutMapping("/class/{id}")
    public ResponseEntity<ResponseStudentPerformanceDTO> updateStudentEvaluation(@PathVariable String id, @RequestBody ResponseStudentPerformanceDTO dto) {
        return ResponseEntity.ok().body(new ResponseStudentPerformanceDTO());
    }
}
