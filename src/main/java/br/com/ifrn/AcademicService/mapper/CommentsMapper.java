package br.com.ifrn.AcademicService.mapper;

import br.com.ifrn.AcademicService.dto.request.RequestCommentDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseCommentDTO;
import br.com.ifrn.AcademicService.models.ClassComments;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentsMapper {
    ClassComments toClassComments(ResponseClassDTO classesDTO);
    ResponseCommentDTO toResponseClassCommentsDTO(ClassComments classComments);
    ClassComments toClassComments(ClassComments classComments);
    RequestCommentDTO  toRequestCommentDTO(ClassComments classComments);
}
