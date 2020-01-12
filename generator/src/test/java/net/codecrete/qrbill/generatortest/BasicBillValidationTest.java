//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.AlternativeScheme;
import net.codecrete.qrbill.generator.ValidationConstants;
import net.codecrete.qrbill.generator.ValidationMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Various unit tests for the bill data validation
 */
@DisplayName("A5 bill generation (PDF and SVG)")
class BasicBillValidationTest extends BillDataValidationBase {

    @Test
    void validCurrency() {
        bill = SampleData.getExample1();
        bill.setCurrency("CHF");
        validate();
        assertNoMessages();
        assertEquals("CHF", validatedBill.getCurrency());
    }

    @Test
    void missingCurrency() {
        bill = SampleData.getExample1();
        bill.setCurrency(null);
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_CURRENCY, "field_is_mandatory");
    }

    @Test
    void invalidCurrency() {
        bill = SampleData.getExample1();
        bill.setCurrency("USD");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_CURRENCY, "currency_is_chf_or_eur");
    }

    @Test
    void openAmount() {
        bill = SampleData.getExample1();
        bill.setAmount(null);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getAmount());
    }

    @Test
    void validAmount() {
        bill = SampleData.getExample1();
        bill.setAmountFromDouble(100.15);
        validate();
        assertNoMessages();
        assertEquals(BigDecimal.valueOf(10015, 2), validatedBill.getAmount());
    }

    @Test
    void amountTooLow() {
        bill = SampleData.getExample1();
        bill.setAmount(BigDecimal.valueOf(-1, 2));
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_AMOUNT, "amount_in_valid_range");
    }

    @Test
    void amountTooHigh2() {
        bill = SampleData.getExample1();
        bill.setAmount(BigDecimal.valueOf(1000000000.0));
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_AMOUNT, "amount_in_valid_range");
    }

    @Test
    void validCHAccount() {
        bill = SampleData.getExample1();
        bill.setAccount("CH4431999123000889012");
        validate();
        assertNoMessages();
        assertEquals("CH4431999123000889012", validatedBill.getAccount());
    }

    @Test
    void validLIAccount() {
        bill = SampleData.getExample3();
        bill.setAccount("LI56 0880 0000 0209 4080 8");
        validate();
        assertNoMessages();
        assertEquals("LI5608800000020940808", validatedBill.getAccount());
    }

    @Test
    void validAccountWithSpaces() {
        bill = SampleData.getExample1();
        bill.setAccount(" CH44 3199 9123 0008 89012");
        validate();
        assertNoMessages();
        assertEquals("CH4431999123000889012", validatedBill.getAccount());
    }

    @Test
    void missingAccount() {
        bill = SampleData.getExample1();
        bill.setAccount(null);
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_ACCOUNT, "field_is_mandatory");
    }

    @Test
    void foreignAccount() {
        bill = SampleData.getExample1();
        bill.setAccount("DE68 2012 0700 3100 7555 55");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_ACCOUNT, "account_is_ch_li_iban");
    }

    @Test
    void invalidIBAN1() {
        bill = SampleData.getExample1();
        bill.setAccount("CH0031999123000889012");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_ACCOUNT, "account_is_valid_iban");
    }

    @Test
    void invalidIBAN2() {
        bill = SampleData.getExample1();
        bill.setAccount("CH503199912300088333339012");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_ACCOUNT, "account_is_valid_iban");
    }

    @Test
    void validUnstructuredMessage() {
        bill = SampleData.getExample1();

        bill.setUnstructuredMessage("Bill no 39133");
        validate();
        assertNoMessages();
        assertEquals("Bill no 39133", validatedBill.getUnstructuredMessage());
    }

    @Test
    void emptyUnstructureMessage() {
        bill = SampleData.getExample1();
        bill.setUnstructuredMessage("   ");
        validate();
        assertNoMessages();
        assertNull(validatedBill.getUnstructuredMessage());
    }

    @Test
    void unstructuredMessageWithLeadingAndTrailingWhitespace() {
        bill = SampleData.getExample1();
        bill.setUnstructuredMessage("  Bill no 39133 ");
        validate();
        assertNoMessages();
        assertEquals("Bill no 39133", validatedBill.getUnstructuredMessage());
    }

    @Test
    void clippedUnstructuredMessage() {
        bill = SampleData.getExample4();
        bill.setUnstructuredMessage("123456789-123456789-123456789-123456789-123456789-123456789-123456789-123456789-123456789-123456789-123456789-123456789-123456789-123456789-A");
        assertEquals(141, bill.getUnstructuredMessage().length());
        validate();
        assertSingleWarningMessage(ValidationConstants.FIELD_UNSTRUCTURED_MESSAGE, ValidationConstants.KEY_FIELD_CLIPPED);
        assertEquals(140, validatedBill.getUnstructuredMessage().length());
    }

    @Test
    void tooLongBillInformation() {
        bill = SampleData.getExample1();
        bill.setUnstructuredMessage(null);
        bill.setBillInformation("//AA4567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789x");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_BILL_INFORMATION, "field_value_too_long");
    }

    @Test
    void invalidBillInformation1() {
        bill = SampleData.getExample1();
        bill.setBillInformation("ABCD");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_BILL_INFORMATION, "bill_info_invalid");
    }

    @Test
    void invalidBillInformation2() {
        bill = SampleData.getExample1();
        bill.setBillInformation("//A");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_BILL_INFORMATION, "bill_info_invalid");
    }

    @Test
    void tooLongUnstrMessageBillInfo() {
        bill = SampleData.getExample6();
        assertEquals(140, bill.getUnstructuredMessage().length() + bill.getBillInformation().length());
        bill.setUnstructuredMessage(bill.getUnstructuredMessage() + "A");
        validate();

        assertTrue(result.hasErrors());
        assertFalse(result.hasWarnings());
        assertTrue(result.hasMessages());
        assertEquals(2, result.getValidationMessages().size());

        ValidationMessage msg = result.getValidationMessages().get(0);
        assertEquals(ValidationMessage.Type.ERROR, msg.getType());
        assertEquals(ValidationConstants.FIELD_UNSTRUCTURED_MESSAGE, msg.getField());
        assertEquals(ValidationConstants.ADDITIONAL_INFO_TOO_LONG, msg.getMessageKey());

        msg = result.getValidationMessages().get(1);
        assertEquals(ValidationMessage.Type.ERROR, msg.getType());
        assertEquals(ValidationConstants.FIELD_BILL_INFORMATION, msg.getField());
        assertEquals(ValidationConstants.ADDITIONAL_INFO_TOO_LONG, msg.getMessageKey());
    }

    @Test
    void tooManyAltSchemes() {
        bill = SampleData.getExample1();
        bill.setAlternativeSchemes(new AlternativeScheme[] {
                new AlternativeScheme("Ultraviolet", "UV;UltraPay005;12345"),
                new AlternativeScheme("Xing Yong", "XY;XYService;54321"),
                new AlternativeScheme("Too Much", "TM/asdfa/asdfa/")
        });
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_ALTERNATIVE_SCHEMES, "alt_scheme_max_exceed");
    }

    @Test
    void tooLongAltSchemeInstructions() {
        bill = SampleData.getExample1();
        bill.setAlternativeSchemes(new AlternativeScheme[] {
                new AlternativeScheme("Ultraviolet",
                        "UV;UltraPay005;12345;xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"),
                new AlternativeScheme("Xing Yong", "XY;XYService;54321")
        });
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_ALTERNATIVE_SCHEMES, "field_value_too_long");
    }
}
