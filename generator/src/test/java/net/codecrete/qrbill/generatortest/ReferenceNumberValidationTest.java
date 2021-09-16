//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.ValidationConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the validation of the reference number of a bill
 */
@DisplayName("Reference number validation (in bill data)")
class ReferenceNumberValidationTest extends BillDataValidationBase {

    @Test
    void validQrRef_ok() {
        bill = SampleData.getExample1();
        bill.setReference("210000000003139471430009017");
        validate();
        assertNoMessages();
        assertEquals("210000000003139471430009017", validatedBill.getReference());
    }

    @Test
    void validQrRefWithSpaces_ok() {
        bill = SampleData.getExample1();
        bill.setReference("21 00000 00003 13947 14300 09017");
        validate();
        assertNoMessages();
        assertEquals("210000000003139471430009017", validatedBill.getReference());
    }

    @Test
    void validCreditorRef_ok() {
        bill = SampleData.getExample3();
        bill.setReference("RF18539007547034");
        validate();
        assertNoMessages();
        assertEquals("RF18539007547034", validatedBill.getReference());
    }

    @Test
    void nonQrIbanAndNoRef_ok() {
        bill = SampleData.getExample1();
        bill.setAccount("CH3709000000304442225"); // non QR-IBAN
        bill.setReference(null);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getReference());
    }

    @Test
    void nonQrIbanAndWhitespaceRef_ok() {
        bill = SampleData.getExample1();
        bill.setAccount("CH3709000000304442225"); // non QR-IBAN
        bill.setReference("   ");
        validate();
        assertNoMessages();
        assertNull(validatedBill.getReference());
    }

    @Test
    void invalidRef_refIsValidErr() {
        bill = SampleData.getExample1();
        bill.setReference("ABC");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_REFERENCE, "ref_is_invalid");
    }

    @Test
    void invalidNumericRef_refIsValidErr() {
        bill = SampleData.getExample1();
        bill.setReference("1234567890");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_REFERENCE, "ref_is_invalid");
    }

    @Test
    void invalidAlphaNumericRef_refIsValidErr() {
        bill = SampleData.getExample1();
        bill.setReference("123ABC7890");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_REFERENCE, "ref_is_invalid");
    }

    @Test
    void invalidCharInRef_refIsValidErr() {
        bill = SampleData.getExample3();
        bill.setReference("RF38302!!3393");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_REFERENCE, "ref_is_invalid");
    }

    @Test
    void invalidCreditorRef_refIsValidErr() {
        bill = SampleData.getExample3();
        bill.setReference("RF00539007547034");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_REFERENCE, "ref_is_invalid");
    }

    @Test
    void missingRefForQrIban_qrRefIsMandatoryErr() {
        bill = SampleData.getExample1();
        bill.setAccount("CH4431999123000889012"); // QR-IBAN
        bill.setReference(null);
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_REFERENCE, "qr_ref_is_missing");
    }

    @Test
    void whitespaceRefForQrIban_qrRefIsMandatoryErr() {
        bill = SampleData.getExample1();
        bill.setReference("   ");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_REFERENCE, "qr_ref_is_missing");
    }

    @Test
    void creditRefForQrIban_useQrRefForQrIbanErr() {
        bill = SampleData.getExample1();
        bill.setAccount("CH4431999123000889012"); // QR-IBAN
        bill.setReference("RF18539007547034");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_REFERENCE, "cred_ref_used_for_qr_iban");
    }

    @Test
    void qrRefForNonQrIban_useQrIbanForQrRefErr() {
        bill = SampleData.getExample1();
        bill.setAccount("CH3709000000304442225"); // non QR-IBAN
        bill.setReference("210000000003139471430009017");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_REFERENCE, "qr_ref_used_for_non_qr_iban");
    }

    @Test
    void invalidRefType_refTypeIsValidErr() {
        bill = SampleData.getExample3();
        bill.setReferenceType("ABC");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_REFERENCE_TYPE, "invalid_ref_type");
    }
}
