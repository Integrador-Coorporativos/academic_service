package br.com.ifrn.AcademicService.file.importer.impl;

import br.com.ifrn.AcademicService.dto.ImporterDTO;
import br.com.ifrn.AcademicService.file.importer.contract.FileImporter;

import java.io.InputStream;
import java.util.List;

public class CsvImporter implements FileImporter {

    @Override
    public List<ImporterDTO> importFile(InputStream inputStream) throws Exception {
        return List.of();
    }
}
