//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.ValidationMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ValidationTests {

    @Autowired
    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void okValidationTest() {
        Bill bill = createBill();

        ValidationResponse response = restTemplate.postForObject("/api/validate", bill, ValidationResponse.class);

        assertNotNull(response);
        assertNull(response.getValidationMessages());
        assertNotNull(response.getValidatedBill());
        assertEquals(bill, response.getValidatedBill());
    }

    @Test
    public void truncationWarningTest() {
        Bill bill = createBill();
        bill.getCreditor().setCity("city56789012345678901234567890123456");

        ValidationResponse response = restTemplate.postForObject("/api/validate", bill, ValidationResponse.class);

        assertNotNull(response);
        assertNotNull(response.getValidationMessages());
        assertEquals(1, response.getValidationMessages().size());
        assertEquals(ValidationMessage.Type.Warning, response.getValidationMessages().get(0).getType());
        assertEquals(Bill.FIELD_CREDITOR_CITY, response.getValidationMessages().get(0).getField());
        assertEquals("field_clipped", response.getValidationMessages().get(0).getMessageKey());

        assertNotNull(response.getValidatedBill());

        bill.getCreditor().setCity("city5678901234567890123456789012345");
        assertEquals(bill, response.getValidatedBill());
    }

    private Bill createBill() {
        Bill bill = new Bill();
        bill.setLanguage(Bill.Language.German);
        bill.setAmountOpen(false);
        bill.setAmount(100.35);
        bill.setCurrency("CHF");
        bill.setAccount("CH4431999123000889012");
        bill.getCreditor().setName("Meierhans AG");
        bill.getCreditor().setStreet("Bahnhofstrasse");
        bill.getCreditor().setHouseNumber("16");
        bill.getCreditor().setPostalCode("2100");
        bill.getCreditor().setCity("Irgendwo");
        bill.getCreditor().setCountryCode("CH");
        bill.setReferenceNo("RF18539007547034");
        return bill;
    }
}
