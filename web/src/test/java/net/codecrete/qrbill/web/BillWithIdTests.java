//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import net.codecrete.qrbill.web.model.QrBill;
import net.codecrete.qrbill.web.model.ValidationResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for retrieving a bill by ID (API test)
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("SVG bill from ID")
class BillWithIdTests {

    private static final String VALID_BILL_ID =
            "eJxdT81OwzAMfpXI50Zqe0C010K1AxoTmTjlYrqQWUqdkqSDddq7kw5Osw-2_P3JFziZEMkztFUBnz6MmKC9gEO2M1oDLVCCAmzA6UhD7P8ZPDtXgJ_TNCdFy8r7CvKDnJOe3TkropkwYPJhf55WOHpHB-mIjfymdJRxoBh9iLDGcupxpKy7GV-LbLY3PzkH1K7TXNZlqbnS3G2qpmzKWz3kWT_WVaNZZST4wYg3HyMKdaJlMQGFFMos-TkjDsYJ5VPKJDaBNL8TCnQORYfjhJZRczZaPTW_zBbZr2ma77vb9Pen7ev2b3nePWmG6y8FaXYA";

    @LocalServerPort
    int randomServerPort;

    private final TestRestTemplate restTemplate;

    BillWithIdTests(@Autowired TestRestTemplate template) {
        restTemplate = template;
    }

    @Test
    void validateAndRetrieveBill() {
        QrBill bill = SampleData.createBill1();

        ValidationResponse response = restTemplate.postForObject("/bill/validate", bill, ValidationResponse.class);
        assertNotNull(response);

        String billId = response.getBillID();
        byte[] result = restTemplate.getForObject("/bill/generate/" + billId, byte[].class);

        assertNotNull(result);
        assertTrue(result.length > 10000);

        String text = new String(result, StandardCharsets.UTF_8);
        assertTrue(text.startsWith("<?xml"));
        assertTrue(text.indexOf("<svg") > 0);
        assertTrue(text.indexOf("Meierhans AG") > 0);
    }

    @Test
    void validateAndRetrieveBillWithDefaultFormat() {
        QrBill bill = SampleData.createBill2();
        bill.setFormat(null);

        ValidationResponse response = restTemplate.postForObject("/bill/validate", bill, ValidationResponse.class);
        assertNotNull(response);

        String billId = response.getBillID();
        byte[] result = restTemplate.getForObject("/bill/generate/" + billId, byte[].class);

        assertNotNull(result);
        assertTrue(result.length > 10000);

        String text = new String(result, StandardCharsets.UTF_8);
        assertTrue(text.startsWith("<?xml"));
        assertTrue(text.indexOf("<svg") > 0);
        assertTrue(text.indexOf("Kramer") > 0);
    }

    @Test
    void validateAndRetrieveBillWithNullFormatValues() {
        QrBill bill = SampleData.createBill2();
        bill.getFormat().setGraphicsFormat(null);
        bill.getFormat().setOutputSize(null);
        bill.getFormat().setLanguage(null);
        bill.getFormat().setFontFamily(null);
        bill.getFormat().setSeparatorType(null);

        ValidationResponse response = restTemplate.postForObject("/bill/validate", bill, ValidationResponse.class);
        assertNotNull(response);

        String billId = response.getBillID();
        byte[] result = restTemplate.getForObject("/bill/generate/" + billId, byte[].class);

        assertNotNull(result);
        assertTrue(result.length > 10000);

        String text = new String(result, StandardCharsets.UTF_8);
        assertTrue(text.startsWith("<?xml"));
        assertTrue(text.indexOf("<svg") > 0);
        assertTrue(text.indexOf("Kramer") > 0);
    }

    @Test
    void retrieveBillOverrideOutputSize() {
        byte[] result = restTemplate.getForObject("/bill/generate/" + VALID_BILL_ID + "?outputSize=qr-code-only", byte[].class);

        assertNotNull(result);
        assertTrue(result.length > 1000 && result.length < 10000);

        String text = new String(result, StandardCharsets.UTF_8);
        assertTrue(text.startsWith("<?xml"));
        assertTrue(text.indexOf("<svg") > 0);
        assertFalse(text.indexOf("Croce") > 0);
    }

    @Test
    void retrieveBillOverrideGraphicsFormat() {
        byte[] result = restTemplate.getForObject("/bill/generate/" + VALID_BILL_ID + "?graphicsFormat=pdf", byte[].class);

        assertNotNull(result);
        assertTrue(result.length > 1000);
        assertTrue(result[0] == '%' && result[1] == 'P' && result[2] == 'D' && result[3] == 'F');
    }


    @Test
    void retrieveWithInvalidBillID() throws IOException {
        Response response = getRequest("/bill/generate/eJxdT81OwzAMfpXI50Zqe0C010K1AxoTmTjlYrqQWUqdkqSDddq7kw5Osw");
        assertEquals(400, response.code());
    }

    private Response getRequest(String relativeUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(String.format("http://localhost:%d/qrbill-api%s", randomServerPort, relativeUrl))
                .build();

        return client.newCall(request).execute();
    }
}
