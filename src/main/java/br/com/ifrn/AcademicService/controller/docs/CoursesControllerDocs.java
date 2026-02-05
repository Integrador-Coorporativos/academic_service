package br.com.ifrn.AcademicService.controller.docs;

import br.com.ifrn.AcademicService.dto.request.RequestCourseDTO;
import br.com.ifrn.AcademicService.dto.response.CoursePanelResponseDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseCourseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Courses", description = "Operações relacionadas a Cursos")
public interface CoursesControllerDocs {

    @Operation(summary = "Lista todos os cursos cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cursos retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<List<ResponseCourseDTO>> getAll();

    @Operation(summary = "Cria um novo curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Curso criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "409", description = "Conflito ao criar curso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<ResponseCourseDTO> create(RequestCourseDTO courseDTO);

    @Operation(summary = "Recupera detalhes de um curso específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<ResponseCourseDTO> getById(Integer id);

    @Operation(summary = "Atualiza informações de um curso existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<ResponseCourseDTO> update(Integer id, RequestCourseDTO course);

    @Operation(summary = "Remove um curso do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Curso removido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Curso não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    ResponseEntity<Void> delete(Integer id);

    @Operation(summary = "Resumo estatístico dos cursos",
            description = "Retorna o ID, nome, total de turmas e total de alunos matriculados por curso para exibição no painel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista do painel retornada com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CoursePanelResponseDTO.class)) }),
            @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor ao processar os dados", content = @Content)
    })
    public ResponseEntity<List<CoursePanelResponseDTO>> getCoursesPanel();
}
