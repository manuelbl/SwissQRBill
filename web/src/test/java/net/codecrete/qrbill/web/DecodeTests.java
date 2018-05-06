//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import net.codecrete.qrbill.web.api.QrCodeInformation;
import net.codecrete.qrbill.web.api.ValidationResponse;


/**
 * Unit test for QR code decoding API
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Decode QR code text")
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
    private TestRestTemplate restTemplate;

    @Test
    public void decodeText() {

        QrCodeInformation info = new QrCodeInformation();
        info.setQrCodeText(VALID_QR_CODE_TEXT);

        ValidationResponse response = restTemplate.postForObject("/bill/decode", info, ValidationResponse.class);

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
