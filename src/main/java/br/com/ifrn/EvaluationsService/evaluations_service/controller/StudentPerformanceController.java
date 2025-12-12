package br.com.ifrn.EvaluationsService.evaluations_service.controller;

import br.com.ifrn.EvaluationsService.evaluations_service.controller.docs.StudentPerformanceControllerDocs;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.request.RequestStudentPerformanceDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.response.ResponseClassEvaluationsDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.services.StudentPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
public class StudentPerformanceController implements StudentPerformanceControllerDocs {

    @Autowired
    private StudentPerformanceService studentPerformanceService;

    @GetMapping("/student/{id}")
    public ResponseEntity<ResponseStudentPerformanceDTO> getStudentPerformanceById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(studentPerformanceService.getStudentPerformanceById(id));
    }
    @GetMapping("/student/all")
    public ResponseEntity<List<ResponseStudentPerformanceDTO>> getAllStudentPerformance() {
        return ResponseEntity.ok().body(studentPerformanceService.getAllStudentPerformance());
    }
    @GetMapping("/class/{id}")
    public ResponseEntity<ResponseClassEvaluationsDTO> getClassEvaluationsById(@PathVariable String id) {
        return ResponseEntity.ok().body(new ResponseClassEvaluationsDTO());
    }
    @PostMapping
    public ResponseEntity<ResponseStudentPerformanceDTO> createStudentPerformance(@RequestBody RequestStudentPerformanceDTO performanceDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentPerformanceService.createStudentPerformance(performanceDTO));
    }
    @PutMapping("/class/{id}")
    public ResponseEntity<ResponseStudentPerformanceDTO> updateStudentEvaluation(@PathVariable Integer id, @RequestBody RequestStudentPerformanceDTO dto) {
        return ResponseEntity.ok().body(studentPerformanceService.updateStudentPerformance(id, dto));
    }
}
