package br.com.ifrn.AcademicService.controller;


import br.com.ifrn.AcademicService.controller.docs.PainelControleControllerDocs;
import br.com.ifrn.AcademicService.dto.StartPeriodDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseProfessorPanelDTO;
import br.com.ifrn.AcademicService.dto.response.StudentDataDTO;
import br.com.ifrn.AcademicService.models.EvaluationPeriod;
import br.com.ifrn.AcademicService.services.PainelControleService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin-panel")
public class PainelControleController implements PainelControleControllerDocs {

    @Autowired
    PainelControleService painelControleService;

    @GetMapping("/professor")
    public ResponseEntity<List<ResponseProfessorPanelDTO>> getAllProfessors() {
        return ResponseEntity.ok(painelControleService.getAllProfessors());
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentDataDTO>> getAllStudents() {
        return ResponseEntity.ok(painelControleService.getAllStudents());
    }

    @PostMapping("/evaluation-periods/start")
    public ResponseEntity<String> startNewEvaluationCycle(@RequestBody StartPeriodDTO dto) {
        // LOG DE SEGURANÇA
        System.out.println("DTO NO CONTROLLER: " + dto.getStepName() + " | " + dto.getYear());

        try {
            painelControleService.startNewPeriod(dto);
            return ResponseEntity.ok("Sucesso!");
        } catch (Exception e) {
            e.printStackTrace(); // ISSO VAI MOSTRAR O ERRO REAL NO CONSOLE DO SEU IDE
            return ResponseEntity.internalServerError().body("ERRO REAL: " + e.getMessage());
        }
    }

    @GetMapping("/evaluation-periods/active")
    public ResponseEntity<EvaluationPeriod> getActivePeriod() {
        return painelControleService.getActivePeriod()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

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