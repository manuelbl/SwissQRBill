package net.codecrete.qrbill.web;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Invalid requests")
class InvalidRequestTests {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @LocalServerPort
    int randomServerPort;

    @Test
    void testInvalidEnum() throws IOException {
        Response response = postRequest("/bill/validated",
                "{ \"format\": { \"language\": \"pl\" }, " +
                        "\"amount\": 100.34, \"currency\": \"CHF\", " +
                        "\"account\": \"CH4431999123000889012\", \"creditor\": {" +
                        "\"name\": \"Meierhans AG\", \"street\": \"Bahnhofstrasse\", \"houseNo\": \"16\", " +
                        "\"postalCode\": \"2100\", \"town\": \"Irgendwo\", \"countryCode\": \"CH\" }, " +
                        "\"reference\": \"RF18539007547034\" }"
        );

        assertEquals(400, response.code());
        assertEquals("text/plain;charset=UTF-8", response.header("Content-Type"));
        assertEquals("Unexpected value 'pl'", response.body().string());
    }

    @Test
    void testInvalidNumber1() throws IOException {
        Response response = postRequest("/bill/validated",
                "{ \"format\": { \"language\": \"pl\" }, " +
                        "\"amount\": abc, \"currency\": \"CHF\", " +
                        "\"account\": \"CH4431999123000889012\", \"creditor\": {" +
                        "\"name\": \"Meierhans AG\", \"street\": \"Bahnhofstrasse\", \"houseNo\": \"16\", " +
                        "\"postalCode\": \"2100\", \"town\": \"Irgendwo\", \"countryCode\": \"CH\" }, " +
                        "\"reference\": \"RF18539007547034\" }"
        );

        assertEquals(400, response.code());
    }

    @Test
    void testInvalidNumber2() throws IOException {
        Response response = postRequest("/bill/validated",
                "{ \"format\": { \"language\": \"pl\" }, " +
                        "\"amount\": \"abc\", \"currency\": \"CHF\", " +
                        "\"account\": \"CH4431999123000889012\", \"creditor\": {" +
                        "\"name\": \"Meierhans AG\", \"street\": \"Bahnhofstrasse\", \"houseNo\": \"16\", " +
                        "\"postalCode\": \"2100\", \"town\": \"Irgendwo\", \"countryCode\": \"CH\" }, " +
                        "\"reference\": \"RF18539007547034\" }"
        );

        assertEquals(400, response.code());
    }

    @Test
    void testInvalidJson() throws IOException {
        Response response = postRequest("/bill/validated",
                "{ \"language\": \"de\", \"amount\": \"100.34\", \"currency\": \"CHF\", [" +
                        "\"account\": \"CH4431999123000889012\", \"creditor\": {" +
                        "\"name\": \"Meierhans AG\", \"street\": \"Bahnhofstrasse\", \"houseNo\": \"16\", " +
                        "\"postalCode\": \"2100\", \"town\": \"Irgendwo\", \"countryCode\": \"CH\" }, " +
                        "\"reference\": \"RF18539007547034\" }"
        );

        assertEquals(400, response.code());
    }

    @Test
    void testInvalidUrl() throws IOException {
        Response response = postRequest("/bill2/validated",
                "{ \"language\": \"de\", \"amount\": \"100.34\", \"currency\": \"CHF\", " +
                        "\"account\": \"CH4431999123000889012\", \"creditor\": {" +
                        "\"name\": \"Meierhans AG\", \"street\": \"Bahnhofstrasse\", \"houseNo\": \"16\", " +
                        "\"postalCode\": \"2100\", \"town\": \"Irgendwo\", \"countryCode\": \"CH\" }, " +
                        "\"reference\": \"RF18539007547034\" }"
        );

        assertEquals(404, response.code());
    }

    private Response postRequest(String relativeUrl, String body) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, body.getBytes(StandardCharsets.UTF_8));
        Request request = new Request.Builder()
                .url(String.format("http://localhost:%d/qrbill-api%s", randomServerPort, relativeUrl))
                .method("POST", requestBody)
                .build();

        return client.newCall(request).execute();
    }
}
