package br.com.ifrn.AcademicService.services.messaging.consumer;

import br.com.ifrn.AcademicService.config.rabbitmq.RabbitMQConfig;
import br.com.ifrn.AcademicService.dto.ImportMessageDTO;
import br.com.ifrn.AcademicService.services.MessagingReceiveService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class MessageConsumer {
    @Autowired
    private MessagingReceiveService messagingReceiveService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ClASS_SERVICE_PRODUCER)
    public void receiveSuccess(
            @Payload ImportMessageDTO message,
            @Header("X-ORIGIN-USER-ID") String actorId // 1. Pegamos o ID do cabeçalho
    ) {
        try {
            // 2. Simulamos a autenticação para esta Thread específica
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    actorId, null, Collections.emptyList());

            SecurityContextHolder.getContext().setAuthentication(auth);

            // 3. Agora o service será executado e o Envers verá o actorId no contexto
            messagingReceiveService.procMessage(message);

        } finally {
            // 4. Limpeza obrigatória para não "contaminar" a próxima mensagem da thread
            SecurityContextHolder.clearContext();
        }
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ERROR)
    public void receiveError(ImportMessageDTO message) {
        // Log ou tratamento de erro
        System.err.println("Erro ao processar linha da planilha para o usuário: " + message.getRegistration());
    }
}

