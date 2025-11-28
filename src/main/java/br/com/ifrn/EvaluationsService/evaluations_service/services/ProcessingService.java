package br.com.ifrn.EvaluationsService.evaluations_service.services;

import br.com.ifrn.EvaluationsService.evaluations_service.dto.ImporterDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.file.importer.contract.FileImporter;
import br.com.ifrn.EvaluationsService.evaluations_service.file.importer.factory.FileImporterFactory;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessingService {

    @Autowired
    FileImporterFactory importer;

    public ResponseEntity<?> getTemplate(){
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> getImports(){
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> getImports(String id){
        return ResponseEntity.ok().build();
    }

    public List<ImporterDTO> uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new BadRequestException("Please set a valid file");

        try (InputStream inputStream = file.getInputStream()) {
            String fileName = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("File mame cannot be null"));

            FileImporter importer = this.importer.getFileImporter(fileName);

            List<ImporterDTO> dataImporter = importer.importFile(inputStream).stream().toList();

            return dataImporter;


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> deleteFile(String id) {
        return ResponseEntity.ok().build();
    }


}
