package br.com.ifrn.EvaluationsService.evaluations_service.file.importer.contract;

import br.com.ifrn.EvaluationsService.evaluations_service.dto.ImporterDTO;

import java.io.InputStream;
import java.util.List;

public interface FileImporter {
    List<ImporterDTO> importFile(InputStream inputStream) throws Exception;
}
