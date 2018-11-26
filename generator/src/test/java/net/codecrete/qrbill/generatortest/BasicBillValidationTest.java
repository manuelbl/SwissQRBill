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

import java.math.BigDecimal;

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
        assertSingleErrorMessage(Bill.FIELD_CURRENCY, "field_is_mandatory");
    }

    @Test
    void invalidCurrency() {
        bill = SampleData.getExample1();
        bill.setCurrency("USD");
        validate();
        assertSingleErrorMessage(Bill.FIELD_CURRENCY, "currency_is_chf_or_eur");
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
    void amountOutOfRange() {
        bill = SampleData.getExample1();
        bill.setAmount(BigDecimal.valueOf(0));
        validate();
        assertSingleErrorMessage(Bill.FIELD_AMOUNT, "amount_in_valid_range");
    }

    @Test
    void validAccount() {
        bill = SampleData.getExample1();
        bill.setAccount("CH4431999123000889012");
        validate();
        assertNoMessages();
        assertEquals("CH4431999123000889012", validatedBill.getAccount());
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
        assertSingleErrorMessage(Bill.FIELD_ACCOUNT, "field_is_mandatory");
    }

    @Test
    void foreignAccount() {
        bill = SampleData.getExample1();
        bill.setAccount("DE68 2012 0700 3100 7555 55");
        validate();
        assertSingleErrorMessage(Bill.FIELD_ACCOUNT, "account_is_ch_li_iban");
    }

    @Test
    void invalidIBAN() {
        bill = SampleData.getExample1();
        bill.setAccount("CH0031999123000889012");
        validate();
        assertSingleErrorMessage(Bill.FIELD_ACCOUNT, "account_is_valid_iban");
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
}
