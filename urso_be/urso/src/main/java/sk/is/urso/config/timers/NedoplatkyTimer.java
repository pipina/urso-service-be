package sk.is.urso.config.timers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import sk.is.urso.repository.SpStavZiadostRepository;
import sk.is.urso.repository.ZpStavZiadostRepository;
import sk.is.urso.service.NedoplatkyService;
import sk.is.urso.util.LoggerUtils;

import java.util.List;

@Configuration
public class NedoplatkyTimer {

    private static final Logger logger = LoggerFactory.getLogger(NedoplatkyTimer.class);

    @Autowired
    private SpStavZiadostRepository spStavZiadostRepository;

    @Autowired
    private ZpStavZiadostRepository zpStavZiadostRepository;

    @Autowired
    private NedoplatkyService nedoplatkyService;

    @Autowired
    private LoggerUtils loggerUtils;

    @Scheduled(cron = "${csru.sp.upate-cron}")
    public void updateStavyZiadostiSp() {
        List<Long> ziadostiIds = spStavZiadostRepository.findAllToCheckState();
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - updateStavyZiadostiSp] Začínam kontrolu stavu žiadostí nedoplatkov SP.");
        for (Long ziadostId : ziadostiIds) {
            try {
                nedoplatkyService.updateStavuZiadostiSp(ziadostId);
            } catch (Exception ignored) {
                loggerUtils.log(LoggerUtils.LogType.ERROR, logger, "[SP][Method - updateStavyZiadostiSp] ERROR: " + ignored.getMessage() + "(Ziadost ID - " + ziadostId + ").");
            }
        }
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - updateStavyZiadostiSp] Ukončujem kontrolu stavu žiadostí nedoplatkov SP.");
    }

    @Scheduled(cron = "${csru.zp.upate-cron}")
    public void updateStavyZiadostiZp() {
        List<Long> ziadostiIds = zpStavZiadostRepository.findAllToCheckState();
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - updateStavyZiadostiZp] Začínam kontrolu stavu žiadostí nedoplatkov ZP.");
        for (Long ziadostId : ziadostiIds) {
            try {
                nedoplatkyService.updateStavuZiadostiZp(ziadostId);
            } catch (Exception ignored) {
                loggerUtils.log(LoggerUtils.LogType.ERROR, logger, "[ZP][Method - updateStavyZiadostiZp] ERROR: " + ignored.getMessage() + "(Ziadost ID - " + ziadostId + ").");
            }
        }
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - updateStavyZiadostiZp] Ukončujem kontrolu stavu žiadostí nedoplatkov ZP.");
    }
}
