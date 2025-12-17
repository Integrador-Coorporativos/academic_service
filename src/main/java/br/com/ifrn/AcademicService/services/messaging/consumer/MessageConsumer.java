package br.com.ifrn.AcademicService.services.messaging.consumer;

import br.com.ifrn.AcademicService.config.rabbitmq.RabbitMQConfig;
import br.com.ifrn.AcademicService.dto.ImportMessageDTO;
import br.com.ifrn.AcademicService.services.MessagingReceiveService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    @Autowired
    MessagingReceiveService  messagingReceiveService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ClASS_SERVICE_PRODUCER)
    public void receiveSuccess(ImportMessageDTO message) {
        messagingReceiveService.procMessage(message);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ERROR)
    public void receiveError(ImportMessageDTO message) {
        System.out.println("Erro ao processar linha: ");
        // Aqui você pode gerar planilha de retorno ou logar
    }
}

