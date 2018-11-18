//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Bill;

/**
 * Unit tests for the validation of the reference number of a bill
 */
@DisplayName("Reference number validation (in bill data)")
class ReferenceNumberValidationTest extends BillDataValidationBase {

    @Test
    void validQRReference() {
        bill = SampleData.getExample1();
        bill.setReferenceNo("210000000003139471430009017");
        validate();
        assertNoMessages();
        assertEquals("210000000003139471430009017", validatedBill.getReferenceNo());
    }

    @Test
    void validQRReferenceWithSpaces() {
        bill = SampleData.getExample1();
        bill.setReferenceNo("21 00000 00003 13947 14300 09017");
        validate();
        assertNoMessages();
        assertEquals("210000000003139471430009017", validatedBill.getReferenceNo());
    }

    @Test
    void validCreditorReference() {
        bill = SampleData.getExample1();
        bill.setReferenceNo("RF18539007547034");
        validate();
        assertNoMessages();
        assertEquals("RF18539007547034", validatedBill.getReferenceNo());
    }

    @Test
    void qrIBANNoAndQRReference() {
        bill = SampleData.getExample1();
        bill.setAccount("CH3709000000304442225"); // non QR-IBAN
        bill.setReferenceNo(null);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getReferenceNo());
    }

    @Test
    void whitespaceReferenceNo() {
        bill = SampleData.getExample1();
        bill.setAccount("CH3709000000304442225"); // non QR-IBAN
        bill.setReferenceNo("   ");
        validate();
        assertNoMessages();
        assertNull(validatedBill.getReferenceNo());
    }

    @Test
    void missingReferenceForQRIBAN() {
        bill = SampleData.getExample1();
        bill.setAccount("CH4431999123000889012"); // QR-IBAN
        bill.setReferenceNo(null);
        validate();
        assertSingleErrorMessage(Bill.FIELD_REFERENCE, "mandatory_for_qr_iban");
    }

    @Test
    void whitespaceReferenceForQRIBAN() {
        bill = SampleData.getExample1();
        bill.setReferenceNo("   ");
        validate();
        assertSingleErrorMessage(Bill.FIELD_REFERENCE, "mandatory_for_qr_iban");
    }

    @Test
    void invalidReferenceNo() {
        bill = SampleData.getExample1();
        bill.setReferenceNo("ABC");
        validate();
        assertSingleErrorMessage(Bill.FIELD_REFERENCE, "valid_qr_ref_no");
    }

    @Test
    void invalidNumericReferenceNo() {
        bill = SampleData.getExample1();
        bill.setReferenceNo("1234567890");
        validate();
        assertSingleErrorMessage(Bill.FIELD_REFERENCE, "valid_qr_ref_no");
    }

    @Test
    void invalidNonNumericReferenceNo() {
        bill = SampleData.getExample1();
        bill.setReferenceNo("123ABC7890");
        validate();
        assertSingleErrorMessage(Bill.FIELD_REFERENCE, "valid_qr_ref_no");
    }

    @Test
    void invalidCharsInCreditorReference() {
        bill = SampleData.getExample1();
        bill.setReferenceNo("RF38302!!3393");
        validate();
        assertSingleErrorMessage(Bill.FIELD_REFERENCE, "valid_iso11649_creditor_ref");
    }

    @Test
    void invalidCreditorReference() {
        bill = SampleData.getExample1();
        bill.setReferenceNo("RF00539007547034");
        validate();
        assertSingleErrorMessage(Bill.FIELD_REFERENCE, "valid_iso11649_creditor_ref");
    }
}