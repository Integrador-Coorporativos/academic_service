package br.com.ifrn.EvaluationsService.evaluations_service.file.importer.impl;

import br.com.ifrn.EvaluationsService.evaluations_service.dto.ImporterDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.file.importer.contract.FileImporter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class XlsxImporter implements FileImporter {
    private final DataFormatter formatter = new DataFormatter();

    @Override
    public List<ImporterDTO> importFile(InputStream inputStream) throws Exception {

        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0); //Pega a aba usada na planilha
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) rowIterator.next(); //Pula a primeira linha da planilha

            return parseRowsToImportDTOList(rowIterator);

        }
    }

    private List<ImporterDTO> parseRowsToImportDTOList(Iterator<Row> rowIterator) {
        List<ImporterDTO>  importedData = new ArrayList<>();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isRowValid(row)) {
                importedData.add(parseRowToImporterDTO(row));
            } else {
                System.out.println("Linha ignorada, célula obrigatória ausente: " + row.getRowNum());
                continue; // pula linha inteira

                //Adicionar lógica para montar e retornar uma planilha com os dados incompletos para correção
            }
        }
        return importedData;
    }

    private ImporterDTO parseRowToImporterDTO(Row row) {
        ImporterDTO importerDTO = new ImporterDTO();

        importerDTO.setName(getString(row, 0));         // Nome_Completo
        importerDTO.setRegistration(getRawString(row, 1)); // Matrícula
        importerDTO.setClassId(getString(row, 2));      // Turma_ID
        importerDTO.setCourse(getString(row, 3));       // Curso
        importerDTO.setShift(getString(row, 4));        // Turno
        importerDTO.setSemester(getString(row, 5));     // Semestre
        importerDTO.setPresence(parseFloat(row, 6));    // Porcentagem_Presença
        importerDTO.setAverage(parseFloat(row, 7));     // Média_Geral
        importerDTO.setIra(parseFloat(row, 8));         // IRA
        importerDTO.setRejections(parseInt(row, 9));    // Reprovações
        return importerDTO;
    }

    private boolean isRowValid(Row row) {
        if (row == null) return false;

        // Definindo colunas obrigatórias
        int[] mandatoryColumns = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

        for (int col : mandatoryColumns) {
            Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell == null) return false;

            String value = formatter.formatCellValue(cell);
            if (value == null || value.isBlank()) return false; // célula vazia
        }

        return true; // todas as células obrigatórias têm valor
    }

    private String getRawString(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";

        if (cell.getCellType() == CellType.NUMERIC) {
            // Converte o número sem notação científica
            return BigDecimal.valueOf(cell.getNumericCellValue())
                    .toPlainString()
                    .replace(".0", ""); // remove decimal se aparecer
        }

        return formatter.formatCellValue(cell);
    }

    private String getString(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

        if (cell == null) {
            return ""; // Retorna vazio sem estourar exceção
        }
        return formatter.formatCellValue(cell);
    }

    private Float parseFloat(Row row, int index) {
        String value = getString(row, index);
        if (value == null || value.isBlank()) return null;
        value = value.replace(",", ".");  // caso venha com vírgula
        return Float.parseFloat(value);
    }

    private Integer parseInt(Row row, int index) {
        String value = getString(row, index);
        if (value == null || value.isBlank()) return null;

        // em muitos Excel, números inteiros vêm como "12.0"
        if (value.contains(".")) {
            value = value.substring(0, value.indexOf("."));
        }

        return Integer.parseInt(value);
    }
}
