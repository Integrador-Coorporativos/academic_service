package br.com.ifrn.EvaluationsService.evaluations_service.messaging.producer;
import br.com.ifrn.EvaluationsService.evaluations_service.messaging.config.RabbitMQConfig;
import br.com.ifrn.EvaluationsService.evaluations_service.messaging.dto.CreateClassMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendRequest(CreateClassMessageDTO message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "planilha.request", message);
    }

    public void sendError(CreateClassMessageDTO message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "planilha.error", message);
    }
}

