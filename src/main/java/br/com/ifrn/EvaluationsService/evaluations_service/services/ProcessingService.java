package br.com.ifrn.EvaluationsService.evaluations_service.services;

import br.com.ifrn.EvaluationsService.evaluations_service.dto.ImporterDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.ResponseImporterDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.file.importer.contract.FileImporter;
import br.com.ifrn.EvaluationsService.evaluations_service.file.importer.factory.FileImporterFactory;
import br.com.ifrn.EvaluationsService.evaluations_service.file.objectstorage.MinioClientConfig;
import br.com.ifrn.EvaluationsService.evaluations_service.keycloak.KeycloakAdminConfig;
import io.minio.ObjectWriteResponse;
import jakarta.ws.rs.core.Response;
import lombok.SneakyThrows;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.coyote.BadRequestException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessingService {

    @Autowired
    FileImporterFactory importer;

    @Autowired
    StudentPerformanceService studentPerformanceService;

    @Autowired
    MinioClientConfig minioClient;

    @Autowired
    KeycloakAdminConfig keycloakAdmin;

    public byte[] getTemplate() throws Exception {

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            XSSFSheet sheet = workbook.createSheet("Modelo Importação Alunos");

            // Criar cabeçalho
            Row header = sheet.createRow(0);

            String[] columns = {
                    "Nome_Completo",
                    "Matricula",
                    "Turma_ID",
                    "Curso",
                    "Turno",
                    "Semestre",
                    "Porcentagem_Presença",
                    "Média_Geral",
                    "IRA",
                    "Reprovações"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Criar linhas de exemplo
            Object[][] exampleData = {
                    {"Ana Beatriz Souza", "20231094040001", "20231.1.09404.1V", "ADS", "Vespertino", "5º", 92, 84, 91, 0},
                    {"Lucas Oliveira", "20231094040002", "20231.1.09404.1V", "Informática", "Matutino", "3º", 74, 61, 67, 2},
                    {"Maria Ferreira", "20231094040003", "20231.1.09404.1M", "Química", "Vespertino", "2º", 88, 75, 83, 1},
                    {"João Santos", "20231094040004", "20231.1.09404.1V", "Alimentos", "Matutino", "6º", 65, 52, 57, 3}
            };

            for (int rowIndex = 0; rowIndex < exampleData.length; rowIndex++) {
                Row row = sheet.createRow(rowIndex + 1); // começa na linha 1

                for (int colIndex = 0; colIndex < exampleData[rowIndex].length; colIndex++) {
                    row.createCell(colIndex).setCellValue(
                            exampleData[rowIndex][colIndex] == null
                                    ? ""
                                    : exampleData[rowIndex][colIndex].toString()
                    );
                }
            }

            // Auto ajustar colunas
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Converter para array de bytes
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new FileNotFoundException("Erro ao gerar o arquivo: " + e);
            // TODO: substituir por exceção personalizada
        }
    }


    public ResponseEntity<?> getImports(){
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> getImports(String id){
        return ResponseEntity.ok().build();
    }

    public List<ResponseImporterDTO> uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new BadRequestException("Please set a valid file");

        try (InputStream inputStream = file.getInputStream()) {
            String fileName = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("File mame cannot be null"));

            FileImporter importer = this.importer.getFileImporter(fileName);

            List<ImporterDTO> dataImporter = importer.importFile(inputStream).stream().toList();

            List<ResponseImporterDTO> responseImporterDTOList = new ArrayList<>();
            for (ImporterDTO importerDTO : dataImporter){
                responseImporterDTOList.add(processImporterDTO(importerDTO));
            };

            try (InputStream uploadStream = file.getInputStream()) {
                minioClient.uploadFile(uploadStream, fileName);
            }
            return responseImporterDTOList;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> deleteFile(String id) {
        return ResponseEntity.ok().build();
    }

    public ResponseImporterDTO processImporterDTO(ImporterDTO importerDTO) throws Exception {
        ResponseStudentPerformanceDTO studentPerformanceDTO = studentPerformanceService.createStudentPerformanceByImporterDTO(importerDTO);
        ResponseImporterDTO responseImporterDTO = new ResponseImporterDTO();
        responseImporterDTO.setStudentPerformance(studentPerformanceDTO);

        Response response = keycloakAdmin.createKeycloakUser(importerDTO.getRegistration(), importerDTO.getName());

        if (response.getStatus() == 201) {//fazer lógica de atualização de dados do usuário

            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            System.out.println("Usuário criado com ID: " + userId);
        } else {
            System.out.println("Erro ao criar usuário: " + response.getStatus() + " " + response.getStatusInfo());
            System.out.println(response.readEntity(String.class));
        }



        //Adicionar chamadas para criação de outros Objetos

        return responseImporterDTO;

    }

    @SneakyThrows
    public ObjectWriteResponse uploadImage(@RequestParam("image") MultipartFile image) {
        try (InputStream uploadStream = image.getInputStream()) {
            String fileName = Optional.ofNullable(image.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("Image mame cannot be null"));
            ObjectWriteResponse response = minioClient.uploadImgage(uploadStream, fileName);
            return response;
        }catch (Exception e) {throw new Exception("Erro ao processar o arquivo: " + e);}
    }
}
