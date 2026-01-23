package br.com.ifrn.AcademicService.mapper;


import br.com.ifrn.AcademicService.dto.ImportMessageDTO;
import br.com.ifrn.AcademicService.dto.request.RequestStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.AcademicService.dto.response.StudentDataDTO;
import br.com.ifrn.AcademicService.models.StudentPerformance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentPerformanceMapper {
    StudentPerformance toEntity (RequestStudentPerformanceDTO dto);

    RequestStudentPerformanceDTO toDto (StudentPerformance entity);

    List<StudentPerformance> toEntityList (List<RequestStudentPerformanceDTO> dto);

    ResponseStudentPerformanceDTO toResponseDto (StudentPerformance entity);

    StudentDataDTO toStudentDataDTO (StudentPerformance entity);

    @Mapping(source = "rejections", target = "failedSubjects", defaultValue = "0")
    @Mapping(source = "classId", target = "classId")
    @Mapping(source = "userId", target = "studentId")
    @Mapping(source = "average", target = "averageScore")
    @Mapping(source = "presence", target = "attendenceRate")
    @Mapping(source = "ira", target = "ira")
    RequestStudentPerformanceDTO toRequestStudentPerformanceByConsumerMessageDto (ImportMessageDTO dto);

    @Mapping(source = "failedSubjects", target = "failedSubjects")
    @Mapping(source = "averageScore", target = "averageScore")
    @Mapping(source = "attendenceRate", target = "attendenceRate")
    @Mapping(source = "ira", target = "ira")
    @Mapping(source = "classId", target = "classId")
    void updateEntityFromDto(RequestStudentPerformanceDTO dto,
                             @MappingTarget StudentPerformance entity);
}