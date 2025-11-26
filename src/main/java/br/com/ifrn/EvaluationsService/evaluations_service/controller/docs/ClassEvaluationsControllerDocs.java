package br.com.ifrn.EvaluationsService.evaluations_service.controller.docs;

import br.com.ifrn.EvaluationsService.evaluations_service.dto.request.RequestClassEvaluationsDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.response.ResponseClassEvaluationsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Avaliações", description = "Endpoints de gerenciamento de avaliações de turma")
public interface ClassEvaluationsControllerDocs {

    @Operation(summary = "Listar todas as avaliações", description = "Retorna todas as avaliações cadastradas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseClassEvaluationsDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<List<ResponseClassEvaluationsDTO>> getAllEvaluations();

    @Operation(summary = "Buscar avaliação por ID", description = "Retorna os detalhes de uma avaliação específica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avaliação encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseClassEvaluationsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ResponseClassEvaluationsDTO> getEvaluationById(@PathVariable Integer id);
    @Operation(summary = "Listar avaliações por turma",
            description = "Retorna todas as avaliações associadas a uma turma específica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseClassEvaluationsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<List<ResponseClassEvaluationsDTO>> getEvaluationsByClassId(@PathVariable Integer id);

    @Operation(summary = "Criar nova avaliação", description = "Cria uma nova avaliação para uma turma.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Avaliação criada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseClassEvaluationsDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflito - avaliação já cadastrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ResponseClassEvaluationsDTO> createEvaluation(@RequestBody RequestClassEvaluationsDTO dto);

    @Operation(summary = "Atualizar avaliação",
            description = "Atualiza os dados de uma avaliação existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avaliação atualizada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseClassEvaluationsDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ResponseClassEvaluationsDTO> updateEvaluation(@PathVariable Integer id,
                                                                        @RequestBody RequestClassEvaluationsDTO dto);

    @Operation(summary = "Excluir avaliação", description = "Remove uma avaliação específica do sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Avaliação removida"),
            @ApiResponse(responseCode = "404", description = "Avaliação não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<?> deleteEvaluation(@PathVariable Integer id);
}
