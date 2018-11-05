package net.codecrete.qrbill.web;

import okhttp3.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Invalid requests")
public class InvalidRequestTests {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @LocalServerPort
    int randomServerPort;

    @Test
    void testInvalidEnum() throws IOException {
        Response response = postRequest("/qrbill-api/bill/validate",
                "{ \"language\": \"pl\", \"amount\": 100.34, \"currency\": \"CHF\", " +
                        "\"account\": \"CH4431999123000889012\", \"creditor\": {" +
                        "\"name\": \"Meierhans AG\", \"street\": \"Bahnhofstrasse\", \"houseNo\": \"16\", " +
                        "\"postalCode\": \"2100\", \"town\": \"Irgendwo\", \"countryCode\": \"CH\" }, " +
                        "\"referenceNo\": \"RF18539007547034\" }"
        );

        assertEquals(400, response.code());
        assertEquals("text/plain;charset=UTF-8", response.header("Content-Type"));
        assertEquals("Unexpected value 'pl'", response.body().string());
    }

    @Test
    void testInvalidEnumInURL() throws IOException {
        Response response = postRequest("/qrbill-api/bill/svg/a7-landscape",
            "{ \"language\": \"de\", \"amount\": 100.34, \"currency\": \"CHF\", " +
                    "\"account\": \"CH4431999123000889012\", \"creditor\": {" +
                    "\"name\": \"Meierhans AG\", \"street\": \"Bahnhofstrasse\", \"houseNo\": \"16\", " +
                    "\"postalCode\": \"2100\", \"town\": \"Irgendwo\", \"countryCode\": \"CH\" }, " +
                    "\"referenceNo\": \"RF18539007547034\" }"
        );

        assertEquals(400, response.code());
        assertEquals("text/plain;charset=UTF-8", response.header("Content-Type"));
        assertEquals("Invalid bill format in URL. Valid values: qr-code-only, a6-landscape, a5-landscape, a4-portrait", response.body().string());
    }

    @Test
    void testInvalidNumber() throws IOException {
        Response response = postRequest("/qrbill-api/bill/validate",
                "{ \"language\": \"de\", \"amount\": \"abc\", \"currency\": \"CHF\", " +
                        "\"account\": \"CH4431999123000889012\", \"creditor\": {" +
                        "\"name\": \"Meierhans AG\", \"street\": \"Bahnhofstrasse\", \"houseNo\": \"16\", " +
                        "\"postalCode\": \"2100\", \"town\": \"Irgendwo\", \"countryCode\": \"CH\" }, " +
                        "\"referenceNo\": \"RF18539007547034\" }"
        );

        assertEquals(400, response.code());
    }

    private Response postRequest(String relativeUrl, String body) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, body.getBytes(StandardCharsets.UTF_8));
        Request request = new Request.Builder()
                .url(String.format("http://localhost:%d%s", randomServerPort, relativeUrl))
                .method("POST", requestBody)
                .build();

        return client.newCall(request).execute();
    }
}
