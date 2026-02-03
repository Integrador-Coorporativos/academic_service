package br.com.ifrn.AcademicService.controller;


import br.com.ifrn.AcademicService.controller.docs.PainelControleControllerDocs;
import br.com.ifrn.AcademicService.dto.response.ResponseProfessorPanelDTO;
import br.com.ifrn.AcademicService.dto.response.StudentDataDTO;
import br.com.ifrn.AcademicService.services.PainelControleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin-panel")
@CrossOrigin(origins = "http://localhost:5173")
public class    PainelControleController implements PainelControleControllerDocs {

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
}
