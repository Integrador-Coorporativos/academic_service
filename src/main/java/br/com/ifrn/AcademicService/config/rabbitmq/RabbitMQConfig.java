package br.com.ifrn.AcademicService.config.rabbitmq;

import br.com.ifrn.AcademicService.dto.ImportMessageDTO;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "planilha-exchange";
    public static final String QUEUE_ClASS_SERVICE_PRODUCER= "planilha.class-service-producer.queue";
    public static final String QUEUE_RESPONSE = "planilha.response.queue";
    public static final String QUEUE_ERROR = "planilha.error.queue";

    @Bean
    public MessageConverter jsonMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*"); // permite desserializar qualquer pacote, ajuste se necessário
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put(
                "br.com.ifrn.ImportReportService.messaging.dto.ImportMessageDTO",
                ImportMessageDTO.class
        );
        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

    @Bean
    public Queue requestQueue() {
        return new Queue(QUEUE_ClASS_SERVICE_PRODUCER, true); // durável
    }

    @Bean
    public Queue responseQueue() {
        return new Queue(QUEUE_RESPONSE, true);
    }
    @Bean
    public Queue errorQueue() {
        return new Queue(QUEUE_ERROR, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding bindingClassServiceProducerQueue(Queue requestQueue, TopicExchange exchange) {
        return BindingBuilder.bind(requestQueue).to(exchange).with("planilha.class-service-producer");
    }

    @Bean
    public Binding bindingResponseQueue(Queue responseQueue, TopicExchange exchange) {
        return BindingBuilder.bind(responseQueue).to(exchange).with("planilha.response");
    }

    @Bean
    public Binding bindingErrorQueue(Queue errorQueue, TopicExchange exchange) {
        return BindingBuilder.bind(errorQueue).to(exchange).with("planilha.error");
    }
}
