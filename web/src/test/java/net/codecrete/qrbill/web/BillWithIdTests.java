//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import net.codecrete.qrbill.web.api.QrBill;
import net.codecrete.qrbill.web.api.ValidationResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BillWithIdTests {

    @Autowired
    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void okTest1() {
        QrBill bill = SampleData.createBill1();

        ValidationResponse response = restTemplate.postForObject("/bill/validate", bill, ValidationResponse.class);
        String billId = response.getBillID();

        byte[] result = restTemplate.getForObject("/bill/svg/a6-landscape/" + billId, byte[].class);

        assertNotNull(response);
        assertTrue(result.length > 1000);

        String text = new String(result, StandardCharsets.UTF_8);
        assertTrue(text.startsWith("<?xml"));
        assertTrue(text.indexOf("<svg") > 0);
        assertTrue(text.indexOf("Meierhans AG") > 0);
    }
}
