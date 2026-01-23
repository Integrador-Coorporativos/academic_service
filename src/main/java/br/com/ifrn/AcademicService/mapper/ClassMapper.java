package br.com.ifrn.AcademicService.mapper;

import br.com.ifrn.AcademicService.dto.response.ResponseClassByIdDTO;
import br.com.ifrn.AcademicService.models.Classes;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClassMapper {

    ResponseClassByIdDTO toResponseClassByDTO(Classes classes);
}
