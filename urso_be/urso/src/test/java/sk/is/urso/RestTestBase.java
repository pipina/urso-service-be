package sk.is.urso;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.alfa.exception.CommonException;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.openapitools.jackson.nullable.JsonNullable;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.util.NestedServletException;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RestTestBase {
    @LocalServerPort
    int port;

    @Autowired
    protected MockMvc mockMvc;

    private static final Class<JsonNullable<LocalDate>> type = (Class<JsonNullable<LocalDate>>) (Object) JsonNullable.class;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, (JsonDeserializer<DateTime>)
                    (json, typeOfT, context) -> new DateTime(json.getAsString()))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                    (json, typeOfT, context) -> LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                    (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString()))
            .registerTypeAdapter(type, (JsonDeserializer<JsonNullable<LocalDate>>)
                    (json, typeOfT, context) -> JsonNullable.of(LocalDate.parse(json.getAsString()))).create();

    public URI getUri(String path) throws URISyntaxException {
        return new URI("http://localhost:" + port + "/" + path);
    }

    protected <T> T getResponseBody(ResultActions result, Class<T> tClass) throws UnsupportedEncodingException {
        String responseBody = result.andReturn().getResponse().getContentAsString();
        assertNotNull(responseBody);
        return gson.fromJson(responseBody, tClass);
    }

    protected String getJsonRequest(Object request) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new JsonNullableModule());
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(request);
    }

    protected void checkExceptionStatusCode(HttpStatus statusCode, MockHttpServletRequestBuilder builder) throws Exception {
        try {
            mockMvc.perform(builder);
        } catch (NestedServletException nestedServletException) {
            CommonException commonException = (CommonException) nestedServletException.getCause();
            Assertions.assertEquals(commonException.getStatus(), statusCode);
        }
    }
}
