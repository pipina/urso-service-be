package sk.is.urso.config.csru;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.bind.Marshaller;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class CsruConfig {

    @Value("${integration.csru.executors:#{5}}")
    private int executorThreadPools;

    @Value("${integration.csru.proxy.hostname}")
    public String hostname;

    @Value("${integration.csru.proxy.port}")
    public Integer port;
    
    @Value("${integration.csru.proxy}")
    public boolean isProxyEnabled;

    @Bean("csruSearchExecutor")
    public ExecutorService initializeExecutorService() {
        return Executors.newFixedThreadPool(executorThreadPools);
    }

    @Bean("csruFoWS")
    public WebServiceTemplate webServiceTemplate() {
        WebServiceTemplate wsTempl = new WebServiceTemplate();
        wsTempl.setMarshaller(jaxb2Marshaller());
        wsTempl.setUnmarshaller(jaxb2Marshaller());
        wsTempl.setCheckConnectionForFault(true);
        return wsTempl;
    }

    @Bean
    Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan(
                "sk.is.urso.csru.fo",
//                "sk.is.urso.csru.fo.codelist",
                "sk.is.urso.model.csru.api.common",
                "sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync",
                "sk.is.urso.model.csru.api.async.GetChangedReferemceDataServiceAsync",
                "sk.is.urso.model.csru.api.async.GetReferenceDataByIdentifiersServiceAsync",
                "sk.is.urso.model.csru.api.async.common",
                "sk.is.urso.model.csru.api.sync.common",
                "sk.is.urso.model.csru.api.sync.GetConsolidatedDataServiceSync",
                "sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync",
                "sk.is.urso.csru.po.changes",
                "sk.is.urso.csru.ra",
                "sk.is.urso.csru.ra.changes");
        Map<String, Object> properties = new HashMap<>();
        properties.put(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setMarshallerProperties(properties);
        return marshaller;
    }

    @Bean("csruGetFile")
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        if(isProxyEnabled)
        {
        	Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port));
        	requestFactory.setProxy(proxy);
        }
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }
}
