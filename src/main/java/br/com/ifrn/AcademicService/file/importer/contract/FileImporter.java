package br.com.ifrn.AcademicService.file.importer.contract;

import br.com.ifrn.AcademicService.dto.ImporterDTO;

import java.io.InputStream;
import java.util.List;

public interface FileImporter {
    List<ImporterDTO> importFile(InputStream inputStream) throws Exception;
}
