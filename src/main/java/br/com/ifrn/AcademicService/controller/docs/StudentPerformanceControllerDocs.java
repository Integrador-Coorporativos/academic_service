package br.com.ifrn.AcademicService.controller.docs;

import br.com.ifrn.AcademicService.dto.request.RequestStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.request.RequestStudentPerformanceUpdateDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassEvaluationsDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseclassificationsClassDTO;
import br.com.ifrn.AcademicService.models.enums.StepName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Desempenho", description = "Endpoints relacionados ao desempenho individual e coletivo dos alunos")
public interface StudentPerformanceControllerDocs {

    @Operation(
            summary = "Obter desempenho individual do aluno",
            description = "Retorna os dados de desempenho de um aluno específico incluindo médias, faltas e status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Desempenho encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseStudentPerformanceDTO.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ResponseStudentPerformanceDTO> getStudentPerformanceById(@PathVariable Integer id);

    @Operation(
            summary = "Obter desempenho coletivo da turma",
            description = "Retorna os indicadores de desempenho consolidados de uma turma (médias de frequência, comportamento, etc.) filtrados por ano letivo e bimestre, incluindo a posição da turma no ranking."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Desempenho da turma recuperado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseclassificationsClassDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Parâmetros de entrada inválidos (ex: bimestre fora do intervalo 1-4)"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado ou token inválido"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada ou sem dados para o período informado"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar as métricas")
    })
    public ResponseEntity<ResponseclassificationsClassDTO> getClassEvaluationsById(
            @Parameter(description = "ID único da turma (PK)", example = "1")
            @PathVariable Integer id,

            @Parameter(description = "Ano letivo de referência", example = "2025")
            @PathVariable Integer year,

            @Parameter(description = "Bimestre da avaliação (1, 2, 3 ou 4)", example = "1")
            @PathVariable Integer bimestre
    );

    @Operation(
            summary = "Cadastrar desempenho do Aluno",
            description = "Cadastra o desempenho do Aluno com base nos dados fornecidos"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Desempenho do Aluno cadastrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseClassEvaluationsDTO.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos enviados"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ResponseStudentPerformanceDTO> createStudentPerformance(@RequestBody RequestStudentPerformanceDTO performanceDTO);

    @Operation(
            summary = "Atualizar desempenho individual de um aluno",
            description = "Atualiza manualmente o desempenho de um aluno, incluindo média, frequência e status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Desempenho atualizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseStudentPerformanceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos enviados"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ResponseStudentPerformanceDTO> updateStudentEvaluation(
            @PathVariable Integer id,
            @RequestBody RequestStudentPerformanceUpdateDTO dto
    );

    @Operation(summary = "Lista o desempenho de todos os alunos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de desempenho de alunos retornada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseStudentPerformanceDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<List<ResponseStudentPerformanceDTO>> getAllStudentPerformance();

    @Operation(summary = "Lista todas as classificações das turmas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de classificações das turmas retornada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseclassificationsClassDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<List<ResponseclassificationsClassDTO>> getClassEvaluationsAll();

    @Operation(
            summary = "Atualizar desempenho individual do aluno por ID",
            description = "Endpoint específico para atualizar os dados de performance de um aluno diretamente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Desempenho atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseStudentPerformanceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<ResponseStudentPerformanceDTO> updateIndividualStudentPeformance(
            @PathVariable Integer id,
            @RequestBody RequestStudentPerformanceUpdateDTO dto
    );
}
