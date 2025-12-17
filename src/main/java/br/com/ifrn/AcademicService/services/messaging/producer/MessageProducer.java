package br.com.ifrn.AcademicService.services.messaging.producer;
import br.com.ifrn.AcademicService.config.rabbitmq.RabbitMQConfig;
import br.com.ifrn.AcademicService.dto.CreateClassMessageDTO;
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

