package sk.is.urso.config.csru;

import com.google.common.base.Throwables;
import org.alfa.service.KeycloakTokenService;
import org.alfa.utils.DateUtils;
import org.alfa.utils.XmlUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.w3c.dom.Node;
import sk.is.urso.csru.fo.*;
import sk.is.urso.csru.fo.detailIfo.TIOS;
import sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.TOEXO;
import sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.TOEXOList;
import sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.TZVYO;
import sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TVSPO;
import sk.is.urso.csru.fo.zoznamIfo.ObjectType;
import sk.is.urso.csru.fo.zoznamIfo.*;
import sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetStatusRequestCType;
import sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetStatusResponseCType;
import sk.is.urso.model.csru.api.sync.GetConsolidatedDataServiceSync.GetConsolidatedDataRequestCType;
import sk.is.urso.model.csru.api.sync.GetConsolidatedDataServiceSync.GetConsolidatedDataResponseCType;
import sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.GetConsolidatedReferenceDataRequestCType;
import sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.GetConsolidatedReferenceDataResponseCType;
import sk.is.urso.model.csru.api.sync.common.DataPlaceholderCType;
//import sk.is.urso.model.csru.test.GetStatusRequestCType;
//import sk.is.urso.model.csru.test.GetStatusResponseCType;
import sk.is.urso.plugin.entity.SubjectReg1DataEntity;
import sk.is.urso.plugin.entity.SubjectReg1IndexEntity;
import sk.is.urso.plugin.repository.SubjectReg1DataRepository;
import sk.is.urso.plugin.repository.SubjectReg1IndexRepository;
import sk.is.urso.reg.AbstractRegEntityIndex;
import sk.is.urso.util.EncryptionUtils;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

@Component
@Transactional
public class CsruEndpoint {

    private static final Logger log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    private static final String AUTHORIZATION = "Authorization";

    private static final String BASIC = "Basic ";

    @Autowired
    SubjectReg1DataRepository dataRepo;

    @Autowired
    SubjectReg1IndexRepository indexRepo;

    @Autowired
    EncryptionUtils encryptionUtils;

    @Autowired
    @Qualifier("csruSearchExecutor")
    ExecutorService executorService;

    @Autowired
    @Qualifier("csruFoWS")
    private WebServiceTemplate csruFoWS;

    @Autowired
    KeycloakTokenService keycloakTokenService;

    @Value("${integration.csru.username}")
    public String rfoUsername;

    @Value("${integration.csru.password}")
    public String rfoPassword;

    @Value("${integration.csru.url}")
    private String csruUrl;

    @Value("${integration.csru.zc.url}")
    private String csruZcUrl;

    @Value("${integration.csru.proxy.hostname}")
    public String hostname;

    @Value("${integration.csru.proxy.port}")
    public Integer port;

    @Value("${mock.rfo-search:#{false}}")
    private boolean mockRfoSearch;

    @Value("${mock.zero.records}")
    private boolean mockZeroRecords;

    @Value("${integration.csru.proxy}")
    private boolean csruProxy;

    @Value("${integration.csru.username}")
    private String username;

    @Value("${integration.csru.password}")
    private String password;

    @Value("${integration.csru.consolidated-data-service-sync.url}")
    private String consolidatedDataServiceSyncUrl;

    @Value("${integration.csru.consolidated-data-service-async.url}")
    private String consolidatedDataServiceAsyncUrl;

    private final ModelMapper modelMapper = new ModelMapper();
    private final sk.is.urso.csru.fo.zoznamIfo.ObjectFactory objectFactory = new sk.is.urso.csru.fo.zoznamIfo.ObjectFactory();

    private static final String CSRU_FO_NAMESPACE = "http://www.egov.sk/mvsr/RFO/Podp/Ext/PoskytnutieZoznamuIFOPodlaVyhladavacichKriteriiWS-v1.0";
    private static final String WSDL_NAME = "CSRU_GetConsolidatedReferenceDataService_Sync_v1_4.wsdl";

    public JAXBElement<GetConsolidatedReferenceDataResponseCType> sendRfoConfirmRequestSynchr(JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {
        return (JAXBElement<GetConsolidatedReferenceDataResponseCType>) callWebService(csruUrl, rfoUsername, rfoPassword, request);
    }

    public JAXBElement<GetConsolidatedReferenceDataResponseCType> sendPoiMarkingRequestSynchr(@RequestPayload JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {
        return (JAXBElement<GetConsolidatedReferenceDataResponseCType>) callWebService(csruUrl, rfoUsername, rfoPassword, request);
    }

    public JAXBElement<GetConsolidatedReferenceDataResponseCType> sendRfoChangeRequestSynchr(JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {
        return (JAXBElement<GetConsolidatedReferenceDataResponseCType>) callWebService(csruUrl, rfoUsername, rfoPassword, request);
    }

    public JAXBElement<GetConsolidatedReferenceDataResponseCType> sendRfoCodelistRequestSync(JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {
        return (JAXBElement<GetConsolidatedReferenceDataResponseCType>) callWebService(csruUrl, rfoUsername, rfoPassword, request);
    }

    public JAXBElement<GetConsolidatedReferenceDataResponseCType> sendRfoLoadRequestSynchr(@RequestPayload JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {
        return (JAXBElement<GetConsolidatedReferenceDataResponseCType>) callWebService(csruUrl, rfoUsername, rfoPassword, request);
    }

    public JAXBElement<GetConsolidatedReferenceDataResponseCType> sendPoiUnmarkingRequestSynchr(@RequestPayload JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {
        return (JAXBElement<GetConsolidatedReferenceDataResponseCType>) callWebService(csruUrl, rfoUsername, rfoPassword, request);
    }

    public CompletionStage<GetConsolidatedReferenceDataResponseCType> sendRfoChangeRequest(JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {
        return sendRequest(request, csruUrl, rfoUsername, rfoPassword);
    }

    public CompletionStage<GetConsolidatedReferenceDataResponseCType> sendRfoConfirmRequest(JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {
        return sendRequest(request, csruUrl, rfoUsername, rfoPassword);
    }

    public CompletionStage<GetConsolidatedReferenceDataResponseCType> sendRpoChangedSubjectsFilesRequest(JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {
        return sendRequest(request, csruUrl, username, password);
    }

    public GetConsolidatedDataResponseCType sendGetConsolidatedDataSyncRequest(JAXBElement<GetConsolidatedDataRequestCType> request) {
        return sendRequestAndWait(request, consolidatedDataServiceSyncUrl, username, password);
    }

    public sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataResponseCType sendGetConsolidatedDataAsyncRequest(JAXBElement<sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataRequestCType> request) {
        return sendRequestAndWait(request, consolidatedDataServiceAsyncUrl, username, password);
    }

    public GetStatusResponseCType sendGetStatusRequest(JAXBElement<GetStatusRequestCType> request) {
        return sendRequestAndWait(request, consolidatedDataServiceAsyncUrl, username, password);
    }

    public CompletableFuture<GetConsolidatedReferenceDataResponseCType> sendPoiMarkingRequest(@RequestPayload JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {

        if (mockRfoSearch) {
            return CompletableFuture.completedFuture(mockPoiMarkingResponse(request));
        }
        return sendRequest(request, csruUrl, rfoUsername, rfoPassword);
    }

    public CompletableFuture<GetConsolidatedReferenceDataResponseCType> sendPoiUnmarkingRequest(@RequestPayload JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {

        if (mockRfoSearch) {
            return CompletableFuture.completedFuture(mockPoiUnmarkingResponse(request));
        }
        return sendRequest(request, csruUrl, rfoUsername, rfoPassword);
    }

    public CompletableFuture<GetConsolidatedReferenceDataResponseCType> sendRfoLoadRequest(@RequestPayload JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {

        if (mockRfoSearch) {
            return CompletableFuture.completedFuture(mockRfoLoadResponse(request));
        }
        return sendRequest(request, csruUrl, rfoUsername, rfoPassword);
    }

    @SuppressWarnings("unchecked")
    private <T> CompletableFuture<T> sendRequest(@RequestPayload JAXBElement<?> request, String url, String username, String password) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        executorService.submit(() -> {
            JAXBElement<T> response = (JAXBElement<T>) callWebService(url, username, password, request);
            completableFuture.complete(response.getValue());
        });
        return completableFuture;
    }

    @SuppressWarnings("unchecked")
    private <T> T sendRequestAndWait(@RequestPayload JAXBElement<?> request, String url, String username, String password) {
        JAXBElement<T> response = (JAXBElement<T>) callWebService(url, username, password, request);
        return response.getValue();
    }

    public Object callWebService(String url, JAXBElement<?> request) {
        if (username != null && password != null) {
            csruFoWS.setMessageSender(createMessageSender(username, password));
        }
        try {
            return csruFoWS.marshalSendAndReceive(url, request);
        } catch (Exception e) {
            log.error("CSRU error : " + Throwables.getStackTraceAsString(e));
            throw (e);
        }
    }

    public Object callWebService(String url, String username, String password, Object request) {
        if (username != null && password != null) {
            csruFoWS.setMessageSender(createMessageSender(username, password));
        }
        try {
            return csruFoWS.marshalSendAndReceive(url, request);
        } catch (Exception e) {
            log.error("CSRU error : " + Throwables.getStackTraceAsString(e));
            throw (e);
        }
    }

    private WebServiceMessageSender createMessageSender(String username, String password) {
        List<Header> headers = new ArrayList<>();
        String authEncoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        BasicHeader authHeader = new BasicHeader("Authorization", "Basic " + authEncoded);
        headers.add(authHeader);

        HttpHost httpPost = null;
        if (csruProxy) {
            httpPost = new HttpHost(hostname, port);
        }

        CloseableHttpClient httpClient = HttpClients.custom().setProxy(httpPost).addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor()).addInterceptorLast(new RequestDefaultHeaders(headers)).build();

        return new HttpComponentsMessageSender(httpClient);
    }

    public RequestEntity<?> prepareGetRequest(URI fileUri) {
        return RequestEntity.get(fileUri).header(AUTHORIZATION, BASIC + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8))).accept(MediaType.ALL).build();
    }

    public RequestEntity<?> prepareGetRequest(URI fileUri, String username, String password) {
        if (username != null && password != null) {
            return RequestEntity.get(fileUri).header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8))).accept(MediaType.ALL).build();
        }
        return RequestEntity.get(fileUri).accept(MediaType.ALL).build();

    }

    @SuppressWarnings("unchecked")
    private GetConsolidatedReferenceDataResponseCType mockRfoLoadResponse(JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {

        sk.is.urso.csru.fo.ObjectFactory of = new sk.is.urso.csru.fo.ObjectFactory();
        sk.is.urso.csru.fo.detailIfo.ObjectFactory objFactory = new sk.is.urso.csru.fo.detailIfo.ObjectFactory();

        GetConsolidatedReferenceDataRequestCType dataRequestCType = request.getValue();
        PodanieType podanie = ((JAXBElement<PodanieType>) dataRequestCType.getPayload().getAny()).getValue();
        byte[] bytes = Base64.getDecoder().decode(podanie.getDataPodaniaBase64());
        String decodedString = new String(bytes, StandardCharsets.UTF_8);
        RegistrationType registration = XmlUtils.parse(decodedString, RegistrationType.class);

        DocumentUnauthorizedType documentUnauthorized = ((JAXBElement<DocumentUnauthorizedType>) registration.getAny().get(0)).getValue();
        ObjectType objectType = documentUnauthorized.getObject().get(0);
        sk.is.urso.csru.fo.detailIfo.TransEnvTypeIn envTypeIn = XmlUtils.unmarshall((Node) objectType.getContent().get(1), sk.is.urso.csru.fo.detailIfo.TransEnvTypeIn.class);

        sk.is.urso.csru.fo.detailIfo.TransEnvTypeOut envTypeOut = new sk.is.urso.csru.fo.detailIfo.TransEnvTypeOut();
        envTypeOut.setCorrID(envTypeIn.getCorrID());
        sk.is.urso.csru.fo.detailIfo.TOEXOEIOList toexoeioList = new sk.is.urso.csru.fo.detailIfo.TOEXOEIOList();
        toexoeioList.getOEX().addAll(mockRfoLoadSubjects(envTypeIn.getPOD().getIOSList().getIOS()));

        sk.is.urso.csru.fo.detailIfo.TPOVO tpovo = new sk.is.urso.csru.fo.detailIfo.TPOVO();
        tpovo.setOEXList(toexoeioList);
        tpovo.setKO(1);
        tpovo.setAC(DateUtils.toXmlDate(new Date()));
        envTypeOut.setPOV(tpovo);

        GetConsolidatedReferenceDataResponseCType dataResponseCType = new GetConsolidatedReferenceDataResponseCType();
        dataResponseCType.setOvmCorrelationId(dataRequestCType.getOvmCorrelationId());
        dataResponseCType.setOvmTransactionId(dataRequestCType.getOvmTransactionId());

        EncryptedDataType encryptedDataType = new EncryptedDataType();
        EncryptionMethodType encryptionMethodType = new EncryptionMethodType();
        encryptionMethodType.setAlgorithm("http://www.w3.org/2001/04/xmlenc#aes128-cbc");
        encryptedDataType.setEncryptionMethod(encryptionMethodType);

        CipherDataType cipherDataType = new CipherDataType();
        byte[] encryptedData = encryptionUtils.rsaEncrypt(XmlUtils.xmlToString(objFactory.createTransEnvOut(envTypeOut)));
        cipherDataType.setCipherValue(encryptedData);
        encryptedDataType.setCipherData(cipherDataType);

        String dataZasielkyBase64 = Base64.getEncoder().encodeToString(XmlUtils.xmlToString(of.createEncryptedData(encryptedDataType)).getBytes(StandardCharsets.UTF_8));
        ZasielkaType zasielkaType = new ZasielkaType();
        zasielkaType.setDataZasielkyBase64(dataZasielkyBase64);

        DataPlaceholderCType dataPlaceholderCType = new DataPlaceholderCType();
        dataPlaceholderCType.setAny(zasielkaType);
        dataResponseCType.setPayload(dataPlaceholderCType);

        return dataResponseCType;
    }

    private List<sk.is.urso.csru.fo.detailIfo.TOEXOEIO> mockRfoLoadSubjects(List<TIOS> tiosList) {

        sk.is.urso.csru.fo.detailIfo.ObjectFactory factory = new sk.is.urso.csru.fo.detailIfo.ObjectFactory();
        List<sk.is.urso.csru.fo.detailIfo.TOEXOEIO> toexoList = new ArrayList<>();
        int randInt = mockZeroRecords ? getRandomNumber(0, 21) : getRandomNumber(1, 21);
        for (TIOS tios : tiosList) {

            SubjectReg1IndexEntity rfoExternalIndexEntity = indexRepo.findByKlucAndHodnotaAndAktualny("rfoExternalId", tios.getIF(), true).orElse(new SubjectReg1IndexEntity());
            SubjectReg1DataEntity dataEntity = rfoExternalIndexEntity.getZaznamId() != null ? dataRepo.findById(rfoExternalIndexEntity.getZaznamId()).orElse(new SubjectReg1DataEntity()) : new SubjectReg1DataEntity();

            sk.is.urso.csru.fo.detailIfo.TOEXOEIO toexoeio = new sk.is.urso.csru.fo.detailIfo.TOEXOEIO();
            toexoeio.setID(factory.createTOEXOEIOID(tios.getIF()));
            toexoeio.setNK(1);

            AbstractRegEntityIndex indexEntity = dataEntity.getEntityIndexes().stream().filter(index -> index.getKluc().equals("su_birthDate")).findFirst().orElse(null);
            if (indexEntity != null) {
                toexoeio.setDN(factory.createTOEXOEIODN(DateUtils.toXmlDate(indexEntity.getHodnota())));
            }
            if (toexoeio.getDN() == null) {
                XMLGregorianCalendar birthDate = DateUtils.nowXmlDate();
                birthDate.setYear(getRandomNumber(1950, 2020));
                toexoeio.setDN(factory.createTOEXOEIODN(birthDate));
            }

            String identifierTypeCode = null;
            String identifierValue = null;
            if (dataEntity.getEntityIndexes().stream().anyMatch(index -> index.getKluc().equals("identifierTypeCode"))) {
                Optional<AbstractRegEntityIndex> tempIdentifierTypeCode = dataEntity.getEntityIndexes().stream().filter(index -> index.getKluc().equals("identifierTypeCode")).findFirst();
                if (tempIdentifierTypeCode.isPresent()) {
                    identifierTypeCode = tempIdentifierTypeCode.get().getHodnota();
                }

                Optional<AbstractRegEntityIndex> tempIdentifierValue = dataEntity.getEntityIndexes().stream().filter(index -> index.getKluc().equals("identifierValue")).findFirst();
                if (tempIdentifierValue.isPresent()) {
                    identifierValue = tempIdentifierValue.get().getHodnota();
                }
            }
            toexoeio.setRC(identifierTypeCode != null && identifierTypeCode.equals("9") ? factory.createTOEXOEIORC(identifierValue) : factory.createTOEXOEIORC(String.valueOf(getRandomNumber(10, 99)).concat(String.valueOf(getRandomNumber(10, 12))).concat(String.valueOf(getRandomNumber(10, 31))).concat("/").concat(String.valueOf(getRandomNumber(1000, 9999)))));

            int gender = getRandomNumber(1, 3);
            toexoeio.setPI(factory.createTOEXOEIOPI(gender));
            toexoeio.setPOHPINA(factory.createTOEXOEIOPOHPINA(getGender(gender)));

            toexoeio.setNI(factory.createTOEXOEIONI(703));
            toexoeio.setNARNINA(factory.createTOEXOEIONARNINA("Slovenská republika"));

            sk.is.urso.csru.fo.detailIfo.TMOSOList tmosoList = new sk.is.urso.csru.fo.detailIfo.TMOSOList();
            sk.is.urso.csru.fo.detailIfo.TMOSO tmoso = new sk.is.urso.csru.fo.detailIfo.TMOSO();
            tmoso.setID(randInt);
            tmoso.setPO(randInt);
            dataEntity.getEntityIndexes().stream().filter(index -> index.getKluc().equals("su_name")).findFirst().ifPresent(index -> tmoso.setME(index.getHodnota()));
            tmosoList.getMOS().add(tmoso);
            toexoeio.setMOSList(tmosoList);

            sk.is.urso.csru.fo.detailIfo.TPRIOList tprioList = new sk.is.urso.csru.fo.detailIfo.TPRIOList();
            sk.is.urso.csru.fo.detailIfo.TPRIO tprio = new sk.is.urso.csru.fo.detailIfo.TPRIO();
            tprio.setID(randInt);
            tprio.setPO(randInt);
            dataEntity.getEntityIndexes().stream().filter(index -> index.getKluc().equals("su_surname")).findFirst().ifPresent(index -> tprio.setPR(index.getHodnota()));
            tprioList.getPRI().add(tprio);
            toexoeio.setPRIList(tprioList);

            sk.is.urso.csru.fo.detailIfo.TRPROList trproList = new sk.is.urso.csru.fo.detailIfo.TRPROList();
            sk.is.urso.csru.fo.detailIfo.TRPRO trpro = new sk.is.urso.csru.fo.detailIfo.TRPRO();
            trpro.setID(randInt);
            trpro.setPO(randInt);
            dataEntity.getEntityIndexes().stream().filter(index -> index.getKluc().equals("su_surname")).findFirst().ifPresent(index -> trpro.setRP(index.getHodnota()));
            trproList.getRPR().add(trpro);
            toexoeio.setRPRList(trproList);

            sk.is.urso.csru.fo.detailIfo.TSPRSPIOList tsprspioList = new sk.is.urso.csru.fo.detailIfo.TSPRSPIOList();
            sk.is.urso.csru.fo.detailIfo.TSPRSPIO tsprspio = new sk.is.urso.csru.fo.detailIfo.TSPRSPIO();
            if (dataEntity.getEntityIndexes().stream().anyMatch(index -> index.getKluc().equals("adr_state"))) {

                tsprspio.setID(randInt);
                tsprspio.setST(randInt);
                dataEntity.getEntityIndexes().stream().filter(index -> index.getKluc().equals("adr_state")).findFirst().ifPresent(index -> tsprspio.setSTASTNA(index.getHodnota()));
                tsprspioList.getSPR().add(tsprspio);
            }
            toexoeio.setSPRList(tsprspioList);

            toexoeio.setPOBList(new sk.is.urso.csru.fo.detailIfo.TPOBPHROList());

            sk.is.urso.csru.fo.detailIfo.TPOBPHRO tpobphro = new sk.is.urso.csru.fo.detailIfo.TPOBPHRO();
            tpobphro.setTP(getRandomNumber(1, 17));
            tpobphro.setMP("true");
            tpobphro.setNO("Bratislava");
            tpobphro.setNU("Zochová");
            tpobphro.setOL(String.valueOf(getRandomNumber(1, 100)));
            tpobphro.setSC(getRandomNumber(1, 100));
            tpobphro.setDI(factory.createTPOBPHRODI(getRandomNumber(1, 100)));
            tpobphro.setCB(String.valueOf(getRandomNumber(1, 100)));
            toexoeio.getPOBList().getPOB().add(tpobphro);
            tpobphro.setREGList(new sk.is.urso.csru.fo.detailIfo.TREGOList());

            sk.is.urso.csru.fo.detailIfo.TPOBPHRO tpobphro1 = new sk.is.urso.csru.fo.detailIfo.TPOBPHRO();
            tpobphro1.setTP(getRandomNumber(1, 17));
            tpobphro1.setMP("false");
            tpobphro1.setOO("Praha");
            tpobphro1.setUM("Panská");
            tpobphro1.setOS(String.valueOf(getRandomNumber(1, 100)));
            tpobphro1.setSI(String.valueOf(getRandomNumber(1, 100)));
            tpobphro1.setCU(String.valueOf(getRandomNumber(1, 100)));

            tpobphro1.setST(152);
            tpobphro1.setNS("Česko");
            tpobphro1.setREGList(new sk.is.urso.csru.fo.detailIfo.TREGOList());
            sk.is.urso.csru.fo.detailIfo.TREGO trego = new sk.is.urso.csru.fo.detailIfo.TREGO();
            trego.setPO(1);
            trego.setRE("Plzeňský");
            tpobphro1.getREGList().getREG().add(trego);
            sk.is.urso.csru.fo.detailIfo.TREGO trego1 = new sk.is.urso.csru.fo.detailIfo.TREGO();
            trego1.setPO(0);
            trego1.setRE("kraj");
            tpobphro1.getREGList().getREG().add(trego1);
            toexoeio.getPOBList().getPOB().add(tpobphro1);

            toexoeio.setTOSList(new sk.is.urso.csru.fo.detailIfo.TTOSTOIOList());
            toexoeio.setRVEList(new sk.is.urso.csru.fo.detailIfo.TRVERVXOList());
            toexoeio.setZPOList(new sk.is.urso.csru.fo.detailIfo.TZPOOList());
            toexoeio.setPZMList(new sk.is.urso.csru.fo.detailIfo.TPZMOList());
            toexoeio.setDCDList(new sk.is.urso.csru.fo.detailIfo.TDCDDCIOList());
            toexoeio.setUSOList(new sk.is.urso.csru.fo.detailIfo.TUSOOList());
            toexoeio.setSNRList(new sk.is.urso.csru.fo.detailIfo.TSNRSNPOList());
            toexoeio.setZUDList(new sk.is.urso.csru.fo.detailIfo.TZUDZUNOList());
            toexoList.add(toexoeio);
        }
        return toexoList;
    }

    String getGender(int enumeration) {
        return switch (enumeration) {
            case 1 -> "nezistené";
            case 2 -> "muž";
            case 3 -> "žena";
            default -> "nezistené";
        };
    }

    @SuppressWarnings("unchecked")
    @PayloadRoot(namespace = CSRU_FO_NAMESPACE, localPart = "PoskytnutieZoznamuIFOPodlaVyhladavacichKriterii_v1_0RequestMessage")
    @ResponsePayload
    public GetConsolidatedReferenceDataResponseCType mockRfoSearchResponse(@RequestPayload JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {

        sk.is.urso.csru.fo.ObjectFactory of = new sk.is.urso.csru.fo.ObjectFactory();

        GetConsolidatedReferenceDataRequestCType dataRequestCType = request.getValue();
        PodanieType podanie = ((JAXBElement<PodanieType>) dataRequestCType.getPayload().getAny()).getValue();
        byte[] bytes = Base64.getDecoder().decode(podanie.getDataPodaniaBase64());
        String decodedString = new String(bytes, StandardCharsets.UTF_8);
        RegistrationType registration = XmlUtils.parse(decodedString, RegistrationType.class);

        DocumentUnauthorizedType documentUnauthorized = ((JAXBElement<DocumentUnauthorizedType>) registration.getAny().get(0)).getValue();
        ObjectType objectType = documentUnauthorized.getObject().get(0);
        TransEnvTypeIn envTypeIn = ((JAXBElement<TransEnvTypeIn>) objectType.getContent().get(1)).getValue();

        TransEnvTypeOut transEnvOut = new TransEnvTypeOut();
        TOEXOEIOList toexoeioList = new TOEXOEIOList();
        toexoeioList.getOEX().addAll(mockRfoSearchSubjects(envTypeIn));

        TPOVO tpovo = new TPOVO();
        tpovo.setOEXList(toexoeioList);
        transEnvOut.setPOV(tpovo);

        GetConsolidatedReferenceDataResponseCType dataResponseCType = new GetConsolidatedReferenceDataResponseCType();
        dataResponseCType.setOvmCorrelationId(dataRequestCType.getOvmCorrelationId());
        dataResponseCType.setOvmTransactionId(dataRequestCType.getOvmTransactionId());

        EncryptedDataType encryptedDataType = new EncryptedDataType();
        EncryptionMethodType encryptionMethodType = new EncryptionMethodType();
        encryptionMethodType.setAlgorithm("http://www.w3.org/2001/04/xmlenc#aes128-cbc");
        encryptedDataType.setEncryptionMethod(encryptionMethodType);

        CipherDataType cipherDataType = new CipherDataType();
        byte[] encryptedData = encryptionUtils.rsaEncrypt(XmlUtils.xmlToString(objectFactory.createTransEnvOut(transEnvOut)));
        cipherDataType.setCipherValue(encryptedData);
        encryptedDataType.setCipherData(cipherDataType);

        String dataZasielkyBase64 = Base64.getEncoder().encodeToString(XmlUtils.xmlToString(of.createEncryptedData(encryptedDataType)).getBytes(StandardCharsets.UTF_8));
        ZasielkaType zasielkaType = new ZasielkaType();
        zasielkaType.setDataZasielkyBase64(dataZasielkyBase64);

        DataPlaceholderCType dataPlaceholderCType = new DataPlaceholderCType();
        dataPlaceholderCType.setAny(zasielkaType);
        dataResponseCType.setPayload(dataPlaceholderCType);

        return dataResponseCType;
    }

    private List<TOEXOEIO> mockRfoSearchSubjects(TransEnvTypeIn envTypeIn) {

        List<TOEXOEIO> toexoeios = new ArrayList<>();

        if (envTypeIn.getPOD().getOEX().getDN() != null && envTypeIn.getPOD().getOEX().getRC() != null) {
            int dnYear = envTypeIn.getPOD().getOEX().getDN().getYear();
            int rnYear = calculateYear(envTypeIn.getPOD().getOEX().getRC());
            if (dnYear != rnYear) {
                return toexoeios;
            }
        }

        int randInt = mockZeroRecords ? getRandomNumber(0, 21) : getRandomNumber(1, 21);
        for (int i = 0; i < randInt; i++) {

            TOEXOEIO toexoeio = new TOEXOEIO();
            toexoeio.setID(objectFactory.createTOEXOEIOID(generateRandomID()));
            toexoeio.setRN(objectFactory.createTOEXOEIORN(envTypeIn.getPOD().getOEX().getRN()));
            toexoeio.setBI(objectFactory.createTOEXOEIOBI(envTypeIn.getPOD().getOEX().getBI()));
            toexoeio.setDN(objectFactory.createTOEXOEIODN(envTypeIn.getPOD().getOEX().getDN()));
            toexoeio.setPI(objectFactory.createTOEXOEIOPI(envTypeIn.getPOD().getOEX().getPI()));
            toexoeio.setRC(objectFactory.createTOEXOEIORC(envTypeIn.getPOD().getOEX().getRC()));
            toexoeio.setPOHPINA(objectFactory.createTOEXOEIOPOHPINA(envTypeIn.getPOD().getOEX().getPOHPINA()));
            toexoeio.setMOSList(objectFactory.createTMOSOList());
            if (envTypeIn.getPOD().getOEX().getMOSList() != null) {
                for (TMOS tmos : envTypeIn.getPOD().getOEX().getMOSList().getMOS()) {
                    toexoeio.getMOSList().getMOS().add(modelMapper.map(tmos, TMOSO.class));
                }
            }
            toexoeio.setPRIList(objectFactory.createTPRIOList());
            if (envTypeIn.getPOD().getOEX().getPRIList() != null) {
                for (TPRI tpri : envTypeIn.getPOD().getOEX().getPRIList().getPRI()) {
                    toexoeio.getPRIList().getPRI().add(modelMapper.map(tpri, TPRIO.class));
                }
            }
            toexoeio.setRPRList(objectFactory.createTRPROList());
            if (envTypeIn.getPOD().getOEX().getRPRList() != null) {
                for (TRPR trpr : envTypeIn.getPOD().getOEX().getRPRList().getRPR()) {
                    toexoeio.getRPRList().getRPR().add(modelMapper.map(trpr, TRPRO.class));
                }
            }
            toexoeios.add(toexoeio);
        }
        return toexoeios;
    }

    public int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public String generateRandomID() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 10;
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    private int calculateYear(String rc) {

        String yy = rc.substring(0, 2);
        int year = Integer.parseInt(yy);
        if (rc.length() == 10) {
            return 1900 + year;
        }
        if (year > 53) {
            return 1900 + year;
        }
        return 2000 + year;
    }

    @Bean(name = "csruFo")
    public Wsdl11Definition defaultWsdl11Definition() {
        SimpleWsdl11Definition wsdlDef = new SimpleWsdl11Definition();
        ClassPathResource wsdlRes = new ClassPathResource("/wsdl/csru/" + WSDL_NAME);
        wsdlDef.setWsdl(wsdlRes);
        return wsdlDef;
    }

    @SuppressWarnings("unchecked")
    private GetConsolidatedReferenceDataResponseCType mockPoiMarkingResponse(JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {

        GetConsolidatedReferenceDataRequestCType dataRequestCType = request.getValue();
        DataPlaceholderCType dataPlaceholder = dataRequestCType.getPayload();
        sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.TransEnvTypeIn envTypeIn = ((JAXBElement<sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.TransEnvTypeIn>) dataPlaceholder.getAny()).getValue();

        sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.TransEnvTypeOut envTypeOut = new sk.is.urso.csru.fo.zaujmovaOsoba.oznacenie.TransEnvTypeOut();
        envTypeOut.setCorrID(envTypeIn.getCorrID());

        TOEXOList toexoList = new TOEXOList();
        envTypeIn.getOSOList().getOSO().forEach(oso -> {
            TOEXO toexo = new TOEXO();
            toexo.setNK(1);
            toexo.setID(oso.getID());
            toexoList.getOEX().add(toexo);
        });

        TZVYO tzvyo = new TZVYO();
        tzvyo.setNK(1);
        tzvyo.setOEXList(toexoList);
        envTypeOut.setZVY(tzvyo);

        GetConsolidatedReferenceDataResponseCType dataResponseCType = new GetConsolidatedReferenceDataResponseCType();
        dataResponseCType.setOvmCorrelationId(dataRequestCType.getOvmCorrelationId());
        dataResponseCType.setOvmTransactionId(dataRequestCType.getOvmTransactionId());

        DataPlaceholderCType dataPlaceholderCType = new DataPlaceholderCType();
        dataPlaceholderCType.setAny(envTypeOut);
        dataResponseCType.setPayload(dataPlaceholderCType);
        return dataResponseCType;
    }

    @SuppressWarnings("unchecked")
    private GetConsolidatedReferenceDataResponseCType mockPoiUnmarkingResponse(JAXBElement<GetConsolidatedReferenceDataRequestCType> request) {

        GetConsolidatedReferenceDataRequestCType dataRequestCType = request.getValue();
        DataPlaceholderCType dataPlaceholder = dataRequestCType.getPayload();
        sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TransEnvTypeIn envTypeIn = ((JAXBElement<sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TransEnvTypeIn>) dataPlaceholder.getAny()).getValue();

        sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TransEnvTypeOut envTypeOut = new sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TransEnvTypeOut();
        envTypeOut.setCorrID(envTypeIn.getCorrID());

        sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TOEXOList toexoList = new sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TOEXOList();
        envTypeIn.getOSOList().getOSO().forEach(oso -> {
            sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TOEXO toexo = new sk.is.urso.csru.fo.zaujmovaOsoba.zrusenie.TOEXO();
            toexo.setNK(1);
            toexo.setID(oso.getID());
            toexoList.getOEX().add(toexo);
        });

        TVSPO tvspo = new TVSPO();
        tvspo.setKI(1);
        tvspo.setOEXList(toexoList);
        envTypeOut.setVSP(tvspo);

        GetConsolidatedReferenceDataResponseCType dataResponseCType = new GetConsolidatedReferenceDataResponseCType();
        dataResponseCType.setOvmCorrelationId(dataRequestCType.getOvmCorrelationId());
        dataResponseCType.setOvmTransactionId(dataRequestCType.getOvmTransactionId());

        DataPlaceholderCType dataPlaceholderCType = new DataPlaceholderCType();
        dataPlaceholderCType.setAny(envTypeOut);
        dataResponseCType.setPayload(dataPlaceholderCType);
        return dataResponseCType;
    }
}