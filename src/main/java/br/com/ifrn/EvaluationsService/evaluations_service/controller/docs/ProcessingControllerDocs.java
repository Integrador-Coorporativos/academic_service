package br.com.ifrn.EvaluationsService.evaluations_service.controller.docs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;

@Tag(name = "Processamento de Planilhas", description = "Endpoints para upload, download e histórico de planilhas de alunos, cursos e turmas")
public interface ProcessingControllerDocs {

    @Operation(summary = "Baixar template de planilha",
            description = "Retorna o modelo de planilha (.xlsx ou .csv) com o formato esperado para upload.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template retornado com sucesso",
                    content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<?> getTemplate();

    @Operation(summary = "Listar histórico de uploads",
            description = "Lista todas as importações de planilhas realizadas no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico listado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<?> getImports();

    @Operation(summary = "Detalhes de uma importação",
            description = "Retorna detalhes de uma importação específica, incluindo data, número de registros processados e erros.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalhes da importação retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Importação não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<?> getImports(@PathVariable String id);

    @Operation(summary = "Upload de planilha",
            description = "Recebe uma planilha (.xlsx ou .csv) e processa os dados para atualizar registros de alunos, cursos e turmas.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Planilha processada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Formato inválido do arquivo"),
            @ApiResponse(responseCode = "422", description = "Erro ao processar planilha"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file);

    @Operation(summary = "Excluir histórico de importação",
            description = "Remove um registro do histórico de uploads de planilhas.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Histórico removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Importação não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<?> deleteFile(@PathVariable String id);
}
