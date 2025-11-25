package br.com.ifrn.EvaluationsService.evaluations_service.controller;

import br.com.ifrn.EvaluationsService.evaluations_service.controller.docs.ProcessingControllerDocs;
import br.com.ifrn.EvaluationsService.evaluations_service.controller.docs.StudentPerformanceControllerDocs;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/processing")
public class ProcessingController implements ProcessingControllerDocs {


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

    @PostMapping("/uploadFile")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @DeleteMapping("/imports/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }
}
