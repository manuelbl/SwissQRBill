//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import net.codecrete.qrbill.web.api.QrBill;
import net.codecrete.qrbill.web.api.ValidationMessage;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ValidationTests {

    @Autowired
    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void okValidationTest() {
        QrBill bill = createBill();

        ValidationResponse response = restTemplate.postForObject("/api/validate", bill, ValidationResponse.class);

        assertNotNull(response);
        assertNull(response.getValidationMessages());
        assertNotNull(response.getValidatedBill());
        assertEquals(bill, response.getValidatedBill());
    }

    @Test
    public void truncationWarningTest() {
        QrBill bill = createBill();
        bill.getCreditor().setTown("city56789012345678901234567890123456");

        ValidationResponse response = restTemplate.postForObject("/api/validate", bill, ValidationResponse.class);

        assertNotNull(response);
        assertNotNull(response.getValidationMessages());
        assertEquals(1, response.getValidationMessages().size());
        assertEquals(ValidationMessage.Type.Warning, response.getValidationMessages().get(0).getType());
        assertEquals(".creditor.town", response.getValidationMessages().get(0).getField());
        assertEquals("field_clipped", response.getValidationMessages().get(0).getMessageKey());

        assertNotNull(response.getValidatedBill());

        bill.getCreditor().setTown("city5678901234567890123456789012345");
        assertEquals(bill, response.getValidatedBill());
    }

    private QrBill createBill() {
        QrBill bill = new QrBill();
        bill.setLanguage(QrBill.Language.de);
        bill.setAmount(100.35);
        bill.setCurrency("CHF");
        bill.setAccount("CH4431999123000889012");
        bill.getCreditor().setName("Meierhans AG");
        bill.getCreditor().setStreet("Bahnhofstrasse");
        bill.getCreditor().setHouseNo("16");
        bill.getCreditor().setPostalCode("2100");
        bill.getCreditor().setTown("Irgendwo");
        bill.getCreditor().setCountryCode("CH");
        bill.setReferenceNo("RF18539007547034");
        return bill;
    }
}
