package br.com.ifrn.AcademicService.controller.docs;

import br.com.ifrn.AcademicService.dto.StartPeriodDTO;
import br.com.ifrn.AcademicService.dto.response.*;
import br.com.ifrn.AcademicService.models.EvaluationPeriod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Tag(name = "Painel de Controle", description = "Operações relacionadas a Turmas")
public interface PainelControleControllerDocs {
    @Operation(summary = "Lista todos os professores cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de professores retornada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseProfessorPanelDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<List<ResponseProfessorPanelDTO>> getAllProfessors();

    @Operation(summary = "Lista todos os alunos cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de alunos retornada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = StudentDataDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<List<StudentDataDTO>> getAllStudents();

    @Operation(
            summary = "Inicia um novo ciclo de avaliação",
            description = "Cria um novo período de avaliação (bimestre) para o ano informado. " +
                    "Este método encerra automaticamente qualquer período anterior que esteja aberto."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Novo período de avaliação iniciado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da requisição inválidos. Verifique se o ano é superior a 2024 e se o bimestre está correto."
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Regra de negócio violada: Não é possível iniciar um período que já foi concluído ou erro na transição de ciclos."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor ao tentar persistir o novo ciclo."
            )
    })
    public ResponseEntity<String> startNewEvaluationCycle(
            @RequestBody(
                    description = "Dados para abertura do ciclo",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = StartPeriodDTO.class),
                            examples = @ExampleObject(
                                    value = "{ \"stepName\": \"PRIMEIRO\", \"year\": 2026 }"
                            )
                    )
            )
            @Valid StartPeriodDTO dto
    );

    @Operation(summary = "Busca o período de avaliação atualmente ativo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Período ativo retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = EvaluationPeriod.class))),
            @ApiResponse(responseCode = "204", description = "Não existe nenhum período de avaliação ativo no momento"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<EvaluationPeriod> getActivePeriod();

    @Operation(summary = "Encerra manualmente o ciclo de avaliação atual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Período encerrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida ou nenhum período ativo para encerrar"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para esta operação"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao tentar encerrar o período")
    })
    public ResponseEntity<String> endCurrentEvaluationCycle();
}
