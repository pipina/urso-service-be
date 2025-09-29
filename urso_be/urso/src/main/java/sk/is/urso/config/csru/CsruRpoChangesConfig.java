package sk.is.urso.config.csru;

import com.google.common.base.Throwables;
import net.javacrumbs.shedlock.core.SchedulerLock;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import org.alfa.exception.CommonException;
import org.alfa.model.UserInfo;
import org.alfa.utils.DateUtils;
import org.alfa.utils.XmlUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import sk.is.urso.controller.ZaznamRegistraController;
import sk.is.urso.csru.po.changes.GeneratedFileCType;
import sk.is.urso.csru.po.changes.ListChangedSubjectsFiles;
import sk.is.urso.csru.po.changes.ListChangedSubjectsFilesResponse;
import sk.is.urso.csru.po.changes.ObjectFactory;
import sk.is.urso.csru.po.changes.TransEnvelopeInType;
import sk.is.urso.csru.po.changes.TransEnvelopeOutType;
import sk.is.urso.model.csru.CsruChange;
import sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.GetConsolidatedReferenceDataRequestCType;
import sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.GetConsolidatedReferenceDataResponseCType;
import sk.is.urso.model.csru.api.sync.common.DataPlaceholderCType;
import sk.is.urso.plugin.SubjectReg1;
import sk.is.urso.plugin.entity.RpoReg1DataEntity;
import sk.is.urso.plugin.entity.SubjectReg1DataEntity;
import sk.is.urso.plugin.entity.SubjectReg1IndexEntity;
import sk.is.urso.plugin.repository.RpoReg1DataRepository;
import sk.is.urso.plugin.repository.RpoReg1IndexRepository;
import sk.is.urso.plugin.repository.SubjectReg1DataRepository;
import sk.is.urso.plugin.repository.SubjectReg1IndexRepository;
import sk.is.urso.reg.AdditionalPluginOps;
import sk.is.urso.repository.csru.CsruChangeRepository;
import sk.is.urso.reg.model.ZaznamRegistraInputDetail;
import sk.is.urso.rest.model.CsruTypeEnum;
import sk.is.urso.service.csru.CsroRpoXmlErrorService;
import sk.is.urso.service.csru.CsruChangeService;
import sk.is.urso.subject.v1.SubjectT;

import javax.xml.bind.JAXBElement;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Component
public class CsruRpoChangesConfig {

    private static final Logger log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    private static final String NAVRATOVY_OZNAM_WS = ", Návratový oznam WS: ";
    private static final String CHYBA_PRI_ZAEVIDNOVANI_ZMIEN_REFERENCNYCH_UDAJOV_KOD_WS = "Chyba pri vyhľadávaní záujmových osôb. Návratový kód WS: ";

    public static final String CHYBA_PRI_UKLADANI_SUBORU = "Chyba pri ukladaní súboru";
    private static final String RPO_CHANGES = "rpo_change_";
    private static final String XML_EXTENSION = ".xml";
    private static final String RPO_CHANGE_XML_TXT = "rpo_change_xml.txt";

    @Value("${integration.csru.ovmIsId}")
    private String ovmIsId;

    @Value("${integration.csru.rpoGetFile.url}")
    private String rpoGetFileUrl;

    @Value("${integration.csru.rpo.changes.xml.files}")
    private String rpoChangesXmlFiles;

    @Autowired
    @Qualifier("csruGetFile")
    protected RestTemplate restTemplate;

    @Value("${user.system.login}")
    private String userSystemLogin;

    @Value("${integration.csru.rpo.changed.files}")
    private String rpoChangedFilesPath;

    @Value("${integration.csru.username}")
    public String username;

    @Value("${integration.csru.password}")
    public String password;

    @Autowired
    AdditionalPluginOps additionalOps;

    @Autowired
    CsruEndpoint csruEndpoint;

//    @Autowired
//    EventService eventService;

    @Autowired
    RpoReg1IndexRepository rpoIndexRepository;

    @Autowired
    SubjectReg1IndexRepository subjectReg1IndexRepository;

    @Autowired
    SubjectReg1DataRepository subjectReg1DataRepository;

    @Autowired
    ZaznamRegistraController zaznamRegistraController;

    @Autowired
    CsruChangeRepository csruChangeRepository;

    @Autowired
    CsruChangeService csruChangeService;

    @Autowired
    CsroRpoXmlErrorService csruRpoXmlErrorService;

//    @Autowired
//    RpoIdentificationConfig rpoIdentificationConfig;

    @Autowired
    RpoReg1DataRepository rpoReg1DataRepository;

    final ObjectFactory objectFactory = new ObjectFactory();

//    @Bean
    public void runAtStartCsruRpoChangesConfig() {
        Thread thread = new Thread(this::rpoChanges);
        thread.start();
    }

//    @Scheduled(cron = "${cron.CsruRpoChangesConfig.expression}")
//    @SchedulerLock(name = "TaskScheduler_csruRpoChangesConfig", lockAtLeastForString = "${shedlock.least}")
    public void rpoChanges() {
        log.info("[RPO][CHANGES][START]");
        try {
            //TODO potom este dorobit
            LocalDate lastDate;
            FileUtils.cleanDirectory(new File(rpoChangedFilesPath));
            CsruChange lastChange = csruChangeRepository.findFirstByTypeAndDateFromNotNullAndDateToNotNullOrderByDateToDescEndDesc(CsruTypeEnum.RPO.getValue());
//            if (lastChange.getResultStatus() == null || lastChange.getResultStatus().equals(CsruResultStatusEnum.ERROR.getValue())) {
//                lastDate = DateUtils.toLocalDate(lastChange.getDateTo());
//            } else {
//                lastDate = DateUtils.toLocalDate(lastChange.getDateTo()).plusDays(1);
//            }
//            if (lastDate.isBefore(LocalDate.now())) {
//                getChangedSubjectsFiles(lastDate, lastDate);
//            }

            getChangedSubjectsFiles(LocalDate.now().minusMonths(3), LocalDate.now());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            log.info("[RPO][CHANGES][END]");
        }
    }

    private void getChangedSubjectsFiles(LocalDate dateFrom, LocalDate dateTo) {

        GetConsolidatedReferenceDataRequestCType referenceDataRequest = new GetConsolidatedReferenceDataRequestCType();
        referenceDataRequest.setOvmIsId(ovmIsId);
        referenceDataRequest.setOeId("RPO_v2");
        referenceDataRequest.setScenario("listChangedSubjectsFiles");
        referenceDataRequest.setOvmTransactionId(UUID.randomUUID().toString());
        referenceDataRequest.setOvmCorrelationId(UUID.randomUUID().toString());

        TransEnvelopeInType transEnvelopeInType = new TransEnvelopeInType();
        transEnvelopeInType.setApplicantName(ovmIsId);
        transEnvelopeInType.setDateFrom(DateUtils.toXmlDate(dateFrom));
        transEnvelopeInType.setDateTo(DateUtils.toXmlDate(dateTo));

        ListChangedSubjectsFiles listChangedSubjectsFiles = new ListChangedSubjectsFiles();
        listChangedSubjectsFiles.setTransEnvelope(transEnvelopeInType);

        DataPlaceholderCType dataPlaceholder = new DataPlaceholderCType();
        dataPlaceholder.setAny(objectFactory.createListChangedSubjectsFiles(listChangedSubjectsFiles));
        referenceDataRequest.setPayload(dataPlaceholder);


        log.info("[RPO][CHANGES]CSRU request from CsruRpoChangesConfig.getChangedSubjectsFiles: " + referenceDataRequest);

        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(referenceDataRequest);
        csruEndpoint.sendRpoChangedSubjectsFilesRequest(request).thenAccept(r -> processRpoChangedSubjectsFilesResponse(r, DateUtils.toDate(dateFrom), DateUtils.toDate(dateTo))).exceptionally(e -> {
            log.info("[RPO][CHANGES]CSRU response from CsruRpoChangesConfig.getChangedSubjectsFiles: " + Throwables.getStackTraceAsString(e));
            return null;
        });
    }

    private void processRpoChangedSubjectsFilesResponse(GetConsolidatedReferenceDataResponseCType dataResponse, Date dateFrom, Date dateTo) {

        CsruChange csruChange = new CsruChange();

        try {

            log.info("[RPO][CHANGES]CSRU response from CsruRpoChangesConfig.getChangedSubjectsFiles: " + dataResponse.toString());

            csruChange = csruChangeService.initialChange(CsruTypeEnum.RPO.getValue(), dateFrom, dateTo);

            if (dataResponse.getResultCode() != 0) {
                String error = CHYBA_PRI_ZAEVIDNOVANI_ZMIEN_REFERENCNYCH_UDAJOV_KOD_WS + dataResponse.getResultCode() + NAVRATOVY_OZNAM_WS + dataResponse.getErrorMessage();
                log.info(error);
                csruChangeService.createErrorChange(csruChange, CsruTypeEnum.RPO.getValue(), error);
            } else {

                TransEnvelopeOutType envelopeOutType = ((JAXBElement<ListChangedSubjectsFilesResponse>) dataResponse.getPayload().getAny()).getValue().getReturn();

                if (envelopeOutType.getResultStatus().getResultCode() != 0) {
                    String error = CHYBA_PRI_ZAEVIDNOVANI_ZMIEN_REFERENCNYCH_UDAJOV_KOD_WS + envelopeOutType.getResultStatus().getResultCode() + NAVRATOVY_OZNAM_WS + envelopeOutType.getResultStatus().getResultReason();
                    log.info(error);
                    csruChangeService.createErrorChange(csruChange, CsruTypeEnum.RPO.getValue(), error);
                } else {

                    csruChange.setStart(new Timestamp(System.currentTimeMillis()));
                    csruChangeService.save(csruChange);

                    int processedItems = 0;
                    for (GeneratedFileCType generatedFile : envelopeOutType.getGeneratedFile()) {
                        processedItems = processedItems + processGeneratedFile(generatedFile);
                        log.info("[RPO][CHANGES][processRpoChangedSubjectsFilesResponse]+processedItems");
                    }

                    csruChangeService.createOkChange(csruChange, processedItems);
                    log.info("[RPO][CHANGES][processRpoChangedSubjectsFilesResponse]OK");
                }
            }

        } catch (Exception ex) {
            csruChangeService.createErrorChange(csruChange, CsruTypeEnum.RPO.getValue(), ex.getMessage());
            ex.printStackTrace();
        }
        log.info("[RPO][CHANGES][processRpoChangedSubjectsFilesResponse][END]");
    }

    private int processGeneratedFile(GeneratedFileCType file) {

        int processedFiles = 0;
        try {

            sk.is.urso.subject.v1.ObjectFactory objectFactory = new sk.is.urso.subject.v1.ObjectFactory();
            ResponseEntity<byte[]> response = restTemplate.exchange(csruEndpoint.prepareGetRequest(new URI(rpoGetFileUrl + file.getGeneratedFileName()), username, password), byte[].class);

            String fileName = rpoChangedFilesPath + file.getGeneratedFileName();
            try (ZipFile zip = new ZipFile(createZipFromChangedFile(response.getBody(), fileName))) {
                for (FileHeader fileHeader : zip.getFileHeaders()) {
                    if (fileHeader != null && !fileHeader.isDirectory()) {
                        String sourceRegisterId = null;
                        try {

                            Files.writeString(Path.of(rpoChangesXmlFiles, RPO_CHANGE_XML_TXT), "\nSpracovavam XML: " + fileHeader.getFileName(), StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE);

                            Document document = readDocument(zip, fileHeader);
                            String xml = XmlUtils.xmlToString(document);
                            Files.writeString(Path.of(rpoChangesXmlFiles, RPO_CHANGES + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_mm").format(new Timestamp(System.currentTimeMillis())) + XML_EXTENSION), xml, StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE);

                            sourceRegisterId = getSimplifiedValue(fileHeader, document, "po:PO/ns2:SourceRegisterId");
                            String sourceRegisterType = getSimplifiedValue(fileHeader, document, "po:PO/po:Source/po:SourceRegister/cs:Codelist/cs:CodelistItem/cs:ItemCode");
                            Long entryId = rpoIndexRepository.findZaznamId(sourceRegisterId, sourceRegisterType);

                            UserInfo userInfo = new UserInfo();
                            userInfo.setLogin(userSystemLogin);
                            userInfo.setAdministrator(true);

                            Files.writeString(Path.of(rpoChangesXmlFiles, RPO_CHANGE_XML_TXT), "\nSpracovavam RPO so sourceRegisterId: " + sourceRegisterId, StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                            if (entryId == null) {
                                zaznamRegistraController.zaznamRegistraPut(prepareRegisterEntryInsertDetail(xml, userInfo), userInfo);

                            } else {
                                zaznamRegistraController.zaznamRegistraPost(prepareRegisterEntryUpdateDetail(entryId, xml, userInfo), userInfo);
                                Optional<SubjectReg1IndexEntity> subjectReg1IndexEntity = subjectReg1IndexRepository.findByKlucAndHodnotaAndAktualny("rpoEntryId", String.valueOf(entryId), true);
                                if (subjectReg1IndexEntity.isPresent()) {

                                    RpoReg1DataEntity rpoReg1DataEntity = rpoReg1DataRepository.findById(entryId).get();
                                    SubjectReg1DataEntity subjectReg1DataEntity = subjectReg1DataRepository.findById(subjectReg1IndexEntity.get().getZaznamId()).get();
                                    SubjectT subject = XmlUtils.parse(subjectReg1DataEntity.getXml(), SubjectT.class);

//                                    rpoIdentificationConfig.reflectData(subject, rpoReg1DataEntity, userInfo);
                                    additionalOps.zaznamRegistraPost(prepareRegisterEntryUpdateDetail(XmlUtils.xmlToString(objectFactory.createSubject(subject)), subjectReg1DataEntity.getId()), userInfo);
                                }
                            }
                            processedFiles++;
                            Files.writeString(Path.of(rpoChangesXmlFiles, RPO_CHANGE_XML_TXT), "\nKoncim spracovavanie RPO so sourceRegisterId: " + sourceRegisterId, StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                            Files.writeString(Path.of(rpoChangesXmlFiles, RPO_CHANGE_XML_TXT), "\nKoncim spracovavanie XML: " + fileHeader.getFileName(), StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE);

                        } catch (Exception ex) {
                            Files.writeString(Path.of(rpoChangesXmlFiles, RPO_CHANGE_XML_TXT), "\nChyba pri spracovani RPO so sourceRegisterId: " + sourceRegisterId, StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                            Files.writeString(Path.of(rpoChangesXmlFiles, RPO_CHANGE_XML_TXT), "\nChyba pri spracovani XML: " + fileHeader.getFileName(), StandardCharsets.UTF_8, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                            csruRpoXmlErrorService.saveErrorXml(fileHeader.getFileName());
                            ex.printStackTrace();
                        }
                    }
                }
                return processedFiles;
            } finally {
                Files.delete(new File(fileName).toPath());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return processedFiles;
    }

    private File createZipFromChangedFile(byte[] body, String fileName) throws IOException {

        File file = new File(fileName);
        try (InputStream inputStream = new ByteArrayInputStream(body)) {
            FileUtils.copyInputStreamToFile(inputStream, file);
            return file;

        } catch (Exception ex) {
            Files.delete(file.toPath());
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, CHYBA_PRI_UKLADANI_SUBORU, ex);
        }
    }

    private ZaznamRegistraInputDetail prepareRegisterEntryUpdateDetail(Long entryId, String xmlData, UserInfo userInfo) {
        return new ZaznamRegistraInputDetail()
                .zaznamId(entryId)
                .pouzivatel(userInfo.getLogin())
                .data(xmlData)
                .registerId(SubjectReg1.RPO)
                .verziaRegistraId(1)
                .ucinnostOd(LocalDate.now());
    }

    private ZaznamRegistraInputDetail prepareRegisterEntryInsertDetail(String xmlData, UserInfo userInfo) {
        return new ZaznamRegistraInputDetail()
                .data(xmlData)
                .pouzivatel(userInfo.getLogin())
                .registerId(SubjectReg1.RPO)
                .verziaRegistraId(1)
                .modul(SubjectReg1.REG)
                .ucinnostOd(DateUtils.toLocalDate(new Date()));
    }

    private ZaznamRegistraInputDetail prepareRegisterEntryUpdateDetail(String xmlData, Long entryId) {
        return new ZaznamRegistraInputDetail()
                .zaznamId(entryId)
                .data(xmlData)
                .registerId(SubjectReg1.REGISTER_ID)
                .verziaRegistraId(1)
                .modul(SubjectReg1.REG)
                .ucinnostOd(LocalDate.now());
    }

    private Document readDocument(ZipFile zip, FileHeader fileHeader) throws IOException, ParserConfigurationException, SAXException {
        try (ZipInputStream is = zip.getInputStream(fileHeader)) {
            Document document = XmlUtils.parse(is.readAllBytes());

            Node rootNode = document.getElementsByTagName("ns2:CorporateBody").item(0);
            document.renameNode(rootNode, "http://www.dominanz.sk/UVZ/Reg/PO", "ns2:PO");

            var newDocument = XmlUtils.newDocument();
            var rootElement = (Element) newDocument.adoptNode(rootNode.cloneNode(true));
            rootElement.setAttribute("xmlns", "http://rpo.statistics.sk/RPO/Datatypes/rpo_core_schema-v2.4");
            rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            newDocument.appendChild(rootElement);
            XmlUtils.renameNamespaceRecursive(rootElement, "http://www.dominanz.sk/UVZ/Reg/PO", "ns2:");

            return newDocument;
        }
    }

    private String getSimplifiedValue(FileHeader fileHeader, Document document, String xpathString) {
        try {
            var xPathfactory = XPathFactory.newInstance();
            var xpath = xPathfactory.newXPath();
            return ((Node) xpath.compile(ignoreNameSpace(xpathString)).evaluate(document, XPathConstants.NODE)).getFirstChild().getNodeValue().toLowerCase(Locale.ROOT);
        } catch (XPathExpressionException e) {
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Xml " + fileHeader.getFileName().substring(fileHeader.getFileName().indexOf("/") + 1) + " neobsahuje typ zdroja registra.");
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

}
