package sk.is.urso.service;

import org.alfa.exception.CommonException;
import org.alfa.utils.DateUtils;
import org.alfa.utils.XmlUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sk.is.urso.config.csru.CsruEndpoint;
import sk.is.urso.csru.fs.nedoplatky.DanNedoplatokPohladavkaCType;
import sk.is.urso.csru.fs.nedoplatky.DanSubjCType;
import sk.is.urso.csru.fs.nedoplatky.FSDanoveNedoplatkyCType;
import sk.is.urso.csru.sp.nedoplatky.CheckArrearsResultServiceResType;
import sk.is.urso.csru.sp.nedoplatky.ResultInfoType;
import sk.is.urso.csru.zp.nedoplatky.EvidenciaCType;
import sk.is.urso.csru.zp.nedoplatky.OdvodovaPovinnostZPCType;
import sk.is.urso.csru.zp.nedoplatky.PlnenieOdvodovejPovinnosti;
import sk.is.urso.enums.CsruNavratovyKodOperacie;
import sk.is.urso.enums.CsruStavZiadosti;
import sk.is.urso.enums.FsDruhDanePohladavky;
import sk.is.urso.enums.FsNedoplatok;
import sk.is.urso.enums.FsNedoplatokChybovyKod;
import sk.is.urso.enums.SpNedoplatok;
import sk.is.urso.enums.ZpNedoplatok;
import sk.is.urso.enums.ZpPoistovna;
import sk.is.urso.enums.ZpPopisKoduVysledkuSpracovania;
import sk.is.urso.model.FsOsobaNedoplatok;
import sk.is.urso.model.FsOsobaZaznam;
import sk.is.urso.model.SpOsobaZaznam;
import sk.is.urso.model.SpStavZiadost;
import sk.is.urso.model.SpVysledokKontroly;
import sk.is.urso.model.SpVystupnySubor;
import sk.is.urso.model.ZpOsobaZaznam;
import sk.is.urso.model.ZpStavZiadost;
import sk.is.urso.model.ZpVysledokKontroly;
import sk.is.urso.model.ZpVystupnySubor;
import sk.is.urso.model.ZpZiadatelia;
import sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetStatusRequestCType;
import sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetStatusResponseCType;
import sk.is.urso.model.csru.api.async.common.FileCType;
import sk.is.urso.model.csru.api.sync.GetConsolidatedDataServiceSync.GetConsolidatedDataRequestCType;
import sk.is.urso.model.csru.api.sync.GetConsolidatedDataServiceSync.GetConsolidatedDataResponseCType;
import sk.is.urso.model.csru.api.sync.GetConsolidatedDataServiceSync.ObjectFactory;
import sk.is.urso.model.csru.api.sync.common.ParameterCType;
import sk.is.urso.model.csru.api.sync.common.ParameterListCType;
import sk.is.urso.repository.FsOsobaNedoplatokRepository;
import sk.is.urso.repository.FsOsobaZaznamRepository;
import sk.is.urso.repository.SpOsobaZaznamRepository;
import sk.is.urso.repository.SpStavZiadostRepository;
import sk.is.urso.repository.SpVysledokKontrolyRepository;
import sk.is.urso.repository.SpVystupnySuborRepository;
import sk.is.urso.repository.ZpOsobaZaznamRepository;
import sk.is.urso.repository.ZpStavZiadostRepository;
import sk.is.urso.repository.ZpVysledokKontrolyRepository;
import sk.is.urso.repository.ZpVystupnySuborRepository;
import sk.is.urso.repository.ZpZiadateliaRepository;
import sk.is.urso.rest.model.InstituciaEnum;
import sk.is.urso.rest.model.SubjektNedoplatokVstupnyDetail;
import sk.is.urso.util.LoggerUtils;

import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class NedoplatkyService {

    private static final Logger logger = LoggerFactory.getLogger(NedoplatkyService.class);

    private static final String ERROR_RESULT_XML_EVALUATION = "Vyskytla sa chyba pri spracovávaní odpovede z CSRÚ.";
    private static final String ERROR_WRONG_ATTRIBUTES_SP_1 = "Neboli zadané spravne atribúty. (Vyžaduje sa kombinácia rodneCislo, meno, priezvisko alebo ico, nazovSpolocnosti)";
    private static final String ERROR_WRONG_ATTRIBUTES_SP_2 = "Neboli zadané spravne atribúty. (Vyžaduje sa kombinácia rodneCislo, meno, priezvisko)";
    private static final String ERROR_WRONG_ATTRIBUTES_FS = "Neboli zadané spravne atribúty. (Vyžaduje sa práve jeden z nasledujúcich atribútov: rodneCislo, ico, dic)";
    private static final String ERROR_WRONG_ATTRIBUTES_ZP = "Neboli zadané spravne atribúty. (Pre fyzickú osobu sa vyžaduje rodneCislo alebo meno, priezvisko, datumNarodania. Pre právnicku osobu sa vyžaduje ico)";

    private static final String ERROR_PARSING_CSRU_GENERATED_FILE = "Chyba pri parsovaní obsahu súboru.";

    private static final String DOVERA = "DÔVERA zdravotná poisťovňa";
    private static final String VSZP = "VšZP";
    private static final String UZP = "UZP";
    private static final String DIC = "DIC";
    private static final String ICO = "ICO";
    private static final String RC = "RC";
    private static final String MENO = "MENO";
    private static final String PRIEZVISKO = "PRIEZVISKO";
    private static final String DATUM_NARODENIA = "DATUM_NARODENIA";
    private static final String NAZOV_SPOLOCNOSTI = "NAZOV_SPOLOCNOSTI";
    private static final String VYBAVUJE_OSOBA = "VYBAVUJE_OSOBA";
    private static final String VYBAVUJE_EMAIL = "VYBAVUJE_EMAIL";
    private static final String VYBAVUJE_TELEFON = "VYBAVUJE_TELEFON";
    private static final String KOD_INSTITUCIA = "KOD_INSTITUCIA";
    private static final String JEDNACIE_CISLO = "JEDNACIE_CISLO";
    private static final String CAS_PODANIA = "CAS_PODANIA";
    private static final String FS_NEDOPLATKY = "FS_NEDOPLATKY";
    private static final String SP_NEDOPLATKY = "SP_NEDOPLATKY_Oversi";
    private static final String ZP_NEDOPLATKY = "ZP_Odvodova_Povinnost";
    private static final String MA_NEDOPLATKY = "Má nedoplatky";
    private static final String MA_NEDOPLATKY_NESPLNENIE_POVINNOSTI = "Má nedoplatky – nesplnenie povinnosti";

    private static final String NDS = "NDS";
    private static final String SPD = "SPD";
    private static final String COL = "COL";

    @Autowired
    private LoggerUtils loggerUtils;

    @Autowired
    private CsruEndpoint csruEndpoint;

    @Autowired
    private SpStavZiadostRepository spStavZiadostRepository;

    @Autowired
    private SpOsobaZaznamRepository spOsobaZaznamRepository;

    @Autowired
    private FsOsobaZaznamRepository fsOsobaZaznamRepository;

    @Autowired
    private FsOsobaNedoplatokRepository fsOsobaNedoplatokRepository;

    @Autowired
    private ZpStavZiadostRepository zpStavZiadostRepository;

    @Autowired
    private ZpOsobaZaznamRepository zpOsobaZaznamRepository;

    @Autowired
    private ZpZiadateliaRepository zpZiadateliaRepository;

    @Autowired
    private SpVystupnySuborRepository spVystupnySuborRepository;

    @Autowired
    private SpVysledokKontrolyRepository spVysledokKontrolyRepository;

    @Autowired
    private ZpVystupnySuborRepository zpVystupnySuborRepository;

    @Autowired
    private ZpVysledokKontrolyRepository zpVysledokKontrolyRepository;

    @Autowired
    private SftpService sftpService;

    @Value("${integration.csru.sftp.host}")
    private String sftpHost;

    @Value("${integration.csru.sftp.port}")
    private Integer sftpPort;

    @Value("${integration.csru.sftp.username}")
    private String sftpUsername;

    @Value("${integration.csru.sftp.password}")
    private String sftpPassword;

    @Value("${integration.csru.proxy.hostname}")
    private String proxyHost;

    @Value("${integration.csru.proxy.port}")
    private Integer proxyPort;

    @Value("${integration.csru.proxy}")
    private Boolean isProxyEnabled;

    @Value("${integration.csru.ovmIsId}")
    private String ovmIsId;

    @Value("${csru.sp.validity-days}")
    private Integer spValidDays;

    @Value("${csru.zp.validity-days}")
    private Integer zpValidDays;

    @Value("${csru.fs.validity-days}")
    private Integer fsValidDays;

    @Value("${csru.zp.vybavuje-osoba}")
    private String zpVybavujeOsoba;

    @Value("${csru.zp.vybavuje-email}")
    private String zpVybavujeEmail;

    @Value("${csru.zp.vybavuje-telefon}")
    private String zpVybavujeTelefon;

    @Value("${csru.zp.kod-institucia}")
    private String kodInstitucia;

    @Transactional
    public ZpStavZiadost getNedoplatkyPreZP(SubjektNedoplatokVstupnyDetail subjektNedoplatokVstupnyDetail, InstituciaEnum institucia) {
        Date currentDate = new Date();
        checkInputZP(subjektNedoplatokVstupnyDetail);
        Map<String, String> parameterMap = getParameterMap(subjektNedoplatokVstupnyDetail, institucia, currentDate);

        ZpOsobaZaznam osoba = getZpOsobaZaznam(subjektNedoplatokVstupnyDetail, parameterMap);
        osoba = zpOsobaZaznamRepository.save(osoba);
        setZiadatelZp(osoba, currentDate);

        ZpStavZiadost spStavZiadost = getValidZpStavZiadost(osoba, currentDate);
        if (spStavZiadost != null) {
            return spStavZiadost;
        }
        JAXBElement<sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataRequestCType> request =
                createSpZpRequest(parameterMap, institucia);
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - getNedoplatkyPreZP] Volám CSRU sluzby GetConsolidatedDataSyncRequest");
        sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataResponseCType response =
                csruEndpoint.sendGetConsolidatedDataAsyncRequest(request);
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - getNedoplatkyPreZP] Volanie CSRU sluzby GetConsolidatedDataSyncRequest úspešné");
        return saveZpStavZiadost(response, osoba, currentDate);
    }

    @Transactional
    public SpStavZiadost getNedoplatkyPreSP(SubjektNedoplatokVstupnyDetail subjektNedoplatokVstupnyDetail, InstituciaEnum institucia) {
        Date currentDate = new Date();
        checkInputSp(subjektNedoplatokVstupnyDetail);
        Map<String, String> parameterMap = getParameterMap(subjektNedoplatokVstupnyDetail, institucia, currentDate);

        SpOsobaZaznam osoba = getSpOsobaZaznam(subjektNedoplatokVstupnyDetail, parameterMap);
        osoba = spOsobaZaznamRepository.save(osoba);

        SpStavZiadost spStavZiadost = getValidSpStavZiadost(osoba, currentDate);
        if (spStavZiadost != null) {
            return spStavZiadost;
        }
        JAXBElement<sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataRequestCType> request =
                createSpZpRequest(parameterMap, institucia);
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - getNedoplatkyPreSP] Volám CSRU sluzby GetConsolidatedDataSyncRequest");
        sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataResponseCType response =
                csruEndpoint.sendGetConsolidatedDataAsyncRequest(request);
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - getNedoplatkyPreSP] Volanie CSRU sluzby GetConsolidatedDataSyncRequest úspešné");
        return saveZpStavZiadost(response, osoba, currentDate);
    }

    @Transactional
    public FsOsobaZaznam getNedoplatkyPreFS(SubjektNedoplatokVstupnyDetail subjektNedoplatokVstupnyDetail, InstituciaEnum institucia) {
        Date currentDate = new Date();
        checkInputFS(subjektNedoplatokVstupnyDetail);
        Map<String, String> parameterMap = getParameterMap(subjektNedoplatokVstupnyDetail, institucia, currentDate);

        FsOsobaZaznam osoba = findFsOsobaZaznamInDb(subjektNedoplatokVstupnyDetail);
        if (osoba != null) {
            Hibernate.initialize(osoba.getNedoplatky());
            return osoba;
        }
        JAXBElement<GetConsolidatedDataRequestCType> request = createFsRequest(parameterMap);
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[FS][Method - getNedoplatkyPreFS] Volám CSRU sluzby GetConsolidatedDataSyncRequest");
        GetConsolidatedDataResponseCType response = csruEndpoint.sendGetConsolidatedDataSyncRequest(request);
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[FS][Method - getNedoplatkyPreFS] Volanie CSRU sluzby GetConsolidatedDataSyncRequest úspešné");

        return saveFsOsobaZaznamAndNedoplatky(response, currentDate);
    }

    private void setZiadatelZp(ZpOsobaZaznam osoba, Date currentDate) {
        ZpZiadatelia ziadatel = new ZpZiadatelia();
        ziadatel.setOsobaZaznam(osoba);
        ziadatel.setCasPoziadavky(currentDate);
        zpZiadateliaRepository.save(ziadatel);
    }

    private FsOsobaZaznam findFsOsobaZaznamInDb(SubjektNedoplatokVstupnyDetail subjektNedoplatokVstupnyDetail) {
        if (subjektNedoplatokVstupnyDetail.getRodneCislo() != null) {
            return fsOsobaZaznamRepository.findValidByRodneCislo(subjektNedoplatokVstupnyDetail.getRodneCislo())
                    .orElse(null);
        }
        if (subjektNedoplatokVstupnyDetail.getIco() != null) {
            return fsOsobaZaznamRepository.findValidByIco(subjektNedoplatokVstupnyDetail.getIco())
                    .orElse(null);
        }
        return fsOsobaZaznamRepository.findValidByDic(subjektNedoplatokVstupnyDetail.getDic())
                .orElse(null);
    }

    private FsOsobaZaznam saveFsOsobaZaznamAndNedoplatky(GetConsolidatedDataResponseCType response, Date currentDate) {
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[FS][Method - saveFsOsobaZaznamAndNedoplatky] Ukladám osobu FS");

        FsOsobaZaznam osoba = new FsOsobaZaznam();
        LocalDate platnostDo = LocalDate.now().plusDays(fsValidDays);

        osoba.setOvmTransactionId(response.getOvmTransactionId());
        osoba.setOvmCorrelationId(response.getOvmCorrelationId());
        osoba.setCsruTransactionId(response.getCsruTransactionId());
        osoba.setChybovaHlaskaOperacie(response.getErrorMessage());
        osoba.setCasPodania(currentDate);
        osoba.setPlatnostDo(DateUtils.toDate(platnostDo));

        switch (response.getResultCode()) {
            case 0 -> osoba.setNavratovyKodOperacie(CsruNavratovyKodOperacie.OK);
            case 1 -> osoba.setNavratovyKodOperacie(CsruNavratovyKodOperacie.CHYBA_OVERENIA_OPRAVNENI);
            case 2 -> osoba.setNavratovyKodOperacie(CsruNavratovyKodOperacie.CHYBA_VALIDACIE_VSTUPNYCH_PARAMETROV);
            case 3 -> osoba.setNavratovyKodOperacie(CsruNavratovyKodOperacie.INTERNA_CHYBA);
            default -> osoba.setNavratovyKodOperacie(CsruNavratovyKodOperacie.NEZNAMY);
        }
        FSDanoveNedoplatkyCType danoveNedoplatky = processResponse(response, "<FSDanoveNedoplatky",
                "<DanSubj", FSDanoveNedoplatkyCType.class);
        DanSubjCType danovySubjekt = danoveNedoplatky.getDanSubj();

        String ico = danovySubjekt.getICO();
        String rc = danovySubjekt.getRodneCis();

        osoba.setMeno(danovySubjekt.getMeno());
        osoba.setPriezvisko(danovySubjekt.getPriezvisko());
        osoba.setIco(ico);
        osoba.setDic(danovySubjekt.getDIC());
        osoba.setRodneCislo(rc);
        osoba.setNazovSpolocnosti(danovySubjekt.getObchMenoNazov());
        osoba.setNedoplatky(getNedoplatkyFs(danovySubjekt, osoba));

        osoba = fsOsobaZaznamRepository.save(osoba);
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[FS][Method - saveFsOsobaZaznamAndNedoplatky] Osoba FS úspešne uložená (IČO = " + ico + ", Rodné číslo = " + rc + ")");

        return osoba;
    }

    private List<FsOsobaNedoplatok> getNedoplatkyFs(DanSubjCType danovySubjekt, FsOsobaZaznam osoba) {
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[FS][Method - getNedoplatkyFs] Ukladám nedoplatky pre osobu FS (IČO = " + osoba.getIco() + ", Rodné číslo = " + osoba.getRodneCislo() + ")");
        List<FsOsobaNedoplatok> osobaNedoplatky = new ArrayList<>();
        List<DanNedoplatokPohladavkaCType> nedoplatky = danovySubjekt.getDanNedoplatokPohladavka();
        osoba.setMaNedoplatok(null);
        for (DanNedoplatokPohladavkaCType nedoplatok : nedoplatky) {
            FsOsobaNedoplatok osobaNedoplatok = new FsOsobaNedoplatok();

            osobaNedoplatok.setNedolpatokChybovaSprava(nedoplatok.getErrorMessage());
            osobaNedoplatok.setMena(nedoplatok.getMena());
            osobaNedoplatok.setDatumNedoplatku(DateUtils.toDate(nedoplatok.getDatum()));

            BigDecimal vyskaNedoplatku = nedoplatok.getVyska();
            if (vyskaNedoplatku != null) {
                osobaNedoplatok.setVyskaNedoplatku(vyskaNedoplatku.toString());
            }
            handleFsErrorCode(nedoplatok.getErrorCode(), osobaNedoplatok);
            handleFsNedoplatok(nedoplatok.getNedoplatok().intValue(), osobaNedoplatok, osoba);
            handleFsDruhPohladavky(nedoplatok.getDruhPohladavky().getTyp(), osobaNedoplatok);
            osobaNedoplatok.setOsobaZaznam(osoba);
            osobaNedoplatky.add(fsOsobaNedoplatokRepository.save(osobaNedoplatok));
        }
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[FS][Method - getNedoplatkyFs] Nedoplatky pre osobu FS úspešne uložené (IČO = " + osoba.getIco() + ", Rodné číslo = " + osoba.getRodneCislo() + ")");
        return osobaNedoplatky;
    }

    private void handleFsErrorCode(String errorCode, FsOsobaNedoplatok osobaNedoplatok) {
        switch (errorCode) {
            case "0" -> osobaNedoplatok.setNedoplatokChybovyKod(FsNedoplatokChybovyKod.OK);
            case "2" ->
                    osobaNedoplatok.setNedoplatokChybovyKod(FsNedoplatokChybovyKod.SUBJEKT_MA_DUPLICITU_V_EVIDENCII);
            default -> osobaNedoplatok.setNedoplatokChybovyKod(FsNedoplatokChybovyKod.NEZNAMY);
        }
    }

    private void handleFsNedoplatok(int value, FsOsobaNedoplatok osobaNedoplatok, FsOsobaZaznam osoba) {
        switch (value) {
            case 0 -> {
                osobaNedoplatok.setNedoplatok(FsNedoplatok.NEMA_NEDOPLATOK);
               if (osoba.getMaNedoplatok() == null || !osoba.getMaNedoplatok()) {
                    osoba.setMaNedoplatok(false);
                }
            }
            case 1 -> {
                osobaNedoplatok.setNedoplatok(FsNedoplatok.MA_NEDOPLATOK);
                osoba.setMaNedoplatok(true);
            }
            case 2 -> osobaNedoplatok.setNedoplatok(FsNedoplatok.CHYBA);
            default -> osobaNedoplatok.setNedoplatok(FsNedoplatok.NEZNAMY);
        }
    }

    private void handleFsDruhPohladavky(String typ, FsOsobaNedoplatok osobaNedoplatok) {
        switch (typ) {
            case NDS -> osobaNedoplatok.setDruhDanePohladavky(FsDruhDanePohladavky.NDS);
            case SPD -> osobaNedoplatok.setDruhDanePohladavky(FsDruhDanePohladavky.SPD);
            case COL -> osobaNedoplatok.setDruhDanePohladavky(FsDruhDanePohladavky.COL);
            default -> osobaNedoplatok.setDruhDanePohladavky(FsDruhDanePohladavky.NEZNAMY);
        }
    }

    private void checkInputZP(SubjektNedoplatokVstupnyDetail subjektNedoplatokVstupnyDetail) {
        String rodneCislo = subjektNedoplatokVstupnyDetail.getRodneCislo();
        String ico = subjektNedoplatokVstupnyDetail.getIco();
        String meno = subjektNedoplatokVstupnyDetail.getMeno();
        String priezvisko = subjektNedoplatokVstupnyDetail.getPriezvisko();
        LocalDate datumNarodenia = subjektNedoplatokVstupnyDetail.getDatumNarodenia();

        if (rodneCislo == null && ico == null && (meno == null || priezvisko == null || datumNarodenia == null)) {
            throw new CommonException(HttpStatus.BAD_REQUEST, ERROR_WRONG_ATTRIBUTES_ZP);
        }
        if (rodneCislo != null && (ico != null || (meno != null || priezvisko != null || datumNarodenia != null))) {
            throw new CommonException(HttpStatus.BAD_REQUEST, ERROR_WRONG_ATTRIBUTES_ZP);
        }
        if (ico != null && (meno != null || priezvisko != null || datumNarodenia != null)) {
            throw new CommonException(HttpStatus.BAD_REQUEST, ERROR_WRONG_ATTRIBUTES_ZP);
        }
    }

    private void checkInputSp(SubjektNedoplatokVstupnyDetail vstupnyDetail) {
        boolean isFoCheck = areAllObjectsNotNull(vstupnyDetail.getRodneCislo(), vstupnyDetail.getMeno(), vstupnyDetail.getPriezvisko());
        boolean isPoCheck = areAllObjectsNotNull(vstupnyDetail.getIco(), vstupnyDetail.getNazovSpolocnosti());
        if (!isFoCheck && !isPoCheck) {
            throw new CommonException(HttpStatus.BAD_REQUEST, ERROR_WRONG_ATTRIBUTES_SP_1);
        }
    }

    private boolean areAllObjectsNotNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return false;
            }
        }
        return true;
    }

    private Map<String, String> getParameterMapSp(SubjektNedoplatokVstupnyDetail vstupnyDetail, Date currentDate) {
        Map<String, String> parameterMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = dateFormat.format(currentDate);
        parameterMap.put(CAS_PODANIA, formattedDate);
        if (vstupnyDetail.getIco() != null) {
            parameterMap.put(ICO, vstupnyDetail.getIco());
            parameterMap.put(NAZOV_SPOLOCNOSTI, vstupnyDetail.getNazovSpolocnosti());
        } else {
            parameterMap.put(RC, vstupnyDetail.getRodneCislo());
            parameterMap.put(MENO, vstupnyDetail.getMeno());
            parameterMap.put(PRIEZVISKO, vstupnyDetail.getPriezvisko());
        }
        return parameterMap;
    }

    private Map<String, String> getParameterMapFs(SubjektNedoplatokVstupnyDetail vstupnyDetail) {
        Map<String, String> parameterMap = new HashMap<>();
        String dic = vstupnyDetail.getDic();
        String ico = vstupnyDetail.getIco();
        String rodneCislo = vstupnyDetail.getRodneCislo();

        if (dic != null) {
            parameterMap.put(DIC, dic);
        } else if (ico != null) {
            parameterMap.put(ICO, ico);
        } else if (rodneCislo != null) {
            parameterMap.put(RC, rodneCislo);
        }
        return parameterMap;
    }

    private Map<String, String> getParameterMapZp(SubjektNedoplatokVstupnyDetail vstupnyDetail, Date currentDate) {
        Map<String, String> parameterMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = dateFormat.format(currentDate);
        parameterMap.put(CAS_PODANIA, formattedDate);
        parameterMap.put(VYBAVUJE_OSOBA, zpVybavujeOsoba);
        parameterMap.put(VYBAVUJE_EMAIL, zpVybavujeEmail);
        parameterMap.put(VYBAVUJE_TELEFON, zpVybavujeTelefon);
        parameterMap.put(KOD_INSTITUCIA, kodInstitucia);
        parameterMap.put(JEDNACIE_CISLO, zpStavZiadostRepository.nextval().toString());

        if (vstupnyDetail.getIco() != null) {
            parameterMap.put(ICO, vstupnyDetail.getIco());
        } else {
            if (vstupnyDetail.getRodneCislo() != null) {
                parameterMap.put(RC, vstupnyDetail.getRodneCislo());
            } else {
                parameterMap.put(MENO, vstupnyDetail.getMeno());
                parameterMap.put(PRIEZVISKO, vstupnyDetail.getPriezvisko());
                parameterMap.put(DATUM_NARODENIA, dateFormat.format(vstupnyDetail.getDatumNarodenia()));
            }
        }
        return parameterMap;
    }

    private Map<String, String> getParameterMap(SubjektNedoplatokVstupnyDetail vstupnyDetail, InstituciaEnum institucia, Date currentDate) {
        switch (institucia) {
            case SP -> {
                return getParameterMapSp(vstupnyDetail, currentDate);
            }
            case FS -> {
                return getParameterMapFs(vstupnyDetail);
            }
            case ZP -> {
                return getParameterMapZp(vstupnyDetail, currentDate);
            }
        }
        return Collections.emptyMap();
    }

    private SpOsobaZaznam getSpOsobaZaznam(SubjektNedoplatokVstupnyDetail subjektNedoplatokVstupnyDetail, Map<String, String> parameterMap) {
        SpOsobaZaznam osoba = findSpOsobaZaznamInDb(subjektNedoplatokVstupnyDetail);
        if (osoba != null) {
            return osoba;
        }
        osoba = new SpOsobaZaznam();
        if (parameterMap.get(RC) != null) {
            osoba.setRodneCislo(parameterMap.get(RC));
            osoba.setMeno(parameterMap.get(MENO));
            osoba.setPriezvisko(parameterMap.get(PRIEZVISKO));
        } else {
            osoba.setIco(parameterMap.get(ICO));
            osoba.setNazovSpolocnosti(parameterMap.get(NAZOV_SPOLOCNOSTI));
        }
        return osoba;
    }

    private ZpOsobaZaznam getZpOsobaZaznam(SubjektNedoplatokVstupnyDetail subjektNedoplatokVstupnyDetail, Map<String, String> parameterMap) {
        ZpOsobaZaznam osoba = findZpOsobaZaznamInDb(subjektNedoplatokVstupnyDetail);
        if (osoba != null) {
            return osoba;
        }
        osoba = new ZpOsobaZaznam();
        osoba.setVybavujeOsoba(parameterMap.get(VYBAVUJE_OSOBA));
        osoba.setVybavujeEmail(parameterMap.get(VYBAVUJE_EMAIL));
        osoba.setVybavujeTelefon(parameterMap.get(VYBAVUJE_TELEFON));
        if (parameterMap.get(RC) != null) {
            osoba.setRodneCislo(parameterMap.get(RC));
        } else if (parameterMap.get(ICO) != null) {
            osoba.setIco(parameterMap.get(ICO));
        } else {
            osoba.setMeno(parameterMap.get(MENO));
            osoba.setPriezvisko(parameterMap.get(PRIEZVISKO));
            osoba.setPriezvisko(parameterMap.get(DATUM_NARODENIA));
        }
        return osoba;
    }

    private SpOsobaZaznam findSpOsobaZaznamInDb(SubjektNedoplatokVstupnyDetail subjektNedoplatokVstupnyDetail) {
        if (subjektNedoplatokVstupnyDetail.getRodneCislo() != null) {
            return spOsobaZaznamRepository.findByRodneCisloAndMenoAndPriezvisko(subjektNedoplatokVstupnyDetail.getRodneCislo(),
                    subjektNedoplatokVstupnyDetail.getMeno(), subjektNedoplatokVstupnyDetail.getPriezvisko()).orElse(null);
        }
        return spOsobaZaznamRepository.findByIcoAndNazovSpolocnosti(subjektNedoplatokVstupnyDetail.getIco(),
                subjektNedoplatokVstupnyDetail.getNazovSpolocnosti()).orElse(null);
    }

    private ZpOsobaZaznam findZpOsobaZaznamInDb(SubjektNedoplatokVstupnyDetail subjektNedoplatokVstupnyDetail) {
        if (subjektNedoplatokVstupnyDetail.getRodneCislo() != null) {
            return zpOsobaZaznamRepository.findByRodneCislo(subjektNedoplatokVstupnyDetail.getRodneCislo()).orElse(null);
        }
        if (subjektNedoplatokVstupnyDetail.getIco() != null) {
            return zpOsobaZaznamRepository.findByIco(subjektNedoplatokVstupnyDetail.getIco()).orElse(null);
        }
        return zpOsobaZaznamRepository.findByMenoAndPriezviskoAndDatumNarodenia(subjektNedoplatokVstupnyDetail.getMeno(),
                        subjektNedoplatokVstupnyDetail.getPriezvisko(),
                        DateUtils.toDate(subjektNedoplatokVstupnyDetail.getDatumNarodenia()))
                .orElse(null);
    }

    private SpStavZiadost getValidSpStavZiadost(SpOsobaZaznam osoba, Date currentDate) {
        if (osoba.getStavZiadosti() != null) {
            return osoba.getStavZiadosti().stream().filter(ziadost ->
                    ziadost.getPlatnostDo().after(currentDate)).findFirst().orElse(null);
        }
        return null;
    }

    private ZpStavZiadost getValidZpStavZiadost(ZpOsobaZaznam osoba, Date currentDate) {
        if (osoba.getStavZiadostList() != null) {
            ZpStavZiadost zpStavZiadost = osoba.getStavZiadostList().stream().filter(ziadost ->
                    ziadost.getPlatnostDo().after(currentDate)).findFirst().orElse(null);
            if (zpStavZiadost != null) {
                Hibernate.initialize(zpStavZiadost.getVysledkyKontrol());
            }
            return zpStavZiadost;
        }
        return null;
    }

    private SpStavZiadost saveZpStavZiadost(sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataResponseCType response,
                                            SpOsobaZaznam osoba, Date currentDate) {
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - saveSpStavZiadost] Ukladám stav žiadosti SP");
        SpStavZiadost spStavZiadost = new SpStavZiadost();

        int resultCode = response.getResultCode();
        LocalDate platnostDo = LocalDate.now().plusDays(spValidDays);

        spStavZiadost.setRequestId(response.getRequestId());
        spStavZiadost.setOvmTransactionId(response.getOvmTransactionId());
        spStavZiadost.setOvmCorrelationId(response.getOvmCorrelationId());
        spStavZiadost.setChybovaHlaskaOperacie(response.getErrorMessage());
        spStavZiadost.setPlatnostDo(DateUtils.toDate(platnostDo));
        spStavZiadost.setCasPodania(currentDate);
        spStavZiadost.setOsobaZaznam(osoba);

        if (resultCode == 0) {
            spStavZiadost.setStav(CsruStavZiadosti.PREBIEHA_SPRACOVANIE);
        }
        switch (resultCode) {
            case 0 -> spStavZiadost.setNavratovyKodOperacie(CsruNavratovyKodOperacie.OK);
            case 1 -> spStavZiadost.setNavratovyKodOperacie(CsruNavratovyKodOperacie.CHYBA_OVERENIA_OPRAVNENI);
            case 2 ->
                    spStavZiadost.setNavratovyKodOperacie(CsruNavratovyKodOperacie.CHYBA_VALIDACIE_VSTUPNYCH_PARAMETROV);
            case 3 -> spStavZiadost.setNavratovyKodOperacie(CsruNavratovyKodOperacie.INTERNA_CHYBA);
            default -> spStavZiadost.setNavratovyKodOperacie(CsruNavratovyKodOperacie.NEZNAMY);
        }
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - saveSpStavZiadost] Stav žiadosti SP úspešne uložený");
        return spStavZiadostRepository.save(spStavZiadost);
    }

    private ZpStavZiadost saveZpStavZiadost(sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataResponseCType response,
                                            ZpOsobaZaznam osoba, Date currentDate) {
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - saveZpStavZiadost] Ukladám stav žiadosti ZP");
        ZpStavZiadost zpStavZiadost = new ZpStavZiadost();

        int resultCode = response.getResultCode();
        LocalDate platnostDo = LocalDate.now().plusDays(zpValidDays);

        zpStavZiadost.setRequestId(response.getRequestId());
        zpStavZiadost.setOvmTransactionId(response.getOvmTransactionId());
        zpStavZiadost.setOvmCorrelationId(response.getOvmCorrelationId());
        zpStavZiadost.setChybovaHlaskaOperacie(response.getErrorMessage());
        zpStavZiadost.setPlatnostDo(DateUtils.toDate(platnostDo));
        zpStavZiadost.setCasPodania(currentDate);
        zpStavZiadost.setOsobaZaznam(osoba);
        zpStavZiadost.setMaNedoplatok(null);

        if (resultCode == 0) {
            zpStavZiadost.setStav(CsruStavZiadosti.PREBIEHA_SPRACOVANIE);
        }
        switch (resultCode) {
            case 0 -> zpStavZiadost.setNavratovyKodOperacie(CsruNavratovyKodOperacie.OK);
            case 1 -> zpStavZiadost.setNavratovyKodOperacie(CsruNavratovyKodOperacie.CHYBA_OVERENIA_OPRAVNENI);
            case 2 ->
                    zpStavZiadost.setNavratovyKodOperacie(CsruNavratovyKodOperacie.CHYBA_VALIDACIE_VSTUPNYCH_PARAMETROV);
            case 3 -> zpStavZiadost.setNavratovyKodOperacie(CsruNavratovyKodOperacie.INTERNA_CHYBA);
            default -> zpStavZiadost.setNavratovyKodOperacie(CsruNavratovyKodOperacie.NEZNAMY);
        }
        ZpStavZiadost savedZiadost = zpStavZiadostRepository.save(zpStavZiadost);
        Hibernate.initialize(savedZiadost.getVysledkyKontrol());
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - saveZpStavZiadost] Stav žiadosti ZP úspešne uložený");
        return savedZiadost;
    }

    private JAXBElement<sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataRequestCType>
    createSpZpRequest(Map<String, String> parameterMap, InstituciaEnum institucia) {
        sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataRequestCType requestObject =
                new sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataRequestCType();
        requestObject.setOvmIsId(ovmIsId);

        if (institucia.equals(InstituciaEnum.SP)) {
            requestObject.setOeId(SP_NEDOPLATKY);
        } else {
            requestObject.setOeId(ZP_NEDOPLATKY);
        }

        sk.is.urso.model.csru.api.async.common.ParameterListCType parameters =
                new sk.is.urso.model.csru.api.async.common.ParameterListCType();

        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            sk.is.urso.model.csru.api.async.common.ParameterCType parameter =
                    new sk.is.urso.model.csru.api.async.common.ParameterCType();
            parameter.setName(name);
            parameter.setValue(value);
            parameters.getParameter().add(parameter);
        }

        requestObject.setOvmTransactionId(UUID.randomUUID().toString());
        requestObject.setOvmCorrelationId(UUID.randomUUID().toString());
        requestObject.setParameters(parameters);

        sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.ObjectFactory of =
                new sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.ObjectFactory();
        return of.createGetConsolidatedDataRequest(requestObject);
    }

    private JAXBElement<GetConsolidatedDataRequestCType> createFsRequest(Map<String, String> parameterMap) {
        GetConsolidatedDataRequestCType requestObject = new GetConsolidatedDataRequestCType();
        requestObject.setOvmIsId(ovmIsId);
        requestObject.setOeId(FS_NEDOPLATKY);

        requestObject.setOvmTransactionId(UUID.randomUUID().toString());
        requestObject.setOvmCorrelationId(UUID.randomUUID().toString());

        ParameterListCType parameters = new ParameterListCType();

        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            ParameterCType parameter = new ParameterCType();
            parameter.setName(name);
            parameter.setValue(value);
            parameters.getParameter().add(parameter);
        }
        requestObject.setParameters(parameters);

        ObjectFactory of = new ObjectFactory();
        return of.createGetConsolidatedDataRequest(requestObject);
    }

    private <T> T processResponse(GetConsolidatedDataResponseCType response, String fromSubstring, String toSubstring, Class<T> clazz) {
        try {
            Element element = (Element) response.getConsolidatedData().getAny();
            String xml = responseStringXml(element, fromSubstring, toSubstring);
            return XmlUtils.parseXml(xml, clazz);
        } catch (Exception ex) {
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_RESULT_XML_EVALUATION, ex);
        }
    }

    private String responseStringXml(Element element, String fromSubstring, String toSubstring) {
        String responseXml = XmlUtils.xmlToString(element.getOwnerDocument());
        int from = responseXml.indexOf(fromSubstring);
        int to = responseXml.indexOf(toSubstring);
        responseXml = responseXml.substring(0, from + fromSubstring.length()) + ">" + responseXml.substring(to);
        return responseXml;
    }

    private void checkInputFS(SubjektNedoplatokVstupnyDetail vstupnyDetail) {
        int countNotNull = 0;
        if (vstupnyDetail.getRodneCislo() != null) {
            countNotNull++;
        }
        if (vstupnyDetail.getIco() != null) {
            countNotNull++;
        }
        if (vstupnyDetail.getDic() != null) {
            countNotNull++;
        }
        if (countNotNull != 1) {
            throw new CommonException(HttpStatus.BAD_REQUEST, ERROR_WRONG_ATTRIBUTES_FS);
        }
    }

    @Transactional
    public void updateStavuZiadostiSp(Long ziadostId) {
        SpStavZiadost ziadost = spStavZiadostRepository.findById(ziadostId).orElse(null);
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - updateStavuZiadostiSp] Kontrolujem stav žiadosti nedoplatkov SP. (Request ID - " + ziadost.getRequestId() + ")");
        GetStatusResponseCType response = getAktualnyStavZiadostiSp(ziadost);
        ziadost.setCsruTransactionId(response.getCsruTransactionId());
        ziadost.setChybovaHlaskaStavu(response.getErrorMessage());

        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - updateStavuZiadostiSp] LOG:"
                + "\n Ziadost ID - " + ziadost.getId()
                + "\n Request ID - " + ziadost.getRequestId()
                + "\n Result code:" + response.getResultCode()
                + "\n Response status:" + response.getStatus()
                + "\n Error message:" + response.getErrorMessage());

        switch (response.getResultCode()) {
            case 0 -> {
            }
            case 1 -> ziadost.setNavratovyKodStavu(CsruNavratovyKodOperacie.CHYBA_OVERENIA_OPRAVNENI);
            case 2 -> ziadost.setNavratovyKodStavu(CsruNavratovyKodOperacie.CHYBA_VALIDACIE_VSTUPNYCH_PARAMETROV);
            case 3 -> ziadost.setNavratovyKodStavu(CsruNavratovyKodOperacie.INTERNA_CHYBA);
            case 4 -> ziadost.setNavratovyKodStavu(CsruNavratovyKodOperacie.NEPLATNE_ID_POZIADAVKY);
            default -> ziadost.setNavratovyKodStavu(CsruNavratovyKodOperacie.NEZNAMY);
        }
        if (response.getResultCode() == 0 && response.getStatus() != 0) {
            ziadost.setNavratovyKodStavu(CsruNavratovyKodOperacie.OK);
            switch ((int) response.getStatus()) {
                case 0 -> ziadost.setStav(CsruStavZiadosti.PREBIEHA_SPRACOVANIE);
                case 1 -> {
                    ziadost.setStav(CsruStavZiadosti.SPRACOVANIE_USPESNE_UKONCENE);

                    FileCType file = response.getFileList().getFile().get(0);
                    if (file != null) {
                        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - updateStavuZiadostiSp] Subor bol vygenerovany v CSRU. (File path - " + file.getPath() + ")");
                        SpVystupnySubor subor = new SpVystupnySubor();
                        subor.setPath(file.getPath());
                        spVystupnySuborRepository.save(subor);
                        ziadost.setVysledokKontroly(setVysledokKontrolySp(ziadost, file.getPath()));
                        ziadost.setVystupnySubor(subor);
                    }
                }
                case 2 -> ziadost.setStav(CsruStavZiadosti.SPRACOVANIE_UKONCENE_S_CHYBOU);
                case 3 -> ziadost.setStav(CsruStavZiadosti.NEZNAMA_POZIADAVKA);
                case 4 -> ziadost.setStav(CsruStavZiadosti.SPRACOVANIE_UKONCENE_S_UPOZORNENIM);
                default -> ziadost.setStav(CsruStavZiadosti.NEZNAMY);
            }
        }
        spStavZiadostRepository.save(ziadost);
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - updateStavuZiadostiSp] Kontrola stau žiadosti nedoplatkov SP úspešne ukončená. (Request ID - " + ziadost.getRequestId() + ", Návratový kód stavu - " + ziadost.getNavratovyKodStavu() + ")");
    }

    @Transactional
    public void updateStavuZiadostiZp(Long ziadostId) {
        ZpStavZiadost ziadost = zpStavZiadostRepository.findById(ziadostId).orElse(null);
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - updateStavuZiadostiZp] Kontrolujem stav žiadosti nedoplatkov ZP. (Request ID - " + ziadost.getRequestId() + ")");
        GetStatusResponseCType response = getAktualnyStavZiadostiZp(ziadost);
        ziadost.setCsruTransactionId(response.getCsruTransactionId());
        ziadost.setChybovaHlaskaStavu(response.getErrorMessage());

        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - updateStavuZiadostiZp] LOG:"
                + "\n Ziadost ID - " + ziadost.getId()
                + "\n Request ID - " + ziadost.getRequestId()
                + "\n Result code:" + response.getResultCode()
                + "\n Response status:" + response.getStatus()
                + "\n Error message:" + response.getErrorMessage());
        switch (response.getResultCode()) {
            case 0 -> {
            }
            case 1 -> ziadost.setNavratovyKodStavu(CsruNavratovyKodOperacie.CHYBA_OVERENIA_OPRAVNENI);
            case 2 -> ziadost.setNavratovyKodStavu(CsruNavratovyKodOperacie.CHYBA_VALIDACIE_VSTUPNYCH_PARAMETROV);
            case 3 -> ziadost.setNavratovyKodStavu(CsruNavratovyKodOperacie.INTERNA_CHYBA);
            case 4 -> ziadost.setNavratovyKodStavu(CsruNavratovyKodOperacie.NEPLATNE_ID_POZIADAVKY);
            default -> ziadost.setNavratovyKodStavu(CsruNavratovyKodOperacie.NEZNAMY);
        }
        if (response.getResultCode() == 0 && response.getStatus() != 0) {
            ziadost.setNavratovyKodStavu(CsruNavratovyKodOperacie.OK);
            switch ((int) response.getStatus()) {
                case 0 -> ziadost.setStav(CsruStavZiadosti.PREBIEHA_SPRACOVANIE);
                case 1 -> {
                    ziadost.setStav(CsruStavZiadosti.SPRACOVANIE_USPESNE_UKONCENE);

                    FileCType file = response.getFileList().getFile().get(0);
                    loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - updateStavuZiadostiZp] Subor bol vygenerovany v CSRU. (File name - " + file + ")");
                    if (file != null) {
                        ZpVystupnySubor subor = new ZpVystupnySubor();
                        subor.setPath(file.getPath());
                        zpVystupnySuborRepository.save(subor);
                        ziadost.setVysledkyKontrol(setVysledokKontrolyZp(ziadost, file.getPath()));
                        ziadost.setVystupnySubor(subor);
                    }
                }
                case 2 -> ziadost.setStav(CsruStavZiadosti.SPRACOVANIE_UKONCENE_S_CHYBOU);
                case 3 -> ziadost.setStav(CsruStavZiadosti.NEZNAMA_POZIADAVKA);
                case 4 -> ziadost.setStav(CsruStavZiadosti.SPRACOVANIE_UKONCENE_S_UPOZORNENIM);
                default -> ziadost.setStav(CsruStavZiadosti.NEZNAMY);
            }
        }
        zpStavZiadostRepository.save(ziadost);
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - updateStavuZiadostiZp] Kontrola stau žiadosti nedoplatkov ZP úspešne ukončená. (Request ID - " + ziadost.getRequestId() + ", Návratový kód stavu - " + ziadost.getNavratovyKodStavu() + ")");
    }

    private GetStatusResponseCType getAktualnyStavZiadostiSp(SpStavZiadost ziadost) {
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - getAktualnyStavZiadostiSp] Dopytujem sa na CSRU. (Request ID - " + ziadost.getRequestId() + ")");
        GetStatusRequestCType request = new GetStatusRequestCType();
        request.setOvmIsId(ovmIsId);
        request.setRequestId(ziadost.getRequestId());
        request.setOvmCorrelationId(ziadost.getOvmCorrelationId());
        request.setOvmTransactionId(ziadost.getOvmTransactionId());
        sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.ObjectFactory of =
                new sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.ObjectFactory();
        GetStatusResponseCType response = csruEndpoint.sendGetStatusRequest(of.createGetStatusRequest(request));
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - getAktualnyStavZiadostiSp] Dopyt na CSRU uspesny. (Request ID - " + ziadost.getRequestId() + ")");
        return response;
    }

    private GetStatusResponseCType getAktualnyStavZiadostiZp(ZpStavZiadost ziadost) {
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - getAktualnyStavZiadostiZp] Dopytujem sa na CSRU. (Request ID - " + ziadost.getRequestId() + ")");
        GetStatusRequestCType request = new GetStatusRequestCType();
        request.setOvmIsId(ovmIsId);
        request.setRequestId(ziadost.getRequestId());
        request.setOvmCorrelationId(ziadost.getOvmCorrelationId());
        request.setOvmTransactionId(ziadost.getOvmTransactionId());
        sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.ObjectFactory of =
                new sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.ObjectFactory();
        GetStatusResponseCType response = csruEndpoint.sendGetStatusRequest(of.createGetStatusRequest(request));
        loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - getAktualnyStavZiadostiZp] Dopyt na CSRU uspesny. (Request ID - " + ziadost.getRequestId() + ")");
        return response;
    }

    private String formXmlBase64ToString(String form) throws IOException {
        if (form == null) {
            return null;
        }
        byte[] zipBytes = Base64.getDecoder().decode(form);
        try (InputStream byteArrayInputStream = new ByteArrayInputStream(zipBytes);
             ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream)) {
            ZipEntry entry;
            String regex = "Object[0-9]*\\.xml";
            Pattern pattern = Pattern.compile(regex);
            while ((entry = zipInputStream.getNextEntry()) != null) {
                Matcher matcher = pattern.matcher(entry.getName());
                if (matcher.matches()) {
                    StringBuilder sb = new StringBuilder();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        String content = new String(buffer, 0, len);
                        sb.append(content);
                    }
                    return sb.toString();
                }
                zipInputStream.closeEntry();
            }
        }
        return null;
    }

    private String xmlFormToState(String xmlString) {
        if (xmlString == null) {
             return null;
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlString.getBytes()));

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            String expression = "//Result/State";
            XPathExpression xPathExpression = xpath.compile(expression);
            Node node = (Node) xPathExpression.evaluate(doc, XPathConstants.NODE);
           return node.getChildNodes().item(0).getNodeValue();
        } catch (Exception e) {
            return null;
        }
    }

    private String xmlFormToDate(String xmlString) {
        if (xmlString == null) {
            return null;
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlString.getBytes()));

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            String expression = "//Result/Date";
            XPathExpression xPathExpression = xpath.compile(expression);
            Node node = (Node) xPathExpression.evaluate(doc, XPathConstants.NODE);
            return node.getChildNodes().item(0).getNodeValue();
        } catch (Exception e) {
            return null;
        }
    }

    private Date stringToDate(String dateString) {
        LocalDate localDate = null;
        if (dateString != null) {
            String dateTimeString = dateString.replace("+", "T00:00:00+");
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateTimeString);
            localDate = offsetDateTime.toLocalDate();
        } else {
            localDate = LocalDate.now();
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private SpVysledokKontroly setVysledokKontrolySp(SpStavZiadost ziadost, String filePath) {
        try {
            loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] Zacinam parsovanie suboru.");
            String fileContent = sftpService.loadFileContent(sftpHost, sftpPort, sftpUsername, sftpPassword, isProxyEnabled, proxyHost, proxyPort, filePath);
            loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] Obsah suboru nacitany.");

            fileContent = addNamespaces(fileContent, "ns2");
            CheckArrearsResultServiceResType result = XmlUtils.parseXml(fileContent, CheckArrearsResultServiceResType.class);
            loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] Obsah suboru konvertovany do objektu.");

            ResultInfoType resultInfo = result.getResultInfo();

            SpVysledokKontroly vysledokKontroly = new SpVysledokKontroly();
            vysledokKontroly.setStavZiadost(ziadost);
            vysledokKontroly.setOsbStatusText(resultInfo.getOsbStatusDescription());

            String form = formXmlBase64ToString(result.getFormXmlBase64());

            switch (resultInfo.getCsruStatus().intValue()) {
                case 0 -> {
                    loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] Obsah prilozeneho zip suboru desifrovany.");
                    String state = xmlFormToState(form);
                    loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] Stav - " + state + ".");

                    int stateLength = state.getBytes(StandardCharsets.UTF_8).length;
                    int maNedoplatkyLength = MA_NEDOPLATKY.getBytes(StandardCharsets.UTF_8).length;

                    loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] state bytes - " + Arrays.toString(state.getBytes(StandardCharsets.UTF_8)) + ".");
                    loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] statelength - " + stateLength + ".");
                    loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] maNedoplatky bytes - " + Arrays.toString(MA_NEDOPLATKY.getBytes(StandardCharsets.UTF_8)) + ".");
                    loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] maNedoplatkyLength - " + maNedoplatkyLength + ".");

                    if (stateLength == maNedoplatkyLength) {
                        String dateString = xmlFormToDate(form);
                        Date date = stringToDate(dateString);
                        vysledokKontroly.setNedoplatok(SpNedoplatok.MA_NEDOPLATOK);
                        ziadost.setMaNedoplatok(true);
                        ziadost.setResultDate(date);
                    }
                }
                case 1 -> {
                    vysledokKontroly.setNedoplatok(SpNedoplatok.NEMA_NEDOPLATOK);
                    String dateString = xmlFormToDate(form);
                    Date date = stringToDate(dateString);
                    ziadost.setMaNedoplatok(false);
                    ziadost.setResultDate(date);
                }
                case 2 -> {
                    loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] Obsah prilozeneho zip suboru desifrovany.");
                    String state = xmlFormToState(form);
                    loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] Stav - " + state + ".");

                    int stateLength = state.getBytes(StandardCharsets.UTF_8).length;
                    int maNedoplatkyLength = MA_NEDOPLATKY_NESPLNENIE_POVINNOSTI.getBytes(StandardCharsets.UTF_8).length;

                    loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] state bytes - " + Arrays.toString(state.getBytes(StandardCharsets.UTF_8)) + ".");
                    loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] statelength - " + stateLength + ".");
                    loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] maNedoplatky bytes - " + Arrays.toString(MA_NEDOPLATKY.getBytes(StandardCharsets.UTF_8)) + ".");
                    loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] maNedoplatkyLength - " + maNedoplatkyLength + ".");

                    if (stateLength == maNedoplatkyLength) {
                        String dateString = xmlFormToDate(form);
                        Date date = stringToDate(dateString);
                        vysledokKontroly.setNedoplatok(SpNedoplatok.MA_NEDOPLATOK_NESPLNENIE_POVINNOSTI);
                        ziadost.setMaNedoplatok(true);
                        ziadost.setResultDate(date);
                    }
                }
                case 3 -> vysledokKontroly.setNedoplatok(SpNedoplatok.NIE_JE_V_EVIDENCII);
                case 4 -> vysledokKontroly.setNedoplatok(SpNedoplatok.NEKOMPLETNE_DATA_TECHNICKA_CHYBA);
                case 5 -> vysledokKontroly.setNedoplatok(SpNedoplatok.NEIDENTIFIKOVANA_OSOBA);
                default -> vysledokKontroly.setNedoplatok(SpNedoplatok.NEZNAMY);
            }
            loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[SP][Method - setVysledokKontrolySp] Ukladam vysledok kontroly.");
            return spVysledokKontrolyRepository.save(vysledokKontroly);
        } catch (Exception ex) {
            loggerUtils.log(LoggerUtils.LogType.ERROR, logger, "[SP][Method - setVysledokKontrolySp] Error - " + ex.getMessage() + ".");
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_PARSING_CSRU_GENERATED_FILE, ex);
        }
    }

    private List<ZpVysledokKontroly> setVysledokKontrolyZp(ZpStavZiadost stavZiadost, String filePath) {
        List<ZpVysledokKontroly> vysledkyKontrol = new ArrayList<>();
        try {
            loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - setVysledokKontrolyZp] Zacinam parsovanie suboru.");
            String fileContent = sftpService.loadFileContent(sftpHost, sftpPort, sftpUsername, sftpPassword, isProxyEnabled, proxyHost, proxyPort, filePath);
            PlnenieOdvodovejPovinnosti plnenieOdvodovejPovinnosti = XmlUtils.parseXml(fileContent, PlnenieOdvodovejPovinnosti.class);
            loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - setVysledokKontrolyZp] Obsah suboru nacitany.");

            for (OdvodovaPovinnostZPCType odvodovaPovinnost : plnenieOdvodovejPovinnosti.getOdvodovaPovinnostZP()) {

                ZpVysledokKontroly vysledokKontroly = new ZpVysledokKontroly();
                vysledokKontroly.setStavZiadost(stavZiadost);

                switch (odvodovaPovinnost.getErrorCode().intValue()) {
                    case 0 -> vysledokKontroly.setNavratovyKod(CsruNavratovyKodOperacie.OK);
                    case 1 -> vysledokKontroly.setNavratovyKod(CsruNavratovyKodOperacie.CHYBA_OVERENIA_OPRAVNENI);
                    case 2 ->
                            vysledokKontroly.setNavratovyKod(CsruNavratovyKodOperacie.CHYBA_VALIDACIE_VSTUPNYCH_PARAMETROV);
                    case 3 -> vysledokKontroly.setNavratovyKod(CsruNavratovyKodOperacie.INTERNA_CHYBA);
                    case 4 -> vysledokKontroly.setNavratovyKod(CsruNavratovyKodOperacie.NEPLATNE_ID_POZIADAVKY);
                    default -> vysledokKontroly.setNavratovyKod(CsruNavratovyKodOperacie.NEZNAMY);
                }

                switch (odvodovaPovinnost.getVysledokSpracovaniaStav()) {
                    case "0" ->
                            vysledokKontroly.setVysledokSpracovania(ZpPopisKoduVysledkuSpracovania.ZASLANE_UDAJE_OD_ZP);
                    case "1" ->
                            vysledokKontroly.setVysledokSpracovania(ZpPopisKoduVysledkuSpracovania.NIE_JE_EVIDOVANY);
                    case "2" ->
                            vysledokKontroly.setVysledokSpracovania(ZpPopisKoduVysledkuSpracovania.NIE_SU_EVIDOVANE_ZIADNE_UDAJE);
                    case "3" ->
                            vysledokKontroly.setVysledokSpracovania(ZpPopisKoduVysledkuSpracovania.NESULAD_SUBJEKTU_V_OBALKE_A_OBSAHU);
                    case "4" ->
                            vysledokKontroly.setVysledokSpracovania(ZpPopisKoduVysledkuSpracovania.NESULAD_RC_ICO_IFO);
                    case "5" ->
                            vysledokKontroly.setVysledokSpracovania(ZpPopisKoduVysledkuSpracovania.NEEVIDOVANIY_PARTNER_PRE_POSKYTNUTIE_UDAJOV);
                    default -> vysledokKontroly.setVysledokSpracovania(ZpPopisKoduVysledkuSpracovania.NEZNAMY);
                }

                switch (odvodovaPovinnost.getZP()) {
                    case DOVERA -> vysledokKontroly.setPoistovna(ZpPoistovna.DOVERA);
                    case VSZP -> vysledokKontroly.setPoistovna(ZpPoistovna.VSEOBECNA_ZDRAVOTNA);
                    case UZP -> vysledokKontroly.setPoistovna(ZpPoistovna.UNION);
                    default -> vysledokKontroly.setPoistovna(ZpPoistovna.NEZNAMA);
                }
                EvidenciaCType evidencia = odvodovaPovinnost.getEvidencia();
                if (evidencia != null && evidencia.getNedoplatokVyska() != null) {
                    vysledokKontroly.setVyskaNedoplatku(evidencia.getNedoplatokVyska().floatValue());
                }
                setZpNedoplatok(vysledokKontroly, odvodovaPovinnost, stavZiadost, filePath);
                loggerUtils.log(LoggerUtils.LogType.INFO, logger, "[ZP][Method - setVysledokKontrolyZp] Ukladam vysledok kontroly.");
                vysledkyKontrol.add(zpVysledokKontrolyRepository.save(vysledokKontroly));
            }
        } catch (Exception ex) {
            loggerUtils.log(LoggerUtils.LogType.ERROR, logger, "[ZP][Method - setVysledokKontrolyZp] Error - " + ex.getMessage() + ".");
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_PARSING_CSRU_GENERATED_FILE, ex);
        }
        return vysledkyKontrol;
    }

    private void setZpNedoplatok(ZpVysledokKontroly vysledokKontroly, OdvodovaPovinnostZPCType odvodovaPovinnost, ZpStavZiadost stavZiadost, String filePath) {
        try {
            EvidenciaCType evidencia = odvodovaPovinnost.getEvidencia();
            if (evidencia != null) {
                vysledokKontroly.setNedoplatok(ZpNedoplatok.fromValue(evidencia.getSplnenaOdvodovaPovinnost()));
            } else {
                vysledokKontroly.setNedoplatok(ZpNedoplatok.NEZNAMY);
            }
            ZpNedoplatok zpNedoplatok = vysledokKontroly.getNedoplatok();
            if (zpNedoplatok != null && (zpNedoplatok.equals(ZpNedoplatok.N))) {
                stavZiadost.setMaNedoplatok(true);
                if (stavZiadost.getDate() == null) {
                    stavZiadost.setDate(DateUtils.toDate(odvodovaPovinnost.getDatum()));
                } else {
                    System.out.println("Subjekt s ICO '" + stavZiadost.getOsobaZaznam().getIco() + "' má nedoplatky na viac ako jednej zdravotnej poisťovni. ('" + filePath + "')");
                }
            } else if (zpNedoplatok != null && (zpNedoplatok.equals(ZpNedoplatok.A) || zpNedoplatok.equals(ZpNedoplatok.C))) {
                if (stavZiadost.getMaNedoplatok() == null || !stavZiadost.getMaNedoplatok()) {
                    stavZiadost.setMaNedoplatok(false);
                }
            }
        } catch (IllegalArgumentException ex) {
            vysledokKontroly.setNedoplatok(ZpNedoplatok.NEZNAMY);
        }
    }

    private String addNamespaces(String xmlString, String namespace) {
        xmlString = xmlString.replaceAll(namespace + ":", "");
        if (xmlString.contains("<?xml")) {
            xmlString = xmlString.substring(xmlString.indexOf("?>") + 2);
        }
        return xmlString.replaceAll("<(?!/)", "<" + namespace + ":").replaceAll("</", "</" + namespace + ":");
    }
}
