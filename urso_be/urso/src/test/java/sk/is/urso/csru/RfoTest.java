package sk.is.urso.csru;

import org.alfa.utils.XmlUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sk.is.urso.be.Application;
import sk.is.urso.config.csru.CsruEndpoint;
import sk.is.urso.csru.fo.PodanieType;
import sk.is.urso.csru.fo.zoznamIfo.DocumentUnauthorizedType;
import sk.is.urso.csru.fo.zoznamIfo.ObjectType;
import sk.is.urso.csru.fo.zoznamIfo.RegistrationType;
import sk.is.urso.csru.fo.zoznamIfo.TMOS;
import sk.is.urso.csru.fo.zoznamIfo.TMOSList;
import sk.is.urso.csru.fo.zoznamIfo.TOEXOEI;
import sk.is.urso.csru.fo.zoznamIfo.TPOD;
import sk.is.urso.csru.fo.zoznamIfo.TPRI;
import sk.is.urso.csru.fo.zoznamIfo.TPRIList;
import sk.is.urso.csru.fo.zoznamIfo.TRPR;
import sk.is.urso.csru.fo.zoznamIfo.TRPRList;
import sk.is.urso.csru.fo.zoznamIfo.TUES;
import sk.is.urso.csru.fo.zoznamIfo.TransEnvTypeIn;
import sk.is.urso.csru.fo.zoznamIfo.TransEnvTypeOut;
import sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.GetConsolidatedReferenceDataRequestCType;
import sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.GetConsolidatedReferenceDataResponseCType;
import sk.is.urso.model.csru.api.sync.common.DataPlaceholderCType;
import sk.is.urso.service.csru.RfoService;
import sk.is.urso.util.EncryptionUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.util.Base64;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RfoTest {

    private static final String OE_ID = "RFO_Person";
    private static final String SCENARIO = "idByAttributes";
    private static final String TYP_PODANIA = "RFO_PS_ZOZNAM_IFO_PODLA_VYHLADAVACICH_KRITERII_BEZ_ZEP_WS_IN_1_0";
    private static final String TYP_SLUZBY = "RFO_Podp_Ext_oznam_IFO_Podla_Vyhladavacich_Kriterii_Bez_Zep_WS_1_0";
    private static final String IDENTIFIER = "http://www.egov.sk/mvsr/RFO/datatypes/Podp/Ext/PoskytnutieZoznamuIFOPodlaVyhladavacichKriteriiWS-v1.0.xsd";
    private static final String DATA_PODANIA = "PFJlZ2lzdHJhdGlvbiB4bWxucz0iaHR0cDovL3d3dy5kaXRlYy5zay9la3IvcmVnaXN0cmF0aW9uL3YxLjAiIElkPSJSRk9fUFNfWk9aTkFNX0lGT19QT0RMQV9WWUhMQURBVkFDSUNIX0tSSVRFUklJX0JFWl9aRVBfV1NfSU5fMV8wIj4NCsKgwqDCoMKgwqDCoMKgwqA8RG9jdW1lbnRVbmF1dGhvcml6ZWQgeG1sbnM9Imh0dHA6Ly93d3cuZGl0ZWMuc2svZWtyL3VuYXV0aG9yaXplZC92MS4wIiBJZD0iUkZPX1BTX1pPWk5BTV9JRk9fUE9ETEFfVllITEFEQVZBQ0lDSF9LUklURVJJSV9CRVpfWkVQX1dTX0lOXzFfMCI+DQoJCTxPYmplY3QgSWQ9ImRlMzEwZDk2LTJjYzMtNGNmNS04NjdlLWJlOTNjMDU3N2VkYiIgSWRlbnRpZmllcj0iaHR0cDovL3d3dy5lZ292LnNrL212c3IvUkZPL2RhdGF0eXBlcy9Qb2RwL0V4dC9Qb3NreXRudXRpZVpvem5hbXVJRk9Qb2RsYVZ5aGxhZGF2YWNpY2hLcml0ZXJpaVdTLXYxLjAueHNkIj4NCgkJCTxUcmFuc0VudkluIHhtbG5zOnhzaT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEtaW5zdGFuY2UiIHhtbG5zPSJodHRwOi8vd3d3LmVnb3Yuc2svbXZzci9SRk8vZGF0YXR5cGVzL1BvZHAvRXh0L1Bvc2t5dG51dGllWm96bmFtdUlGT1BvZGxhVnlobGFkYXZhY2ljaEtyaXRlcmlpV1MtdjEuMC54c2QiPg0KCQkJCTxQT0Q+DQogIDwhLS0gUkZPLlRfUE9TS1lUTlVUSUVfT0RQSVNVX1ZTVFVQICgxLCBfKSAtLT4NCiAgPE9FWD4NCiAgICA8IS0tIFJGTy5UX09TT0JBX0VYVCAoMSwgXykgLS0+DQogICAgPCEtLTxETj4xOTY0LTEyLTEyPC9ETj4gRFRfREFUVU1fTkFST0RFTklBICgxLCAqKSAtLT4NCiAgICA8UkM+NjQxMjEyLzYyOTI8L1JDPg0KICAgIDwhLS1TVl9ST0RORV9DSVNMTyAoMSwgKiktLT4NCiAgICA8RE4+MTk2NC0xMi0xMjwvRE4+DQogICAgPCEtLTE5NjQtMTItMTItLT4NCiAgICA8UEk+MTwvUEk+DQogICAgPCEtLU5MX1BPSExBVklFX0lEICgxLCBfKSBNVVogPSAxLCBaRU5BID0gMiwgTkVVUkNFTkUgPSAzLCAtLT4NCiAgICA8Uk4+MTk2NDwvUk4+DQogICAgPCEtLVJPSyBOQVJPREVOSUEgKDEsICopLS0+DQogICAgPE1PU0xpc3Q+DQogICAgICA8TU9TPg0KICAgICAgICA8TUU+UGV0ZXI8L01FPg0KICAgICAgPC9NT1M+DQogICAgPC9NT1NMaXN0Pg0KICAgIDxQUklMaXN0Pg0KICAgICAgPFBSST4NCiAgICAgICAgPFBSPkdzY2h3ZW5kdDwvUFI+DQogICAgICA8L1BSST4NCiAgICA8L1BSSUxpc3Q+DQogICAgPFJQUkxpc3Q+DQogICAgICA8UlBSPg0KICAgICAgICA8UlA+R3NjaHdlbmR0PC9SUD4NCiAgICAgIDwvUlBSPg0KICAgIDwvUlBSTGlzdD4NCiAgPC9PRVg+DQogIDxVRVM+DQogICAgPCEtLSBSRk8uVF9VREFKRV9FWFRfU1lTVEVNVSAoMSwgXykgLS0+DQogICAgPFBPPmNzcnVfdGVzdDwvUE8+DQogICAgPCEtLSBEZXprbyBTVl9FWFRFUk5ZX1BPVVpJVkFURUwgKDEsIF8pIC0tPg0KICAgIDxUST5hMzYyMGJiZi01YzNhLTRmZmYtYjI4MC0yZWM2YjhjZWNlOWY8L1RJPg0KICAgIDwhLS0gMTIzNDU2IE5MX1RJRF9FWFRFUk5FSE9fU1lTVEVNVSAoMSwgXykgLS0+DQogIDwvVUVTPg0KPC9QT0Q+DQoJCQk8L1RyYW5zRW52SW4+DQoJCTwvT2JqZWN0Pg0KCTwvRG9jdW1lbnRVbmF1dGhvcml6ZWQ+DQo8L1JlZ2lzdHJhdGlvbj4NCg";

    @Autowired
    private CsruEndpoint csruEndpoint;

    @Autowired
    private EncryptionUtils encryptionUtils;

    @Value("${integration.csru.ovmIsId}")
    private String ovmIsId;

    @Autowired
    private RfoService rfoService;

    final sk.is.urso.csru.fo.ObjectFactory objectFactory = new sk.is.urso.csru.fo.ObjectFactory();


//    private <T> T processDecryptedResponse(Document document, String namespaceUri, Class<T> transEnvTypeOutClass) {
//        Element transEnvOut = (Element) document.getElementsByTagNameNS(namespaceUri, "TransEnvIn").item(0);
//        if (transEnvOut == null) {
//            return null;
//        }
//        return XmlUtils.unmarshall(transEnvOut, transEnvTypeOutClass);
//    }

//    @Test
//    public void sendChangedFoRequestSynchr() {
//        GetConsolidatedReferenceDataRequestCType dataRequest = new GetConsolidatedReferenceDataRequestCType();
//        dataRequest.setOvmIsId(ovmIsId);
//        dataRequest.setOeId(OE_ID);
//        dataRequest.setScenario(SCENARIO);
//        dataRequest.setOvmTransactionId(UUID.randomUUID().toString());
//        dataRequest.setOvmCorrelationId(UUID.randomUUID().toString());
//
//        PodanieType podanieType = new PodanieType();
//        podanieType.setTypPodania(TYP_PODANIA);
//        podanieType.setTypSluzby(TYP_SLUZBY);
//        podanieType.setDataPodaniaBase64(DATA_PODANIA);
//
//        DataPlaceholderCType dataPlaceholder = new DataPlaceholderCType();
//        dataPlaceholder.setAny(objectFactory.createPodanie(podanieType));
//        dataRequest.setPayload(dataPlaceholder);
//
//        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
//        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = of.createGetConsolidatedReferenceDataRequest(dataRequest);
//        JAXBElement<GetConsolidatedReferenceDataResponseCType>  response = csruEndpoint.sendRfoChangeRequestSynchr(request);
//
//        Document doc = encryptionUtils.decryptResponse(response.getValue().getPayload());
//        var b = processDecryptedResponse(doc, "http://www.egov.sk/mvsr/RFO/datatypes/Podp/Ext/PoskytnutieZoznamuIFOPodlaVyhladavacichKriteriiWS-v1.0.xsd", TransEnvTypeIn.class);
//        var a = 0;
//    }



//    @Test
//    public void IT1() {
//        TransEnvTypeOut transEnvTypeOut = rfoService.getRfoByRodneCislo("880629/7929");
//        var a = 0;
//    }
}
