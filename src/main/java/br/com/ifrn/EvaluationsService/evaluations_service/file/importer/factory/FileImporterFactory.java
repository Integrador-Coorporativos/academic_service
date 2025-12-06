package br.com.ifrn.EvaluationsService.evaluations_service.file.importer.factory;

import br.com.ifrn.EvaluationsService.evaluations_service.file.importer.contract.FileImporter;
import br.com.ifrn.EvaluationsService.evaluations_service.file.importer.impl.CsvImporter;
import br.com.ifrn.EvaluationsService.evaluations_service.file.importer.impl.PngOrJpegImporter;
import br.com.ifrn.EvaluationsService.evaluations_service.file.importer.impl.XlsxImporter;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileImporterFactory {

    private Logger logger = LoggerFactory.getLogger(FileImporterFactory.class);

    @Autowired
    private ApplicationContext applicationContext;

    public FileImporter getFileImporter(String filename) throws BadRequestException {
        if (filename.endsWith(".xlsx")) {
            return applicationContext.getBean(XlsxImporter.class);
        }else if (filename.endsWith(".csv")) {
            return applicationContext.getBean(CsvImporter.class);
        } else if (filename.endsWith(".png") || filename.endsWith(".jpeg")) {
            return applicationContext.getBean(PngOrJpegImporter.class);
        } else {
            throw new BadRequestException("Filename doesn't end with .xlsx or .csv");
        }
    }
}
