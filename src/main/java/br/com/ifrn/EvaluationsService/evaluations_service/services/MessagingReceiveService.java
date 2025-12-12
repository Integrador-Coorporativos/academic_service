package br.com.ifrn.EvaluationsService.evaluations_service.services;


import br.com.ifrn.EvaluationsService.evaluations_service.dto.ResponseImporterDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.dto.response.ResponseStudentPerformanceDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.messaging.dto.ConsumerMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class MessagingReceiveService {
    @Autowired
    StudentPerformanceService studentPerformanceService;

    public void procMessage(ConsumerMessageDTO classMessageDTO) {

        ResponseStudentPerformanceDTO studentPerformanceDTO = studentPerformanceService.createStudentPerformanceByConsumerMessageDTO(classMessageDTO);
        ResponseImporterDTO responseImporterDTO = new ResponseImporterDTO();
        responseImporterDTO.setStudentPerformance(studentPerformanceDTO);

        System.out.println("Performance do Estudante Registrada: " + studentPerformanceDTO);
    }
}
