package br.com.ifrn.EvaluationsService.evaluations_service.file.importer.impl;

import br.com.ifrn.EvaluationsService.evaluations_service.dto.ImporterDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.file.importer.contract.FileImporter;

import java.io.InputStream;
import java.util.List;

public class PngOrJpegImporter implements FileImporter {
    @Override
    public List<ImporterDTO> importFile(InputStream inputStream) throws Exception {
        return List.of();
    }
}
