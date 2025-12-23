package br.com.ifrn.AcademicService.controller.docs;

import br.com.ifrn.AcademicService.dto.request.RequestCommentDTO;
import br.com.ifrn.AcademicService.models.ClassComments;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
@Tag(name = "Comments", description = "Operações relacionadas a comentários")
public interface ClassCommentsControllerDocs {

    @Operation(summary = "Lista comentários da turma (apenas professores)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comentários retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada"),
    })
    ResponseEntity<List<ClassComments>> getByClass(Integer classId);

    @Operation(summary = "Cria um comentário para a turma")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comentário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Turma não encontrada")
    })
    ResponseEntity<ClassComments> create(Integer classId, Integer professorId, RequestCommentDTO commentDTO);

    @Operation(summary = "Edita um comentário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comentário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido"),
            @ApiResponse(responseCode = "404", description = "Comentário ou turma não encontrada"),
    })
    ResponseEntity<ClassComments> update(Integer classId, Integer commentId, ClassComments comment);

    @Operation(summary = "Remove um comentário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comentário removido com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido"),
            @ApiResponse(responseCode = "404", description = "Comentário ou turma não encontrada"),
    })
    ResponseEntity<Void> delete(Integer classId, Integer commentId);

}
