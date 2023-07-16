//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import net.codecrete.qrbill.testhelper.SampleData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

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
        assertSingleErrorMessage(ValidationConstants.FIELD_CURRENCY, ValidationConstants.KEY_FIELD_VALUE_MISSING);
    }

    @Test
    void invalidCurrency() {
        bill = SampleData.getExample1();
        bill.setCurrency("USD");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_CURRENCY, ValidationConstants.KEY_CURRENCY_NOT_CHF_OR_EUR);
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
        assertSingleErrorMessage(ValidationConstants.FIELD_AMOUNT, ValidationConstants.KEY_AMOUNT_OUTSIDE_VALID_RANGE);
    }

    @Test
    void amountTooHigh2() {
        bill = SampleData.getExample1();
        bill.setAmount(BigDecimal.valueOf(1000000000.0));
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_AMOUNT, ValidationConstants.KEY_AMOUNT_OUTSIDE_VALID_RANGE);
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

    @ParameterizedTest
    @CsvSource({
            ",field_value_missing",
            "DE68 2012 0700 3100 7555 55,account_iban_not_from_ch_or_li",
            "CH0031999123000889012,account_iban_invalid",
            "CH503199912300088333339012,account_iban_invalid"
    })
    void invalidAccounts(String account, String messageKey) {
        bill = SampleData.getExample1();
        bill.setAccount(account);
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_ACCOUNT, messageKey);
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
    void emptyUnstructuredMessage() {
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
        assertSingleWarningMessage(ValidationConstants.FIELD_UNSTRUCTURED_MESSAGE, ValidationConstants.KEY_FIELD_VALUE_CLIPPED);
        assertEquals(140, validatedBill.getUnstructuredMessage().length());
    }

    @Test
    void tooLongBillInformation() {
        bill = SampleData.getExample1();
        bill.setUnstructuredMessage(null);
        bill.setBillInformation("//AA4567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789x");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_BILL_INFORMATION, ValidationConstants.KEY_FIELD_VALUE_TOO_LONG);
    }

    @Test
    void invalidBillInformation1() {
        bill = SampleData.getExample1();
        bill.setBillInformation("ABCD");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_BILL_INFORMATION, ValidationConstants.KEY_BILL_INFO_INVALID);
    }

    @Test
    void invalidBillInformation2() {
        bill = SampleData.getExample1();
        bill.setBillInformation("//A");
        validate();
        assertSingleErrorMessage(ValidationConstants.FIELD_BILL_INFORMATION, ValidationConstants.KEY_BILL_INFO_INVALID);
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
        assertEquals(ValidationConstants.KEY_ADDITIONAL_INFO_TOO_LONG, msg.getMessageKey());

        msg = result.getValidationMessages().get(1);
        assertEquals(ValidationMessage.Type.ERROR, msg.getType());
        assertEquals(ValidationConstants.FIELD_BILL_INFORMATION, msg.getField());
        assertEquals(ValidationConstants.KEY_ADDITIONAL_INFO_TOO_LONG, msg.getMessageKey());
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
        assertSingleErrorMessage(ValidationConstants.FIELD_ALTERNATIVE_SCHEMES, ValidationConstants.KEY_ALT_SCHEME_MAX_EXCEEDED);
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
        assertSingleErrorMessage(ValidationConstants.FIELD_ALTERNATIVE_SCHEMES, ValidationConstants.KEY_FIELD_VALUE_TOO_LONG);
    }
}
