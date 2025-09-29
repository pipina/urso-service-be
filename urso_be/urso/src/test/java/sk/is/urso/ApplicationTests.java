package sk.is.urso;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sk.is.urso.be.Application;
import sk.is.urso.rest.model.Version;


import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ApplicationTests extends RestTestBase {

    @Test
    void contextLoads() throws Exception {
        final URI uri = getUri("version");

        ResultActions result = mockMvc.perform(get(uri));
        result.andExpect(MockMvcResultMatchers.status().isOk());

        Version versionOutput = getResponseBody(result, Version.class);
        assertNotNull(versionOutput);
    }
}
