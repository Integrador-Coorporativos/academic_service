package br.com.ifrn.EvaluationsService.evaluations_service.controller;

import br.com.ifrn.EvaluationsService.evaluations_service.controller.docs.ProcessingControllerDocs;
import br.com.ifrn.EvaluationsService.evaluations_service.controller.docs.StudentPerformanceControllerDocs;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.ImporterDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.services.ProcessingService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/processing")
public class ProcessingController implements ProcessingControllerDocs {

    @Autowired
    private ProcessingService processingService;


    @GetMapping("/template")
    public ResponseEntity<?> getTemplate(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/imports")
    public ResponseEntity<?> getImports(){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/imports/{id}")
    public ResponseEntity<?> getImports(@PathVariable String id){
        return ResponseEntity.ok().build();
    }

    @SneakyThrows
    @PostMapping(
            value="/uploadFile"
    )
    public ResponseEntity<List<ImporterDTO>> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(processingService.uploadFile(file));
    }

    @DeleteMapping("/imports/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }
}
