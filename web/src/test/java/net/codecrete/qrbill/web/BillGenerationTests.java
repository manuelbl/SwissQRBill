//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import net.codecrete.qrbill.web.model.BillFormat;
import net.codecrete.qrbill.web.model.QrBill;
import net.codecrete.qrbill.web.model.ValidationMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for QR bill generation API (PDF and SVG)
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("QR bill generation")
class BillGenerationTests {

    private final TestRestTemplate restTemplate;

    BillGenerationTests(@Autowired TestRestTemplate template) {
        restTemplate = template;
    }

    @Test
    void svgQrBill() {

        QrBill bill = SampleData.createBill1();
        byte[] response = restTemplate.postForObject("/bill/image", bill, byte[].class);

        assertNotNull(response);
        assertTrue(response.length > 1000);

        String text = new String(response, StandardCharsets.UTF_8);
        assertTrue(text.startsWith("<?xml"));
        assertTrue(text.indexOf("<svg") > 0);
        assertTrue(text.indexOf("Meierhans AG") > 0);
    }

    @Test
    void pdfQrBill() {

        QrBill bill = SampleData.createBill1();
        bill.getFormat().setGraphicsFormat(BillFormat.GraphicsFormatEnum.PDF);
        byte[] response = restTemplate.postForObject("/bill/image", bill, byte[].class);

        assertNotNull(response);
        assertTrue(response.length > 1000);

        String text = new String(response, 0, 8, StandardCharsets.UTF_8);
        assertEquals("%PDF-1.4", text);
    }

    @Test
    void svgWithTruncatedTown() {
        QrBill bill = SampleData.createBill1();
        bill.getCreditor().setTown("city56789012345678901234567890123456");

        byte[] response = restTemplate.postForObject("/bill/image", bill, byte[].class);

        assertNotNull(response);
        assertTrue(response.length > 1000);

        String text = new String(response, StandardCharsets.UTF_8);
        assertTrue(text.startsWith("<?xml"));
        assertTrue(text.indexOf("<svg") > 0);
        assertTrue(text.indexOf("font-size=\"10\">2100 city5678901234567890123456789012345</text>") > 0);
    }

    @Test
    void oneValidationError() {
        QrBill bill = SampleData.createBill1();
        bill.getCreditor().setTown(null);

        ValidationMessage[] response = restTemplate.postForObject("/bill/image", bill,
                ValidationMessage[].class);

        assertNotNull(response);
        assertEquals(1, response.length);
        assertEquals(ValidationMessage.TypeEnum.ERROR, response[0].getType());
        assertEquals("creditor.town", response[0].getField());
        assertEquals("field_is_mandatory", response[0].getMessageKey());
    }

    @Test
    void languageFromHeaderIT() {
        testLanguageFromHeader("it-CH", "Sezione pagamento");
    }

    @Test
    void languageFromHeaderDE() {
        testLanguageFromHeader("zh, de", "Zahlteil");
    }

    @Test
    void languageFromHeaderFR() {
        testLanguageFromHeader("fr-CH, fr;q=0.9, en;q=0.8, de;q=0.7, *;q=0.5", "Section paiement");
    }

    @Test
    void languageFromHeaderEN() {
        testLanguageFromHeader("en-US, en", "Payment part");
    }

    @Test
    void languageFromHeaderDefault() {
        testLanguageFromHeader(null, "Payment part");
    }

    private void testLanguageFromHeader(String language, String textFragment) {
        QrBill bill = SampleData.createBill1();
        bill.setFormat(null);

        HttpHeaders headers = new HttpHeaders();
        if (language != null)
            headers.setAcceptLanguage(Locale.LanguageRange.parse(language));

        HttpEntity<QrBill> entity = new HttpEntity<>(bill, headers);
        byte[] response = restTemplate.postForObject("/bill/image", entity, byte[].class);

        assertNotNull(response);
        assertTrue(response.length > 1000);

        String text = new String(response, StandardCharsets.UTF_8);
        assertTrue(text.startsWith("<?xml"));
        assertTrue(text.indexOf("<svg") > 0);
        assertTrue(text.contains(textFragment));
    }

    @Test
    void graphicsFormatFromHeader() {
        QrBill bill = SampleData.createBill1();
        bill.setFormat(null);

        MediaType mediaType1 = new MediaType("text", "plain");
        MediaType mediaType2 = new MediaType("application", "pdf");
        List<MediaType> mediaTypes = new ArrayList<>(2);
        mediaTypes.add(mediaType1);
        mediaTypes.add(mediaType2);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(mediaTypes);

        HttpEntity<QrBill> entity = new HttpEntity<>(bill, headers);
        byte[] response = restTemplate.postForObject("/bill/image", entity, byte[].class);

        assertNotNull(response);
        assertTrue(response.length > 1000);
        assertTrue(response[0] == '%' && response[1] == 'P' && response[2] == 'D' && response[3] == 'F');
    }

    @Test
    void testDefault() {
        QrBill bill = SampleData.createBill1();
        bill.setFormat(null);

        byte[] response = restTemplate.postForObject("/bill/image", bill, byte[].class);

        String text = new String(response, StandardCharsets.UTF_8);
        assertTrue(text.startsWith("<?xml"));
        assertTrue(text.indexOf("<svg") > 0);
        assertTrue(text.contains("Payment part"));
    }
}
