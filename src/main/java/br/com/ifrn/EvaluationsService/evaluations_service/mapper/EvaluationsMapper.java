package br.com.ifrn.EvaluationsService.evaluations_service.mapper;

import br.com.ifrn.EvaluationsService.evaluations_service.dto.request.RequestClassEvaluationsDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.response.ResponseClassEvaluationsDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.models.ClassEvaluations;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EvaluationsMapper {

    @Mapping(source = "date", target = "date")
    @Mapping(source = "professorId", target = "professorId")
    @Mapping(source = "classId", target = "classId")
    @Mapping(source = "averageScore", target = "averageScore")
    ResponseClassEvaluationsDTO toResponseClassEvaluationsDTO(ClassEvaluations classEvaluations);

    @Mapping(source = "date", target = "date")
    @Mapping(source = "professorId", target = "professorId")
    @Mapping(source = "classId", target = "classId")
    @Mapping(source = "averageScore", target = "averageScore")
    ClassEvaluations toClassEvaluations(ResponseClassEvaluationsDTO responseClassEvaluationsDTO);


    @Mapping(source = "date", target = "date")
    @Mapping(source = "professorId", target = "professorId")
    @Mapping(source = "classId", target = "classId")
    @Mapping(source = "averageScore", target = "averageScore")
    ClassEvaluations toClassEvaluationsRequest(RequestClassEvaluationsDTO requestClassEvaluationsDTO);


    @Mapping(source = "date", target = "date")
    @Mapping(source = "professorId", target = "professorId")
    @Mapping(source = "classId", target = "classId")
    @Mapping(source = "averageScore", target = "averageScore")
    void updateEntityFromDto(RequestClassEvaluationsDTO dto,
                             @MappingTarget ClassEvaluations entity);
}