//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import net.codecrete.qrbill.web.api.QrCodeInformation;
import net.codecrete.qrbill.web.api.ValidationResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DecodeTests {

    private static final String VALID_QR_CODE_TEXT =
            "SPC\r\n" +
            "0100\r\n" +
            "1\r\n" +
            "CH2109000000450980316\r\n" +
            "Druckerei Stefan Meierhans\r\n" +
            "Trittligasse\r\n" +
            "12\r\n" +
            "3001\r\n" +
            "Bern\r\n" +
            "CH\r\n" +
            "\r\n" +
            "\r\n" +
            "\r\n" +
            "\r\n" +
            "\r\n" +
            "\r\n" +
            "45.60\r\n" +
            "CHF\r\n" +
            "2018-04-26\r\n" +
            "Anneliese Schmid\r\n" +
            "Segelhofstrasse\r\n" +
            "13\r\n" +
            "5057\r\n" +
            "Reitnau\r\n" +
            "CH\r\n" +
            "QRR\r\n" +
            "829300097829382938291172974\r\n";

    @Autowired
    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void okValidationTest() {

        QrCodeInformation info = new QrCodeInformation();
        info.setQrCodeText(VALID_QR_CODE_TEXT);

        ValidationResponse response = restTemplate.postForObject("//bill/decode", info, ValidationResponse.class);

        assertNotNull(response);
        assertTrue(response.isValid());
        assertNull(response.getValidationMessages());
        assertNotNull(response.getValidatedBill());
        assertNotNull(response.getBillID());
        assertTrue(response.getBillID().length() > 100);
        assertNotNull(response.getQrCodeText());
        assertEquals(VALID_QR_CODE_TEXT, response.getQrCodeText());
    }
}
