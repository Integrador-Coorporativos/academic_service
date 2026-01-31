package br.com.ifrn.AcademicService.mapper;

import br.com.ifrn.AcademicService.dto.request.RequestCourseDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseCourseDTO;
import br.com.ifrn.AcademicService.models.Courses;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CoursesMapper {
    List<ResponseCourseDTO> toResponseCourseDTO(List<Courses> courses);
    ResponseCourseDTO toResponseCourseDTO(Courses courses);
    Courses toCourses(ResponseCourseDTO responseCourseDTO);
    RequestCourseDTO toRequestCourseDTO(Courses courses);
}
