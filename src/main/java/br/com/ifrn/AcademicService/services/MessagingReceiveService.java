package br.com.ifrn.AcademicService.services;


import br.com.ifrn.AcademicService.dto.ImportMessageDTO;
import br.com.ifrn.AcademicService.dto.ResponseImporterDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.AcademicService.models.Classes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class MessagingReceiveService {
    @Autowired
    StudentPerformanceService studentPerformanceService;

    @Autowired
    ClassesService classesService;

    public void procMessage(ImportMessageDTO classMessageDTO) {

        Classes classes = classesService.createOrUpdateClassByClassId(
            classMessageDTO.getCourse(),
            classMessageDTO.getClassId(),
            classMessageDTO.getShift(),
            classMessageDTO.getSemester(),
            classMessageDTO.getUserId()
        );

        ResponseStudentPerformanceDTO studentPerformanceDTO = studentPerformanceService.createStudentPerformanceByConsumerMessageDTO(classMessageDTO);
        ResponseImporterDTO responseImporterDTO = new ResponseImporterDTO();
        responseImporterDTO.setStudentPerformance(studentPerformanceDTO);

        System.out.println("Performance do Estudante Registrada: \n" + studentPerformanceDTO.getClassId() +"\n" + studentPerformanceDTO.getFailedSubjects());
    }
}
