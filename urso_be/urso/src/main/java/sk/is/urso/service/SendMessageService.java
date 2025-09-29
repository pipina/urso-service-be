package sk.is.urso.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.alfa.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class SendMessageService {

    private final JmsTemplate jmsTemplate;

    @Autowired
    public SendMessageService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ERROR_ACTIVEMQ_SEND_MESSAGE = "Chyba pri posielaní správy do ActiveMQ";

    public void sendMessage(String destination, String message) {
        try {
            //String jsonMessage = objectMapper.writeValueAsString(message);
            jmsTemplate.convertAndSend(destination, message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CommonException(ERROR_ACTIVEMQ_SEND_MESSAGE, e);
        }
    }
}
