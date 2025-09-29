package sk.is.urso.config.csru;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.alfa.exception.CommonException;
import org.alfa.model.UserInfo;
import org.alfa.utils.DateUtils;
import org.alfa.utils.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;
import sk.is.urso.csru.fo.codelist.ObjectFactory;
import sk.is.urso.csru.fo.codelist.TPCI;
import sk.is.urso.csru.fo.codelist.TTIT;
import sk.is.urso.csru.fo.codelist.TTITO;
import sk.is.urso.csru.fo.codelist.TUCE;
import sk.is.urso.csru.fo.codelist.TUCEO;
import sk.is.urso.csru.fo.codelist.TransEnvTypeIn;
import sk.is.urso.csru.fo.codelist.TransEnvTypeOut;
import sk.is.urso.enumimpexp.v1.CodeListDataRecType;
import sk.is.urso.enumimpexp.v1.CodeListType;
import sk.is.urso.enumimpexp.v1.ImportDescriptionType;
import sk.is.urso.enumimpexp.v1.ImportExportChoiceType;
import sk.is.urso.enumimpexp.v1.ImportExportType;
import sk.is.urso.enumimpexp.v1.ImportOperationType;
import sk.is.urso.enumimpexp.v1.LocalizedAdditionalContentType;
import sk.is.urso.enumimpexp.v1.LocalizedCodelistManagerType;
import sk.is.urso.enumimpexp.v1.LocalizedCodelistNameType;
import sk.is.urso.enumimpexp.v1.LocalizedItemNameType;
import sk.is.urso.enumimpexp.v1.RecordsType;
import sk.is.urso.model.Udalost;
import sk.is.urso.model.ciselniky.Ciselnik;
import sk.is.urso.model.csru.api.sync.common.DataPlaceholderCType;
import sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.GetConsolidatedReferenceDataRequestCType;
import sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.GetConsolidatedReferenceDataResponseCType;
import sk.is.urso.repository.ciselniky.CiselnikRepository;
import sk.is.urso.rest.model.HodnotaCiselnikaShortDetail;
import sk.is.urso.service.UdalostService;
import sk.is.urso.service.csru.ImportService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CsruRfoCodelistConfig {
    private static final Logger log = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    private static final String RFO_CODELIST = "RFO_Codelist";
    private static final String CODELIST = "codelist";
    private static final String UZEMNY_CELOK = "Územný celok";
    private static final String XML_EXTENSION = ".xml";
    public static final String CSRU = "CSRU";

    @Autowired
    @Qualifier("csruFoWS")
    WebServiceTemplate csruFoWS;

    @Autowired
    CsruEndpoint csruEndpoint;

    @Autowired
    @Qualifier("csruGetFile")
    protected RestTemplate restTemplate;

//    @Autowired
//    private EnumerationListService enumerationListService;
//
//    @Autowired
//    private EnumerationValueListRepository enumerationValueListRepository;
//
//    @Autowired
//    private EnumerationRepositoryService enumerationRepositoryService;
//
//    @Autowired
//    private EnumerationMultivaluesService enumerationMultivaluesService;

    @Autowired
    private CiselnikRepository ciselnikRepository;

    @Autowired
    private UdalostService udalostService;

    @Autowired
    private ImportService importService;

    @Value("${integration.csru.ovmIsId}")
    private String ovmIsId;

    @Value("${integration.rfo.codelist.initial.import.allowed:false}")
    private boolean csruRfoCodelistInitialImportAllowed;

    @Value("${user.system.login}")
    private String userSystemLogin;

    @Value("${integration.csru.rfo.codelist.codelistCode}")
    private String rfoCodelistCodelistCode;

    @Value("${integration.csru.rfo.codelist.xml.files}")
    private String rfoCodelistXmlFiles;

    final ObjectFactory objectFactory = new ObjectFactory();

//    @Bean
//    @Scheduled(initialDelay = 1000, fixedDelay=Long.MAX_VALUE)
//    @SchedulerLock(name = "TaskScheduler_csruRfoCodelistConfigInit", lockAtLeastForString = "${shedlock.least}")
    public void runAtStartCsruRfoCodelistConfig()
    {
        Thread thread;
        if (csruRfoCodelistInitialImportAllowed) {
            thread = new Thread(this::rfoCodelistInitialImport);
        }
        else {
            thread = new Thread(this::rfoCodelistChanges);
        }
        thread.start();
    }

//    @Scheduled(cron = "${cron.CsruRfoCodelistConfig.expression}")
//    @SchedulerLock(name = "TaskScheduler_csruRfoCodelistConfig", lockAtLeastForString = "${shedlock.least}")
    private void rfoCodelistChanges() {
        log.info("[RFO CODELIST][CHANGES][START]");
        try {



        } finally {
            log.info("[RFO CODELIST][CHANGES][END]");
        }
    }

    private void rfoCodelistInitialImport() {
        log.info("[RFO CODELIST][INITIAL_LOAD][START]");
        try {

            //Ak ciselnik existuje a ma ulozene hodnoty, tak uz ho nestahujeme
//            if (enumerationListService.findByEnumPrefix(rfoCodelistCodelistCode) != null &&
//                    !enumerationValueListRepository.findAllByEnumerationValueListId_CodelistCode(rfoCodelistCodelistCode).isEmpty()) {
//                return;
//            }

            Ciselnik ciselnik = ciselnikRepository.findByKodCiselnikaAndDeletedIsFalse(rfoCodelistCodelistCode).orElse(null);
            if (ciselnik == null) {
                ciselnik = new Ciselnik();
                ciselnik.setKodCiselnika(rfoCodelistCodelistCode);
                ciselnik.setPlatnostOd(LocalDate.now());
                ciselnik.setId(null);
                ciselnik.setNazovCiselnika(UZEMNY_CELOK);
                ciselnikRepository.save(ciselnik);

//                EnumerationMultivalues enumerationMultivalues = new EnumerationMultivalues();
//                enumerationMultivalues.setCodelistName(UZEMNY_CELOK);
//                enumerationMultivalues.setEnumeration(ciselnik);
//                enumerationMultivalues.setEnumerationMultivaluesId(new EnumerationMultivaluesId());
//                enumerationMultivalues.getEnumerationMultivaluesId().setLocaleId(SK_LOCALE);
//                enumerationMultivalues.getEnumerationMultivaluesId().setMultivalueIndex(0);
//                enumerationMultivalues.getEnumerationMultivaluesId().setId(ciselnik.getId());
//                enumerationMultivaluesService.save(enumerationMultivalues);
            }

            RecordsType records = new RecordsType();
            Map<String, CodeListDataRecType> uniqueEnumValues = new HashMap<>();
            int page = 1;
            while (true) {
                List<TUCEO> tuceoList = initialLoad(page++);
                for (TUCEO tuceo : tuceoList) {
                    if (tuceo.getTU() == 3) {

                        CodeListDataRecType codeListDataRecType = new CodeListDataRecType();
                        codeListDataRecType.setItemCode(String.valueOf(tuceo.getID()));
                        codeListDataRecType.setValidFrom(DateUtils.nowXmlDateTime());
                        codeListDataRecType.setEffectiveFrom(tuceo.getFR());
                        if (codeListDataRecType.getEffectiveFrom() == null) {
                            if (tuceo.getTO() != null) {
                                codeListDataRecType.setEffectiveFrom(tuceo.getTO());
                            }
                            else {
                                codeListDataRecType.setEffectiveFrom(DateUtils.nowXmlDateTime());
                            }
                        }
                        codeListDataRecType.setEffectiveTo(tuceo.getTO());
                        codeListDataRecType.setItemLogicalOrder(1);

                        LocalizedItemNameType localizedItemNameType = new LocalizedItemNameType();
                        localizedItemNameType.setDefaultItemName(tuceo.getEK());
                        codeListDataRecType.getItemName().add(localizedItemNameType);

                        LocalizedAdditionalContentType additionalContentType = new LocalizedAdditionalContentType();
                        additionalContentType.setDefaultAdditionalContent(tuceo.getNA());
                        codeListDataRecType.getAdditionalContent().add(additionalContentType);

                        uniqueEnumValues.put(String.valueOf(tuceo.getID()), codeListDataRecType);
                    }
                }
                if (tuceoList.stream().anyMatch(tuceo -> tuceo.getTU() > 3)) {
                    break;
                }
            }
            records.getRecord().addAll(uniqueEnumValues.values());

            LocalizedCodelistNameType localizedCodelistName = new LocalizedCodelistNameType();
            localizedCodelistName.setDefaultCodelistName(UZEMNY_CELOK);

            LocalizedCodelistManagerType localizedCodelistManager = new LocalizedCodelistManagerType();
            localizedCodelistManager.setDefaultCodelistManager("Registre");

            CodeListType codelistType = new CodeListType();
            codelistType.setCodelistCode(rfoCodelistCodelistCode);
            codelistType.setCodelistName(localizedCodelistName);
            codelistType.getCodelistManager().add(localizedCodelistManager);
            codelistType.setValidFrom(DateUtils.nowXmlDateTime());
            codelistType.setEffectiveFrom(DateUtils.nowXmlDateTime());

            ImportDescriptionType importDescriptionType = new ImportDescriptionType();
            importDescriptionType.setOperation(ImportOperationType.INITIAL_LOAD);
            importDescriptionType.setCodelistCode(ciselnik.getKodCiselnika());
            importDescriptionType.setImportCreationTime(DateUtils.nowXmlDateTime());
            importDescriptionType.setCodelistDataDate(DateUtils.nowXmlDateTime());
            importDescriptionType.setCodeList(codelistType);
            importDescriptionType.setDataAuthor(CSRU);

            ImportExportChoiceType importExportChoiceType = new ImportExportChoiceType();
            importExportChoiceType.setImport(importDescriptionType);

            ImportExportType newImportExport = new ImportExportType();
            newImportExport.setVersion(1);
            newImportExport.setType(importExportChoiceType);
            newImportExport.setRecords(records);

            UserInfo userInfo = new UserInfo();
            userInfo.setLogin(userSystemLogin);
            userInfo.setAdministrator(true);

            sk.is.urso.enumimpexp.v1.ObjectFactory factory = new sk.is.urso.enumimpexp.v1.ObjectFactory();
            String xml = XmlUtils.xmlToString(factory.createImportExport(newImportExport));

            LocalTime time = new Timestamp(System.currentTimeMillis()).toLocalDateTime().toLocalTime();
            String formattedTime = time.format(DateTimeFormatter.ofPattern("-HH-mm-ss"));
            Files.writeString(Path.of(rfoCodelistXmlFiles, RFO_CODELIST + formattedTime + XML_EXTENSION), xml, StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE);

//            Udalost event = udalostService.createEvent(EventDomainEnum.ENUMERATION, EventCategoryEnum.UPDATE, userSystemLogin);
//            importService.processOperation(ImportHeader.OperationEnum.INITIAL_LOAD, event, newImportExport, userInfo);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            log.info("[RFO CODELIST][INITIAL_LOAD][END]");
        }
    }

    private List<TUCEO> initialLoad(int page) {

        try {

            GetConsolidatedReferenceDataResponseCType response = sendRfoCodelistRequest(page);
            if (response.getResultCode() == 0) {

                JAXBElement<TransEnvTypeOut> transEnvTypeOut = (JAXBElement<TransEnvTypeOut>) response.getPayload().getAny();
                TransEnvTypeOut envTypeOut = transEnvTypeOut.getValue();
                if (envTypeOut.getVSP().getKI() == 1) {
                    return envTypeOut.getUCEList().getUCE();
                }
                else {
                    //processError(localCopy, envTypeOut.getVSP().getPO());
                }
            }
            else {
                //processError(localCopy, response.getErrorMessage());
                Thread.sleep(5000);
                initialLoad(page);
            }

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    public GetConsolidatedReferenceDataResponseCType sendRfoCodelistRequest(int page) {

        GetConsolidatedReferenceDataRequestCType dataRequest = new GetConsolidatedReferenceDataRequestCType();
        dataRequest.setOvmIsId(ovmIsId);
        dataRequest.setOeId(RFO_CODELIST);
        dataRequest.setScenario(CODELIST);
        dataRequest.setOvmTransactionId(UUID.randomUUID().toString());
        dataRequest.setOvmCorrelationId(UUID.randomUUID().toString());

        TransEnvTypeIn envTypeIn = new TransEnvTypeIn();
        envTypeIn.setCorrID(UUID.randomUUID().toString());

        TPCI tpci = new TPCI();
        tpci.setDO(DateUtils.toXmlDate("0001-01-01T00:00:00+02:00"));
        tpci.setDD(DateUtils.toXmlDate(OffsetDateTime.now().toString()));
        tpci.setPS(page);
        tpci.setUCE(new TUCE());
        envTypeIn.setPCI(tpci);


        DataPlaceholderCType dataPlaceholder = new DataPlaceholderCType();
        dataPlaceholder.setAny(objectFactory.createTransEnvIn(envTypeIn));
        dataRequest.setPayload(dataPlaceholder);

        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequest);


//        try {
//            // Create JAXBContext
//            JAXBContext jaxbContext = JAXBContext.newInstance(GetConsolidatedReferenceDataRequestCType.class);
//
//            // Create Marshaller
//            Marshaller marshaller = jaxbContext.createMarshaller();
//
//            // Set properties for formatting
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//
//            // Marshal to XML string
//            StringWriter stringWriter = new StringWriter();
//            marshaller.marshal(request, stringWriter);
//
//            // Get XML string
//            String xmlString = stringWriter.toString();
//
//            // Print XML string
//            System.out.println(xmlString);
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        }

        JAXBElement<GetConsolidatedReferenceDataResponseCType> response = csruEndpoint.sendRfoCodelistRequestSync(request);

//        try {
//            JAXBElement<GetConsolidatedReferenceDataResponseCType> response = csruEndpoint.sendRfoCodelistRequestSync(request);
//        } catch (Exception ex) {
//            System.out.println(ex);
//        }
//        return null;
        return response.getValue();
    }

    public List<HodnotaCiselnikaShortDetail> sendRfoCodelistRequestTitles() {
        GetConsolidatedReferenceDataRequestCType dataRequest = new GetConsolidatedReferenceDataRequestCType();
        dataRequest.setOvmIsId(ovmIsId);
        dataRequest.setOeId(RFO_CODELIST);
        dataRequest.setScenario(CODELIST);
        dataRequest.setOvmTransactionId(UUID.randomUUID().toString());
        dataRequest.setOvmCorrelationId(UUID.randomUUID().toString());

        TransEnvTypeIn envTypeIn = new TransEnvTypeIn();
        envTypeIn.setCorrID(UUID.randomUUID().toString());

        TPCI tpci = new TPCI();
        tpci.setDO(DateUtils.toXmlDate("0001-01-01T00:00:00+02:00"));
        tpci.setDD(DateUtils.toXmlDate(OffsetDateTime.now().toString()));
        tpci.setPS(1);
        tpci.setTIT(new TTIT());
        envTypeIn.setPCI(tpci);

        DataPlaceholderCType dataPlaceholder = new DataPlaceholderCType();
        dataPlaceholder.setAny(objectFactory.createTransEnvIn(envTypeIn));
        dataRequest.setPayload(dataPlaceholder);

        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequest);

        JAXBElement<GetConsolidatedReferenceDataResponseCType> response = csruEndpoint.sendRfoCodelistRequestSync(request);

        GetConsolidatedReferenceDataResponseCType responseType = response.getValue();
        if (responseType.getResultCode() == 0) {
            JAXBElement<TransEnvTypeOut> transEnvTypeOut = (JAXBElement<TransEnvTypeOut>) responseType.getPayload().getAny();
            TransEnvTypeOut envTypeOut = transEnvTypeOut.getValue();
            if (envTypeOut.getVSP().getKI() == 1) {
                List<HodnotaCiselnikaShortDetail> hodnotaCiselnikaList = new ArrayList<>();
                List<TTITO> ttitoList = envTypeOut.getTITList().getTIT();
                for (TTITO ttito : ttitoList) {
                    HodnotaCiselnikaShortDetail hodnotaCiselnika = new HodnotaCiselnikaShortDetail();
                    hodnotaCiselnika.setKodPolozky(String.valueOf(ttito.getID()));
                    hodnotaCiselnika.setNazovPolozky(ttito.getNA());
                    hodnotaCiselnikaList.add(hodnotaCiselnika);
                }
                return hodnotaCiselnikaList;
            }
        }
        throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Error pri dotahovani ciselnika titulov.");
    }
}
