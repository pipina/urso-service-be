package sk.is.urso.config.timers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.alfa.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import sk.is.urso.model.urso.SetDlznici;
import sk.is.urso.model.urso.SetDlzniciDto;
import sk.is.urso.model.urso.SetDlzniciObdobie;
import sk.is.urso.model.urso.SetDlzniciRefresh;
import sk.is.urso.model.urso.SetObdobieDto;
import sk.is.urso.repository.urso.SetDlzniciObdobieRepository;
import sk.is.urso.repository.urso.SetDlzniciRefreshRepository;
import sk.is.urso.repository.urso.SetDlzniciRepository;
import sk.is.urso.service.SendMessageService;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SendNedoplatkyUpdateTimer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SetDlzniciObdobieRepository setDlzniciObdobieRepository;

    @Autowired
    private SetDlzniciRefreshRepository setDlzniciRefreshRepository;

    @Autowired
    private SetDlzniciRepository setDlzniciRepository;

    @Autowired
    private SendMessageService sendMessageService;

    private static final String ACTIVEMQ_TOPIC_QUEUE = "aktualne_nedoplatky";
    private static final String ERROR_PARSING_TO_JSON = "Chyba pri spracovaní objektov do JSON formátu.";


    @Scheduled(cron = "${system.cron.send-updated-data}")
    private void poslatAktualneNedoplatky() {
        try {
            List<SetDlzniciObdobie> dlzniciObdobie = setDlzniciObdobieRepository.findAll();
            List<SetDlzniciRefresh> dlzniciRefresh = setDlzniciRefreshRepository.findAll();
            List<SetDlznici> setDlznici = setDlzniciRepository.findAll();

            List<SetObdobieDto> setObdobieDtos = mapObdobieToDto(dlzniciObdobie);
            List<SetDlzniciDto> setDlzniciDtos = mapDlzniciToDto(setDlznici);
            String obdobia = objectMapper.writeValueAsString(setObdobieDtos);
            String refresh = objectMapper.writeValueAsString(dlzniciRefresh);
            String dlznici = objectMapper.writeValueAsString(setDlzniciDtos);

            List<String> messagesList = new ArrayList<>();
            messagesList.add(obdobia);
            messagesList.add(refresh);
            messagesList.add(dlznici);

            String message = objectMapper.writeValueAsString(messagesList);
            sendMessageService.sendMessage(ACTIVEMQ_TOPIC_QUEUE, message);

        } catch (Exception exception){
            System.err.println(ERROR_PARSING_TO_JSON);
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_PARSING_TO_JSON);
        }
    }

    private List<SetObdobieDto> mapObdobieToDto(List<SetDlzniciObdobie> setDlzniciObdobieList){
        List<SetObdobieDto> setObdobieDtos = new ArrayList<>();
        for (SetDlzniciObdobie setDlzniciObdobie : setDlzniciObdobieList){
            SetObdobieDto setObdobieDto = new SetObdobieDto();
            setObdobieDto.setObdobieOd(setDlzniciObdobie.getObdobieOd());
            setObdobieDto.setObdobieDo(setDlzniciObdobie.getObdobieDo());
            setObdobieDto.setZdroj(setDlzniciObdobie.getZdroj());
            setObdobieDto.setTypDlznika(setDlzniciObdobie.getTypDlznika());
            setObdobieDto.setId(setDlzniciObdobie.getId());
            setObdobieDto.setZdroj(setDlzniciObdobie.getZdroj());
            setObdobieDto.setSetDlznici(setDlzniciObdobie.getSetDlznici().getId());
            setObdobieDtos.add(setObdobieDto);
        }
        return setObdobieDtos;
    }

    private List<SetDlzniciDto> mapDlzniciToDto(List<SetDlznici> setDlzniciList) {
        List<SetDlzniciDto> setDlzniciDtos = new ArrayList<>();
        for (SetDlznici dlznik : setDlzniciList) {
            SetDlzniciDto dlznikDto = new SetDlzniciDto();
            dlznikDto.setId(dlznik.getId());
            dlznikDto.setNazov(dlznik.getNazov());
            dlznikDto.setIco(dlznik.getIco());
            dlznikDto.setIdPo(dlznik.getIdPo());
            if (dlznik.getSetDlzniciRefresh() != null) {
                dlznikDto.setSetDlzniciRefresh(dlznik.getSetDlzniciRefresh().getId());
            }
            dlznikDto.setSync(dlznik.getSync());
            setDlzniciDtos.add(dlznikDto);
        }
        return setDlzniciDtos;
    }


}
