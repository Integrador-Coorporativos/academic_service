package br.com.ifrn.AcademicService.controller;


import br.com.ifrn.AcademicService.controller.docs.PainelControleControllerDocs;
import br.com.ifrn.AcademicService.dto.StartPeriodDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseProfessorPanelDTO;
import br.com.ifrn.AcademicService.dto.response.StudentDataDTO;
import br.com.ifrn.AcademicService.models.EvaluationPeriod;
import br.com.ifrn.AcademicService.services.PainelControleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin-panel")
public class PainelControleController implements PainelControleControllerDocs {

    @Autowired
    PainelControleService painelControleService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/professor")
    public ResponseEntity<List<ResponseProfessorPanelDTO>> getAllProfessors() {
        return ResponseEntity.ok(painelControleService.getAllProfessors());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/students")
    public ResponseEntity<List<StudentDataDTO>> getAllStudents() {
        return ResponseEntity.ok(painelControleService.getAllStudents());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/evaluation-periods/start")
    public ResponseEntity<String> startNewEvaluationCycle(@RequestBody @Valid StartPeriodDTO dto) {
        painelControleService.startNewPeriod(dto);
        return ResponseEntity.ok("Sucesso!");
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/evaluation-periods/active")
    public ResponseEntity<EvaluationPeriod> getActivePeriod() {
        return painelControleService.getActivePeriod()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/evaluation-periods/end-current")
    public ResponseEntity<String> endCurrentEvaluationCycle() {
        try {
            painelControleService.manuallyEndCurrentPeriod();
            return ResponseEntity.ok("O período de avaliação foi encerrado manualmente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao encerrar período: " + e.getMessage());
        }
    }
}