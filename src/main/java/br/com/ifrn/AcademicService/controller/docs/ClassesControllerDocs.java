package br.com.ifrn.AcademicService.controller.docs;

import br.com.ifrn.AcademicService.dto.request.RequestClassDTO;
import br.com.ifrn.AcademicService.dto.response.ClassPanelResponseDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassByIdDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassDTO;
import br.com.ifrn.AcademicService.models.Classes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;


@Tag(name = "Classes", description = "Operações relacionadas a Turmas")
public interface ClassesControllerDocs {


    @Operation(summary = "Lista todas as turmas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de turmas retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
    })
    ResponseEntity<List<ResponseClassDTO>> getAll(Authentication authentication);

    @Operation(summary = "Lista todas as turmas do professor logado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de turmas retornada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseClassDTO.class)))),
            @ApiResponse(responseCode = "204", description = "Nenhuma turma encontrada para o professor"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
    })

    public ResponseEntity<List<ResponseClassDTO>> getMyClasses(Authentication authentication);


    @Operation(summary = "Cria uma nova turma")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Turma criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito ao criar turma"),
    })
    ResponseEntity<ResponseClassDTO> create(Integer courseId, RequestClassDTO classDTO);

    @Operation(summary = "Recupera detalhes de uma turma específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turma encontrada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada"),
    })
    ResponseEntity<ResponseClassByIdDTO> getById(Integer id) throws Exception;

    @Operation(summary = "Atualiza informações de uma turma existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turma atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada"),
    })
    ResponseEntity<ResponseClassDTO> update(Integer id, Integer courseId, RequestClassDTO classes);

    @Operation(summary = "Remove uma turma do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Turma removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada"),
    })
    ResponseEntity<Void> delete(Integer id);


    @Operation(
            summary = "Listar turmas para o painel",
            description = "Retorna uma lista simplificada de todas as turmas para exibição no dashboard."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar este recurso"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<List<ClassPanelResponseDTO>> getClassesForPanel();

    @Operation(summary = "Adiciona o professor logado a uma turma específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Professor adicionado à turma com sucesso",
                    content = @Content(schema = @Schema(implementation = ResponseClassDTO.class))),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada"),
            @ApiResponse(responseCode = "400", description = "Erro na adição do professor à turma")
    })
    public ResponseEntity<ResponseClassDTO> addProfessorToClass(@PathVariable Integer id, Authentication authentication);
}
