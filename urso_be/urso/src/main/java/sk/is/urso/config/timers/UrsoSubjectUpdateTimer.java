package sk.is.urso.config.timers;

import org.alfa.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sk.is.urso.enums.CsruStavZiadosti;
import sk.is.urso.enums.FsDruhDanePohladavky;
import sk.is.urso.enums.FsNedoplatok;
import sk.is.urso.enums.UrsoNedoplatokTyp;
import sk.is.urso.enums.UrsoSubjectStav;
import sk.is.urso.model.FsOsobaNedoplatok;
import sk.is.urso.model.FsOsobaZaznam;
import sk.is.urso.model.SpStavZiadost;
import sk.is.urso.model.UrsoSubjectStack;
import sk.is.urso.model.ZpStavZiadost;
import sk.is.urso.model.urso.SetDlznici;
import sk.is.urso.model.urso.SetDlzniciObdobie;
import sk.is.urso.model.urso.SetDlzniciRefresh;
import sk.is.urso.repository.urso.SetDlzniciObdobieRepository;
import sk.is.urso.repository.urso.SetDlzniciRefreshRepository;
import sk.is.urso.repository.urso.SetDlzniciRepository;
import sk.is.urso.repository.urso.UrsoSubjectStackRepository;
import sk.is.urso.rest.model.InstituciaEnum;
import sk.is.urso.rest.model.SubjektNedoplatokVstupnyDetail;
import sk.is.urso.service.NedoplatkyService;
import sk.is.urso.util.LoggerUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UrsoSubjectUpdateTimer {

    private static final Logger logger = LoggerFactory.getLogger(UrsoSubjectUpdateTimer.class);

    private static final String SPD = "1";
    private static final String COL = "2";
    private static final String NDS = "3";

    @Autowired
    private SetDlzniciObdobieRepository setDlzniciObdobieRepository;

    @Autowired
    private SetDlzniciRefreshRepository setDlzniciRefreshRepository;

    @Autowired
    private SetDlzniciRepository setDlzniciRepository;

    @Autowired
    private UrsoSubjectStackRepository ursoSubjectStackRepository;

    @Autowired
    private NedoplatkyService nedoplatkyService;

    @Autowired
    private LoggerUtils loggerUtils;

    @Scheduled(cron = "${csru.set.update-stack-cron}")
    public void updateFsAndSendSpZpRequests() {
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[FS/SP/ZP][Method - updateFsAndSendSpZpRequests] Zacinam aktualizáciu FS dlžníkov a posielam CSRU dopyty pre SP/ZP dlžníkov.");
        List<SetDlznici> dlznici = setDlzniciRepository.findAll();

        SetDlzniciRefresh setDlzniciRefreshFS = new SetDlzniciRefresh();
        setDlzniciRefreshFS.setSource((short)3);
        setDlzniciRefreshFS.setRecordModified(0);
        setDlzniciRefreshFS.setDateRefresh(new Date(System.currentTimeMillis()));
        setDlzniciRefreshRepository.save(setDlzniciRefreshFS);

        List<SetDlznici> subjekty = new ArrayList<>();
        for (SetDlznici subjekt : dlznici) {
            try {
                FsOsobaZaznam fsOsobaZaznam = nedoplatkyService.getNedoplatkyPreFS(
                        new SubjektNedoplatokVstupnyDetail()
                                .ico(subjekt.getIco()),
                        InstituciaEnum.FS);
                resolveNedoplatkyFs(subjekt, fsOsobaZaznam, setDlzniciRefreshFS);
                subjekty.add(subjekt);
            } catch (Exception ignore) {
            }
            try {
                nedoplatkyRequestSp(subjekt);
            } catch (Exception ignore) {
            }
            try {
                nedoplatkyRequestZp(subjekt);
            } catch (Exception ignore) {
            }
        }

        if (setDlzniciRefreshFS.getRecordModified() > 0){
            setDlzniciRefreshRepository.save(setDlzniciRefreshFS);
            setDlzniciRepository.saveAll(subjekty);
        } else {
            setDlzniciRefreshRepository.delete(setDlzniciRefreshFS);
        }
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[FS/SP/ZP][Method - updateFsAndSendSpZpRequests] Aktualizácia FS dlžníkov a posielanie CSRU dopytov pre SP/ZP dlžníkov úspešne ukončené.");
    }

    @Scheduled(cron = "${csru.set.update-records-cron}")
    public void processSpZpResponses() {
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP/ZP][Method - processSpZpResponses] Zacinam aktualizáciu SP/ZP dlžníkov.");
        try {
            nedoplatkyResponseSp();
        } catch (Exception ignore) {
        }
        try {
            nedoplatkyResponseZp();
        } catch (Exception ignore) {
        }
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP/ZP][Method - processSpZpResponses] Aktualizácia SP/ZP dlžníkov úspešne ukončená.");
    }

    public void nedoplatkyResponseSp() {
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - nedoplatkyResponseSp] Zacinam aktualizáciu SP dlžníkov.");
        List<UrsoSubjectStack> ursoSpSubjectStacks = ursoSubjectStackRepository.findAllSpWhereStavEqualsPrebieha();

        SetDlzniciRefresh setDlzniciRefresh = new SetDlzniciRefresh();
        setDlzniciRefresh.setSource((short)1);
        setDlzniciRefresh.setRecordModified(0);
        setDlzniciRefresh.setDateRefresh(new Date(System.currentTimeMillis()));
        setDlzniciRefreshRepository.save(setDlzniciRefresh);

        List<SetDlznici> subjekty = new ArrayList<>();
        for (UrsoSubjectStack ursoSubjectStack : ursoSpSubjectStacks) {
            SetDlznici subjekt = ursoSubjectStack.getSetDlznici();
            SpStavZiadost spStavZiadost = nedoplatkyService.getNedoplatkyPreSP(
                    new SubjektNedoplatokVstupnyDetail()
                            .ico(subjekt.getIco())
                            .nazovSpolocnosti(subjekt.getNazov()),
                    InstituciaEnum.SP);

            if (spStavZiadost.getStav().equals(CsruStavZiadosti.PREBIEHA_SPRACOVANIE)) {
                continue;
            } else if (spStavZiadost.getStav().equals(CsruStavZiadosti.SPRACOVANIE_USPESNE_UKONCENE)) {
                resolveNedoplatkySp(subjekt, spStavZiadost, setDlzniciRefresh);
                ursoSubjectStack.setUrsoSubjectStav(UrsoSubjectStav.SPRACOVANE);
                ursoSubjectStackRepository.save(ursoSubjectStack);
            } else {
                ursoSubjectStack.setUrsoSubjectStav(UrsoSubjectStav.CHYBA);
                ursoSubjectStackRepository.save(ursoSubjectStack);
                loggerUtils.log(LoggerUtils.LogType.ERROR, logger, "[SP] set_dlznici identifikátor: " + subjekt.getId() + " ;Návratový kód stavu: " + spStavZiadost.getNavratovyKodStavu().getValue() + "; Chybová hláška stavu: " + spStavZiadost.getChybovaHlaskaStavu() + ";");
            }
            subjekty.add(subjekt);
        }

        if (setDlzniciRefresh.getRecordModified() > 0){
            setDlzniciRefreshRepository.save(setDlzniciRefresh);
            setDlzniciRepository.saveAll(subjekty);
        } else {
            setDlzniciRefreshRepository.delete(setDlzniciRefresh);
        }
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - nedoplatkyResponseSp] Aktualizáciu SP dlžníkov úspešne ukončená.");
    }

    public void nedoplatkyResponseZp() {
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - nedoplatkyResponseZp] Zacinam aktualizáciu ZP dlžníkov.");
        List<UrsoSubjectStack> ursoZpSubjectStacks = ursoSubjectStackRepository.findAllZpWhereStavEqualsPrebieha();

        SetDlzniciRefresh setDlzniciRefresh = new SetDlzniciRefresh();
        setDlzniciRefresh.setSource((short)2);
        setDlzniciRefresh.setRecordModified(0);
        setDlzniciRefresh.setDateRefresh(new Date(System.currentTimeMillis()));
        setDlzniciRefreshRepository.save(setDlzniciRefresh);

        List<SetDlznici> subjekty = new ArrayList<>();
        for (UrsoSubjectStack ursoSubjectStack : ursoZpSubjectStacks) {
            SetDlznici subjekt = ursoSubjectStack.getSetDlznici();
            ZpStavZiadost zpStavZiadost = nedoplatkyService.getNedoplatkyPreZP(
                    new SubjektNedoplatokVstupnyDetail()
                            .ico(subjekt.getIco())
                            .nazovSpolocnosti(subjekt.getNazov()),
                    InstituciaEnum.ZP);

            if (zpStavZiadost.getStav().equals(CsruStavZiadosti.PREBIEHA_SPRACOVANIE)) {
                continue;
            } else if (zpStavZiadost.getStav().equals(CsruStavZiadosti.SPRACOVANIE_USPESNE_UKONCENE)) {
                resolveNedoplatkyZp(subjekt, zpStavZiadost, setDlzniciRefresh);
                ursoSubjectStack.setUrsoSubjectStav(UrsoSubjectStav.SPRACOVANE);
                ursoSubjectStackRepository.save(ursoSubjectStack);
            } else {
                ursoSubjectStack.setUrsoSubjectStav(UrsoSubjectStav.CHYBA);
                ursoSubjectStackRepository.save(ursoSubjectStack);
                loggerUtils.log(LoggerUtils.LogType.ERROR, logger, "[ZP] set_dlznici identifikátor: " + subjekt.getId() + " ;Návratový kód stavu: " + zpStavZiadost.getNavratovyKodStavu().getValue() + "; Chybová hláška stavu: " + zpStavZiadost.getChybovaHlaskaStavu() + ";");
            }
            subjekty.add(subjekt);
        }

        if (setDlzniciRefresh.getRecordModified() > 0){
            setDlzniciRefreshRepository.save(setDlzniciRefresh);
            setDlzniciRepository.saveAll(subjekty);
        } else {
            setDlzniciRefreshRepository.delete(setDlzniciRefresh);
        }
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - nedoplatkyResponseZp] Aktualizácia ZP dlžníkov úspešne ukončená.");
    }

    private void resolveNedoplatkyFs(SetDlznici subjekt, FsOsobaZaznam osobaZaznam, SetDlzniciRefresh setDlzniciRefresh) {
        Map<String, SetDlzniciObdobie> obdobiaDlzoby = getObdobiaDzlobyFs(subjekt);

        for (FsOsobaNedoplatok fsOsobaNedoplatok : osobaZaznam.getNedoplatky()) {
            FsNedoplatok nedoplatok = fsOsobaNedoplatok.getNedoplatok();

            if (nedoplatok.equals(FsNedoplatok.MA_NEDOPLATOK)) {
                resolveFsMaNedoplatok(fsOsobaNedoplatok, obdobiaDlzoby, subjekt, setDlzniciRefresh);
            } else if (nedoplatok.equals(FsNedoplatok.NEMA_NEDOPLATOK)) {
                resolveFsNemaNedoplatok(fsOsobaNedoplatok, obdobiaDlzoby, setDlzniciRefresh);
            }
        }
        subjekt.setSetDlzniciRefresh(setDlzniciRefresh);
    }

    private Map<String, SetDlzniciObdobie> getObdobiaDzlobyFs(SetDlznici subjekt) {
        return subjekt.getSetDlzniciObdobieList()
                .stream()
                .filter(obdobie -> Objects.equals(obdobie.getZdroj(), (short) 3) && obdobie.getObdobieDo() == null)
                .collect(Collectors.toMap(
                        SetDlzniciObdobie::getTypDlznika, // Key mapper function
                        obdobie -> obdobie,               // Value mapper function
                        (existing, replacement) -> existing // Merge function in case of key collision
                ));
    }

    private SetDlzniciObdobie getObdobieByDruhPohladavky(FsDruhDanePohladavky druhDanePohladavky, Map<String, SetDlzniciObdobie> obdobiaDlzoby) {
        switch(druhDanePohladavky) {
            case SPD -> {
                return obdobiaDlzoby.get(SPD);
            }
            case COL -> {
                return obdobiaDlzoby.get(COL);
            }
            case NDS -> {
                return obdobiaDlzoby.get(NDS);
            }
            default -> {
                return null;
            }
        }
    }

    private void resolveFsNemaNedoplatok(FsOsobaNedoplatok fsOsobaNedoplatok, Map<String, SetDlzniciObdobie> obdobiaDlzoby, SetDlzniciRefresh setDlzniciRefresh) {
        SetDlzniciObdobie obdobie = getObdobieByDruhPohladavky(fsOsobaNedoplatok.getDruhDanePohladavky(), obdobiaDlzoby);
        if (obdobie != null) {
            obdobie.setObdobieDo(new Date());
            setDlzniciObdobieRepository.save(obdobie);
            setDlzniciRefresh.setRecordModified(setDlzniciRefresh.getRecordModified() + 1);
        }
    }

    private void resolveFsMaNedoplatok(FsOsobaNedoplatok fsOsobaNedoplatok, Map<String, SetDlzniciObdobie> obdobiaDlzoby, SetDlznici subjekt, SetDlzniciRefresh setDlzniciRefresh) {
        SetDlzniciObdobie obdobie = getObdobieByDruhPohladavky(fsOsobaNedoplatok.getDruhDanePohladavky(), obdobiaDlzoby);
        if (obdobie == null) {
            obdobie = new SetDlzniciObdobie();
        }
        LocalDate datumNedoplatku = DateUtils.toLocalDate(fsOsobaNedoplatok.getDatumNedoplatku());
        LocalDate obdobieOd = DateUtils.toLocalDate(obdobie.getObdobieOd());
        if (obdobieOd == null || !obdobieOd.equals(datumNedoplatku)) {
            obdobie.setZdroj((short) 3);
            obdobie.setSetDlznici(subjekt);

            switch(fsOsobaNedoplatok.getDruhDanePohladavky()) {
                case SPD -> obdobie.setTypDlznika(SPD);
                case COL -> obdobie.setTypDlznika(COL);
                case NDS -> obdobie.setTypDlznika(NDS);
            }
            obdobie.setObdobieOd(fsOsobaNedoplatok.getDatumNedoplatku());
            setDlzniciObdobieRepository.save(obdobie);
            setDlzniciRefresh.setRecordModified(setDlzniciRefresh.getRecordModified() + 1);
        }
    }

    private void resolveNedoplatkySp(SetDlznici subjekt, SpStavZiadost spStavZiadost, SetDlzniciRefresh setDlzniciRefresh) {
        Boolean maNedoplatok = spStavZiadost.getMaNedoplatok();
        List<SetDlzniciObdobie> obdobiaDlzobySP = subjekt.getSetDlzniciObdobieList()
                .stream()
                .filter(obdobie -> Objects.equals(obdobie.getZdroj(), (short) 1) && obdobie.getObdobieDo() == null)
                .toList();

        if (maNedoplatok != null) {
            if (!obdobiaDlzobySP.isEmpty()) {
                if (maNedoplatok.equals(Boolean.FALSE)) {
                    for (SetDlzniciObdobie obdobie : obdobiaDlzobySP) {
                        obdobie.setObdobieDo(spStavZiadost.getResultDate());
                        setDlzniciObdobieRepository.save(obdobie);
                    }
                    setDlzniciRefresh.setRecordModified(setDlzniciRefresh.getRecordModified() + 1);
                }
            } else {
                if (maNedoplatok.equals(Boolean.TRUE)) {
                    SetDlzniciObdobie setDlzniciObdobieSP = new SetDlzniciObdobie();
                    setDlzniciObdobieSP.setZdroj((short) 1);
                    setDlzniciObdobieSP.setObdobieOd(spStavZiadost.getResultDate());
                    setDlzniciObdobieSP.setSetDlznici(subjekt);
                    setDlzniciObdobieRepository.save(setDlzniciObdobieSP);
                    setDlzniciRefresh.setRecordModified(setDlzniciRefresh.getRecordModified() + 1);
                }
            }
        }
        subjekt.setSetDlzniciRefresh(setDlzniciRefresh);
    }

    private void resolveNedoplatkyZp(SetDlznici subjekt, ZpStavZiadost zpStavZiadost, SetDlzniciRefresh setDlzniciRefresh) {
        Boolean maNedoplatok = zpStavZiadost.getMaNedoplatok();
        Date currentDate = new Date();
        List<SetDlzniciObdobie> obdobiaDlzobySP = subjekt.getSetDlzniciObdobieList()
                .stream()
                .filter(obdobie -> Objects.equals(obdobie.getZdroj(), (short) 2) && obdobie.getObdobieDo() == null)
                .toList();

        if (maNedoplatok != null) {
            if (!obdobiaDlzobySP.isEmpty()) {
                if (maNedoplatok.equals(Boolean.FALSE)) {
                    for (SetDlzniciObdobie obdobie : obdobiaDlzobySP) {
                        obdobie.setObdobieDo(currentDate);
                        setDlzniciObdobieRepository.save(obdobie);
                    }
                    setDlzniciRefresh.setRecordModified(setDlzniciRefresh.getRecordModified() + 1);
                }
            } else {
                if (maNedoplatok.equals(Boolean.TRUE)) {
                    SetDlzniciObdobie setDlzniciObdobieSP = new SetDlzniciObdobie();
                    setDlzniciObdobieSP.setZdroj((short) 2);
                    setDlzniciObdobieSP.setObdobieOd(zpStavZiadost.getDate());
                    setDlzniciObdobieSP.setSetDlznici(subjekt);
                    setDlzniciObdobieRepository.save(setDlzniciObdobieSP);
                    setDlzniciRefresh.setRecordModified(setDlzniciRefresh.getRecordModified() + 1);
                }
            }
        }
        subjekt.setSetDlzniciRefresh(setDlzniciRefresh);
    }

    public void nedoplatkyRequestSp(SetDlznici subjekt) {
        SpStavZiadost spStavZiadost = nedoplatkyService.getNedoplatkyPreSP(
                new SubjektNedoplatokVstupnyDetail()
                        .ico(subjekt.getIco())
                        .nazovSpolocnosti(subjekt.getNazov()),
                InstituciaEnum.SP);

        Long requestId = spStavZiadost.getRequestId();
        if (ursoSubjectStackRepository.existsByRequestId(requestId)) {
            return;
        }
        UrsoSubjectStack ursoSubjectStack = new UrsoSubjectStack();
        ursoSubjectStack.setRequestId(spStavZiadost.getRequestId());
        ursoSubjectStack.setSetDlznici(subjekt);
        ursoSubjectStack.setDoplnujucaTextovaInformacia(spStavZiadost.getChybovaHlaskaOperacie());
        ursoSubjectStack.setCasVytvorenia(new Date());
        ursoSubjectStack.setUrsoNedoplatokTyp(UrsoNedoplatokTyp.SP);
        if (spStavZiadost.getStav().equals(CsruStavZiadosti.PREBIEHA_SPRACOVANIE)) {
            ursoSubjectStack.setUrsoSubjectStav(UrsoSubjectStav.PREBIEHA);
        } else {
            ursoSubjectStack.setUrsoSubjectStav(UrsoSubjectStav.CHYBA);
        }
        ursoSubjectStackRepository.save(ursoSubjectStack);
    }

    public void nedoplatkyRequestZp(SetDlznici subjekt) {
        ZpStavZiadost zpStavZiadost = nedoplatkyService.getNedoplatkyPreZP(
                new SubjektNedoplatokVstupnyDetail()
                        .ico(subjekt.getIco()),
                InstituciaEnum.ZP);

        Long requestId = zpStavZiadost.getRequestId();
        if (ursoSubjectStackRepository.existsByRequestId(requestId)) {
            return;
        }
        UrsoSubjectStack ursoSubjectStack = new UrsoSubjectStack();
        ursoSubjectStack.setRequestId(requestId);
        ursoSubjectStack.setSetDlznici(subjekt);
        ursoSubjectStack.setDoplnujucaTextovaInformacia(zpStavZiadost.getChybovaHlaskaOperacie());
        ursoSubjectStack.setCasVytvorenia(new Date());
        ursoSubjectStack.setUrsoNedoplatokTyp(UrsoNedoplatokTyp.ZP);
        if (zpStavZiadost.getStav().equals(CsruStavZiadosti.PREBIEHA_SPRACOVANIE)) {
            ursoSubjectStack.setUrsoSubjectStav(UrsoSubjectStav.PREBIEHA);
        } else {
            ursoSubjectStack.setUrsoSubjectStav(UrsoSubjectStav.CHYBA);
        }
        ursoSubjectStackRepository.save(ursoSubjectStack);
    }
}
