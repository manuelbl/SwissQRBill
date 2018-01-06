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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ValidationTests {

    @Autowired
    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void okValidationTest() {
        QrBill bill = SampleData.createBill1();

        ValidationResponse response = restTemplate.postForObject("/bill/validate", bill, ValidationResponse.class);

        assertNotNull(response);
        assertTrue(response.isValid());
        assertNull(response.getValidationMessages());
        assertNotNull(response.getValidatedBill());
        assertEquals(bill, response.getValidatedBill());
        assertNotNull(response.getBillID());
        assertTrue(response.getBillID().length() > 100);
        assertNotNull(response.getQrCodeText());
        assertTrue(response.getQrCodeText().length() > 100);
    }

    @Test
    public void truncationWarningTest() {
        QrBill bill = SampleData.createBill1();
        bill.getCreditor().setTown("city56789012345678901234567890123456");

        ValidationResponse response = restTemplate.postForObject("/bill/validate", bill, ValidationResponse.class);

        assertNotNull(response);
        assertTrue(response.isValid());
        assertNotNull(response.getValidationMessages());
        assertEquals(1, response.getValidationMessages().size());
        assertEquals(ValidationMessage.Type.Warning, response.getValidationMessages().get(0).getType());
        assertEquals("creditor.town", response.getValidationMessages().get(0).getField());
        assertEquals("field_clipped", response.getValidationMessages().get(0).getMessageKey());

        assertNotNull(response.getValidatedBill());

        bill.getCreditor().setTown("city5678901234567890123456789012345");
        assertEquals(bill, response.getValidatedBill());

        assertNotNull(response.getBillID());
        assertTrue(response.getBillID().length() > 100);
        assertNotNull(response.getQrCodeText());
        assertTrue(response.getQrCodeText().length() > 100);
    }

    @Test
    public void missingCreditorTest() {
        QrBill bill = SampleData.createBill1();
        bill.setCreditor(null);

        ValidationResponse response = restTemplate.postForObject("/bill/validate", bill, ValidationResponse.class);

        assertNotNull(response);
        assertFalse(response.isValid());
        assertNotNull(response.getValidationMessages());
        assertEquals(4, response.getValidationMessages().size());
        for (ValidationMessage message : response.getValidationMessages()) {
            assertEquals(ValidationMessage.Type.Error, message.getType());
            assertTrue(message.getField().startsWith("creditor."));
            assertEquals("field_is_mandatory", message.getMessageKey());
        }
        assertNull(response.getBillID());
        assertNull(response.getQrCodeText());
    }
}
