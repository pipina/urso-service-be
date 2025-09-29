package sk.is.urso.config.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import sk.is.urso.model.urso.SetDlznici;
import sk.is.urso.repository.urso.SetDlzniciRepository;
import sk.is.urso.service.SendMessageService;


import java.util.List;

@Component
public class PodnikyListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SendMessageService sendMessageService;

    @Autowired
    private SetDlzniciRepository setDlzniciRepository;

    private static final String ERROR_PARSING_JSON = "Chyba pri prijatí dát a ich spracovaní do objektov.";

    @JmsListener(destination = "nove_podniky")
    public void receiveMessage(String message) {
        try {
            System.out.println("Received <" + message + ">");
            List<SetDlznici> dlznici = convertStringToObjects(message);

            dlznici.forEach(x -> x.setSync(true));
            setDlzniciRepository.saveAll(dlznici);

            String potvrdenie = convertListToJson(dlznici);
            sendMessageService.sendMessage("nove_podniky_potvrdenie", potvrdenie);

        } catch (JsonProcessingException ex){
            System.err.println(ERROR_PARSING_JSON);
            ex.printStackTrace();
        }
    }

    private List<SetDlznici> convertStringToObjects(String json) throws JsonProcessingException{
        return objectMapper.readValue(json, new TypeReference<List<SetDlznici>>(){});
    }

    private String convertListToJson(List<SetDlznici> entities) throws JsonProcessingException {
        return objectMapper.writeValueAsString(entities);
    }
}
