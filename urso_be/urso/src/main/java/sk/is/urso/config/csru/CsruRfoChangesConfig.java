package sk.is.urso.config.csru;

import com.google.common.base.Throwables;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sk.is.urso.controller.ZaznamRegistraController;
import sk.is.urso.csru.fo.prijatieZmien.TPPZZZV;
import sk.is.urso.csru.fo.zoznamIfoSoZmenRefUdajmi.ObjectFactory;
import sk.is.urso.csru.fo.zoznamIfoSoZmenRefUdajmi.TPOD;
import sk.is.urso.csru.fo.zoznamIfoSoZmenRefUdajmi.TUES;
import sk.is.urso.csru.fo.zoznamIfoSoZmenRefUdajmi.TransEnvTypeIn;
import sk.is.urso.csru.fo.zoznamIfoSoZmenRefUdajmi.TransEnvTypeOut;
import sk.is.urso.model.csru.CsruChange;
import sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.GetConsolidatedReferenceDataRequestCType;
import sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.GetConsolidatedReferenceDataResponseCType;
import sk.is.urso.model.csru.api.sync.common.DataPlaceholderCType;
import sk.is.urso.model.subject.detailIfo.TDCDDCIO;
import sk.is.urso.model.subject.detailIfo.TMOSO;
import sk.is.urso.model.subject.detailIfo.TOEXOEIO;
import sk.is.urso.model.subject.detailIfo.TPOBPHRO;
import sk.is.urso.model.subject.detailIfo.TPRIO;
import sk.is.urso.model.subject.detailIfo.TREGO;
import sk.is.urso.model.subject.detailIfo.TRPRO;
import sk.is.urso.plugin.SubjectReg1;
import sk.is.urso.plugin.entity.RfoReg1IndexEntity;
import sk.is.urso.plugin.entity.SubjectReg1DataEntity;
import sk.is.urso.plugin.entity.SubjectReg1IndexEntity;
import sk.is.urso.plugin.repository.RfoReg1IndexRepository;
import sk.is.urso.plugin.repository.SubjectReg1DataRepository;
import sk.is.urso.plugin.repository.SubjectReg1IndexRepository;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.reg.model.ZaznamRegistraInputDetail;
import sk.is.urso.reg.model.ZaznamRegistraOutputDetail;
import sk.is.urso.rest.model.CsruTypeEnum;
import sk.is.urso.service.UdalostService;
import sk.is.urso.service.csru.CsruChangeService;
import sk.is.urso.subject.v1.ExternalRegisterReferenceT;
import sk.is.urso.subject.v1.FormattedNameT;
import sk.is.urso.subject.v1.IdentificatorT;
import sk.is.urso.subject.v1.PhysicalPersonT;
import sk.is.urso.subject.v1.PostalAddressT;
import sk.is.urso.subject.v1.REGCodelistItemOptionalT;
import sk.is.urso.subject.v1.REGCodelistItemT;
import sk.is.urso.subject.v1.REGItemWithHistoryT;
import sk.is.urso.subject.v1.SimplifiedAddressT;
import sk.is.urso.subject.v1.StreetT;
import sk.is.urso.subject.v1.SubjectT;
import sk.is.urso.subject.v1.SubjectTypeT;
import sk.is.urso.util.EncryptionUtils;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CsruRfoChangesConfig {

    private static final Logger log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    private static final String NAVRATOVY_OZNAM_WS = ", Návratový oznam WS: ";
    private static final String RFO_CHANGE = "RFO_Change";
    private static final String GET_CHANGES = "getChanges";
    private static final String CHYBA_PRI_VYHLADAVANI_ZOZNAMU_OSOB_SO_ZMEN_UDAJMI_NAVRATOVY_KOD_WS = "Chyba pri vyhľadávaní zoznamu osôb so zmenenými referenčnými údajmi. Návratový kód WS: ";
    private static final String CONFIRM_CHANGES = "confirmChanges";
    public static final String IDENTIFIER_TYPE_BIRTH_NUMBER = "9";
    private static final String ENUMERATION_COUNTRY_SLOVAKIA = "703";
    private static final String XML_EXTENSION = ".xml";
    private static final String RFO_CHANGES = "rfo_change_";
    private static final String RFO_CHANGE_XML_TXT = "rfo_change_xml.txt";

    @Value("${integration.csru.ovmIsId}")
    private String ovmIsId;

    @Autowired
    @Qualifier("csruGetFile")
    protected RestTemplate restTemplate;

    @Value("${user.system.login}")
    private String userSystemLogin;

    @Value("${integration.csru.rfo.transform.file}")
    private String rfoTransformFile;

    @Value("${integration.csru.rfo.confirm.changes}")
    private boolean confirmChanges;

    @Value("${integration.csru.rfo.changes.xml.files}")
    private String rfoChangesXmlFiles;

    @Autowired
    CsruEndpoint csruEndpoint;

    @Autowired
    UdalostService eventService;

    @Autowired
    RfoReg1IndexRepository rfoIndexRepository;

    @Autowired
    ZaznamRegistraController zaznamRegistraController;

    @Autowired
    EncryptionUtils encryptionUtils;

    @Autowired
    SubjectReg1IndexRepository subjectReg1IndexRepository;

    @Autowired
    SubjectReg1DataRepository subjectDataRepository;

    @Autowired
    CsruChangeService csruChangeService;

    final ObjectFactory objectFactory = new ObjectFactory();

//    @Bean
    public void runAtStartCsruRfoChangesConfig()
    {
        Thread thread = new Thread(this::rfoChangesWeekdays);
        thread.start();
    }

//    @Scheduled(cron = "${cron.CsruRfoChangesConfig.expression.weekday}")
//    @SchedulerLock(name = "TaskScheduler_csruRfoChangesConfig", lockAtLeastForString = "${shedlock.least}")
    public void rfoChangesWeekdays() {
        log.info("[RFO][CHANGES][START]");
        try {

            sendChangedFoRequest();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            log.info("[RFO][CHANGES][END]");
        }
    }
    
//    @Scheduled(cron = "${cron.CsruRfoChangesConfig.expression.weekend}")
//    @SchedulerLock(name = "TaskScheduler_csruRfoChangesConfig", lockAtLeastForString = "${shedlock.least}")
    public void rfoChangesWeekend() {
        log.info("[RFO][CHANGES][START]");
        try {

            sendChangedFoRequest();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            log.info("[RFO][CHANGES][END]");
        }
    }
    
    @Scheduled(cron = "0 1/15 * * * *")
    public void scheduleTest() {
        log.info("[SCHEDULING][TEST][CRON]" + new Date().toString());
    }
    
    @Scheduled(fixedRate = 900000)
    public void scheduleTestFixed() {
        log.info("[SCHEDULING][TEST][FIXED]" + new Date().toString());
    }

    private void sendChangedFoRequest() {
        sendChangedFoRequest(true);
    }

    public void sendChangedFoRequest(Boolean confirmChangesActual) {

        GetConsolidatedReferenceDataRequestCType dataRequest = new GetConsolidatedReferenceDataRequestCType();
        dataRequest.setOvmIsId(ovmIsId);
        dataRequest.setOeId(RFO_CHANGE);
        dataRequest.setScenario(GET_CHANGES);
        dataRequest.setOvmTransactionId(UUID.randomUUID().toString());
        dataRequest.setOvmCorrelationId(UUID.randomUUID().toString());

        String corrId = UUID.randomUUID().toString();
        TransEnvTypeIn envTypeIn = new TransEnvTypeIn();
        envTypeIn.setCorrID(corrId);

        TPOD tpod = new TPOD();
        tpod.setUD(true);

        TUES tues = new TUES();
        tues.setPO(ovmIsId);
        tues.setTI(corrId);
        tpod.setUES(tues);
        envTypeIn.setPOD(tpod);

        DataPlaceholderCType dataPlaceholder = new DataPlaceholderCType();
        dataPlaceholder.setAny(objectFactory.createTransEnvIn(envTypeIn));
        dataRequest.setPayload(dataPlaceholder);

        log.info("[RFO][CHANGES]CSRU request from CsruRfoChangesConfig.sendChangedFoRequest: " + dataRequest.toString());

        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequest);
        csruEndpoint.sendRfoChangeRequest(request).thenAccept(r -> processChangedRfoResponse(r, confirmChangesActual)).exceptionally(e -> {log.info("[RFO][CHANGES]CSRU response from CsruRfoChangesConfig.sendChangedFoRequest: " + Throwables.getStackTraceAsString(e));return null;});;
    }

    private static String jaxbObjectToXML(GetConsolidatedReferenceDataRequestCType customer) {
        String xmlString = "";
        try {
            JAXBContext context = JAXBContext.newInstance(GetConsolidatedReferenceDataRequestCType.class);
            Marshaller m = context.createMarshaller();

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML

            StringWriter sw = new StringWriter();
            m.marshal(customer, sw);
            xmlString = sw.toString();

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return xmlString;
    }

    @Transactional
    public List<String> processChangedRfoResponse(GetConsolidatedReferenceDataResponseCType response, Boolean confirmChangesActual) {

        CsruChange csruChange = new CsruChange();

        try {

        	log.info("[RFO][CHANGES]CSRU response from CsruRfoChangesConfig.sendChangedFoRequest: " + response.toString());

            csruChange = csruChangeService.initialChange(CsruTypeEnum.RFO.getValue(), null, null);
            
            csruChange.setStart(new Timestamp(System.currentTimeMillis()));
            csruChangeService.save(csruChange);

            UserInfo userInfo = new UserInfo();
            userInfo.setLogin(userSystemLogin);
            userInfo.setAdministrator(true);

            TransEnvTypeOut envTypeOut = processDecryptedResponse(encryptionUtils.decryptResponse(response.getPayload()), "http://www.egov.sk/mvsr/RFO/Podp/Ext/ZoznamIFOSoZmenenymiReferencnymiUdajmiWS-v1.0", TransEnvTypeOut.class);
            if (envTypeOut.getPOV().getKO() != 1) {
                String error = CHYBA_PRI_VYHLADAVANI_ZOZNAMU_OSOB_SO_ZMEN_UDAJMI_NAVRATOVY_KOD_WS + envTypeOut.getPOV().getKO() + NAVRATOVY_OZNAM_WS + envTypeOut.getPOV().getNU().getValue();
                log.info(error);
                csruChangeService.createErrorChange(csruChange, CsruTypeEnum.RFO.getValue(), error);
            }

            String xml = XmlUtils.xmlToString(objectFactory.createTransEnvOut(envTypeOut));

            LocalTime time = new Timestamp(System.currentTimeMillis()).toLocalDateTime().toLocalTime();
            String formattedTime = time.format(DateTimeFormatter.ofPattern("-HH-mm-ss"));
            Files.writeString(Path.of(rfoChangesXmlFiles, RFO_CHANGES + formattedTime + XML_EXTENSION), xml, StandardCharsets.UTF_8, StandardOpenOption.APPEND,StandardOpenOption.CREATE);
            Document document = XmlUtils.parse(xml);
            Document rfoDocument = transformFile(document, rfoTransformFile);

            Element zzvElement;
            Element oexElement;
            List<String> confirmIds = new ArrayList<>();

            NodeList oexList = rfoDocument.getElementsByTagName("OEX");
            NodeList zzvList = document.getElementsByTagName("ZZV");


            int processedItems = 0;
            String confirmationId = null;
            String tmp_confirmationId = null;

            for (int i = 0; i < zzvList.getLength(); i++) {
            	log.info("[RFO][CHANGES]Process element " + processedItems);
            	
                zzvElement = (Element) zzvList.item(i);
                oexElement = (Element) oexList.item(i);

                tmp_confirmationId = processElement(zzvElement, oexElement, userInfo);
                if(tmp_confirmationId!=null)
                {	
                	confirmationId = tmp_confirmationId;
                	confirmIds.add(confirmationId);
                }
                
                log.info("[RFO][CHANGES]Process element " + processedItems + " " + tmp_confirmationId);

                if (i%50==0 && confirmChanges && confirmChangesActual && confirmationId!=null) {
                	log.info("[RFO][CHANGES]sending confirmation  for " + confirmationId);
                	sendConfirmation(Long.valueOf(confirmationId));
                	log.info("[RFO][CHANGES]sending confirmation OK");
                }
                
                processedItems++;
            }
            
            if (confirmChanges && confirmChangesActual && confirmationId!=null) {
            	log.info("[RFO][CHANGES]sending confirmation  for " + confirmationId);
            	sendConfirmation(Long.valueOf(confirmationId));
            	log.info("[RFO][CHANGES]sending confirmation OK");
            }

            csruChangeService.createOkChange(csruChange, processedItems);
            return confirmIds;
        }
        catch (Exception e) {
            csruChangeService.createErrorChange(csruChange, CsruTypeEnum.RFO.getValue(), e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private String processElement(Element zzvElement, Element oexElement, UserInfo userInfo) {
        String id = null;
        try {

            Document oexDocument = XmlUtils.newDocument();
            oexDocument.appendChild(oexDocument.adoptNode(oexElement.cloneNode(true)));

            Element rootNode = oexDocument.getDocumentElement();
            rootNode.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            rootNode.setAttribute("xsi:schemaLocation", "http://www.dominanz.sk/UVZ/Reg/FO ../../uvz_reg_common/configuration/RFO_1/RFO_1_DATA.xsd");
            oexDocument.renameNode(rootNode, "http://www.dominanz.sk/UVZ/Reg/FO", "fo:FO");
            XmlUtils.addNamespaceRecursive(oexDocument.getFirstChild(), "http://www.dominanz.sk/UVZ/Reg/FO", "fo:");

            id = getSimplifiedValue(oexDocument, "fo:FO/fo:ID");
            Files.writeString(Path.of(rfoChangesXmlFiles, RFO_CHANGE_XML_TXT), "\nSpracovavam RFO s ID: " + id, StandardCharsets.UTF_8, StandardOpenOption.APPEND,StandardOpenOption.CREATE);
            Optional<RfoReg1IndexEntity> rfoIndex = rfoIndexRepository.findByKlucAndHodnotaAndAktualny("ID", id, true);
            if (rfoIndex.isEmpty()) {
                ResponseEntity<ZaznamRegistraOutputDetail> outputDetail = zaznamRegistraController.zaznamRegistraPut(prepareRegisterEntryInsertDetail(XmlUtils.xmlToString(oexDocument), SubjectReg1.RFO, userInfo), userInfo);
//                zaznamRegistraController.registerEntryReferenceRegisterIdRegisterVersionIdEntryIdModuleIdPut(SubjectReg1.RFO, 1, outputDetail.getBody().getEntryId(), SubjectReg1.REG, null, userInfo);

                insertSubject(outputDetail, oexDocument, userInfo);
            }
            else {
                ResponseEntity<ZaznamRegistraOutputDetail> outputDetail = zaznamRegistraController.zaznamRegistraPost(prepareRegisterEntryUpdateDetail(Long.valueOf(rfoIndex.get().getZaznamId()), XmlUtils.xmlToString(oexDocument), SubjectReg1.RFO, userInfo), userInfo);
                rfoIndexRepository.zneaktualizuj(rfoIndex.get().getZaznamId());
                //                updateSubject(outputDetail, oexDocument, userInfo, id); // TODO : zakomentovane dokym nebude fungovat aktualny update
            }
            String confirmationId = zzvElement.getElementsByTagName("ZZ").item(0).getFirstChild().getNodeValue();
            Files.writeString(Path.of(rfoChangesXmlFiles, RFO_CHANGE_XML_TXT), "\nKoncim spracovananie RFO s ID: " + id, StandardCharsets.UTF_8, StandardOpenOption.APPEND,StandardOpenOption.CREATE);
            return confirmationId;

        } catch (Exception ex) {
        	try {
        		Files.writeString(Path.of(rfoChangesXmlFiles, RFO_CHANGE_XML_TXT), "\nChyba pri spracovani RFO s ID: " + id, StandardCharsets.UTF_8, StandardOpenOption.APPEND,StandardOpenOption.CREATE);
        	}
        	catch(IOException e)
        	{
        		e.printStackTrace();
        	}
            ex.printStackTrace();
        }
        return null;
    }

    private void insertSubject(ResponseEntity<ZaznamRegistraOutputDetail> outputDetail, Document oexDocument, UserInfo userInfo) {
        SubjectT subjectT = reflectRfoDataToSubject(XmlUtils.parse(oexDocument, TOEXOEIO.class), outputDetail.getBody().getZaznamId());
        subjectT.setSubjectID(AbstractRegPlugin.ID_NULL);
        String subjectXml = XmlUtils.xmlToString(new sk.is.urso.subject.v1.ObjectFactory().createSubject(subjectT));

        var subj = zaznamRegistraController.zaznamRegistraPut(prepareRegisterEntryInsertDetail(subjectXml, SubjectReg1.REGISTER_ID, userInfo), userInfo);

        SubjectReg1DataEntity dataEntity = subjectDataRepository.findById(subj.getBody().getZaznamId()).get();
        dataEntity.setFoId(subjectT.getRfoReference().getExternalId());
        subjectDataRepository.save(dataEntity);
//        zaznamRegistraController.registerEntryReferenceRegisterIdRegisterVersionIdEntryIdModuleIdPut(SubjectReg1.REGISTER_ID, 1, subj.getBody().getEntryId(), SubjectReg1.REG, null, userInfo);
    }

    private void updateSubject(ResponseEntity<ZaznamRegistraOutputDetail> outputDetail, Document oexDocument, UserInfo userInfo, String rfoEntryId) {
        SubjectT subjectT = reflectRfoDataToSubject(XmlUtils.parse(oexDocument, TOEXOEIO.class), outputDetail.getBody().getZaznamId());

        Optional<SubjectReg1IndexEntity> subjectIndex =  subjectReg1IndexRepository.findByKlucAndHodnotaAndAktualny(SubjectReg1.INDEXED_FIELD_KEY_RFO_EXTERNAL_ID, rfoEntryId, true);
        Long subjectEntryId = subjectIndex.get().getZaznamId();

        Optional<SubjectReg1IndexEntity> subjectIndexSubjectId =  subjectReg1IndexRepository.findByZaznamIdAndKlucAndAktualny(subjectEntryId, SubjectReg1.INDEXED_FIELD_KEY_SUBJECT_ID, true);
        subjectT.setSubjectID(subjectIndexSubjectId.get().getHodnota());

        String subjectXml = XmlUtils.xmlToString(new sk.is.urso.subject.v1.ObjectFactory().createSubject(subjectT));

//        zaznamRegistraController.registerEntryActualPost(prepareRegisterEntryUpdateDetail(subjectEntryId, subjectXml, SubjectReg1.REGISTER_ID, userInfo), userInfo);
        zaznamRegistraController.zaznamRegistraPost(prepareRegisterEntryUpdateDetail(subjectEntryId, subjectXml, SubjectReg1.REGISTER_ID, userInfo), userInfo);
    }

    public SubjectT reflectRfoDataToSubject(TOEXOEIO toexoeio, long entryId) {

        sk.is.urso.subject.v1.ObjectFactory objectFactory = new sk.is.urso.subject.v1.ObjectFactory();
        XMLGregorianCalendar today = DateUtils.toXmlDate(LocalDate.now());

        SubjectT subject = objectFactory.createSubjectT();

        subject.setType(SubjectTypeT.FO);
        if (toexoeio.getPobList() != null) {
            List<SimplifiedAddressT> postalAddresses = subject.getAddress().stream().filter(address -> address.getPostalAddress() != null).collect(Collectors.toList());
            short maxSequence = (short) postalAddresses.stream().mapToInt(address -> address.getPostalAddress().getSequence()).max().orElse(0);

            List<SimplifiedAddressT> newAddresses = new ArrayList<>();
            if (toexoeio.getPobList().getPob() != null) {
                for (int i = 0; i < toexoeio.getPobList().getPob().size(); i++) {
                    TPOBPHRO pob = toexoeio.getPobList().getPob().get(i);
                    List<SimplifiedAddressT> addresses = subject.getAddress().stream().filter(address -> address.getAddressUsage().getItemCode().equalsIgnoreCase(String.valueOf(pob.getTp()))).collect(Collectors.toList());
                    for (SimplifiedAddressT address : addresses) {
                        if (address.getPostalAddress() != null) {
                            invalidateItemWithHistory(address.getPostalAddress());
                        }
                    }
                    if (i != 0 || !postalAddresses.isEmpty()) {
                        ++maxSequence;
                    }
                    SimplifiedAddressT simplifiedAddressT = createAddress(pob, maxSequence);
                    if (simplifiedAddressT != null) {
                        newAddresses.add(simplifiedAddressT);
                    }
                }
            }
            subject.getAddress().addAll(newAddresses);
        }

        subject.getPhysicalPerson().forEach(fo -> {
            fo.setEffectiveTo(today);
            fo.setCurrent(false);
        });
        short maxSequence = (short) subject.getPhysicalPerson().stream().mapToInt(PhysicalPersonT::getSequence).max().orElse(0);

        PhysicalPersonT physicalPerson = createPhysicalPerson(toexoeio, !subject.getPhysicalPerson().isEmpty() ? ++maxSequence : 0);
        if (physicalPerson != null) {
            subject.getPhysicalPerson().add(physicalPerson);
            subject.getFormattedName().add(createFormattedName(physicalPerson, maxSequence));
        }

        if (toexoeio.getDcdList() != null && toexoeio.getDcdList().getDcd() != null) {
            maxSequence = (short) subject.getIdentificator().stream().mapToInt(IdentificatorT::getSequence).max().orElse(0);
            for (TDCDDCIO dcd : toexoeio.getDcdList().getDcd()) {
                List<IdentificatorT> identifiers = subject.getIdentificator().stream().filter(identifier -> identifier.getIDType().getItemCode().equalsIgnoreCase(String.valueOf(dcd.getDd()))).collect(Collectors.toList());
                for (IdentificatorT identifier : identifiers) {
                    identifier.setEffectiveTo(today);
                    identifier.setCurrent(false);
                }
                subject.getIdentificator().add(createIdentifier(dcd, null, null, !subject.getIdentificator().isEmpty() ? ++maxSequence : 0));
            }
        }

        if (subject.getIdentificator().stream().noneMatch(identifier -> identifier.getIDType().getItemCode().equalsIgnoreCase(IDENTIFIER_TYPE_BIRTH_NUMBER))) {
            subject.getIdentificator().add(createIdentifier(null, IDENTIFIER_TYPE_BIRTH_NUMBER, toexoeio.getRc(), !subject.getIdentificator().isEmpty() ? ++maxSequence : 0));
        }

        ExternalRegisterReferenceT rfoReference = new ExternalRegisterReferenceT();
        rfoReference.setEntryId(String.valueOf(entryId));
        rfoReference.setExternalId(toexoeio.getId());
        subject.setRfoReference(rfoReference);

        return subject;
    }

    private void invalidateItemWithHistory(REGItemWithHistoryT itemWithHistory) {
        if (DateUtils.toDate(itemWithHistory.getEffectiveFrom()).after(new Date())) {
            itemWithHistory.setValid(false);
        }
        else {
            itemWithHistory.setEffectiveTo(DateUtils.toXmlDate(DateUtils.toLocalDate(new Date()).minusDays(1)));
        }
    }

    private SimplifiedAddressT createAddress(TPOBPHRO pob, short sequence) {
        SimplifiedAddressT address = new SimplifiedAddressT();

        REGCodelistItemT addressUsage = new REGCodelistItemT();
        addressUsage.setItemCode(String.valueOf(pob.getTp()));
        address.setAddressUsage(addressUsage);

        PostalAddressT postalAddress = null;

        REGCodelistItemOptionalT municipality = null;
        REGCodelistItemOptionalT region = new REGCodelistItemOptionalT();
        REGCodelistItemT state = null;
        StreetT street = null;

        if (Boolean.parseBoolean(pob.getMp())) {
            if (pob.getOo() != null && !pob.getOo().isEmpty()) {
                municipality = new REGCodelistItemOptionalT();
                municipality.setItemValue(pob.getOo());
            }

            street = new StreetT();
            street.setStreetName(pob.getUm() != null && !pob.getUm().isEmpty() ? pob.getUm() : null);
            street.setStreetNumber(pob.getOs() != null && !pob.getOs().isEmpty() ? pob.getOs() : null);
            street.setRegistrationNumber(pob.getSi() != null && !pob.getSi().isEmpty() ? pob.getSi() : null);
            street.setBuildingPart(pob.getCu() != null && !pob.getCu().isEmpty() ? pob.getCu() : null);

            if (pob.getSt() != null) {
                state = new REGCodelistItemT();
                state.setItemCode(String.valueOf(pob.getSt()));
                state.setItemValue(pob.getNs() != null && !pob.getNs().isEmpty() ? pob.getNs() : null);
            }
            if (pob.getRegList() != null) {
                if (postalAddress == null) {
                    postalAddress = new PostalAddressT();
                }
                region.setItemValue(createForeignAddressRegion(pob.getRegList().getReg()));
                postalAddress.setRegion(region);
            }
        }
        else {
            if (pob.getNo() != null && !pob.getNo().isEmpty()) {
                municipality = new REGCodelistItemOptionalT();
                municipality.setItemValue(pob.getNo());
            }
            street = new StreetT();
            street.setRegistrationNumber(pob.getSc() != null ? String.valueOf(pob.getSc()) : null);
            street.setBuilding(pob.getDi() != null ? String.valueOf(pob.getDi().intValue()) : null);
            street.setStreetName(pob.getNu() != null && !pob.getNu().isEmpty() ? pob.getNu() : null);
            street.setStreetNumber(pob.getOl() != null &&!pob.getOl().isEmpty() ? pob.getOl() : null);
            street.setBuildingPart(pob.getCb() != null && !pob.getCb().isEmpty() ? pob.getCb() : null);

            if (pob.getSi() != null && !pob.getSi().isEmpty()) {
                state = new REGCodelistItemT();
                state.setItemCode(pob.getSi());
                state.setItemValue(pob.getNs() != null && !pob.getNs().isEmpty() ? pob.getNs() : null);
            }
        }
        if (municipality != null) {
            if (postalAddress == null) {
                postalAddress = new PostalAddressT();
            }
            postalAddress.setMunicipality(municipality);
        }
        if (state != null) {
            if (postalAddress == null) {
                postalAddress = new PostalAddressT();
            }
            postalAddress.setState(state);
        }
        if (street != null) {
            if (postalAddress == null) {
                postalAddress = new PostalAddressT();
            }
            postalAddress.setStreet(street);
        }
        if (postalAddress != null) {
            postalAddress.setAddressInSyncWithREG(true);
            setAttributes(postalAddress, sequence);
            address.setPostalAddress(postalAddress);
        }
        address.setFormattedAddress(SubjectReg1.createFormattedAddress(postalAddress));
        
        return address;
    }

    private String createForeignAddressRegion(List<TREGO> regList) {
        regList.sort(Comparator.comparing(TREGO::getPo));
        String region = null;
        for (TREGO reg : regList) {
            if (region == null) {
                region = reg.getRe();
            }
            else {
                String tmpItemCode = region.concat(" ").concat(reg.getRe());
                region = tmpItemCode.length() + 3 <= 255 ? tmpItemCode : region.concat("...");
            }
        }
        return region;
    }

    private IdentificatorT createIdentifier(TDCDDCIO dcd, String type, String value, short sequence) {
        IdentificatorT identifier = new IdentificatorT();
        setAttributes(identifier, sequence);

        REGCodelistItemT idType = new REGCodelistItemT();
        idType.setItemCode(dcd != null ? String.valueOf(dcd.getDd()) : type);
        identifier.setIDType(idType);
        identifier.setIDValue(dcd != null ? String.valueOf(dcd.getCd()) : value);
        return identifier;
    }

    private void setAttributes(REGItemWithHistoryT itemWithHistory, short sequence) {
        itemWithHistory.setSequence(sequence);
        itemWithHistory.setCurrent(true);
        itemWithHistory.setValid(true);
        itemWithHistory.setEffectiveFrom(DateUtils.nowXmlDate());
    }

    private PhysicalPersonT createPhysicalPerson(TOEXOEIO toexoeio, short sequence) {
        PhysicalPersonT physicalPerson = new PhysicalPersonT();
        setAttributes(physicalPerson, sequence);
        boolean empty = true;

        if (toexoeio.getPi() != null) {
            REGCodelistItemT gender = new REGCodelistItemT();
            gender.setItemCode(String.valueOf(toexoeio.getPi().intValue()));
            physicalPerson.setGender(gender);
            empty = false;
        }

        if (toexoeio.getSn() != null && toexoeio.getSn().intValue() == 211) {
            REGCodelistItemT nationality = new REGCodelistItemT();
            nationality.setItemCode(ENUMERATION_COUNTRY_SLOVAKIA);
            physicalPerson.setNationality(nationality);
            empty = false;
        }
        if (toexoeio.getMosList() != null && toexoeio.getMosList().getMos() != null) {
            toexoeio.getMosList().getMos().sort(Comparator.comparing(TMOSO::getPo));
            physicalPerson.setName(!toexoeio.getMosList().getMos().isEmpty() ? toexoeio.getMosList().getMos().get(0).getMe() : null);
            empty = false;
        }
        if (toexoeio.getPriList() != null && toexoeio.getPriList().getPri() != null) {
            toexoeio.getPriList().getPri().sort(Comparator.comparing(TPRIO::getPo));
            physicalPerson.setSurname(!toexoeio.getPriList().getPri().isEmpty() ? toexoeio.getPriList().getPri().get(0).getPr() : null);
            empty = false;
        }
        if (toexoeio.getRprList() != null && toexoeio.getRprList().getRpr() != null) {
            toexoeio.getRprList().getRpr().sort(Comparator.comparing(TRPRO::getPo));
            physicalPerson.setBirthname(!toexoeio.getRprList().getRpr().isEmpty() ? toexoeio.getRprList().getRpr().get(0).getRp() : null);
            empty = false;
        }

        physicalPerson.setBirthDate(toexoeio.getDn());
        if (empty) {
            return null;
        }
        return physicalPerson;
    }

    private FormattedNameT createFormattedName(PhysicalPersonT physicalPerson, short sequence) {
        FormattedNameT formattedName = new FormattedNameT();
        setAttributes(formattedName, sequence);
        formattedName.setName(prepareName(physicalPerson.getName(), physicalPerson.getSurname(), physicalPerson.getTitleBefore(), physicalPerson.getTitleAfter()));
        return formattedName;
    }

    private String prepareName(String name, String surname, List<REGCodelistItemT> titlesBefore, List<REGCodelistItemT> titlesAfter) {
        StringBuilder fullName = new StringBuilder();
        if (titlesBefore != null) {
            titlesBefore.forEach(title -> fullName.append(title.getItemValue()).append(" "));
        }
        fullName.append(name).append(" ");
        fullName.append(surname);
        if (titlesAfter != null) {
            for (REGCodelistItemT title : titlesAfter) {
                if (titlesAfter.indexOf(title) == 0) {
                    fullName.append(" ");
                }
                fullName.append(title.getItemValue());
                if (titlesAfter.indexOf(title) < titlesAfter.size() - 1) {
                    fullName.append(" ");
                }
            }
        }
        return fullName.toString();
    }

    public void sendConfirmation(Long changeId) {

        sk.is.urso.csru.fo.prijatieZmien.ObjectFactory objFactory = new sk.is.urso.csru.fo.prijatieZmien.ObjectFactory();
        GetConsolidatedReferenceDataRequestCType dataRequest = new GetConsolidatedReferenceDataRequestCType();
        dataRequest.setOvmIsId(ovmIsId);
        dataRequest.setOeId(RFO_CHANGE);
        dataRequest.setScenario(CONFIRM_CHANGES);
        dataRequest.setOvmTransactionId(UUID.randomUUID().toString());
        dataRequest.setOvmCorrelationId(UUID.randomUUID().toString());

        sk.is.urso.csru.fo.prijatieZmien.TransEnvTypeIn envTypeIn = new sk.is.urso.csru.fo.prijatieZmien.TransEnvTypeIn();
        String corrId = UUID.randomUUID().toString();
        envTypeIn.setCorrID(corrId);

        TPPZZZV tppzzzv = new TPPZZZV();
        tppzzzv.setIP(changeId);
        envTypeIn.setPPZ(tppzzzv);

        sk.is.urso.csru.fo.prijatieZmien.TUES tues = new sk.is.urso.csru.fo.prijatieZmien.TUES();
        tues.setPO(ovmIsId);
        tues.setTI(corrId);
        envTypeIn.setUES(tues);

        DataPlaceholderCType dataPlaceholder = new DataPlaceholderCType();
        dataPlaceholder.setAny(objFactory.createTransEnvIn(envTypeIn));
        dataRequest.setPayload(dataPlaceholder);

        log.info("[RFO][CHANGES]CSRU request from CsruRfoChangesConfig.sendConfirmation: " + dataRequest.toString());

        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequest);
        csruEndpoint.sendRfoConfirmRequest(request).thenAccept(this::processRfoConfirmResponse).exceptionally(e -> {log.info("[RFO][CHANGES]CSRU response from CsruRfoChangesConfig.sendConfirmation: " + Throwables.getStackTraceAsString(e));return null;});
    }

    private void processRfoConfirmResponse(GetConsolidatedReferenceDataResponseCType response) {
        try {
        	log.info("[RFO][CHANGES]CSRU response from CsruRfoChangesConfig.sendConfirmation: " + response.toString());

            sk.is.urso.csru.fo.prijatieZmien.TransEnvTypeOut envTypeOut = processDecryptedResponse(encryptionUtils.decryptResponse(response.getPayload()), "http://www.egov.sk/mvsr/RFO/Podp/Ext/PrijatieZmienWS-v1.0", sk.is.urso.csru.fo.prijatieZmien.TransEnvTypeOut.class);

            if (envTypeOut.getVSP().getKI() != 1) {
                String error = CHYBA_PRI_VYHLADAVANI_ZOZNAMU_OSOB_SO_ZMEN_UDAJMI_NAVRATOVY_KOD_WS + envTypeOut.getVSP().getKI() + NAVRATOVY_OZNAM_WS + envTypeOut.getVSP().getPO();
                log.info(error);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ZaznamRegistraInputDetail prepareRegisterEntryUpdateDetail(Long entryId, String xmlData, String registerId, UserInfo userInfo) {
        return new ZaznamRegistraInputDetail()
                .zaznamId(entryId)
                .data(xmlData)
                .registerId(registerId)
                .verziaRegistraId(1)
                .ucinnostOd(LocalDate.now())
                .pouzivatel(userInfo.getLogin());
    }

    private ZaznamRegistraInputDetail prepareRegisterEntryInsertDetail(String xmlData, String registerId, UserInfo userInfo) {
        return new ZaznamRegistraInputDetail()
                .data(xmlData)
                .registerId(registerId)
                .verziaRegistraId(1)
                .ucinnostOd(LocalDate.now())
                .pouzivatel(userInfo.getLogin());
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

    private static Document transformFile(Document changedRfo, String transformerXslFilePath) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream(); // zapise do streamu prekonvertovany subor
            StreamResult result = new StreamResult(baos);
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(new FileInputStream(transformerXslFilePath)));

            transformer.transform(new DOMSource(changedRfo), result);
            return XmlUtils.parse(baos.toByteArray());

        } catch (Exception ex) {
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Chyba pri transformovani xml suboru");
        }
    }

    private <T> T processDecryptedResponse(Document document, String namespaceUri, Class<T> transEnvTypeOutClass) {

        Element transEnvOut = (Element) document.getElementsByTagNameNS(namespaceUri, "TransEnvOut").item(0);
        if (transEnvOut == null) {
            return null;
        }
        return XmlUtils.unmarshall(transEnvOut, transEnvTypeOutClass);
    }

    public List<String> sendChangedFoRequestSynchr(Boolean confirmChangesActual) {

        GetConsolidatedReferenceDataRequestCType dataRequest = new GetConsolidatedReferenceDataRequestCType();
        dataRequest.setOvmIsId(ovmIsId);
        dataRequest.setOeId(RFO_CHANGE);
        dataRequest.setScenario(GET_CHANGES);
        dataRequest.setOvmTransactionId(UUID.randomUUID().toString());
        dataRequest.setOvmCorrelationId(UUID.randomUUID().toString());

        String corrId = UUID.randomUUID().toString();
        TransEnvTypeIn envTypeIn = new TransEnvTypeIn();
        envTypeIn.setCorrID(corrId);

        TPOD tpod = new TPOD();
        tpod.setUD(true);

        TUES tues = new TUES();
        tues.setPO(ovmIsId);
        tues.setTI(corrId);
        tpod.setUES(tues);
        envTypeIn.setPOD(tpod);

        DataPlaceholderCType dataPlaceholder = new DataPlaceholderCType();
        dataPlaceholder.setAny(objectFactory.createTransEnvIn(envTypeIn));
        dataRequest.setPayload(dataPlaceholder);

        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequest);
        var response = csruEndpoint.sendRfoChangeRequestSynchr(request);
        return processChangedRfoResponse(response.getValue(), confirmChangesActual);
    }

    public void sendConfirmationSynchr(Long changeId) {

        sk.is.urso.csru.fo.prijatieZmien.ObjectFactory objFactory = new sk.is.urso.csru.fo.prijatieZmien.ObjectFactory();
        GetConsolidatedReferenceDataRequestCType dataRequest = new GetConsolidatedReferenceDataRequestCType();
        dataRequest.setOvmIsId(ovmIsId);
        dataRequest.setOeId(RFO_CHANGE);
        dataRequest.setScenario(CONFIRM_CHANGES);
        dataRequest.setOvmTransactionId(UUID.randomUUID().toString());
        dataRequest.setOvmCorrelationId(UUID.randomUUID().toString());

        sk.is.urso.csru.fo.prijatieZmien.TransEnvTypeIn envTypeIn = new sk.is.urso.csru.fo.prijatieZmien.TransEnvTypeIn();
        String corrId = UUID.randomUUID().toString();
        envTypeIn.setCorrID(corrId);

        TPPZZZV tppzzzv = new TPPZZZV();
        tppzzzv.setIP(changeId);
        envTypeIn.setPPZ(tppzzzv);

        sk.is.urso.csru.fo.prijatieZmien.TUES tues = new sk.is.urso.csru.fo.prijatieZmien.TUES();
        tues.setPO(ovmIsId);
        tues.setTI(corrId);
        envTypeIn.setUES(tues);

        DataPlaceholderCType dataPlaceholder = new DataPlaceholderCType();
        dataPlaceholder.setAny(objFactory.createTransEnvIn(envTypeIn));
        dataRequest.setPayload(dataPlaceholder);

        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequest);
        var response = csruEndpoint.sendRfoConfirmRequestSynchr(request);
        processRfoConfirmResponse(response.getValue());
    }

    /**
     * Tato metoda sa bude moct neskor vymaza, sluzi cisto na testovanie zmien rpo z csru.
     * @return
     */
    public String BodyForTestingRfoChange() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soapenv:Header xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" />\n" +
                "  <soapenv:Body xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                "    <zoz:TransEnvOut corrID=\"61e890f15912eb39:5ceb90f7:144ffeba4ce:-6c40\" xmlns:zoz=\"http://www.egov.sk/mvsr/RFO/Podp/Ext/ZoznamIFOSoZmenenymiReferencnymiUdajmiWS-v1.0\">\n" +
                "                <zoz:POV>\n" +
                "                    <zoz:NU>123</zoz:NU>\n" +
                "                    <zoz:KO>123</zoz:KO>\n" +
                "                    <zoz:AC>2012-12-13T12:12:12</zoz:AC>\n" +
                "                    <zoz:ZZVList>\n" +
                "                        <zoz:ZZV>\n" +
                "                            <zoz:ZZ>176059</zoz:ZZ>\n" +
                "\t\t\t\t\t\t\t<zoz:ID>2100215012</zoz:ID>\n" +
                "                            <zoz:NI>123</zoz:NI>\n" +
                "                            <zoz:SZ>123</zoz:SZ>\n" +
                "                            <zoz:SZVSZNA>123</zoz:SZVSZNA>\n" +
                "                            <zoz:DU>2012-12-13</zoz:DU>\n" +
                "                            <zoz:DN>2012-12-13</zoz:DN>\n" +
                "                            <zoz:RC>123</zoz:RC>\n" +
                "                            <zoz:RS>123</zoz:RS>\n" +
                "                            <zoz:RSTRSNA>123</zoz:RSTRSNA>\n" +
                "                            <zoz:PO>1</zoz:PO>\n" +
                "                            <zoz:POHPONA>123</zoz:POHPONA>\n" +
                "                            <zoz:ND>123</zoz:ND>\n" +
                "                            <zoz:NARNDNA>123</zoz:NARNDNA>\n" +
                "                            <zoz:MN>123</zoz:MN>\n" +
                "                            <zoz:UC>123</zoz:UC>\n" +
                "                            <zoz:UCEUCNA>123</zoz:UCEUCNA>\n" +
                "                            <zoz:ON>123</zoz:ON>\n" +
                "                            <zoz:UE>123</zoz:UE>\n" +
                "                            <zoz:UCEUENA>123</zoz:UCEUENA>\n" +
                "                            <zoz:SN>123</zoz:SN>\n" +
                "                            <zoz:STASNNA>123</zoz:STASNNA>\n" +
                "                            <zoz:MU>123</zoz:MU>\n" +
                "                            <zoz:UL>123</zoz:UL>\n" +
                "                            <zoz:UCEULNA>123</zoz:UCEULNA>\n" +
                "                            <zoz:OU>123</zoz:OU>\n" +
                "                            <zoz:UO>123</zoz:UO>\n" +
                "                            <zoz:UCEUONA>123</zoz:UCEUONA>\n" +
                "                            <zoz:SU>123</zoz:SU>\n" +
                "                            <zoz:STASUNA>123</zoz:STASUNA>\n" +
                "                            <zoz:DK>2012-12-13</zoz:DK>\n" +
                "                            <zoz:DT>123</zoz:DT>\n" +
                "                            <zoz:DP>2012-12-13</zoz:DP>\n" +
                "                            <zoz:IC>123</zoz:IC>\n" +
                "                            <zoz:PA>123</zoz:PA>\n" +
                "                            <zoz:RM>123</zoz:RM>\n" +
                "                            <zoz:PG>123</zoz:PG>\n" +
                "                            <zoz:UM>123</zoz:UM>\n" +
                "                            <zoz:BI>123</zoz:BI>\n" +
                "                            <zoz:IP>12345</zoz:IP>\n" +
                "                            <zoz:TV>123</zoz:TV>\n" +
                "                            <zoz:TVKTVNA>123</zoz:TVKTVNA>\n" +
                "                            <zoz:RN>123</zoz:RN>\n" +
                "                            <zoz:DA>2012-12-13T12:12:12</zoz:DA>\n" +
                "                            <zoz:DZ>2012-12-13</zoz:DZ>\n" +
                "                            <zoz:SP>123</zoz:SP>\n" +
                "                            <zoz:SPZSPNA>123</zoz:SPZSPNA>\n" +
                "                            <zoz:NU>123</zoz:NU>\n" +
                "                            <zoz:KO>123</zoz:KO>\n" +
                "                            <zoz:ZMEList>\n" +
                "                                <zoz:ZME ID=\"123\">\n" +
                "                                    <zoz:ME>123</zoz:ME>\n" +
                "                                    <zoz:PA>123</zoz:PA>\n" +
                "                                </zoz:ZME>\n" +
                "                            </zoz:ZMEList>\n" +
                "                            <zoz:ZPRList>\n" +
                "                                <zoz:ZPR ID=\"123\">\n" +
                "                                    <zoz:PO>123</zoz:PO>\n" +
                "                                    <zoz:PR>123</zoz:PR>\n" +
                "                                </zoz:ZPR>\n" +
                "                            </zoz:ZPRList>\n" +
                "                            <zoz:ZRPList>\n" +
                "                                <zoz:ZRP ID=\"123\">\n" +
                "                                    <zoz:PO>123</zoz:PO>\n" +
                "                                    <zoz:RP>123</zoz:RP>\n" +
                "                                </zoz:ZRP>\n" +
                "                            </zoz:ZRPList>\n" +
                "                            <zoz:ZTIList>\n" +
                "                                <zoz:ZTI ID=\"123\">\n" +
                "                                    <zoz:TI>123</zoz:TI>\n" +
                "                                    <zoz:TITTINA>123</zoz:TITTINA>\n" +
                "                                    <zoz:TT>123</zoz:TT>\n" +
                "                                    <zoz:TTITTNA>123</zoz:TTITTNA>\n" +
                "                                    <zoz:FR>2012-12-13T12:12:12</zoz:FR>\n" +
                "                                </zoz:ZTI>\n" +
                "                            </zoz:ZTIList>\n" +
                "                            <zoz:ZSPList>\n" +
                "                                <zoz:ZSP ID=\"123\">\n" +
                "                                    <zoz:SI>123</zoz:SI>\n" +
                "                                    <zoz:STASINA>123</zoz:STASINA>\n" +
                "                                </zoz:ZSP>\n" +
                "                            </zoz:ZSPList>\n" +
                "                            <zoz:ZRVList>\n" +
                "                                <zoz:ZRV ID=\"123\">\n" +
                "                                    <zoz:ID>123</zoz:ID>\n" +
                "                                    <zoz:TR>123</zoz:TR>\n" +
                "                                    <zoz:TRZTRNA>123</zoz:TRZTRNA>\n" +
                "                                    <zoz:DV>2012-12-13</zoz:DV>\n" +
                "                                    <zoz:MV>123</zoz:MV>\n" +
                "                                    <zoz:UC>123</zoz:UC>\n" +
                "                                    <zoz:UCEUCNA>123</zoz:UCEUCNA>\n" +
                "                                    <zoz:TO>123</zoz:TO>\n" +
                "                                    <zoz:TRRTONA>123</zoz:TRRTONA>\n" +
                "                                    <zoz:TL>123</zoz:TL>\n" +
                "                                    <zoz:TRRTLNA>123</zoz:TRRTLNA>\n" +
                "                                    <zoz:SM>123</zoz:SM>\n" +
                "                                    <zoz:PA>123</zoz:PA>\n" +
                "                                </zoz:ZRV>\n" +
                "                            </zoz:ZRVList>\n" +
                "                            <zoz:ZHSList>\n" +
                "                                <zoz:ZHS ID=\"123\">\n" +
                "                                    <zoz:SA>123</zoz:SA>\n" +
                "                                    <zoz:SNPSANA>123</zoz:SNPSANA>\n" +
                "                                    <zoz:DZ>2012-12-13</zoz:DZ>\n" +
                "                                    <zoz:DK>2012-12-13</zoz:DK>\n" +
                "                                    <zoz:PO>123</zoz:PO>\n" +
                "                                </zoz:ZHS>\n" +
                "                            </zoz:ZHSList>\n" +
                "                            <zoz:ZUSList>\n" +
                "                                <zoz:ZUS ID=\"123\">\n" +
                "                                    <zoz:DP>2012-12-13</zoz:DP>\n" +
                "                                    <zoz:UD>true</zoz:UD>\n" +
                "                                </zoz:ZUS>\n" +
                "                            </zoz:ZUSList>\n" +
                "                            <zoz:ZZPList>\n" +
                "                                <zoz:ZZP ID=\"123\">\n" +
                "                                    <zoz:DZ>2012-12-13</zoz:DZ>\n" +
                "                                    <zoz:DK>2012-12-13</zoz:DK>\n" +
                "                                    <zoz:PO>123</zoz:PO>\n" +
                "                                    <zoz:UC>123</zoz:UC>\n" +
                "                                    <zoz:UCEUCNA>123</zoz:UCEUCNA>\n" +
                "                                </zoz:ZZP>\n" +
                "                            </zoz:ZZPList>\n" +
                "                            <zoz:ZPBList>\n" +
                "                                <zoz:ZPB ID=\"1\">\n" +
                "                                    <zoz:PI>1</zoz:PI>\n" +
                "                                    <zoz:TP>1</zoz:TP>\n" +
                "                                    <zoz:TPOTPNA>1</zoz:TPOTPNA>\n" +
                "                                    <zoz:DP>2012-12-13</zoz:DP>\n" +
                "                                    <zoz:DK>2012-12-13</zoz:DK>\n" +
                "                                    <zoz:PM>true</zoz:PM>\n" +
                "                                    <zoz:SI>123</zoz:SI>\n" +
                "                                    <zoz:STASINA>123</zoz:STASINA>\n" +
                "                                    <zoz:UC>123</zoz:UC>\n" +
                "                                    <zoz:UCEUCNA>123</zoz:UCEUCNA>\n" +
                "                                    <zoz:SC>123</zoz:SC>\n" +
                "                                    <zoz:OC>123</zoz:OC>\n" +
                "                                    <zoz:ZO>s</zoz:ZO>\n" +
                "                                    <zoz:UE>123</zoz:UE>\n" +
                "                                    <zoz:UCEUENA>123</zoz:UCEUENA>\n" +
                "                                    <zoz:UL>123</zoz:UL>\n" +
                "                                    <zoz:ON>123</zoz:ON>\n" +
                "                                    <zoz:UI>123</zoz:UI>\n" +
                "                                    <zoz:ULIUINA>123</zoz:ULIUINA>\n" +
                "                                    <zoz:CB>123</zoz:CB>\n" +
                "                                    <zoz:OP>123</zoz:OP>\n" +
                "                                    <zoz:OO>123</zoz:OO>\n" +
                "                                    <zoz:CO>123</zoz:CO>\n" +
                "                                    <zoz:UM>123</zoz:UM>\n" +
                "                                    <zoz:OI>123</zoz:OI>\n" +
                "                                    <zoz:SS>123</zoz:SS>\n" +
                "                                    <zoz:MP>123</zoz:MP>\n" +
                "                                    <zoz:PC>123</zoz:PC>\n" +
                "                                    <zoz:CY>123</zoz:CY>\n" +
                "                                    <zoz:DI>123</zoz:DI>\n" +
                "                                    <zoz:VD>123</zoz:VD>\n" +
                "                                    <zoz:IA>123</zoz:IA>\n" +
                "                                    <zoz:OL>123</zoz:OL>\n" +
                "                                    <zoz:ZREList>\n" +
                "                                        <zoz:ZRE ID=\"123\">\n" +
                "                                            <zoz:PO>123</zoz:PO>\n" +
                "                                            <zoz:RE>123</zoz:RE>\n" +
                "                                        </zoz:ZRE>\n" +
                "                                    </zoz:ZREList>\n" +
                "                                </zoz:ZPB>\n" +
                "                            </zoz:ZPBList>\n" +
                "                            <zoz:ZDCList>\n" +
                "                                <zoz:ZDC ID=\"123\">\n" +
                "                                    <zoz:DD>1</zoz:DD>\n" +
                "                                    <zoz:DDCDDNA>123</zoz:DDCDDNA>\n" +
                "                                    <zoz:CC>123</zoz:CC>\n" +
                "                                    <zoz:DU>true</zoz:DU>\n" +
                "                                </zoz:ZDC>\n" +
                "                            </zoz:ZDCList>\n" +
                "                            <zoz:ZUDList>\n" +
                "                                <zoz:ZUD>\n" +
                "                                    <zoz:ZO>123</zoz:ZO>\n" +
                "                                    <zoz:ZOBZONA>123</zoz:ZOBZONA>\n" +
                "                                </zoz:ZUD>\n" +
                "                            </zoz:ZUDList>\n" +
                "                        </zoz:ZZV>\n" +
                "                    </zoz:ZZVList>\n" +
                "                </zoz:POV>\n" +
                "    </zoz:TransEnvOut>\n" +
                "  </soapenv:Body>\n" +
                "</soapenv:Envelope>";
    }
    
    //@Bean
    @Scheduled(cron = "${cron.CsruRfoChangesReload.expression}")
    @SchedulerLock(name = "TaskScheduler_csruRfoChangesReload", lockAtLeastForString = "${shedlock.least}")
    @Transactional
    public void test() throws SAXException, IOException, ParserConfigurationException
    {
    	File inputDirectory = new File(rfoChangesXmlFiles + "/reload");
    	if(!inputDirectory.exists())
    		return;
    	for (final File fileEntry : inputDirectory.listFiles()) {
    	
	    	UserInfo userInfo = new UserInfo();
            userInfo.setLogin(userSystemLogin);
	        userInfo.setAdministrator(true);
	    	
	        String xml = Files.readString(fileEntry.toPath());
	    	
	    	Document document = XmlUtils.parse(xml);
	        Document rfoDocument = transformFile(document, rfoTransformFile);
	
	        Element zzvElement;
	        Element oexElement;
	        List<String> confirmIds = new ArrayList<>();
	
	        NodeList oexList = rfoDocument.getElementsByTagName("OEX");
	        NodeList zzvList = document.getElementsByTagName("ZZV");
	        
	        int processedItems = 0;
            String confirmationId = null;
	        for (int i = 0; i < zzvList.getLength(); i++) {
            	log.info("[RFO][CHANGES]Process element " + processedItems);
            	
                zzvElement = (Element) zzvList.item(i);
                oexElement = (Element) oexList.item(i);

                confirmationId = processElement(zzvElement, oexElement, userInfo);
                confirmIds.add(confirmationId);
                
                log.info("[RFO][CHANGES]Process element " + processedItems + " " + confirmationId);

                processedItems++;
            }
	        
	        fileEntry.delete();
    	}
    }
}
