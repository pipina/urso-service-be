package sk.is.urso.service.csru;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sk.is.urso.config.csru.CsruEndpoint;
import sk.is.urso.model.csru.api.async.GetChangedReferenceDataServiceAsync.GetChangedReferenceDataRequestCType;
import sk.is.urso.model.csru.api.async.GetChangedReferenceDataServiceAsync.GetChangedReferenceDataResponseCType;
import sk.is.urso.model.csru.api.async.GetReferenceDataByIdentifiersServiceAsync.GetReferenceDataByIdentifiersRequestCType;
import sk.is.urso.model.csru.api.async.GetReferenceDataByIdentifiersServiceAsync.GetReferenceDataByIdentifiersResponseCType;
import sk.is.urso.model.csru.api.async.common.GetStatusRequestCType;
import sk.is.urso.model.csru.api.async.common.GetStatusResponseCType;
import sk.is.urso.model.csru.api.async.common.ParameterCType;
import sk.is.urso.model.csru.api.async.common.ParameterListCType;
import sk.is.urso.model.csru.api.async.common.FileCType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class CsruZcService {
    private static final String ZC = "ZC";

    private static final String ZC_KOD = "ZC_KOD";

    private static final String DATE_FROM = "DateFrom";

    private static final String DATE_TO = "DateTo";

    private static final String SCENARIO = "Scenario";

    private static final String P_ZC_ALL = "P_ZC_ALL";

    @Autowired
    CsruEndpoint csruEndpoint;

    @Autowired

    @Qualifier("csruGetFile")
    protected RestTemplate restTemplate;

    @Value("${integration.csru.ovmIsId}")
    private String ovmIsId;

    @Value("${integration.csru.ref-async.url}")
    private String csruRefAsyncUrl;

    @Value("${integration.csru.rest-file.url}")
    private String csruRestFileUrl;

    public GetReferenceDataByIdentifiersResponseCType getReferenceDataByIdentifiers(String correlationId, List<String> codelistCodes) {
        ParameterListCType parameterList = new ParameterListCType();
        GetReferenceDataByIdentifiersRequestCType dataByIdentifiersRequest = new GetReferenceDataByIdentifiersRequestCType();
        dataByIdentifiersRequest.setOvmIsId(ovmIsId);
        dataByIdentifiersRequest.setOeId(ZC);
        dataByIdentifiersRequest.setOvmTransactionId(UUID.randomUUID().toString());
        dataByIdentifiersRequest.setOvmCorrelationId(correlationId);
//        dataByIdentifiersRequest.setOvmCorrelationId("23962e3d-8d78-4abc-a533-ad2e1caf9059");
        dataByIdentifiersRequest.setParameters(parameterList);

        codelistCodes.forEach(codelistCode -> {
            ParameterCType parameter = new ParameterCType();
            parameter.setName(ZC_KOD);
            parameter.setValue(codelistCode);
            parameterList.getParameter().add(parameter);
        });

        sk.is.urso.model.csru.api.async.common.ObjectFactory of = new sk.is.urso.model.csru.api.async.common.ObjectFactory();
        JAXBElement<GetReferenceDataByIdentifiersRequestCType> request = of.createGetReferenceDataByIdentifiersRequest(dataByIdentifiersRequest);
        JAXBElement<GetReferenceDataByIdentifiersResponseCType> response = (JAXBElement<GetReferenceDataByIdentifiersResponseCType>) csruEndpoint.callWebService(csruRefAsyncUrl, request);


        return response.getValue();
    }

    public GetChangedReferenceDataResponseCType getChangedReferenceData(String correlationId, LocalDate date_from) {
        ParameterListCType parameterList = new ParameterListCType();
        GetChangedReferenceDataRequestCType changedReferenceDataRequest = new GetChangedReferenceDataRequestCType();
        changedReferenceDataRequest.setOvmIsId(ovmIsId);
        changedReferenceDataRequest.setOeId(ZC);
        changedReferenceDataRequest.setOvmTransactionId(UUID.randomUUID().toString());
        changedReferenceDataRequest.setOvmCorrelationId(correlationId);
        changedReferenceDataRequest.setParameters(parameterList);

        ParameterCType parameterDateFrom = new ParameterCType();
        parameterDateFrom.setName(DATE_FROM);
        parameterDateFrom.setValue(date_from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        parameterList.getParameter().add(parameterDateFrom);

        ParameterCType parameterDateTo = new ParameterCType();
        parameterDateTo.setName(DATE_TO);
        parameterDateTo.setValue(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        parameterList.getParameter().add(parameterDateTo);

        ParameterCType parameterScenario = new ParameterCType();
        parameterScenario.setName(SCENARIO);
        parameterScenario.setValue(P_ZC_ALL);
        parameterList.getParameter().add(parameterScenario);

        sk.is.urso.model.csru.api.async.common.ObjectFactory of = new sk.is.urso.model.csru.api.async.common.ObjectFactory();
        JAXBElement<GetChangedReferenceDataRequestCType> request = of.createGetChangedReferenceDataRequest(changedReferenceDataRequest);
        JAXBElement<GetChangedReferenceDataResponseCType> response = (JAXBElement<GetChangedReferenceDataResponseCType>) csruEndpoint.callWebService(csruRefAsyncUrl, request);

        return response.getValue();
    }

    public GetStatusResponseCType getStatus(String correlationId, Long requestId) {
        GetStatusRequestCType statusRequest = new GetStatusRequestCType();
        statusRequest.setOvmIsId(ovmIsId);
        statusRequest.setOvmTransactionId(UUID.randomUUID().toString());
        statusRequest.setOvmCorrelationId(correlationId);
        statusRequest.setRequestId(requestId);
        sk.is.urso.model.csru.api.async.common.ObjectFactory of = new sk.is.urso.model.csru.api.async.common.ObjectFactory();
        JAXBElement<GetStatusRequestCType> request = of.createGetStatusRequest(statusRequest);
        JAXBElement<GetStatusResponseCType> response = (JAXBElement<GetStatusResponseCType>) csruEndpoint.callWebService(csruRefAsyncUrl, request);

        return response.getValue();
    }

    public String getFile(FileCType file) throws URISyntaxException {
        ResponseEntity<String> response = restTemplate.exchange(csruEndpoint.prepareGetRequest(new URI(csruRestFileUrl + ovmIsId + file.getPath())), String.class);
        return response.getBody();
    }
}
