package br.com.ifrn.AcademicService.mapper;

import br.com.ifrn.AcademicService.dto.request.RequestClassEvaluationsDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassEvaluationsDTO;
import br.com.ifrn.AcademicService.models.ClassEvaluations;
import br.com.ifrn.AcademicService.models.EvaluationsCriteria;
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

    ClassEvaluations toClassEvaluationsRequest(RequestClassEvaluationsDTO requestClassEvaluationsDTO);

    EvaluationsCriteria toEvaluationsCriteria(RequestClassEvaluationsDTO evaluationsCriteria);

    void updateEntityFromDto(RequestClassEvaluationsDTO dto,
                             @MappingTarget ClassEvaluations entity);
}