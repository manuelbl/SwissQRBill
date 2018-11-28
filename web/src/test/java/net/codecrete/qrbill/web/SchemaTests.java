package net.codecrete.qrbill.web;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Schema")
class SchemaTests {

    @LocalServerPort
    int randomServerPort;

    @Test
    void testInvalidEnum() throws IOException {
        Response response = getRequest("/qrbill.yaml");

        assertEquals(200, response.code());
        assertEquals("application/x-yaml;charset=UTF-8", response.header("Content-Type"));
        String body = response.body().string();
        assertNotNull(body);
        assertTrue(body.startsWith("openapi: "));
        assertTrue(body.contains("/bill/validate"));
        assertTrue(body.length() > 10000);
    }

    private Response getRequest(String relativeUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(String.format("http://localhost:%d/qrbill-api%s", randomServerPort, relativeUrl))
                .build();

        return client.newCall(request).execute();
    }
}
