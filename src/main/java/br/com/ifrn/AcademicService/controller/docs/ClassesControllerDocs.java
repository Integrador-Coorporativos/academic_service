package br.com.ifrn.AcademicService.controller.docs;

import br.com.ifrn.AcademicService.dto.request.RequestClassDTO;
import br.com.ifrn.AcademicService.models.Classes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;


import java.util.List;


@Tag(name = "Classes", description = "Operações relacionadas a Turmas")
public interface ClassesControllerDocs {


    @Operation(summary = "Lista todas as turmas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de turmas retornada com sucesso"),
    })
    ResponseEntity<List<Classes>> getAll();

    @Operation(summary = "Cria uma nova turma")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Turma criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito ao criar turma"),
    })
    ResponseEntity<Classes> create(Integer courseId, RequestClassDTO classDTO);

    @Operation(summary = "Recupera detalhes de uma turma específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turma encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada"),
    })
    ResponseEntity<Classes> getById(Integer id);

    @Operation(summary = "Atualiza informações de uma turma existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Turma atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada"),
    })
    ResponseEntity<Classes> update(Integer id, Classes classes);

    @Operation(summary = "Remove uma turma do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Turma removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada"),
    })
    ResponseEntity<Void> delete(Integer id);
}
