package br.com.ifrn.AcademicService.mapper;

import br.com.ifrn.AcademicService.dto.response.ResponseClassByIdDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassDTO;
import br.com.ifrn.AcademicService.models.Classes;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClassMapper {

    ResponseClassByIdDTO toResponseClassByDTO(Classes classes);

    List<ResponseClassDTO> toResponseClassDTO(List<Classes> classes);
}
