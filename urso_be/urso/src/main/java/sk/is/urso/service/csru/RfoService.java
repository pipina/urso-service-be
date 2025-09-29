package sk.is.urso.service.csru;

import org.alfa.model.UserInfo;
import org.alfa.utils.DateUtils;
import org.alfa.utils.Utils;
import org.alfa.utils.XmlUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sk.is.urso.config.csru.CsruEndpoint;
import sk.is.urso.csru.fo.PodanieType;
import sk.is.urso.csru.fo.detailIfo.TIOSList;
import sk.is.urso.csru.fo.zoznamIfo.DocumentUnauthorizedType;
import sk.is.urso.csru.fo.zoznamIfo.ObjectType;
import sk.is.urso.csru.fo.zoznamIfo.RegistrationType;
import sk.is.urso.csru.fo.zoznamIfo.TMOSList;
import sk.is.urso.csru.fo.zoznamIfo.TOEXOEI;
import sk.is.urso.csru.fo.zoznamIfo.TPOD;
import sk.is.urso.csru.fo.zoznamIfo.TPRIList;
import sk.is.urso.csru.fo.zoznamIfo.TRPRList;
import sk.is.urso.csru.fo.zoznamIfo.TUES;
import sk.is.urso.csru.fo.zoznamIfo.TransEnvTypeIn;
import sk.is.urso.csru.fo.zoznamIfo.TransEnvTypeOut;
import sk.is.urso.model.ciselniky.HodnotaCiselnika;
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
import sk.is.urso.model.subject.detailIfo.TTOSTOIO;
import sk.is.urso.plugin.entity.SubjectReg1RfoIdentificationEntity;
import sk.is.urso.plugin.repository.SubjectReg1RfoIdentificationRepository;
import sk.is.urso.repository.ciselniky.HodnotaCiselnikaRepository;
import sk.is.urso.rest.model.HodnotaCiselnikaSimpleOutput;
import sk.is.urso.rest.model.ZaznamRegistra;
import sk.is.urso.rest.model.ZaznamRegistraOutputDetail;
import sk.is.urso.service.HodnotaCiselnikaService;
import sk.is.urso.subject.v1.ExternalRegisterReferenceT;
import sk.is.urso.subject.v1.FormattedAddressT;
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

import javax.xml.bind.JAXBElement;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RfoService {

//    private static final String OE_ID = "RFO_Person";
//    private static final String SCENARIO = "idByAttributes";
//    private static final String TYP_PODANIA = "RFO_PS_ZOZNAM_IFO_PODLA_VYHLADAVACICH_KRITERII_BEZ_ZEP_WS_IN_1_0";
//    private static final String TYP_SLUZBY = "RFO_Podp_Ext_oznam_IFO_Podla_Vyhladavacich_Kriterii_Bez_Zep_WS_1_0";
//    private static final String IDENTIFIER = "http://www.egov.sk/mvsr/RFO/datatypes/Podp/Ext/PoskytnutieZoznamuIFOPodlaVyhladavacichKriteriiWS-v1.0.xsd";
//    private static final String DATA_PODANIA = "PFJlZ2lzdHJhdGlvbiB4bWxucz0iaHR0cDovL3d3dy5kaXRlYy5zay9la3IvcmVnaXN0cmF0aW9uL3YxLjAiIElkPSJSRk9fUFNfWk9aTkFNX0lGT19QT0RMQV9WWUhMQURBVkFDSUNIX0tSSVRFUklJX0JFWl9aRVBfV1NfSU5fMV8wIj4NCsKgwqDCoMKgwqDCoMKgwqA8RG9jdW1lbnRVbmF1dGhvcml6ZWQgeG1sbnM9Imh0dHA6Ly93d3cuZGl0ZWMuc2svZWtyL3VuYXV0aG9yaXplZC92MS4wIiBJZD0iUkZPX1BTX1pPWk5BTV9JRk9fUE9ETEFfVllITEFEQVZBQ0lDSF9LUklURVJJSV9CRVpfWkVQX1dTX0lOXzFfMCI+DQoJCTxPYmplY3QgSWQ9ImRlMzEwZDk2LTJjYzMtNGNmNS04NjdlLWJlOTNjMDU3N2VkYiIgSWRlbnRpZmllcj0iaHR0cDovL3d3dy5lZ292LnNrL212c3IvUkZPL2RhdGF0eXBlcy9Qb2RwL0V4dC9Qb3NreXRudXRpZVpvem5hbXVJRk9Qb2RsYVZ5aGxhZGF2YWNpY2hLcml0ZXJpaVdTLXYxLjAueHNkIj4NCgkJCTxUcmFuc0VudkluIHhtbG5zOnhzaT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEtaW5zdGFuY2UiIHhtbG5zPSJodHRwOi8vd3d3LmVnb3Yuc2svbXZzci9SRk8vZGF0YXR5cGVzL1BvZHAvRXh0L1Bvc2t5dG51dGllWm96bmFtdUlGT1BvZGxhVnlobGFkYXZhY2ljaEtyaXRlcmlpV1MtdjEuMC54c2QiPg0KCQkJCTxQT0Q+DQogIDwhLS0gUkZPLlRfUE9TS1lUTlVUSUVfT0RQSVNVX1ZTVFVQICgxLCBfKSAtLT4NCiAgPE9FWD4NCiAgICA8IS0tIFJGTy5UX09TT0JBX0VYVCAoMSwgXykgLS0+DQogICAgPCEtLTxETj4xOTY0LTEyLTEyPC9ETj4gRFRfREFUVU1fTkFST0RFTklBICgxLCAqKSAtLT4NCiAgICA8UkM+NjQxMjEyLzYyOTI8L1JDPg0KICAgIDwhLS1TVl9ST0RORV9DSVNMTyAoMSwgKiktLT4NCiAgICA8RE4+MTk2NC0xMi0xMjwvRE4+DQogICAgPCEtLTE5NjQtMTItMTItLT4NCiAgICA8UEk+MTwvUEk+DQogICAgPCEtLU5MX1BPSExBVklFX0lEICgxLCBfKSBNVVogPSAxLCBaRU5BID0gMiwgTkVVUkNFTkUgPSAzLCAtLT4NCiAgICA8Uk4+MTk2NDwvUk4+DQogICAgPCEtLVJPSyBOQVJPREVOSUEgKDEsICopLS0+DQogICAgPE1PU0xpc3Q+DQogICAgICA8TU9TPg0KICAgICAgICA8TUU+UGV0ZXI8L01FPg0KICAgICAgPC9NT1M+DQogICAgPC9NT1NMaXN0Pg0KICAgIDxQUklMaXN0Pg0KICAgICAgPFBSST4NCiAgICAgICAgPFBSPkdzY2h3ZW5kdDwvUFI+DQogICAgICA8L1BSST4NCiAgICA8L1BSSUxpc3Q+DQogICAgPFJQUkxpc3Q+DQogICAgICA8UlBSPg0KICAgICAgICA8UlA+R3NjaHdlbmR0PC9SUD4NCiAgICAgIDwvUlBSPg0KICAgIDwvUlBSTGlzdD4NCiAgPC9PRVg+DQogIDxVRVM+DQogICAgPCEtLSBSRk8uVF9VREFKRV9FWFRfU1lTVEVNVSAoMSwgXykgLS0+DQogICAgPFBPPmNzcnVfdGVzdDwvUE8+DQogICAgPCEtLSBEZXprbyBTVl9FWFRFUk5ZX1BPVVpJVkFURUwgKDEsIF8pIC0tPg0KICAgIDxUST5hMzYyMGJiZi01YzNhLTRmZmYtYjI4MC0yZWM2YjhjZWNlOWY8L1RJPg0KICAgIDwhLS0gMTIzNDU2IE5MX1RJRF9FWFRFUk5FSE9fU1lTVEVNVSAoMSwgXykgLS0+DQogIDwvVUVTPg0KPC9QT0Q+DQoJCQk8L1RyYW5zRW52SW4+DQoJCTwvT2JqZWN0Pg0KCTwvRG9jdW1lbnRVbmF1dGhvcml6ZWQ+DQo8L1JlZ2lzdHJhdGlvbj4NCg";
//
//    @Autowired
//    private CsruEndpoint csruEndpoint;
//
//    @Autowired
//    private EncryptionUtils encryptionUtils;
//
//    @Value("${integration.csru.ovmIsId}")
//    private String ovmIsId;
//
//    final sk.is.urso.csru.fo.ObjectFactory objectFactory = new sk.is.urso.csru.fo.ObjectFactory();
//
//    public TransEnvTypeOut getRfoByRodneCislo(String rodneCislo) {
//        GetConsolidatedReferenceDataRequestCType dataRequest = new GetConsolidatedReferenceDataRequestCType();
//        dataRequest.setOvmIsId(ovmIsId);
//        dataRequest.setOeId(OE_ID);
//        dataRequest.setScenario(SCENARIO);
//        dataRequest.setOvmTransactionId(UUID.randomUUID().toString());
//        dataRequest.setOvmCorrelationId(UUID.randomUUID().toString());
//
//        sk.is.urso.csru.fo.zoznamIfo.ObjectFactory factory = new sk.is.urso.csru.fo.zoznamIfo.ObjectFactory();
//
//        TOEXOEI toxei = factory.createTOEXOEI();
//        toxei.setMOSList(factory.createTMOSList());
//        toxei.setPRIList(factory.createTPRIList());
//        toxei.setRPRList(factory.createTRPRList());
//        toxei.setRC(rodneCislo);
//
//        TUES tues = factory.createTUES();
//        tues.setPO(ovmIsId);
//        tues.setTI(UUID.randomUUID().toString());
//
//        TPOD tpod = factory.createTPOD();
//        tpod.setOEX(toxei);
//        tpod.setUES(tues);
//
//        TransEnvTypeIn transEnvTypeIn = factory.createTransEnvTypeIn();
//        transEnvTypeIn.setPOD(tpod);
//
//        ObjectType objectType = factory.createObjectType();
//        objectType.setId(UUID.randomUUID().toString());
//        objectType.setIdentifier(IDENTIFIER);
//        objectType.getContent().add(factory.createTransEnvIn(transEnvTypeIn));
//
//        DocumentUnauthorizedType documentUnauthorizedType = factory.createDocumentUnauthorizedType();
//        documentUnauthorizedType.setId(TYP_PODANIA);
//        documentUnauthorizedType.setDescription("");
//        documentUnauthorizedType.getObject().add(objectType);
//
//        RegistrationType registrationType = factory.createRegistrationType();
//        registrationType.setId(TYP_PODANIA);
//        registrationType.getAny().add(factory.createDocumentUnauthorized(documentUnauthorizedType));
//
//        String dataPodaniaXml = XmlUtils.xmlToString(factory.createRegistration(registrationType));
//        String dataPodaniaEncoded = Base64.getEncoder().encodeToString(dataPodaniaXml.getBytes());
//
//        PodanieType podanieType = new PodanieType();
//        podanieType.setTypPodania(TYP_PODANIA);
//        podanieType.setTypSluzby(TYP_SLUZBY);
//        podanieType.setDataPodaniaBase64(dataPodaniaEncoded);
//
//        DataPlaceholderCType dataPlaceholder = new DataPlaceholderCType();
//        dataPlaceholder.setAny(objectFactory.createPodanie(podanieType));
//        dataRequest.setPayload(dataPlaceholder);
//
//        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
//        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequest);
//        JAXBElement<GetConsolidatedReferenceDataResponseCType> response = csruEndpoint.sendRfoChangeRequestSynchr(request);
//
//        Document doc = encryptionUtils.decryptResponse(response.getValue().getPayload());
//        return processDecryptedResponse(doc);
//    }
//
//    private TransEnvTypeOut processDecryptedResponse(Document document) {
//        Element dz = (Element) document.getElementsByTagName("DataZasielkyBase64").item(0);
//
//        if (dz == null) {
//            sk.is.urso.csru.fo.zoznamIfo.ObjectFactory objectFactory = new sk.is.urso.csru.fo.zoznamIfo.ObjectFactory();
//            return objectFactory.createTransEnvTypeOut();
//        }
//        String decryptedData = new String(Base64.getDecoder().decode(dz.getTextContent()));
//        RegistrationType registration = XmlUtils.parse(decryptedData, RegistrationType.class);
//        DocumentUnauthorizedType documentUnauthorized = ((JAXBElement<DocumentUnauthorizedType>) registration.getAny().get(0)).getValue();
//        ObjectType objectType = documentUnauthorized.getObject().get(0);
//
//        return ((JAXBElement<TransEnvTypeOut>) objectType.getContent().get(0)).getValue();
//    }

    private static final String SLOVAKIA_ITEM_VALUE = "Slovenská republika";
    @Autowired
    SubjectReg1RfoIdentificationRepository rfoIdentificationRepository;
    @Autowired
    CsruEndpoint csruEndpoint;
    @Autowired
    EncryptionUtils encryptionUtils;
    @Autowired
    HodnotaCiselnikaService hodnotaCiselnikaService;
    @Autowired
    HodnotaCiselnikaRepository hodnotaCiselnikaRepository;

    private static final String RFO_PERSON = "RFO_Person";
    private static final String ID_BY_ATTRIBUTES = "idByAttributes";
    private static final String RFO_PS_ZOZNAM_IFO_PODLA_VYHLADAVACICH_KRITERII_BEZ_ZEP_WS_IN_1_0 = "RFO_PS_ZOZNAM_IFO_PODLA_VYHLADAVACICH_KRITERII_BEZ_ZEP_WS_IN_1_0";
    private static final String RFO_PODP_EXT_OZNAM_IFO_PODLA_VYHLADAVACICH_KRITERII_BEZ_ZEP_WS_1_0 = "RFO_Podp_Ext_oznam_IFO_Podla_Vyhladavacich_Kriterii_Bez_Zep_WS_1_0";
    public static final String POSKYTNUTIE_ZOZNAMU_IFOPODLA_VYHLADAVACICH_KRITERII_WS_V_1_0_XSD = "http://www.egov.sk/mvsr/RFO/datatypes/Podp/Ext/PoskytnutieZoznamuIFOPodlaVyhladavacichKriteriiWS-v1.0.xsd";
    public static final String IDENTIFIER_TYPE_BIRTH_NUMBER = "9";
    private static final String SLOVAKIA_ITEM_CODE = "703";
    public static final String SU_NAME = "su_name";
    public static final String SU_SURNAME = "su_surname";
    public static final String SU_BIRTH_DATE = "su_birthDate";
    public static final String IDENTIFIER_VALUE = "identifierValue";
    public static final String IDENTIFIER_TYPE_CODE = "identifierTypeCode";
    public static final String SUBJ_FORMATTED_NAME = "subj_formattedname";
    public static final String INDEXED_FIELD_KEY_SUBJ_TYPE = "subj_type";
    public static final String INDEXED_FIELD_KEY_RFO_EXTERNAL_ID = "rfoExternalId";

    public static final String ADDRESS_USAGE_CODELIST_CODE = "CUREG000016";



    public static final String TITLES_BEFORE = "CUREG000014";
    public static final String TITLES_AFTER = "CUREG000015";



    @Value("${integration.csru.ovmIsId}")
    private String ovmIsId;
    @Value("${integration.csru.identifikatorSubjektu}")
    private String identifikatorSubjektu;
    @Value("${integration.csru.url}")
    private String csruRefSyncUrl;
    @Value("${integration.csru.username}")
    public String username;
    @Value("${integration.csru.password}")
    public String password;
    @Value("${integration.csru.rfo.codelist.codelistCode}")
    private String rfoCodelistCodelistCode;
    @Value("${user.system.login}")
    private String userSystemLogin;

    @Autowired
    @Qualifier("csruFoWS")
    WebServiceTemplate csruFoWS;

    private final ModelMapper modelMapper = new ModelMapper();

//    public List<ZaznamRegistraOutputDetail> rfoDataById(List<SubjectReg1RfoIdentificationEntity> subjects) {
//
//        var jaxbContext = ((Jaxb2Marshaller) csruFoWS.getMarshaller()).getJaxbContext();
//        sk.is.urso.csru.fo.zoznamIfo.ObjectFactory objFactory = new sk.is.urso.csru.fo.zoznamIfo.ObjectFactory();
//        sk.is.urso.csru.fo.detailIfo.ObjectFactory objectFactory = new sk.is.urso.csru.fo.detailIfo.ObjectFactory();
//        sk.is.urso.csru.fo.detailIfo.TransEnvTypeIn envTypeIn = new sk.is.urso.csru.fo.detailIfo.TransEnvTypeIn();
//        TPOD tpod = new TPOD();
//
//        sk.is.urso.csru.fo.detailIfo.TUES tues = new sk.is.urso.csru.fo.detailIfo.TUES();
//        tues.setPO(ovmIsId);
//        tues.setTI(UUID.randomUUID().toString());
//        tpod.setUES(tues);
//
//        TIOSList tiosList = new TIOSList();
//        subjects.forEach(subject -> {
//            TIOS tios = new TIOS();
//            tios.setIF(subject.getRfoId());
//            tiosList.getIOS().add(tios);
//        });
//        tpod.setIOSList(tiosList);
//
//        TSPISPNList tspispnList = new TSPISPNList();
//        List<String> values = Arrays.asList("Administratívne údaje", "Lokačné údaje", "Vzťahové údaje", "Identifikačné údaje");
//        for (int i = 0; i < 4; i++) {
//            TSPISPN tspispn = new TSPISPN();
//            tspispn.setHO(i + 1);
//            tspispn.setTUDHONA(values.get(i));
//            tspispnList.getSPI().add(tspispn);
//        }
//        tpod.setSPIList(tspispnList);
//
//        envTypeIn.setPOD(tpod);
//
//        ObjectType object = new ObjectType();
//        object.setId(UUID.randomUUID().toString());
//        object.setIdentifier(POSKYTNUTIE_UDAJOV_IFOONLINE_WS_V_1_0_XSD);
//        object.getContent().add(objectFactory.createTransEnvIn(envTypeIn));
//
//        DocumentUnauthorizedType documentUnauthorized = new DocumentUnauthorizedType();
//        documentUnauthorized.setId(RFO_PS_REFERENCNE_UDAJE_ZOZNAMU_IFO_ONLINE_BEZ_ZEP_WS_IN_1_0);
//        documentUnauthorized.getObject().add(object);
//
//        RegistrationType registrationType = new RegistrationType();
//        registrationType.setId(RFO_PS_REFERENCNE_UDAJE_ZOZNAMU_IFO_ONLINE_BEZ_ZEP_WS_IN_1_0);
//        registrationType.getAny().add(objFactory.createDocumentUnauthorized(documentUnauthorized));
//
//        PodanieType podanieType = new PodanieType();
//        podanieType.setIdentifikatorSubjektu(identifikatorSubjektu);
//        podanieType.setTypPodania(RFO_PS_REFERENCNE_UDAJE_ZOZNAMU_IFO_ONLINE_BEZ_ZEP_WS_IN_1_0);
//        podanieType.setTypSluzby(RFO_PODP_EXT_REFERENCNE_UDAJE_ZOZNAMU_IFO_ONLINE_BEZ_ZEP_WS_1_0);
//        String dataPodaniaBase64 = Base64.getEncoder().encodeToString(XmlUtils.xmlToString(objFactory.createRegistration(registrationType), jaxbContext).getBytes(StandardCharsets.UTF_8));
//        podanieType.setDataPodaniaBase64(dataPodaniaBase64);
//
//        GetConsolidatedReferenceDataRequestCType dataRequestCType = new GetConsolidatedReferenceDataRequestCType();
//        dataRequestCType.setOvmIsId(ovmIsId);
//        dataRequestCType.setOeId(RFO_PERSON);
//        dataRequestCType.setScenario(DATA_BY_ID);
//        dataRequestCType.setOvmTransactionId(UUID.randomUUID().toString());
//        dataRequestCType.setOvmCorrelationId(UUID.randomUUID().toString());
//
//        DataPlaceholderCType dataPlaceholderCType = new DataPlaceholderCType();
//        sk.is.urso.csru.fo.ObjectFactory factory = new sk.is.urso.csru.fo.ObjectFactory();
//        dataPlaceholderCType.setAny(factory.createPodanie(podanieType));
//        dataRequestCType.setPayload(dataPlaceholderCType);
//
//        sk.is.urso.csru.common.ObjectFactory of = new sk.is.urso.csru.common.ObjectFactory();
//        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequestCType);
//        var response = csruEndpoint.sendRfoLoadRequestSynchr(request);
//        return processRfoLoadResponseAndSaveSubject(response.getValue(), subjects);
//    }

    public List<SubjectReg1RfoIdentificationEntity> getSubjects(List<String> rfoExternalIds) {
        List<SubjectReg1RfoIdentificationEntity> subjects = new ArrayList<>();
        for (String rfoExternalId : rfoExternalIds) {
            SubjectReg1RfoIdentificationEntity subjectReg1RfoIdentificationEntity = rfoIdentificationRepository.findByRfoId(rfoExternalId);
            if (subjectReg1RfoIdentificationEntity == null) {
                subjectReg1RfoIdentificationEntity = new SubjectReg1RfoIdentificationEntity();
                subjectReg1RfoIdentificationEntity.setRfoId(rfoExternalId.replaceAll("[^0-9/]", ""));
                subjectReg1RfoIdentificationEntity.setIdentifikovany(false);
                subjectReg1RfoIdentificationEntity.setOznaceny(false);
                subjectReg1RfoIdentificationEntity.setZluceny(false);
                subjectReg1RfoIdentificationEntity.setChybny(false);
                subjectReg1RfoIdentificationEntity.setDatumCasVytvorenia(DateUtils.toLocalDateTime(new Timestamp(System.currentTimeMillis())));
            }
            subjects.add(subjectReg1RfoIdentificationEntity);
        }
        return subjects;
    }

    public SubjectT createSubject(Document document, Long entryId) {
        TOEXOEIO toexoeio = XmlUtils.parse(document, TOEXOEIO.class);
        return reflectRfoDataToSubject(toexoeio, entryId);
    }


    public SubjectT reflectRfoDataToSubject(sk.is.urso.model.subject.detailIfo.TOEXOEIO toexoeio, Long entryId) {

        sk.is.urso.subject.v1.ObjectFactory objectFactory = new sk.is.urso.subject.v1.ObjectFactory();

        UserInfo userInfo = new UserInfo();
        userInfo.setLogin(userSystemLogin);
        userInfo.setAdministrator(true);

        SubjectT subject = objectFactory.createSubjectT();
        subject.setType(SubjectTypeT.FO);

        if (toexoeio.getPobList() != null) {
            List<SimplifiedAddressT> newAddresses = new ArrayList<>();
            if (toexoeio.getPobList().getPob() != null) {
                for (int i = 0; i < toexoeio.getPobList().getPob().size(); i++) {
                    TPOBPHRO pob = toexoeio.getPobList().getPob().get(i);
                    newAddresses.add(createAddress(pob, (short) i, userInfo));
                }
            }
            subject.getAddress().addAll(newAddresses);
        }

        short maxSequence = 0;
        PhysicalPersonT physicalPerson = createPhysicalPerson(toexoeio, maxSequence, userInfo);
        subject.getPhysicalPerson().add(physicalPerson);
        subject.getFormattedName().add(createFormattedName(physicalPerson, maxSequence));

        if (toexoeio.getDcdList() != null && toexoeio.getDcdList().getDcd() != null) {
            for (TDCDDCIO dcd : toexoeio.getDcdList().getDcd()) {
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

    private SimplifiedAddressT createAddress(TPOBPHRO pob, short sequence, UserInfo userInfo) {
        SimplifiedAddressT address = new SimplifiedAddressT();

        REGCodelistItemT addressUsage = new REGCodelistItemT();
//        List<HodnotaCiselnikaSimpleOutput> enumerationValues = hodnotaCiselnikaService.findByItemName(ADDRESS_USAGE_CODELIST_CODE, String.valueOf(pob.getTb()), userInfo);
        HodnotaCiselnika hodnotaCiselnika = hodnotaCiselnikaRepository.findByKodPolozkyAndKodCiselnika(String.valueOf(pob.getTb()), ADDRESS_USAGE_CODELIST_CODE).orElse(null);
        if (hodnotaCiselnika != null) {
            addressUsage.setItemCode(hodnotaCiselnika.getKodPolozky());
        }
        else {
            addressUsage.setItemCode(String.valueOf(pob.getTp()));
        }
        address.setAddressUsage(addressUsage);

        PostalAddressT postalAddress = new PostalAddressT();
        postalAddress.setAddressInSyncWithREG(true);
        setAttributes(postalAddress, sequence);

        REGCodelistItemOptionalT municipality = new REGCodelistItemOptionalT();
        REGCodelistItemOptionalT region = new REGCodelistItemOptionalT();
        REGCodelistItemT state = new REGCodelistItemT();
        StreetT street = new StreetT();

        if (pob.isPm()) {

            municipality.setItemValue(pob.getOo());

            street.setStreetName(pob.getUm());
            street.setStreetNumber(pob.getOs());
            street.setRegistrationNumber(pob.getSi());
            street.setBuildingPart(pob.getCu());

            state.setItemCode(String.valueOf(pob.getSt()));
            state.setItemValue(pob.getNs());

            if (pob.getRegList() != null && pob.getRegList().getReg() != null) {
                region.setItemValue(createForeignAddressRegion(pob.getRegList().getReg()));
                postalAddress.setRegion(region);
            }
        }
        else {

            HodnotaCiselnika rfoMunicipality = hodnotaCiselnikaRepository.findByKodPolozkyAndKodCiselnika(String.valueOf(pob.getOa()), rfoCodelistCodelistCode).orElse(null);
            if (rfoMunicipality != null) {

//                List<String> units = parseMunicipality(rfoMunicipality.getEnumerationValuesMultivalues().get(0).getItemName());
                List<String> units = parseMunicipality(rfoMunicipality.getNazovPolozky());
                municipality = new REGCodelistItemOptionalT();
                municipality.setItemCode(units.get(2));
                postalAddress.setMunicipality(municipality);

                region = new REGCodelistItemOptionalT();
                region.setItemCode(units.get(0));
                postalAddress.setRegion(region);
            }
            else {
                if (pob.getNo() != null && !pob.getNo().isEmpty()) {
                    municipality = new REGCodelistItemOptionalT();
                    municipality.setItemValue(pob.getNo());
                }
            }

            street.setStreetName(pob.getNu());
            street.setStreetNumber(pob.getOl());
            street.setRegistrationNumber(String.valueOf(pob.getSc()));
            street.setBuilding(String.valueOf(pob.getDi().intValue()));
            street.setBuildingPart(pob.getCb());

            state.setItemCode(SLOVAKIA_ITEM_CODE);
            state.setItemValue(SLOVAKIA_ITEM_VALUE);
        }

        if (municipality.getItemValue() != null) {
            postalAddress.setMunicipality(municipality);
        }
        if (municipality.getItemValue() != null) {
            postalAddress.setState(state);
        }
        postalAddress.setStreet(street);

        address.setPostalAddress(postalAddress);
        address.setFormattedAddress(createFormattedAddress(postalAddress));
        return address;
    }

    private List<String> parseMunicipality(String input) {
        List<String> result = new ArrayList<>();
        result.add(input.substring(0, 5));
        result.add(input.substring(0, 6));
        result.add(input);
        return result;
    }

    private FormattedAddressT createFormattedAddress(PostalAddressT postalAddress) {
        StringBuilder address = new StringBuilder();
        final var street = postalAddress.getStreet();
        final var municipality = postalAddress.getMunicipality();
        final var municipalitySet = municipality != null && Utils.isFilled(municipality.getItemValue());
        if (street != null) {
            final var streetNameSet = Utils.isFilled(street.getStreetName());
            final var registrationNumberSet = Utils.isFilled(street.getRegistrationNumber());
            final var streetNumberSet = Utils.isFilled(street.getStreetNumber());
            if (streetNameSet) {
                address.append(street.getStreetName());
            } else {
                if (postalAddress.getPobox() == null && municipalitySet && municipality.getItemValue() != null) {
                    address.append(municipality.getItemValue());
                }
            }
            if (registrationNumberSet) {
                Utils.appendOpt(address, " ");
                address.append(street.getRegistrationNumber());
            }
            if (streetNumberSet) {
                if (registrationNumberSet) { // Napr. Krátka 134/1
                    address.append("/");
                } else if (streetNameSet) { // Napr. Krátka 1
                    address.append(" ");
                } else if (municipalitySet) { // FIXME toto nesedí. Adresa s obcou by mala používať registrationNumber
                    address.append(municipality.getItemValue());
                    address.append(" ");
                }
                address.append(street.getStreetNumber());
            }
        }
        if (postalAddress.getPobox() != null && postalAddress.getPobox().getPoBox() != null && !postalAddress.getPobox().getPoBox().isEmpty()) {
            if (address.length() == 0)
                address.append("P. O. Box ");
            else
                address.append(", P. O. Box ");
            address.append(postalAddress.getPobox().getPoBox());
        }
        if (postalAddress.getZIP() != null) {
            Utils.appendOpt(address, ", ");
            address.append(postalAddress.getZIP());
        }
        if (municipalitySet) {
            if (postalAddress.getZIP() != null) {
                Utils.appendOpt(address, " ");
            } else {
                Utils.appendOpt(address, ", ");
            }
            address.append(municipality.getItemValue());
        }
        if (postalAddress.getState() != null && postalAddress.getState().getItemValue() != null && !postalAddress.getState().getItemValue().isEmpty()) {
            Utils.appendOpt(address, ", ");
            address.append(postalAddress.getState().getItemValue());
        }

        FormattedAddressT formattedAddress = modelMapper.map(postalAddress, FormattedAddressT.class);
        formattedAddress.setAddress(address.toString());
        return formattedAddress;
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

    private PhysicalPersonT createPhysicalPerson(TOEXOEIO toexoeio, short sequence, UserInfo userInfo) {
        PhysicalPersonT physicalPerson = new PhysicalPersonT();
        setAttributes(physicalPerson, sequence);

        if (toexoeio.getPi() != null) {
            REGCodelistItemT gender = new REGCodelistItemT();
            gender.setItemCode(String.valueOf(toexoeio.getPi().intValue()));
            gender.setItemValue(toexoeio.getPohpina());
            physicalPerson.setGender(gender);
        }

        if (toexoeio.getNi() != null) {
            REGCodelistItemT nationality = new REGCodelistItemT();
            nationality.setItemCode(String.valueOf(toexoeio.getNi()));
            nationality.setItemValue(toexoeio.getNarnina());
            physicalPerson.setNationality(nationality);
        }

        createTitles(physicalPerson, toexoeio.getTosList().getTos(), userInfo);

        toexoeio.getMosList().getMos().sort(Comparator.comparing(TMOSO::getPo));
        physicalPerson.setName(createName(toexoeio.getMosList().getMos()));
        toexoeio.getPriList().getPri().sort(Comparator.comparing(TPRIO::getPo));
        physicalPerson.setSurname(createSurname(toexoeio.getPriList().getPri()));
        if (toexoeio.getRprList().getRpr() != null) {
            toexoeio.getRprList().getRpr().sort(Comparator.comparing(TRPRO::getPo));
            physicalPerson.setBirthname(createBirthName(toexoeio.getRprList().getRpr()));
        }
        physicalPerson.setBirthDate(toexoeio.getDn());
        return physicalPerson;
    }

    private void createTitles(PhysicalPersonT physicalPerson, List<TTOSTOIO> tosList, UserInfo userInfo) {
        if (tosList == null) {
            return;
        }
        for (sk.is.urso.model.subject.detailIfo.TTOSTOIO ttostoio : tosList) {

            HodnotaCiselnika hodnotaCiselnika;
            String codelistCode = TITLES_BEFORE;

            if (ttostoio.getTt() == 1) {
                hodnotaCiselnika = hodnotaCiselnikaRepository.findByKodPolozkyAndKodCiselnika(codelistCode, ttostoio.getTittina()).orElse(null);
            }
            else if (ttostoio.getTt() == 2) {
                codelistCode = TITLES_AFTER;
                hodnotaCiselnika = hodnotaCiselnikaRepository.findByKodPolozkyAndKodCiselnika(codelistCode, ttostoio.getTittina()).orElse(null);
            }
            else {
                hodnotaCiselnika = hodnotaCiselnikaRepository.findByKodPolozkyAndKodCiselnika(codelistCode, ttostoio.getTittina()).orElse(null);
                if (hodnotaCiselnika == null) {
                    codelistCode = TITLES_AFTER;
                    hodnotaCiselnika = hodnotaCiselnikaRepository.findByKodPolozkyAndKodCiselnika(codelistCode, ttostoio.getTittina()).orElse(null);
                }
            }

            if (hodnotaCiselnika == null) {
//                List<HodnotaCiselnikaSimpleOutput> titles = hodnotaCiselnika.stream().filter(valueSimple -> valueSimple.getNazovPolozky().equalsIgnoreCase(ttostoio.getTittina())).collect(Collectors.toList());
                String titleNazov = hodnotaCiselnika.getNazovPolozky();
                String titleKod = hodnotaCiselnika.getKodPolozky();
//                if (!titles.isEmpty()) {
                if (titleNazov.equalsIgnoreCase(ttostoio.getTittina())) {
                    if (codelistCode.equals(TITLES_BEFORE)) {
                        REGCodelistItemT titleBefore = new REGCodelistItemT();
                        titleBefore.setItemValue(titleNazov);
                        titleBefore.setItemCode(titleKod);
                        physicalPerson.getTitleBefore().add(titleBefore);
                    }
                    else {
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
            fullName.append(trpro.getRp());
            if (trproList.indexOf(trpro) < trproList.size() - 1) {
                fullName.append(" ");
            }
        }
        return fullName.toString();
    }

    private String createSurname(List<TPRIO> tprioList) {
        StringBuilder fullName = new StringBuilder();
        for (TPRIO tprio : tprioList) {
            fullName.append(tprio.getPr());
            if (tprioList.indexOf(tprio) < tprioList.size() - 1) {
                fullName.append(" ");
            }
        }
        return fullName.toString();
    }

    private String createName(List<TMOSO> tmosoList) {
        StringBuilder fullName = new StringBuilder();
        for (TMOSO tmoso : tmosoList) {
            fullName.append(tmoso.getMe());
            if (tmosoList.indexOf(tmoso) < tmosoList.size() - 1) {
                fullName.append(" ");
            }
        }
        return fullName.toString();
    }

    public ZaznamRegistra csruSearch(String rodneCislo) {

        GetConsolidatedReferenceDataResponseCType response = sendRfoIdByAttributesRequest(rodneCislo);
        TransEnvTypeOut transEnvOut = processDecryptedResponse(encryptionUtils.decryptResponse(response.getPayload()));
        if (transEnvOut.getPOV() != null && transEnvOut.getPOV().getOEXList() != null && !transEnvOut.getPOV().getOEXList().getOEX().isEmpty()) {
            return prepareRegisterEntry(transEnvOut.getPOV().getOEXList().getOEX().get(0));
        }
        return new ZaznamRegistra();
    }

    private ZaznamRegistra prepareRegisterEntry(sk.is.urso.csru.fo.zoznamIfo.TOEXOEIO toexoeio) {

        ZaznamRegistra registerEntry = new ZaznamRegistra();
        registerEntry.setPlatnost(true);
        registerEntry.setVerziaRegistraId(1);
        registerEntry.setRegisterId("SUBJECT");

        LocalDate effectiveFrom = DateUtils.toLocalDate(new Date());
        if (toexoeio.getDN() != null && toexoeio.getDN().getValue() != null) {
            effectiveFrom = DateUtils.toLocalDate(toexoeio.getDN().getValue());
            registerEntry.getPolia().add(new sk.is.urso.rest.model.DvojicaKlucHodnotaSHistoriou().kluc(SU_BIRTH_DATE).hodnota(toexoeio.getDN().getValue().toString()).nazovZobrazenia("Dátum narodenia").ucinnostOd(effectiveFrom).platna(true).aktualna(true).sekvencia(0));
        }
        if (toexoeio.getRC() != null && toexoeio.getRC().getValue() != null) {
            registerEntry.getPolia().add((new sk.is.urso.rest.model.DvojicaKlucHodnotaSHistoriou().kluc(IDENTIFIER_TYPE_CODE).hodnota(IDENTIFIER_TYPE_BIRTH_NUMBER).nazovZobrazenia("Typ identifikátora").ucinnostOd(effectiveFrom).platna(true).aktualna(true).sekvencia(0)));
            registerEntry.getPolia().add((new sk.is.urso.rest.model.DvojicaKlucHodnotaSHistoriou().kluc(IDENTIFIER_VALUE).hodnota(toexoeio.getRC().getValue()).nazovZobrazenia("Hodnota identifikátora").ucinnostOd(effectiveFrom).platna(true).aktualna(true).sekvencia(0)));
        }
        String form = ""; //TODO: test - doplnenie formatovaneho mena
        if (toexoeio.getMOSList() != null && toexoeio.getMOSList().getMOS() != null) {
            for (int i = 0; i < toexoeio.getMOSList().getMOS().size(); i++) {
                registerEntry.getPolia().add((new sk.is.urso.rest.model.DvojicaKlucHodnotaSHistoriou().kluc(SU_NAME).hodnota(toexoeio.getMOSList().getMOS().get(i).getME()).nazovZobrazenia("Meno").ucinnostOd(effectiveFrom).platna(true).aktualna(true).sekvencia(i)));
                form += toexoeio.getMOSList().getMOS().get(i).getME();
                form += " ";
            }
        }
        if (toexoeio.getPRIList() != null && toexoeio.getPRIList().getPRI() != null) {
            for (int i = 0; i < toexoeio.getPRIList().getPRI().size(); i++) {
                registerEntry.getPolia().add((new sk.is.urso.rest.model.DvojicaKlucHodnotaSHistoriou().kluc(SU_SURNAME).hodnota(toexoeio.getPRIList().getPRI().get(i).getPR()).nazovZobrazenia("Priezvisko").ucinnostOd(effectiveFrom).platna(true).aktualna(true).sekvencia(i)));
                form += toexoeio.getPRIList().getPRI().get(i).getPR();
            }
        }
        registerEntry.getPolia().add((new sk.is.urso.rest.model.DvojicaKlucHodnotaSHistoriou().kluc(SUBJ_FORMATTED_NAME).hodnota(form).nazovZobrazenia("Formatované meno").ucinnostOd(effectiveFrom).platna(true).aktualna(true).sekvencia(0)));

        registerEntry.setUcinnostOd(effectiveFrom);
        registerEntry.getPolia().add(new sk.is.urso.rest.model.DvojicaKlucHodnotaSHistoriou().kluc(INDEXED_FIELD_KEY_SUBJ_TYPE).hodnota("FO"));
        registerEntry.getPolia().add(new sk.is.urso.rest.model.DvojicaKlucHodnotaSHistoriou().kluc(INDEXED_FIELD_KEY_RFO_EXTERNAL_ID).hodnota(toexoeio.getID().getValue()));
        return registerEntry;
    }

    public GetConsolidatedReferenceDataResponseCType sendRfoIdByAttributesRequest(String rodneCislo) {

        sk.is.urso.csru.fo.zoznamIfo.ObjectFactory objectFactory = new sk.is.urso.csru.fo.zoznamIfo.ObjectFactory();

        TOEXOEI toexoei = new TOEXOEI();
        toexoei.setMOSList(new TMOSList());
        toexoei.setPRIList(new TPRIList());
        toexoei.setRPRList(new TRPRList());
        toexoei.setRC(rodneCislo.replaceAll("[^0-9/]", ""));

        TransEnvTypeIn envTypeIn = new TransEnvTypeIn();
        TPOD tpod = new TPOD();
        tpod.setOEX(toexoei);

        TUES tues = new TUES();
        tues.setPO(ovmIsId);
        tues.setTI(UUID.randomUUID().toString());
        tpod.setUES(tues);

        envTypeIn.setPOD(tpod);

        ObjectType object = new ObjectType();
        object.setId(UUID.randomUUID().toString());
        object.setIdentifier(POSKYTNUTIE_ZOZNAMU_IFOPODLA_VYHLADAVACICH_KRITERII_WS_V_1_0_XSD);
        object.getContent().add(objectFactory.createTransEnvIn(envTypeIn));

        DocumentUnauthorizedType documentUnauthorized = new DocumentUnauthorizedType();
        documentUnauthorized.setId(RFO_PS_ZOZNAM_IFO_PODLA_VYHLADAVACICH_KRITERII_BEZ_ZEP_WS_IN_1_0);
        documentUnauthorized.getObject().add(object);

        RegistrationType registrationType = new RegistrationType();
        registrationType.setId(RFO_PS_ZOZNAM_IFO_PODLA_VYHLADAVACICH_KRITERII_BEZ_ZEP_WS_IN_1_0);
        registrationType.getAny().add(objectFactory.createDocumentUnauthorized(documentUnauthorized));

        PodanieType podanieType = new PodanieType();
        podanieType.setIdentifikatorSubjektu(identifikatorSubjektu);
        podanieType.setTypPodania(RFO_PS_ZOZNAM_IFO_PODLA_VYHLADAVACICH_KRITERII_BEZ_ZEP_WS_IN_1_0);
        podanieType.setTypSluzby(RFO_PODP_EXT_OZNAM_IFO_PODLA_VYHLADAVACICH_KRITERII_BEZ_ZEP_WS_1_0);
        podanieType.setDataPodaniaBase64(Base64.getEncoder().encodeToString(XmlUtils.xmlToString(objectFactory.createRegistration(registrationType)).getBytes(StandardCharsets.UTF_8)));

        GetConsolidatedReferenceDataRequestCType dataRequestCType = new GetConsolidatedReferenceDataRequestCType();
        dataRequestCType.setOvmIsId(ovmIsId);
        dataRequestCType.setOeId(RFO_PERSON);
        dataRequestCType.setScenario(ID_BY_ATTRIBUTES);
        dataRequestCType.setOvmTransactionId(UUID.randomUUID().toString());
        dataRequestCType.setOvmCorrelationId(UUID.randomUUID().toString());

        DataPlaceholderCType dataPlaceholderCType = new DataPlaceholderCType();
        sk.is.urso.csru.fo.ObjectFactory objFactory = new sk.is.urso.csru.fo.ObjectFactory();
        dataPlaceholderCType.setAny(objFactory.createPodanie(podanieType));
        dataRequestCType.setPayload(dataPlaceholderCType);

        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequestCType);
        JAXBElement<GetConsolidatedReferenceDataResponseCType> response = (JAXBElement<GetConsolidatedReferenceDataResponseCType>) csruEndpoint.callWebService(csruRefSyncUrl, username, password, request);
        return response.getValue();
    }

    private TransEnvTypeOut processDecryptedResponse(Document document) {

        Element dz = (Element) document.getElementsByTagName("DataZasielkyBase64").item(0);
        if (dz == null) {
            sk.is.urso.csru.fo.zoznamIfo.ObjectFactory objectFactory = new sk.is.urso.csru.fo.zoznamIfo.ObjectFactory();
            return objectFactory.createTransEnvTypeOut();
        }

        String decryptedData = new String(Base64.getDecoder().decode(dz.getTextContent()));
        RegistrationType registration = XmlUtils.parse(decryptedData, RegistrationType.class);
        DocumentUnauthorizedType documentUnauthorized = ((JAXBElement<DocumentUnauthorizedType>) registration.getAny().get(0)).getValue();
        ObjectType objectType = documentUnauthorized.getObject().get(0);
        return ((JAXBElement<TransEnvTypeOut>) objectType.getContent().get(0)).getValue();
    }
}
