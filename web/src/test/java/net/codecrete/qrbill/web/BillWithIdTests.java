//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import net.codecrete.qrbill.web.model.QrBill;
import net.codecrete.qrbill.web.model.ValidationResponse;

/**
 * Unit test for retrieving a bill by ID (API test)
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("SVG bill from ID")
class BillWithIdTests {

    private final TestRestTemplate restTemplate;

    BillWithIdTests(@Autowired TestRestTemplate template) {
        restTemplate = template;
    }

    @Test
    void validateAndRetrieveBill() {
        QrBill bill = SampleData.createBill1();

        ValidationResponse response = restTemplate.postForObject("/bill/validate", bill, ValidationResponse.class);
        String billId = response.getBillID();

        byte[] result = restTemplate.getForObject("/bill/generate/" + billId, byte[].class);

        assertNotNull(response);
        assertTrue(result.length > 1000);

        String text = new String(result, StandardCharsets.UTF_8);
        assertTrue(text.startsWith("<?xml"));
        assertTrue(text.indexOf("<svg") > 0);
        assertTrue(text.indexOf("Meierhans AG") > 0);
    }
}
