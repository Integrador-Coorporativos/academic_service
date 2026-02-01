package br.com.ifrn.AcademicService.controller.docs;

import br.com.ifrn.AcademicService.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
}
