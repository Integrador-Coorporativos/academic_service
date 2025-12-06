package br.com.ifrn.EvaluationsService.evaluations_service.file.objectstorage;

import br.com.ifrn.EvaluationsService.evaluations_service.file.ContentTypes;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class MinioClientConfig {

    @Autowired
    MinioPropertiesConfig envMinio;

    @Bean
    MinioClient minioClient() {
        return MinioClient.builder().endpoint(envMinio.serverUrl())
                .credentials(envMinio.adminUser(), envMinio.adminPassword())
                .build();
    }

    @SneakyThrows
    public ObjectWriteResponse uploadFile(InputStream uploadStream, String fileName) throws IOException {
        MinioClient minioClient = minioClient();

        String userId = "user123"; // identificação do usuário que enviou

        String objectName = userId + "/" + UUID.randomUUID() + "_" + fileName;
        // pesquisar metodo de pegar o id do usuario apos implementar autenticacao

        Map<String, String> meta = new HashMap<>();
        meta.put("uploaded-by", userId);
        ObjectWriteResponse response = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(envMinio.bucketFiles())
                        .object(objectName)
                        .stream(uploadStream, uploadStream.available(), -1)
                        .contentType(ContentTypes.APPLICATION_XLSX_VALUE)
                        .headers(meta)
                        .build()
        );
        return response;
    }

    @SneakyThrows
    public ObjectWriteResponse uploadImgage(InputStream uploadStream, String fileName) throws IOException {
        MinioClient minioClient = minioClient();

        String userId = "user123"; // identificação do usuário que enviou

        String objectName = userId + "/" + UUID.randomUUID() + "_" + fileName;
        // pesquisar metodo de pegar o id do usuario apos implementar autenticacao

        Map<String, String> meta = new HashMap<>();
        meta.put("uploaded-by", userId);
        ObjectWriteResponse response = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(envMinio.bucketImages())
                        .object(objectName)
                        .stream(uploadStream, uploadStream.available(), -1)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .headers(meta)
                        .build()
        );
        return response;
    }

}
