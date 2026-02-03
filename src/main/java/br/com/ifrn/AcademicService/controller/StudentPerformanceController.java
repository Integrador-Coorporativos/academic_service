package br.com.ifrn.AcademicService.controller;

import br.com.ifrn.AcademicService.controller.docs.StudentPerformanceControllerDocs;
import br.com.ifrn.AcademicService.dto.request.RequestStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.request.RequestStudentPerformanceUpdateDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseclassificationsClassDTO;
import br.com.ifrn.AcademicService.services.StudentPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/performance")
public class StudentPerformanceController implements StudentPerformanceControllerDocs {

    @Autowired
    private StudentPerformanceService studentPerformanceService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR', 'ROLE_ALUNO')")
    @GetMapping("/student/{id}")
    public ResponseEntity<ResponseStudentPerformanceDTO> getStudentPerformanceById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(studentPerformanceService.getStudentPerformanceById(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    @GetMapping("/student/all")
    public ResponseEntity<List<ResponseStudentPerformanceDTO>> getAllStudentPerformance() {
        return ResponseEntity.ok().body(studentPerformanceService.getAllStudentPerformance());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    @GetMapping("/class/{id}/{year}/bimestre/{bimestre}")
    public ResponseEntity<ResponseclassificationsClassDTO> getClassEvaluationsById(@PathVariable Integer id, @PathVariable Integer year, @PathVariable Integer bimestre) {
        return ResponseEntity.ok().body(studentPerformanceService.getClassificationByClassId(id, year, bimestre));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_PROFESSOR', 'ROLE_ALUNO')")
    @GetMapping("/class/all")
    public ResponseEntity<List<ResponseclassificationsClassDTO>> getClassEvaluationsAll() {
        return ResponseEntity.ok().body(studentPerformanceService.getClassifications());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ResponseStudentPerformanceDTO> createStudentPerformance(@RequestBody RequestStudentPerformanceDTO performanceDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentPerformanceService.createStudentPerformance(performanceDTO));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PutMapping("/class/{id}")
    public ResponseEntity<ResponseStudentPerformanceDTO> updateStudentEvaluation(@PathVariable Integer id, @RequestBody RequestStudentPerformanceUpdateDTO dto) {
        return ResponseEntity.ok().body(studentPerformanceService.updateStudentPerformance(id, dto));
    }
}
