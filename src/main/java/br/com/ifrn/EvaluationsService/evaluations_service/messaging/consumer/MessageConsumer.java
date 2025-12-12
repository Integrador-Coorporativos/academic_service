package br.com.ifrn.EvaluationsService.evaluations_service.messaging.consumer;

import br.com.ifrn.EvaluationsService.evaluations_service.messaging.config.RabbitMQConfig;
import br.com.ifrn.EvaluationsService.evaluations_service.messaging.dto.ConsumerMessageDTO;
import br.com.ifrn.EvaluationsService.evaluations_service.services.MessagingReceiveService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    @Autowired
    MessagingReceiveService  messagingReceiveService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ClASS_SERVICE_PRODUCER)
    public void receiveSuccess(ConsumerMessageDTO message) {
        messagingReceiveService.procMessage(message);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ERROR)
    public void receiveError(ConsumerMessageDTO message) {
        System.out.println("Erro ao processar linha: ");
        // Aqui você pode gerar planilha de retorno ou logar
    }
}

