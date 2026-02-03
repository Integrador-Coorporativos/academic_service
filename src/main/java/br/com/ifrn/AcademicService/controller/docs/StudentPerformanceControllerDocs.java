package br.com.ifrn.AcademicService.controller.docs;

import br.com.ifrn.AcademicService.dto.request.RequestStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.request.RequestStudentPerformanceUpdateDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassEvaluationsDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseclassificationsClassDTO;
import io.swagger.v3.oas.annotations.Operation;
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
            description = "Retorna os indicadores de desempenho consolidados da turma com base nas avaliações registradas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Desempenho da turma encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseClassEvaluationsDTO.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<ResponseclassificationsClassDTO> getClassEvaluationsById(@PathVariable Integer id, @PathVariable Integer year);

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
