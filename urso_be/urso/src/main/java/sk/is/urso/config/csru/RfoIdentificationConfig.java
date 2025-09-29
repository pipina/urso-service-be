package sk.is.urso.config.csru;

import com.google.common.base.Throwables;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.alfa.exception.CommonException;
import org.alfa.model.UserInfo;
import org.alfa.utils.DateUtils;
import org.alfa.utils.PagingUtils;
import org.alfa.utils.XmlUtils;
import org.apache.activemq.artemis.core.server.management.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sk.is.urso.controller.ZaznamRegistraController;
import sk.is.urso.csru.fo.PodanieType;
import sk.is.urso.csru.fo.detailIfo.TDCDDCIO;
import sk.is.urso.csru.fo.detailIfo.TIOS;
import sk.is.urso.csru.fo.detailIfo.TIOSList;
import sk.is.urso.csru.fo.detailIfo.TMOSO;
import sk.is.urso.csru.fo.detailIfo.TOEXOEIO;
import sk.is.urso.csru.fo.detailIfo.TPOBPHRO;
import sk.is.urso.csru.fo.detailIfo.TPOD;
import sk.is.urso.csru.fo.detailIfo.TPRIO;
import sk.is.urso.csru.fo.detailIfo.TREGO;
import sk.is.urso.csru.fo.detailIfo.TRPRO;
import sk.is.urso.csru.fo.detailIfo.TSPISPN;
import sk.is.urso.csru.fo.detailIfo.TSPISPNList;
import sk.is.urso.csru.fo.detailIfo.TTOSTOIO;
import sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.TOEXO;
import sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.TOSO;
import sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.TOSOList;
import sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.TUES;
import sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.TransEnvTypeIn;
import sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.TransEnvTypeOut;
import sk.is.urso.csru.fo.zoznamIfo.DocumentUnauthorizedType;
import sk.is.urso.csru.fo.zoznamIfo.ObjectType;
import sk.is.urso.csru.fo.zoznamIfo.RegistrationType;
import sk.is.urso.model.Udalost;
import sk.is.urso.model.ciselniky.HodnotaCiselnika;
import sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.GetConsolidatedReferenceDataRequestCType;
import sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.GetConsolidatedReferenceDataResponseCType;
import sk.is.urso.model.csru.api.sync.common.DataPlaceholderCType;
import sk.is.urso.notifcation.v1.IdentificationErrorT;
import sk.is.urso.plugin.SubjectReg1;
import sk.is.urso.plugin.entity.RfoReg1IndexEntity;
import sk.is.urso.plugin.entity.SubjectReg1DataEntity;
import sk.is.urso.plugin.entity.SubjectReg1RfoIdentificationEntity;
import sk.is.urso.plugin.repository.RfoReg1IndexRepository;
import sk.is.urso.plugin.repository.SubjectReg1DataRepository;
import sk.is.urso.plugin.repository.SubjectReg1RfoIdentificationRepository;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.reg.model.ZaznamRegistraInputDetail;
import sk.is.urso.reg.model.ZaznamRegistraOutputDetail;
import sk.is.urso.rest.model.UdalostDomenaEnum;
import sk.is.urso.rest.model.UdalostKategoriaEnum;
import sk.is.urso.service.HodnotaCiselnikaService;
import sk.is.urso.service.UdalostService;
import sk.is.urso.service.csru.RfoService;
import sk.is.urso.subject.v1.ExternalRegisterReferenceT;
import sk.is.urso.subject.v1.IdentificatorT;
import sk.is.urso.subject.v1.PhysicalPersonT;
import sk.is.urso.subject.v1.PostalAddressT;
import sk.is.urso.subject.v1.REGCodelistItemOptionalT;
import sk.is.urso.subject.v1.REGCodelistItemT;
import sk.is.urso.subject.v1.REGItemWithHistoryT;
import sk.is.urso.subject.v1.SimplifiedAddressT;
import sk.is.urso.subject.v1.StreetT;
import sk.is.urso.subject.v1.SubjectT;
import sk.is.urso.util.EncryptionUtils;

import javax.persistence.criteria.Predicate;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static sk.is.urso.service.csru.RfoService.ADDRESS_USAGE_CODELIST_CODE;
import static sk.is.urso.service.csru.RfoService.TITLES_AFTER;
import static sk.is.urso.service.csru.RfoService.TITLES_BEFORE;

@Component
public class RfoIdentificationConfig {
    private static final Logger log = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    private static final String NAVRATOVY_OZNAM_WS = ", Návratový oznam WS: ";
    private static final String CHYBA_PRI_VYHLADAVANI_ZAUJMOVYCH_OSOB_NAVRATOVY_KOD_WS = "Chyba pri vyhľadávaní záujmových osôb. Návratový kód WS: ";
    private static final String RFO_ID = "rfoId";
    private static final String IS_POI = "isPoi";
    private static final String IS_MERGED = "isMerged";
    private static final String SUBJECT_1 = "SUBJECT_1";

    private static final String FO = "fo:";
    private static final String ID = "ID";
    private static final String RFO_MARK = "RFO_Mark";
    private static final String RFO_PERSON = "RFO_Person";
    private static final String DATA_BY_ID = "dataById";
    private static final String MARK_PERSON_OF_INTEREST = "markPersonOfInterest";
    private static final String UNMARK_PERSON_OF_INTEREST = "unmarkPersonOfInterest";
    private static final String RFO_PS_REFERENCNE_UDAJE_ZOZNAMU_IFO_ONLINE_BEZ_ZEP_WS_IN_1_0 = "RFO_PS_REFERENCNE_UDAJE_ZOZNAMU_IFO_ONLINE_BEZ_ZEP_WS_IN_1_0";
    private static final String RFO_PODP_EXT_REFERENCNE_UDAJE_ZOZNAMU_IFO_ONLINE_BEZ_ZEP_WS_1_0 = "RFO_Podp_Ext_Referencne_Udaje_Zoznamu_IFO_Online_Bez_Zep_WS_1_0";
    public static final String POSKYTNUTIE_UDAJOV_IFOONLINE_WS_V_1_0_XSD = "http://www.egov.sk/mvsr/RFO/datatypes/Podp/Ext/PoskytnutieUdajovIFOOnlineWS-v1.0.xsd";

    @Value("${user.system.login}")
    private String userSystemLogin;

    @Value("${integration.csru.executors:#{5}}")
    private int executorThreadPools;

    @Bean("rfoIdentificationExecutor")
    public ExecutorService initializeExecutorService() {
        return Executors.newFixedThreadPool(executorThreadPools);
    }

    @Value("${integration.csru.ovmIsId}")
    private String ovmIsId;

    @Value("${integration.csru.identifikatorSubjektu}")
    private String identifikatorSubjektu;

    @Value("${integration.csru.rfo.codelist.codelistCode}")
    private String rfoCodelistCodelistCode;

    @Autowired
    @Qualifier("csruFoWS")
    WebServiceTemplate csruFoWS;

    @Autowired
    CsruEndpoint csruEndpoint;

    @Autowired
    EncryptionUtils encryptionUtils;

    @Autowired
    UdalostService udalostService;

//    @Autowired
//    RegisterValueEventService registerValueEventService;

//    @Autowired
//    SubjectController subjectController;

    @Autowired
    ZaznamRegistraController zaznamRegistraController;

    @Autowired
    SubjectReg1DataRepository subjectDataRepository;

//    @Autowired
//    NotificationService notificationService;

//    @Autowired
//    SubjectService subjectService;

    @Autowired
    SubjectReg1RfoIdentificationRepository rfoIdentificationRepository;

    @Autowired
    RfoService rfoService;

    @Autowired
    RfoReg1IndexRepository rfoIndexRepository;

    @Autowired
    HodnotaCiselnikaService hodnotaCiselnikaService;

    private static final int MAX_POI_RFO_RECORDS = 1000;
    private static final int MAX_READ_RFO_RECORDS = 10;

    //@EventListener(ApplicationStartedEvent.class)
//    @Scheduled(cron = "${cron.RfoIdentificationConfig.expression}")
//    @SchedulerLock(name = "TaskScheduler_rfoIdentification", lockAtLeastForString = "${shedlock.least}")
    public void rfoIdentification() {
        identification();
        poiMarking();
        loadRfoSubjectsAndMerge();
        poiUnmarking();
    }

    void poiMarking() {
        log.info("[RFO][IDENTIFICATION][POI-MARK][START]");
        try {
            List<SubjectReg1RfoIdentificationEntity> result = rfoIdentificationRepository.findAll(getPoiMarkingSpec());
            if (!result.isEmpty()) {
                for (int i = 0; i < result.size() / MAX_POI_RFO_RECORDS + 1; i++) {
                    createPoiMarkingRequest(PagingUtils.pagingElements(result, i, MAX_POI_RFO_RECORDS));
                }
            }
        } finally {
            log.info("[RFO][IDENTIFICATION][POI-MARK][END]");
        }
    }


    public void createPoiMarkingRequest(List<SubjectReg1RfoIdentificationEntity> subjects) {

        GetConsolidatedReferenceDataRequestCType dataRequest = new GetConsolidatedReferenceDataRequestCType();
        dataRequest.setOvmIsId(ovmIsId);
        dataRequest.setOeId(RFO_MARK);
        dataRequest.setScenario(MARK_PERSON_OF_INTEREST);
        dataRequest.setOvmTransactionId(UUID.randomUUID().toString());
        dataRequest.setOvmCorrelationId(UUID.randomUUID().toString());

        String corrId = UUID.randomUUID().toString();
        TransEnvTypeIn envTypeIn = new TransEnvTypeIn();
        envTypeIn.setCorrID(corrId);

        TOSOList tosoList = new TOSOList();
        subjects.forEach(subject -> {
            TOSO toso = new TOSO();
            toso.setID(subject.getRfoId());
            tosoList.getOSO().add(toso);
        });
        envTypeIn.setOSOList(tosoList);

        TUES tues = new TUES();
        tues.setPO(ovmIsId);
        tues.setTI(corrId);
        envTypeIn.setUES(tues);

        sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.ObjectFactory objectFactory = new sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.ObjectFactory();
        DataPlaceholderCType dataPlaceholder = new DataPlaceholderCType();
        dataPlaceholder.setAny(objectFactory.createTransEnvIn(envTypeIn));
        dataRequest.setPayload(dataPlaceholder);

        log.info("[RFO][IDENTIFICATION][POI-MARK]CSRU request from RfoIdentificationConfig.createPoiMarkingRequest: " + dataRequest.toString());

        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequest);
        csruEndpoint.sendPoiMarkingRequest(request).thenAccept(this::processPoiMarkingResponse).exceptionally(e -> {
            log.info("[RFO][IDENTIFICATION][POI-MARK]CSRU response from RfoIdentificationConfig.createPoiMarkingRequest: " + Throwables.getStackTraceAsString(e));
            return null;
        });
    }

    void processPoiMarkingResponse(GetConsolidatedReferenceDataResponseCType response) {
        SubjectReg1RfoIdentificationEntity rfoIdentification = null;
        try {
            log.info("[RFO][IDENTIFICATION][POI-MARK]CSRU response from RfoIdentificationConfig.createPoiMarkingRequest: " + response.toString());

            Udalost event = udalostService.createEvent(UdalostDomenaEnum.HODNOTA_REGISTRA, UdalostKategoriaEnum.IDENTIFICATION, userSystemLogin);

            TransEnvTypeOut envTypeOut = processDecryptedResponse(encryptionUtils.decryptResponse(response.getPayload()), "http://www.egov.sk/mvsr/RFO/Zapis/Ext/OznacenieZaujmovejOsobyWS-v1.0", TransEnvTypeOut.class);

            if (envTypeOut.getZVY().getNK() != 1) {
                String error = CHYBA_PRI_VYHLADAVANI_ZAUJMOVYCH_OSOB_NAVRATOVY_KOD_WS + envTypeOut.getZVY().getNK() + NAVRATOVY_OZNAM_WS + envTypeOut.getZVY().getDN();
                log.info(error);
            }

            for (TOEXO toexo : envTypeOut.getZVY().getOEXList().getOEX()) {

                rfoIdentification = rfoIdentificationRepository.findByRfoId(toexo.getID());
                if (toexo.getNK() == 1) {
                    rfoIdentification.setOznaceny(true);
                } else {
                    rfoIdentification.setChybny(true);
                    rfoIdentification.setSprava(toexo.getDE());
                    IdentificationErrorT identificationError = new IdentificationErrorT();
                    identificationError.setMessage(toexo.getDE());

                    SubjectReg1DataEntity subjectDataEntity = subjectDataRepository.findById(rfoIdentification.getZaznamId().getId()).get();
//                    registerValueEventService.createRegisterValueEvent(subjectDataEntity, new RegisterId(SubjectReg1.REGISTER_ID, 1), event);
//                    createAndSendNotification(subjectDataEntity, event, SubCategoryT.IDENIFICATION_ERROR, identificationError);
                }
                rfoIdentificationRepository.save(rfoIdentification);
            }
        } catch (Exception e) {
            if (rfoIdentification != null) {
                saveErrorRfoIdentification(rfoIdentification, e.getMessage());
            }
            e.printStackTrace();
        }
    }

    void processPoiMarkingResponseTesting(GetConsolidatedReferenceDataResponseCType response) {
        SubjectReg1RfoIdentificationEntity rfoIdentification;

        Udalost event = udalostService.createEvent(UdalostDomenaEnum.HODNOTA_REGISTRA, UdalostKategoriaEnum.IDENTIFICATION, userSystemLogin);

        TransEnvTypeOut envTypeOut = processDecryptedResponse(encryptionUtils.decryptResponse(response.getPayload()), "http://www.egov.sk/mvsr/RFO/Zapis/Ext/OznacenieZaujmovejOsobyWS-v1.0", TransEnvTypeOut.class);

        if (envTypeOut.getZVY().getNK() != 1) {
            String error = CHYBA_PRI_VYHLADAVANI_ZAUJMOVYCH_OSOB_NAVRATOVY_KOD_WS + envTypeOut.getZVY().getNK() + NAVRATOVY_OZNAM_WS + envTypeOut.getZVY().getDN();
            log.info(error);
            throw new CommonException(HttpStatus.BAD_REQUEST, error);
        }

        for (TOEXO toexo : envTypeOut.getZVY().getOEXList().getOEX()) {

            rfoIdentification = rfoIdentificationRepository.findByRfoId(toexo.getID());
            if (toexo.getNK() == 1) {
                rfoIdentification.setOznaceny(true);
            } else {
                rfoIdentification.setChybny(true);
                rfoIdentification.setSprava(toexo.getDE());
                IdentificationErrorT identificationError = new IdentificationErrorT();
                identificationError.setMessage(toexo.getDE());

                SubjectReg1DataEntity subjectDataEntity = subjectDataRepository.findById(rfoIdentification.getZaznamId().getId()).get();
//                registerValueEventService.createRegisterValueEvent(subjectDataEntity, new RegisterId(SubjectReg1.REGISTER_ID, 1), event);
//                createAndSendNotification(subjectDataEntity, event, SubCategoryT.IDENIFICATION_ERROR, identificationError);
            }
            rfoIdentificationRepository.save(rfoIdentification);
        }
    }

    void loadRfoSubjectsAndMerge() {
        log.info("[RFO][IDENTIFICATION][LOAD-RFO][START]");
        try {
            List<SubjectReg1RfoIdentificationEntity> result = rfoIdentificationRepository.findAll(getLoadRfoSubjectsSpec());
            if (!result.isEmpty()) {
                for (int i = 0; i < result.size() / MAX_READ_RFO_RECORDS + 1; i++) {
                    createRfoLoadRequest(PagingUtils.pagingElements(result, i, MAX_READ_RFO_RECORDS));
                }
            }
        } finally {
            log.info("[RFO][IDENTIFICATION][LOAD-RFO][END]");
        }
    }

    public void createRfoLoadRequest(List<SubjectReg1RfoIdentificationEntity> subjects) {

        var jaxbContext = ((Jaxb2Marshaller) csruFoWS.getMarshaller()).getJaxbContext();
        sk.is.urso.csru.fo.zoznamIfo.ObjectFactory objFactory = new sk.is.urso.csru.fo.zoznamIfo.ObjectFactory();
        sk.is.urso.csru.fo.detailIfo.ObjectFactory objectFactory = new sk.is.urso.csru.fo.detailIfo.ObjectFactory();
        sk.is.urso.csru.fo.detailIfo.TransEnvTypeIn envTypeIn = new sk.is.urso.csru.fo.detailIfo.TransEnvTypeIn();
        TPOD tpod = new TPOD();

        sk.is.urso.csru.fo.detailIfo.TUES tues = new sk.is.urso.csru.fo.detailIfo.TUES();
        tues.setPO(ovmIsId);
        tues.setTI(UUID.randomUUID().toString());
        tpod.setUES(tues);

        TIOSList tiosList = new TIOSList();
        subjects.forEach(subject -> {
            TIOS tios = new TIOS();
            tios.setIF(subject.getRfoId());
            tiosList.getIOS().add(tios);
        });
        tpod.setIOSList(tiosList);

        TSPISPNList tspispnList = new TSPISPNList();
        List<String> values = Arrays.asList("Administratívne údaje", "Lokačné údaje", "Vzťahové údaje", "Identifikačné údaje");
        for (int i = 0; i < 4; i++) {
            TSPISPN tspispn = new TSPISPN();
            tspispn.setHO(i + 1);
            tspispn.setTUDHONA(values.get(i));
            tspispnList.getSPI().add(tspispn);
        }
        tpod.setSPIList(tspispnList);

        envTypeIn.setPOD(tpod);

        ObjectType object = new ObjectType();
        object.setId(UUID.randomUUID().toString());
        object.setIdentifier(POSKYTNUTIE_UDAJOV_IFOONLINE_WS_V_1_0_XSD);
        object.getContent().add(objectFactory.createTransEnvIn(envTypeIn));

        DocumentUnauthorizedType documentUnauthorized = new DocumentUnauthorizedType();
        documentUnauthorized.setId(RFO_PS_REFERENCNE_UDAJE_ZOZNAMU_IFO_ONLINE_BEZ_ZEP_WS_IN_1_0);
        documentUnauthorized.getObject().add(object);

        RegistrationType registrationType = new RegistrationType();
        registrationType.setId(RFO_PS_REFERENCNE_UDAJE_ZOZNAMU_IFO_ONLINE_BEZ_ZEP_WS_IN_1_0);
        registrationType.getAny().add(objFactory.createDocumentUnauthorized(documentUnauthorized));

        PodanieType podanieType = new PodanieType();
        podanieType.setIdentifikatorSubjektu(identifikatorSubjektu);
        podanieType.setTypPodania(RFO_PS_REFERENCNE_UDAJE_ZOZNAMU_IFO_ONLINE_BEZ_ZEP_WS_IN_1_0);
        podanieType.setTypSluzby(RFO_PODP_EXT_REFERENCNE_UDAJE_ZOZNAMU_IFO_ONLINE_BEZ_ZEP_WS_1_0);
        String dataPodaniaBase64 = Base64.getEncoder().encodeToString(XmlUtils.xmlToString(objFactory.createRegistration(registrationType), jaxbContext).getBytes(StandardCharsets.UTF_8));
        podanieType.setDataPodaniaBase64(dataPodaniaBase64);

        GetConsolidatedReferenceDataRequestCType dataRequestCType = new GetConsolidatedReferenceDataRequestCType();
        dataRequestCType.setOvmIsId(ovmIsId);
        dataRequestCType.setOeId(RFO_PERSON);
        dataRequestCType.setScenario(DATA_BY_ID);
        dataRequestCType.setOvmTransactionId(UUID.randomUUID().toString());
        dataRequestCType.setOvmCorrelationId(UUID.randomUUID().toString());

        DataPlaceholderCType dataPlaceholderCType = new DataPlaceholderCType();
        sk.is.urso.csru.fo.ObjectFactory factory = new sk.is.urso.csru.fo.ObjectFactory();
        dataPlaceholderCType.setAny(factory.createPodanie(podanieType));
        dataRequestCType.setPayload(dataPlaceholderCType);

        log.info("[RFO][IDENTIFICATION][LOAD-RFO]CSRU request from RfoIdentificationConfig.createRfoLoadRequest: " + dataRequestCType.toString());

        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequestCType);
        csruEndpoint.sendRfoLoadRequest(request).thenAccept(response -> processRfoLoadResponse(response, subjects)).exceptionally(e -> {
            log.info("[RFO][IDENTIFICATION][LOAD-RFO]CSRU response from RfoIdentificationConfig.createRfoLoadRequest: " + Throwables.getStackTraceAsString(e));
            return null;
        });
    }

    @Transactional
    public void processRfoLoadResponse(GetConsolidatedReferenceDataResponseCType response, List<SubjectReg1RfoIdentificationEntity> subjects) {
        SubjectReg1RfoIdentificationEntity errorRfoIdentification = null;

        try {
            log.info("[RFO][IDENTIFICATION][LOAD-RFO]CSRU response from RfoIdentificationConfig.createRfoLoadRequest: " + response.toString());

            sk.is.urso.csru.fo.detailIfo.TransEnvTypeOut envTypeOut = processDecryptedResponse(encryptionUtils.decryptResponse(response.getPayload()));

            if (envTypeOut.getPOV().getKO() != 1) {
                String error = CHYBA_PRI_VYHLADAVANI_ZAUJMOVYCH_OSOB_NAVRATOVY_KOD_WS + envTypeOut.getPOV().getKO() + NAVRATOVY_OZNAM_WS + envTypeOut.getPOV().getNU();
                log.info(error);
            }

            sk.is.urso.csru.fo.detailIfo.ObjectFactory objectFactory = new sk.is.urso.csru.fo.detailIfo.ObjectFactory();
            Document document = XmlUtils.parse(XmlUtils.xmlToString(objectFactory.createTransEnvOut(envTypeOut)));

            Document newDocument = null;
            Element oexElement = null;

            NodeList oexList = document.getElementsByTagName("OEX");
            for (int i = 0; i < oexList.getLength(); i++) {

                oexElement = (Element) oexList.item(i);
                newDocument = XmlUtils.newDocument();
                newDocument.appendChild(newDocument.adoptNode(oexElement.cloneNode(true)));

                Element rootNode = newDocument.getDocumentElement();
                rootNode.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                rootNode.setAttribute("xsi:schemaLocation", "http://www.dominanz.sk/UVZ/Reg/FO ../../uvz_reg_common/configuration/RFO_1/RFO_1_DATA.xsd");
                newDocument.renameNode(rootNode, "http://www.dominanz.sk/UVZ/Reg/FO", "fo:FO");
                XmlUtils.addNamespaceRecursive(newDocument.getFirstChild(), "http://www.dominanz.sk/UVZ/Reg/FO", "fo:");
            }

            UserInfo userInfo = new UserInfo();
            userInfo.setLogin(userSystemLogin);
            userInfo.setAdministrator(true);
            ResponseEntity<ZaznamRegistraOutputDetail> insertResponse = zaznamRegistraController.zaznamRegistraPut(prepareRegisterEntryInsertDetail(XmlUtils.xmlToString(newDocument), SubjectReg1.RFO), userInfo);
            if (insertResponse.getStatusCode().equals(HttpStatus.OK)) {

                ZaznamRegistraOutputDetail outputDetail = insertResponse.getBody();
                TOEXOEIO toexoeio = null;
                for (TOEXOEIO oex : envTypeOut.getPOV().getOEXList().getOEX()) {
                    if (oex.getID().getValue().equals(oexElement.getElementsByTagName(ID).item(0).getFirstChild().getNodeValue())) {
                        toexoeio = oex;
                        break;
                    }
                }

                SubjectReg1RfoIdentificationEntity rfoIdentification = null;
                for (SubjectReg1RfoIdentificationEntity rfo : subjects) {
                    errorRfoIdentification = rfo;
                    if (rfo.getRfoId().equalsIgnoreCase(toexoeio.getID().getValue())) {
                        rfoIdentification = rfo;
                        break;
                    }
                }

                zaznamRegistraController.zaznamRegistraReferenciaRegisterIdVerziaRegistraIdZaznamIdModulIdPut(SubjectReg1.RFO, 1, outputDetail.getZaznamId(), SubjectReg1.REG);
//                zaznamRegistraController.registerEntryReferenceRegisterIdRegisterVersionIdEntryIdModuleIdPut(SubjectReg1.RFO, 1, outputDetail.getZaznamId(), SubjectReg1.REG, null, userInfo);
                reflectRfoDataToSubject(toexoeio, outputDetail.getZaznamId(), rfoIdentification, rfoIdentification.getZaznamId().getId(), userInfo);
            }

        } catch (Exception ex) {
            if (errorRfoIdentification != null) {
                saveErrorRfoIdentification(errorRfoIdentification, ex.getMessage());
            }
            ex.printStackTrace();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorRfoIdentification(SubjectReg1RfoIdentificationEntity rfoIdentification, String errorMessage) {
        rfoIdentification.setChybny(true);
        rfoIdentification.setSprava(errorMessage);
        rfoIdentificationRepository.save(rfoIdentification);
    }

    void reflectRfoDataToSubject(TOEXOEIO toexoeio, Long entryId, SubjectReg1RfoIdentificationEntity rfoIdentification, Long id, UserInfo userInfo) {

        Udalost event = udalostService.createEvent(UdalostDomenaEnum.HODNOTA_REGISTRA, UdalostKategoriaEnum.IDENTIFICATION, userSystemLogin);
        sk.is.urso.subject.v1.ObjectFactory objectFactory = new sk.is.urso.subject.v1.ObjectFactory();
        XMLGregorianCalendar today = DateUtils.toXmlDate(LocalDate.now());
        String externalId = toexoeio.getID() != null ? toexoeio.getID().getValue() : rfoIdentification.getRfoId();

        SubjectReg1DataEntity subjectDataEntity = subjectDataRepository.findById(id).get();
        if (subjectDataEntity.getFoId() == null) {
            subjectDataEntity.setFoId(externalId);
        }
        subjectDataEntity.setUcinnostOd(DateUtils.toDate(toexoeio.getDN().getValue()));
        if (toexoeio.getDU() != null) {
            subjectDataEntity.setUcinnostDo(DateUtils.toDate(toexoeio.getDU().getValue()));
        }

        ResponseEntity<ZaznamRegistraOutputDetail> response = zaznamRegistraController.zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdGet(SubjectReg1.REGISTER_ID, 1, subjectDataEntity.getId(), userInfo);
        SubjectT subject = XmlUtils.parse(response.getBody().getData(), SubjectT.class);

        if (toexoeio.getPOBList() != null) {
            List<SimplifiedAddressT> postalAddresses = subject.getAddress().stream().filter(address -> address.getPostalAddress() != null).collect(Collectors.toList());
            short maxSequence = (short) postalAddresses.stream().mapToInt(address -> address.getPostalAddress().getSequence()).max().orElse(0);

            List<SimplifiedAddressT> newAddresses = new ArrayList<>();
            for (int i = 0; i < toexoeio.getPOBList().getPOB().size(); i++) {
                TPOBPHRO pob = toexoeio.getPOBList().getPOB().get(i);
                List<SimplifiedAddressT> addresses = subject.getAddress().stream().filter(address -> address.getAddressUsage().getItemCode().equalsIgnoreCase(String.valueOf(pob.getTP()))).collect(Collectors.toList());
                for (SimplifiedAddressT address : addresses) {
                    if (address.getPostalAddress() != null) {
                        invalidateItemWithHistory(address.getPostalAddress());
                    }
                }
                if (i != 0 || !postalAddresses.isEmpty()) {
                    ++maxSequence;
                }
                newAddresses.add(createAddress(pob, maxSequence, userInfo));
            }
            subject.getAddress().addAll(newAddresses);
        }

        subject.getPhysicalPerson().forEach(fo -> {
            fo.setEffectiveTo(today);
            fo.setCurrent(false);
        });
        short maxSequence = (short) subject.getPhysicalPerson().stream().mapToInt(PhysicalPersonT::getSequence).max().orElse(0);
        subject.getPhysicalPerson().add(createPhysicalPerson(toexoeio, !subject.getPhysicalPerson().isEmpty() ? ++maxSequence : 0, userInfo));

        if (toexoeio.getDCDList() != null) {
            maxSequence = (short) subject.getIdentificator().stream().mapToInt(IdentificatorT::getSequence).max().orElse(0);
            for (TDCDDCIO dcd : toexoeio.getDCDList().getDCD()) {
                List<IdentificatorT> identifiers = subject.getIdentificator().stream().filter(identifier -> identifier.getIDType().getItemCode().equalsIgnoreCase(String.valueOf(dcd.getDD()))).collect(Collectors.toList());
                for (IdentificatorT identifier : identifiers) {
                    identifier.setEffectiveTo(today);
                    identifier.setCurrent(false);
                }
                subject.getIdentificator().add(createIdentifier(dcd, null, null, !subject.getIdentificator().isEmpty() ? ++maxSequence : 0));
            }
        }
        if (subject.getIdentificator().stream().noneMatch(identifier -> identifier.getIDType().getItemCode().equalsIgnoreCase(SubjectReg1.IDENTIFIER_TYPE_BIRTH_NUMBER))) {
            subject.getIdentificator().add(createIdentifier(null, SubjectReg1.IDENTIFIER_TYPE_BIRTH_NUMBER, toexoeio.getRC().getValue(), !subject.getIdentificator().isEmpty() ? ++maxSequence : 0));
        }
        if (subject.getIdentificator().stream().noneMatch(identifier -> identifier.getIDType().getItemCode().equalsIgnoreCase(SubjectReg1.IDENTIFIER_TYPE_PERSON_RECORD))) {
            subject.getIdentificator().add(createIdentifier(null, SubjectReg1.IDENTIFIER_TYPE_PERSON_RECORD, toexoeio.getID() != null ? toexoeio.getID().getValue() : rfoIdentification.getRfoId(), !subject.getIdentificator().isEmpty() ? ++maxSequence : 0));
        }
        if (subject.getIdentificator().stream().noneMatch(identifier -> identifier.getIDType().getItemCode().equalsIgnoreCase(SubjectReg1.IDENTIFIER_TYPE_VERIFIED_PERSON_RECORD))) {
            subject.getIdentificator().add(createIdentifier(null, SubjectReg1.IDENTIFIER_TYPE_VERIFIED_PERSON_RECORD, toexoeio.getID() != null ? toexoeio.getID().getValue() : rfoIdentification.getRfoId(), !subject.getIdentificator().isEmpty() ? ++maxSequence : 0));
        }

        ExternalRegisterReferenceT rfoReference = new ExternalRegisterReferenceT();
        rfoReference.setEntryId(entryId.toString());
        rfoReference.setExternalId(externalId);
        subject.setRfoReference(rfoReference);

        ResponseEntity<ZaznamRegistraOutputDetail> insertResponse = zaznamRegistraController.zaznamRegistraPost(prepareRegisterEntryUpdateDetail(XmlUtils.xmlToString(objectFactory.createSubject(subject)), id, userInfo), userInfo);
        if (insertResponse.getStatusCode().equals(HttpStatus.OK)) {
            rfoIdentification.setZluceny(true);
            rfoIdentificationRepository.save(rfoIdentification);

//            registerValueEventService.createRegisterValueEvent(subjectDataEntity, new RegisterId(SubjectReg1.REGISTER_ID, 1), event);
//            createAndSendNotification(subjectDataEntity, event, SubCategoryT.IDENTIFIED, null);
        }
    }

    private void invalidateItemWithHistory(REGItemWithHistoryT itemWithHistory) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date dateWithoutTime;
        try {
            dateWithoutTime = formatter.parse(formatter.format(new Date()));
        } catch (ParseException e) {
            dateWithoutTime = new Date();
        }
        if (DateUtils.toDate(itemWithHistory.getEffectiveFrom()).after(dateWithoutTime) || DateUtils.toDate(itemWithHistory.getEffectiveFrom()).equals(dateWithoutTime)) {
            itemWithHistory.setValid(false);
        } else {
            itemWithHistory.setEffectiveTo(DateUtils.toXmlDate(DateUtils.toLocalDate(new Date()).minusDays(1)));
        }
    }

    private IdentificatorT createIdentifier(TDCDDCIO dcd, String type, String value, short sequence) {

        IdentificatorT identifier = new IdentificatorT();
        setAttributes(identifier, sequence);

        REGCodelistItemT idType = new REGCodelistItemT();
        idType.setItemCode(dcd != null ? String.valueOf(dcd.getDD()) : type);
        identifier.setIDType(idType);
        identifier.setIDValue(dcd != null ? String.valueOf(dcd.getCD()) : value);
        return identifier;
    }

    private void setAttributes(REGItemWithHistoryT itemWithHistory, short sequence) {
        itemWithHistory.setSequence(sequence);
        itemWithHistory.setCurrent(true);
        itemWithHistory.setValid(true);
        itemWithHistory.setEffectiveFrom(DateUtils.nowXmlDate());
    }

    private ZaznamRegistraInputDetail prepareRegisterEntryUpdateDetail(String xmlData, Long entryId, UserInfo userInfo) {
        return new ZaznamRegistraInputDetail()
                .zaznamId(entryId)
                .data(xmlData)
                .registerId(SubjectReg1.REGISTER_ID)
                .verziaRegistraId(1)
                .modul(SubjectReg1.REG)
                .ucinnostDo(LocalDate.now())
                .pouzivatel(userInfo.getLogin());
    }

    private PhysicalPersonT createPhysicalPerson(TOEXOEIO toexoeio, short sequence, UserInfo userInfo) {

        PhysicalPersonT physicalPerson = new PhysicalPersonT();
        setAttributes(physicalPerson, sequence);

        if (toexoeio.getPI() != null) {
            REGCodelistItemT gender = new REGCodelistItemT();
            gender.setItemCode(String.valueOf(toexoeio.getPI().getValue()));
            physicalPerson.setGender(gender);
        }

        if (toexoeio.getSN() != null && toexoeio.getSN().getValue() == 211) {
            REGCodelistItemT nationality = new REGCodelistItemT();
            nationality.setItemCode(SubjectReg1.ENUMERATION_COUNTRY_SLOVAKIA);
            physicalPerson.setNationality(nationality);
        }

        createTitles(physicalPerson, toexoeio.getTOSList().getTOS(), userInfo);

        toexoeio.getMOSList().getMOS().sort(Comparator.comparing(TMOSO::getPO));
        physicalPerson.setName(createName(toexoeio.getMOSList().getMOS()));
        toexoeio.getPRIList().getPRI().sort(Comparator.comparing(TPRIO::getPO));
        physicalPerson.setSurname(createSurname(toexoeio.getPRIList().getPRI()));
        toexoeio.getRPRList().getRPR().sort(Comparator.comparing(TRPRO::getPO));
        physicalPerson.setBirthname(createBirthName(toexoeio.getRPRList().getRPR()));

        physicalPerson.setBirthDate(toexoeio.getDN().getValue());
        return physicalPerson;
    }

    private void createTitles(PhysicalPersonT physicalPerson, List<TTOSTOIO> tosList, UserInfo userInfo) {

        for (TTOSTOIO ttostoio : tosList) {

            HodnotaCiselnika hodnotaCiselnika;
            String codelistCode = TITLES_BEFORE;

            if (ttostoio.getTT() == 1) {
                hodnotaCiselnika = hodnotaCiselnikaService.findByNazovPolozky(codelistCode, ttostoio.getTITTINA());
            } else if (ttostoio.getTT() == 2) {
                codelistCode = TITLES_AFTER;
                hodnotaCiselnika = hodnotaCiselnikaService.findByNazovPolozky(codelistCode, ttostoio.getTITTINA());
            } else {
                hodnotaCiselnika = hodnotaCiselnikaService.findByNazovPolozky(codelistCode, ttostoio.getTITTINA());
                if (hodnotaCiselnika == null) {
                    codelistCode = TITLES_AFTER;
                    hodnotaCiselnika = hodnotaCiselnikaService.findByNazovPolozky(codelistCode, ttostoio.getTITTINA());
                }
            }

            if (hodnotaCiselnika != null) {
                String titleNazov = hodnotaCiselnika.getNazovPolozky();
                String titleKod = hodnotaCiselnika.getKodPolozky();
                if (titleNazov.equals(ttostoio.getTITTINA())) {
                    if (codelistCode.equals(TITLES_BEFORE)) {
                        REGCodelistItemT titleBefore = new REGCodelistItemT();
                        titleBefore.setItemValue(titleNazov);
                        titleBefore.setItemCode(titleKod);
                        physicalPerson.getTitleBefore().add(titleBefore);
                    } else {
                        REGCodelistItemT titleAfter = new REGCodelistItemT();
                        titleAfter.setItemValue(titleNazov);
                        titleAfter.setItemCode(titleKod);
                        physicalPerson.getTitleAfter().add(titleAfter);
                    }
                }
            }
        }
    }

    private String createBirthName(List<TRPRO> trproList) {
        StringBuilder fullName = new StringBuilder();
        for (TRPRO trpro : trproList) {
            fullName.append(trpro.getRP());
            if (trproList.indexOf(trpro) < trproList.size() - 1) {
                fullName.append(" ");
            }
        }
        return fullName.toString();
    }

    private String createSurname(List<TPRIO> tprioList) {
        StringBuilder fullName = new StringBuilder();
        for (TPRIO tprio : tprioList) {
            fullName.append(tprio.getPR());
            if (tprioList.indexOf(tprio) < tprioList.size() - 1) {
                fullName.append(" ");
            }
        }
        return fullName.toString();
    }

    private String createName(List<TMOSO> tmosoList) {
        StringBuilder fullName = new StringBuilder();
        for (TMOSO tmoso : tmosoList) {
            fullName.append(tmoso.getME());
            if (tmosoList.indexOf(tmoso) < tmosoList.size() - 1) {
                fullName.append(" ");
            }
        }
        return fullName.toString();
    }

    private SimplifiedAddressT createAddress(TPOBPHRO pob, short sequence, UserInfo userInfo) {

        SimplifiedAddressT address = new SimplifiedAddressT();

        REGCodelistItemT addressUsage = new REGCodelistItemT();
        HodnotaCiselnika hodnotaCiselnika = hodnotaCiselnikaService.findByNazovPolozky(ADDRESS_USAGE_CODELIST_CODE, String.valueOf(pob.getTB()));
        if (hodnotaCiselnika != null) {
            addressUsage.setItemCode(hodnotaCiselnika.getKodPolozky());
        } else {
            addressUsage.setItemCode(String.valueOf(pob.getTP()));
        }
        address.setAddressUsage(addressUsage);

        PostalAddressT postalAddress = new PostalAddressT();
        postalAddress.setAddressInSyncWithREG(true);
        setAttributes(postalAddress, sequence);

        REGCodelistItemOptionalT municipality = new REGCodelistItemOptionalT();
        REGCodelistItemOptionalT region = new REGCodelistItemOptionalT();
        REGCodelistItemT state = new REGCodelistItemT();
        StreetT street = new StreetT();

        if (Boolean.parseBoolean(pob.getMP())) {

            municipality.setItemValue(pob.getOO());

            street.setStreetName(pob.getUM());
            street.setStreetNumber(pob.getOS());
            street.setRegistrationNumber(pob.getSI());
            street.setBuildingPart(pob.getCU());

            state.setItemCode(String.valueOf(pob.getST()));
            state.setItemValue(pob.getNS());

            if (pob.getREGList() != null) {
                region.setItemValue(createForeignAddressRegion(pob.getREGList().getREG()));
                postalAddress.setRegion(region);
            }
        } else {

            HodnotaCiselnika rfoMunicipality = hodnotaCiselnikaService.findByKodPolozkyAndKodCiselnika(String.valueOf(pob.getOA()), rfoCodelistCodelistCode);
            if (rfoMunicipality != null) {

                List<String> units = parseMunicipality(rfoMunicipality.getNazovPolozky());
                municipality = new REGCodelistItemOptionalT();
                municipality.setItemCode(units.get(2));

                region = new REGCodelistItemOptionalT();
                region.setItemCode(units.get(0));
                postalAddress.setRegion(region);
            } else {
                if (pob.getNO() != null && !pob.getNO().isEmpty()) {
                    municipality = new REGCodelistItemOptionalT();
                    municipality.setItemValue(pob.getNO());
                }
            }

            street.setStreetName(pob.getNU());
            street.setStreetNumber(pob.getOL());
            street.setRegistrationNumber(String.valueOf(pob.getSC()));
            street.setBuilding(String.valueOf(pob.getDI().getValue()));
            street.setBuildingPart(pob.getCB());

            state.setItemCode(SubjectReg1.ENUMERATION_COUNTRY_SLOVAKIA);
            state.setItemValue(pob.getNS());
        }

        postalAddress.setMunicipality(municipality);
        postalAddress.setState(state);
        postalAddress.setStreet(street);

        address.setPostalAddress(postalAddress);
        return address;
    }

    private List<String> parseMunicipality(String input) {
        List<String> result = new ArrayList<>();
        result.add(input.substring(0, 5));
        result.add(input.substring(0, 6));
        result.add(input);
        return result;
    }

    private String createForeignAddressRegion(List<TREGO> regList) {

        regList.sort(Comparator.comparing(TREGO::getPO));
        String region = null;
        for (TREGO reg : regList) {
            if (region == null) {
                region = reg.getRE();
            } else {
                String tmpItemCode = region.concat(" ").concat(reg.getRE());
                region = tmpItemCode.length() + 3 <= 255 ? tmpItemCode : region.concat("...");
            }
        }
        return region;
    }

    private ZaznamRegistraInputDetail prepareRegisterEntryInsertDetail(String xmlData, String registerId) {
        return new ZaznamRegistraInputDetail()
                .data(xmlData)
                .registerId(registerId)
                .verziaRegistraId(1)
                .modul(SubjectReg1.REG) // TODO
                .ucinnostOd(DateUtils.toLocalDate(new Date()));
    }

    private Specification<SubjectReg1RfoIdentificationEntity> getLoadRfoSubjectsSpec() {
        return (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.and(builder.isNotNull(root.get(AbstractRegPlugin.FIELD_ENTRY_ID)), builder.isNotNull(root.get(RFO_ID)), builder.isTrue(root.get(IS_POI)), builder.isFalse(root.get(IS_MERGED))));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

//    private void createAndSendNotification(SubjectReg1DataEntity subjectData, Event event, SubCategoryT subCategory, IdentificationErrorT identificationError) {
//        try {
//            NotificationT notification = notificationService.createNotification(notificationService.createRegisterValueEventData(SubjectReg1.REGISTER_ID, 1, subjectData.getId(), subCategory, null, null, identificationError, null, null, null, null, DateUtils.toLocalDate(subjectData.getValidFrom()),  DateUtils.toLocalDate(subjectData.getEffectiveFrom()), DateUtils.toLocalDate(subjectData.getEffectiveTo()), subjectData.getSubjectId()), event.getId(), CategoryT.IDENTIFICATION, DomainT.REGISTER_VALUE, null, null);
//            notificationService.sendNotifications(REGISTER_ENTRY_TOPIC.concat(SUBJECT_1), Collections.singletonList(notification));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private Specification<SubjectReg1RfoIdentificationEntity> getPoiMarkingSpec() {
        return (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.and(builder.isNotNull(root.get(AbstractRegPlugin.FIELD_ENTRY_ID)), builder.isNotNull(root.get(RFO_ID)), builder.isFalse(root.get(IS_POI))));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void poiUnmarking() {
        log.info("[RFO][IDENTIFICATION][POI-UNMARK][START]");
        try {
            List<SubjectReg1RfoIdentificationEntity> result = rfoIdentificationRepository.findAll(getPoiUnmarkingSpec());
            if (!result.isEmpty()) {
                for (int i = 0; i < result.size() / MAX_POI_RFO_RECORDS + 1; i++) {
                    createPoiUnmarkingRequest(PagingUtils.pagingElements(result, i, MAX_POI_RFO_RECORDS));
                }
            }
        } finally {
            log.info("[RFO][IDENTIFICATION][POI-UNMARK][END]");
        }
    }

    private void createPoiUnmarkingRequest(List<SubjectReg1RfoIdentificationEntity> subjects) {

        GetConsolidatedReferenceDataRequestCType dataRequest = new GetConsolidatedReferenceDataRequestCType();
        dataRequest.setOvmIsId(ovmIsId);
        dataRequest.setOeId(RFO_MARK);
        dataRequest.setScenario(UNMARK_PERSON_OF_INTEREST);
        dataRequest.setOvmTransactionId(UUID.randomUUID().toString());
        dataRequest.setOvmCorrelationId(UUID.randomUUID().toString());

        String corrId = UUID.randomUUID().toString();
        sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TransEnvTypeIn envTypeIn = new sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TransEnvTypeIn();
        envTypeIn.setCorrID(corrId);

        sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TOSOList tosoList = new sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TOSOList();
        subjects.forEach(subject -> {
            sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TOSO toso = new sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TOSO();
            toso.setID(subject.getRfoId());
            tosoList.getOSO().add(toso);
        });
        envTypeIn.setOSOList(tosoList);

        sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TUES tues = new sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TUES();
        tues.setPO(ovmIsId);
        tues.setTI(corrId);
        envTypeIn.setUES(tues);

        sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.ObjectFactory objectFactory = new sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.ObjectFactory();
        DataPlaceholderCType dataPlaceholder = new DataPlaceholderCType();
        dataPlaceholder.setAny(objectFactory.createTransEnvIn(envTypeIn));
        dataRequest.setPayload(dataPlaceholder);

        log.info("[RFO][IDENTIFICATION][POI-UNMARK]CSRU request from RfoIdentificationConfig.createPoiUnmarkingRequest: " + dataRequest.toString());

        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequest);
        csruEndpoint.sendPoiUnmarkingRequest(request).thenAccept(this::processPoiUnmarkingResponse).exceptionally(e -> {
            log.info("[RFO][IDENTIFICATION][POI-UNMARK]CSRU response from RfoIdentificationConfig.createPoiUnmarkingRequest: " + Throwables.getStackTraceAsString(e));
            return null;
        });
    }

    public void createPoiUnmarkingRequestSynchr(List<SubjectReg1RfoIdentificationEntity> subjects) {

        GetConsolidatedReferenceDataRequestCType dataRequest = new GetConsolidatedReferenceDataRequestCType();
        dataRequest.setOvmIsId(ovmIsId);
        dataRequest.setOeId(RFO_MARK);
        dataRequest.setScenario(UNMARK_PERSON_OF_INTEREST);
        dataRequest.setOvmTransactionId(UUID.randomUUID().toString());
        dataRequest.setOvmCorrelationId(UUID.randomUUID().toString());

        String corrId = UUID.randomUUID().toString();
        sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TransEnvTypeIn envTypeIn = new sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TransEnvTypeIn();
        envTypeIn.setCorrID(corrId);

        sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TOSOList tosoList = new sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TOSOList();
        subjects.forEach(subject -> {
            sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TOSO toso = new sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TOSO();
            toso.setID(subject.getRfoId());
            tosoList.getOSO().add(toso);
        });
        envTypeIn.setOSOList(tosoList);

        sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TUES tues = new sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TUES();
        tues.setPO(ovmIsId);
        tues.setTI(corrId);
        envTypeIn.setUES(tues);

        sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.ObjectFactory objectFactory = new sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.ObjectFactory();
        DataPlaceholderCType dataPlaceholder = new DataPlaceholderCType();
        dataPlaceholder.setAny(objectFactory.createTransEnvIn(envTypeIn));
        dataRequest.setPayload(dataPlaceholder);

        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequest);
        var response = csruEndpoint.sendPoiUnmarkingRequestSynchr(request);
        processPoiUnmarkingResponse(response.getValue());
    }

    private void processPoiUnmarkingResponse(GetConsolidatedReferenceDataResponseCType response) {
        SubjectReg1RfoIdentificationEntity rfoIdentification = null;
        try {
            log.info("[RFO][IDENTIFICATION][POI-UNMARK]CSRU response from RfoIdentificationConfig.createPoiUnmarkingRequest: " + response.toString());

            sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TransEnvTypeOut envTypeOut = processDecryptedResponse(encryptionUtils.decryptResponse(response.getPayload()), "http://www.egov.sk/mvsr/RFO/Zapis/Ext/ZrusenieOznaceniaZaujmovejOsobyWS-v1.0", sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TransEnvTypeOut.class);

            if (envTypeOut.getVSP().getKI() != 1) {
                String error = CHYBA_PRI_VYHLADAVANI_ZAUJMOVYCH_OSOB_NAVRATOVY_KOD_WS + envTypeOut.getVSP().getKI() + NAVRATOVY_OZNAM_WS + envTypeOut.getVSP().getPO();
                log.info(error);
            }

            for (sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TOEXO toexo : envTypeOut.getVSP().getOEXList().getOEX()) {
                rfoIdentification = rfoIdentificationRepository.findByRfoId(toexo.getID());
                if (toexo.getNK() == 1) {
                    rfoIdentificationRepository.delete(rfoIdentification);

                    Optional<RfoReg1IndexEntity> rfoIndex = rfoIndexRepository.findByKlucAndHodnotaAndAktualny("ID", rfoIdentification.getRfoId(), true);
                    zaznamRegistraController.zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdDelete(SubjectReg1.RFO, 1, rfoIndex.get().getZaznamId());
                } else {
                    saveErrorRfoIdentification(rfoIdentification, toexo.getDR());
                }
            }
        } catch (Exception e) {
            if (rfoIdentification != null) {
                saveErrorRfoIdentification(rfoIdentification, e.getMessage());
            }
            e.printStackTrace();
        }
    }

    private Specification<SubjectReg1RfoIdentificationEntity> getPoiUnmarkingSpec() {
        return (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.and(builder.isNull(root.get(AbstractRegPlugin.FIELD_ENTRY_ID)), builder.isNotNull(root.get(RFO_ID)), builder.isTrue(root.get(IS_POI))));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void identification() {
        log.info("[RFO][IDENTIFICATION][IDENT][START]");
        try {
            @SuppressWarnings("unused")
            List<SubjectReg1RfoIdentificationEntity> result = rfoIdentificationRepository.findAll(getIdentificationSpec());
        } finally {
            log.info("[RFO][IDENTIFICATION][IDENT][END]");
        }
    }

    private Specification<SubjectReg1RfoIdentificationEntity> getIdentificationSpec() {
        return (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.and(builder.isNotNull(root.get(AbstractRegPlugin.FIELD_ENTRY_ID)), builder.isNull(root.get(RFO_ID))));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private sk.is.urso.csru.fo.detailIfo.TransEnvTypeOut processDecryptedResponse(Document document) {

        Element dz = (Element) document.getElementsByTagName("DataZasielkyBase64").item(0);
        if (dz == null) {
            sk.is.urso.csru.fo.detailIfo.ObjectFactory objectFactory = new sk.is.urso.csru.fo.detailIfo.ObjectFactory();
            return objectFactory.createTransEnvTypeOut();
        }

        String decryptedData = new String(Base64.getDecoder().decode(dz.getTextContent()));
        RegistrationType registration = XmlUtils.parse(decryptedData, RegistrationType.class);
        DocumentUnauthorizedType documentUnauthorized = ((JAXBElement<DocumentUnauthorizedType>) registration.getAny().get(0)).getValue();
        ObjectType objectType = documentUnauthorized.getObject().get(0);
        return XmlUtils.unmarshall((Node) objectType.getContent().get(0), sk.is.urso.csru.fo.detailIfo.TransEnvTypeOut.class);
    }

    private <T> T processDecryptedResponse(Document document, String namespaceUri, Class<T> transEnvTypeOutClass) {

        Element transEnvOut = (Element) document.getElementsByTagNameNS(namespaceUri, "TransEnvOut").item(0);
        if (transEnvOut == null) {
            return null;
        }
        return XmlUtils.unmarshall(transEnvOut, transEnvTypeOutClass);
    }

    public List<ZaznamRegistraOutputDetail> rfoDataById(List<SubjectReg1RfoIdentificationEntity> subjects) {

        var jaxbContext = ((Jaxb2Marshaller) csruFoWS.getMarshaller()).getJaxbContext();
        sk.is.urso.csru.fo.zoznamIfo.ObjectFactory objFactory = new sk.is.urso.csru.fo.zoznamIfo.ObjectFactory();
        sk.is.urso.csru.fo.detailIfo.ObjectFactory objectFactory = new sk.is.urso.csru.fo.detailIfo.ObjectFactory();
        sk.is.urso.csru.fo.detailIfo.TransEnvTypeIn envTypeIn = new sk.is.urso.csru.fo.detailIfo.TransEnvTypeIn();
        TPOD tpod = new TPOD();

        sk.is.urso.csru.fo.detailIfo.TUES tues = new sk.is.urso.csru.fo.detailIfo.TUES();
        tues.setPO(ovmIsId);
        tues.setTI(UUID.randomUUID().toString());
        tpod.setUES(tues);

        TIOSList tiosList = new TIOSList();
        subjects.forEach(subject -> {
            TIOS tios = new TIOS();
            tios.setIF(subject.getRfoId());
            tiosList.getIOS().add(tios);
        });
        tpod.setIOSList(tiosList);

        TSPISPNList tspispnList = new TSPISPNList();
        List<String> values = Arrays.asList("Administratívne údaje", "Lokačné údaje", "Vzťahové údaje", "Identifikačné údaje");
        for (int i = 0; i < 4; i++) {
            TSPISPN tspispn = new TSPISPN();
            tspispn.setHO(i + 1);
            tspispn.setTUDHONA(values.get(i));
            tspispnList.getSPI().add(tspispn);
        }
        tpod.setSPIList(tspispnList);

        envTypeIn.setPOD(tpod);

        ObjectType object = new ObjectType();
        object.setId(UUID.randomUUID().toString());
        object.setIdentifier(POSKYTNUTIE_UDAJOV_IFOONLINE_WS_V_1_0_XSD); // TODO
        object.getContent().add(objectFactory.createTransEnvIn(envTypeIn));

        DocumentUnauthorizedType documentUnauthorized = new DocumentUnauthorizedType();
        documentUnauthorized.setId(RFO_PS_REFERENCNE_UDAJE_ZOZNAMU_IFO_ONLINE_BEZ_ZEP_WS_IN_1_0);
        documentUnauthorized.getObject().add(object);

        RegistrationType registrationType = new RegistrationType();
        registrationType.setId(RFO_PS_REFERENCNE_UDAJE_ZOZNAMU_IFO_ONLINE_BEZ_ZEP_WS_IN_1_0);
        registrationType.getAny().add(objFactory.createDocumentUnauthorized(documentUnauthorized));

        PodanieType podanieType = new PodanieType();
        podanieType.setIdentifikatorSubjektu(identifikatorSubjektu);
        podanieType.setTypPodania(RFO_PS_REFERENCNE_UDAJE_ZOZNAMU_IFO_ONLINE_BEZ_ZEP_WS_IN_1_0);
        podanieType.setTypSluzby(RFO_PODP_EXT_REFERENCNE_UDAJE_ZOZNAMU_IFO_ONLINE_BEZ_ZEP_WS_1_0);
        String dataPodaniaBase64 = Base64.getEncoder().encodeToString(XmlUtils.xmlToString(objFactory.createRegistration(registrationType), jaxbContext).getBytes(StandardCharsets.UTF_8));
        podanieType.setDataPodaniaBase64(dataPodaniaBase64);

        GetConsolidatedReferenceDataRequestCType dataRequestCType = new GetConsolidatedReferenceDataRequestCType();
        dataRequestCType.setOvmIsId(ovmIsId);
        dataRequestCType.setOeId(RFO_PERSON);
        dataRequestCType.setScenario(DATA_BY_ID);
        dataRequestCType.setOvmTransactionId(UUID.randomUUID().toString());
        dataRequestCType.setOvmCorrelationId(UUID.randomUUID().toString());

        DataPlaceholderCType dataPlaceholderCType = new DataPlaceholderCType();
        sk.is.urso.csru.fo.ObjectFactory factory = new sk.is.urso.csru.fo.ObjectFactory();
        dataPlaceholderCType.setAny(factory.createPodanie(podanieType));
        dataRequestCType.setPayload(dataPlaceholderCType);

        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequestCType);
        var response = csruEndpoint.sendRfoLoadRequestSynchr(request);
        return processRfoLoadResponseAndSaveSubject(response.getValue(), subjects);
    }

    private String getSimplifiedValue(Document document, String xpathString) {
        try {
            var xPathfactory = XPathFactory.newInstance();
            var xpath = xPathfactory.newXPath();
            return ((Node) xpath.compile(ignoreNameSpace(xpathString)).evaluate(document, XPathConstants.NODE)).getFirstChild().getNodeValue().toLowerCase(Locale.ROOT);
        } catch (XPathExpressionException e) {
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Xml neobsahuje ID element.");
        }
    }

    private String ignoreNameSpace(String xPath) {
        List<String> elementList = Arrays.asList(xPath.split("/"));

        StringBuilder stringBuilder = new StringBuilder();

        elementList.forEach(element -> {
            if (!element.equals("")) {
                element = element.replaceAll("[a-zA-Z]*.:", "");
                stringBuilder.append("/*[local-name() = '").append(element).append("']");
            }
        });
        return stringBuilder.toString();
    }

    private List<ZaznamRegistraOutputDetail> processRfoLoadResponseAndSaveSubject(GetConsolidatedReferenceDataResponseCType response, List<SubjectReg1RfoIdentificationEntity> subjects) {
        SubjectReg1RfoIdentificationEntity errorRfoIdentification = null;
        List<ZaznamRegistraOutputDetail> rfoList = new ArrayList<>();

        try {

            sk.is.urso.csru.fo.detailIfo.TransEnvTypeOut envTypeOut = processDecryptedResponse(encryptionUtils.decryptResponse(response.getPayload()));

            if (envTypeOut.getPOV().getKO() != 1) {
                String error = CHYBA_PRI_VYHLADAVANI_ZAUJMOVYCH_OSOB_NAVRATOVY_KOD_WS + envTypeOut.getPOV().getKO() + NAVRATOVY_OZNAM_WS + envTypeOut.getPOV().getNU();
                log.info(error);
            }

            sk.is.urso.csru.fo.detailIfo.ObjectFactory objectFactory = new sk.is.urso.csru.fo.detailIfo.ObjectFactory();
            Document document = XmlUtils.parse(XmlUtils.xmlToString(objectFactory.createTransEnvOut(envTypeOut)));

            Document newDocument = null;
            Element oexElement = null;

            NodeList oexList = document.getElementsByTagName("OEX");
            for (int i = 0; i < oexList.getLength(); i++) {

                oexElement = (Element) oexList.item(i);
                newDocument = XmlUtils.newDocument();
                newDocument.appendChild(newDocument.adoptNode(oexElement.cloneNode(true)));

                Element rootNode = newDocument.getDocumentElement();
                rootNode.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                rootNode.setAttribute("xsi:schemaLocation", "http://www.dominanz.sk/UVZ/Reg/FO ../../uvz_reg_common/configuration/RFO_1/RFO_1_DATA.xsd");
                newDocument.renameNode(rootNode, "http://www.dominanz.sk/UVZ/Reg/FO", "fo:FO");
                XmlUtils.addNamespaceRecursive(newDocument.getFirstChild(), "http://www.dominanz.sk/UVZ/Reg/FO", "fo:");


                UserInfo userInfo = new UserInfo();
                userInfo.setLogin(userSystemLogin);
                userInfo.setAdministrator(true);

                // ak existuje nevytvaraj novy
                String id = getSimplifiedValue(newDocument, "fo:FO/fo:ID");
                Optional<RfoReg1IndexEntity> rfoIndex = rfoIndexRepository.findByKlucAndHodnotaAndAktualny("ID", id, true);
                if (!rfoIndex.isPresent()) {
                    // uloz
                    ResponseEntity<ZaznamRegistraOutputDetail> insertResponse = zaznamRegistraController.zaznamRegistraPut(prepareRegisterEntryInsertDetail(XmlUtils.xmlToString(newDocument), SubjectReg1.RFO), userInfo);
                    if (insertResponse.getStatusCode().equals(HttpStatus.OK)) {

                        SubjectT subjectT = rfoService.reflectRfoDataToSubject(XmlUtils.parse(newDocument, sk.is.urso.model.subject.detailIfo.TOEXOEIO.class), insertResponse.getBody().getZaznamId());
                        subjectT.setSubjectID(AbstractRegPlugin.ID_NULL);
                        String subjectXml = XmlUtils.xmlToString(new sk.is.urso.subject.v1.ObjectFactory().createSubject(subjectT));
                        // ak existuje subjekt tak neurob nic
                        SubjectReg1DataEntity subjectReg1DataEntity = subjectDataRepository.findByFoId(subjectT.getRfoReference().getExternalId());
                        if (subjectReg1DataEntity == null) {
                            var subj = zaznamRegistraController.zaznamRegistraPut(prepareRegisterEntryInsertDetail(subjectXml, SubjectReg1.REGISTER_ID), userInfo);

                            SubjectReg1DataEntity dataEntity = (SubjectReg1DataEntity) subjectDataRepository.findById(subj.getBody().getZaznamId()).get();
                            dataEntity.setFoId(subjectT.getRfoReference().getExternalId());
                            subjectDataRepository.save(dataEntity);

                            ZaznamRegistraOutputDetail outputDetail = insertResponse.getBody();
                            TOEXOEIO toexoeio = null;
                            for (TOEXOEIO oex : envTypeOut.getPOV().getOEXList().getOEX()) {
                                if (oex.getID().getValue().equals(oexElement.getElementsByTagName(ID).item(0).getFirstChild().getNodeValue())) {
                                    toexoeio = oex;
                                    break;
                                }
                            }

                            SubjectReg1RfoIdentificationEntity rfoIdentification = null;
                            for (SubjectReg1RfoIdentificationEntity rfo : subjects) {
                                errorRfoIdentification = rfo;
                                if (rfo.getRfoId().equalsIgnoreCase(toexoeio.getID().getValue())) {
                                    rfoIdentification = rfo;
                                    break;
                                }
                            }

                            if (subj.getStatusCode().equals(HttpStatus.OK)) {
                                Optional<SubjectReg1DataEntity> subjectDataEntity = subjectDataRepository.findById(subj.getBody().getZaznamId());
                                rfoIdentification.setZaznamId(subjectDataEntity.get());
                                rfoIdentification.setZluceny(true);
                                rfoIdentificationRepository.save(rfoIdentification);
                            }

                            zaznamRegistraController.zaznamRegistraReferenciaRegisterIdVerziaRegistraIdZaznamIdModulIdPut(SubjectReg1.RFO, 1, outputDetail.getZaznamId(), SubjectReg1.REG);
                            zaznamRegistraController.zaznamRegistraReferenciaRegisterIdVerziaRegistraIdZaznamIdModulIdPut(SubjectReg1.REGISTER_ID, 1, subj.getBody().getZaznamId(), SubjectReg1.REG);
                        }
                        rfoList.add(insertResponse.getBody());
                    }
                } else {
                    var rfo = zaznamRegistraController.zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdGet(SubjectReg1.RFO, 1, rfoIndex.get().getZaznamId(), userInfo);
                    rfoList.add(rfo.getBody());
                }
            }

        } catch (Exception ex) {
            if (errorRfoIdentification != null) {
                saveErrorRfoIdentification(errorRfoIdentification, ex.getMessage());
            }
            ex.printStackTrace();
        }
        return rfoList;
    }

    public void createPoiMarkingRequestSynchr(List<SubjectReg1RfoIdentificationEntity> subjects) {

        GetConsolidatedReferenceDataRequestCType dataRequest = new GetConsolidatedReferenceDataRequestCType();
        dataRequest.setOvmIsId(ovmIsId);
        dataRequest.setOeId(RFO_MARK);
        dataRequest.setScenario(MARK_PERSON_OF_INTEREST);
        dataRequest.setOvmTransactionId(UUID.randomUUID().toString());
        dataRequest.setOvmCorrelationId(UUID.randomUUID().toString());

        String corrId = UUID.randomUUID().toString();
        TransEnvTypeIn envTypeIn = new TransEnvTypeIn();
        envTypeIn.setCorrID(corrId);

        TOSOList tosoList = new TOSOList();
        subjects.forEach(subject -> {
            TOSO toso = new TOSO();
            toso.setID(subject.getRfoId());
            tosoList.getOSO().add(toso);
        });
        envTypeIn.setOSOList(tosoList);

        TUES tues = new TUES();
        tues.setPO(ovmIsId);
        tues.setTI(corrId);
        envTypeIn.setUES(tues);

        sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.ObjectFactory objectFactory = new sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.ObjectFactory();
        DataPlaceholderCType dataPlaceholder = new DataPlaceholderCType();
        dataPlaceholder.setAny(objectFactory.createTransEnvIn(envTypeIn));
        dataRequest.setPayload(dataPlaceholder);

        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequest);
        var response = csruEndpoint.sendPoiMarkingRequestSynchr(request);
        processPoiMarkingResponseTesting(response.getValue());
    }
}
