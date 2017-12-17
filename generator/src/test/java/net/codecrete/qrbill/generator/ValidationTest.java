//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import org.junit.Test;

import java.time.LocalDate;

import static net.codecrete.qrbill.generator.ValidationMessage.Type;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ValidationTest {

    private Bill bill;
    private ValidationResult result;
    private Bill validatedBill;

    @Test
    public void currencyTest() {
        bill = SampleData.getExample1();

        bill.setCurrency("CHF");
        validate();
        assertNoMessages();
        assertEquals("CHF", validatedBill.getCurrency());

        bill.setCurrency(null);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CURRENCY, "field_is_mandatory");

        bill.setCurrency("USD");
        validate();
        assertSingleErrorMessage(Bill.FIELD_CURRENCY, "currency_is_chf_or_eur");
    }

    @Test
    public void openAmountTest() {
        bill = SampleData.getExample1();

        bill.setAmount(null);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getAmount());
    }

    @Test
    public void amountTest() {
        bill = SampleData.getExample1();

        bill.setAmount(100.15);
        validate();
        assertNoMessages();
        assertEquals(Double.valueOf(100.15), validatedBill.getAmount());

        bill.setAmount(0.0);
        validate();
        assertSingleErrorMessage(Bill.FIELD_AMOUNT, "amount_in_valid_range");
    }

    @Test
    public void accountTest() {
        bill = SampleData.getExample1();

        bill.setAccount("CH4431999123000889012");
        validate();
        assertNoMessages();
        assertEquals("CH4431999123000889012", validatedBill.getAccount());

        bill.setAccount(" CH44 3199 9123 0008 89012");
        validate();
        assertNoMessages();
        assertEquals("CH4431999123000889012", validatedBill.getAccount());

        bill.setAccount(null);
        validate();
        assertSingleErrorMessage(Bill.FIELD_ACCOUNT, "field_is_mandatory");

        bill.setAccount("DE68 2012 0700 3100 7555 55");
        validate();
        assertSingleErrorMessage(Bill.FIELD_ACCOUNT, "account_is_ch_li_iban");

        bill.setAccount("CH0031999123000889012");
        validate();
        assertSingleErrorMessage(Bill.FIELD_ACCOUNT, "account_is_valid_iban");
    }

    @Test
    public void creditorTest() {
        bill = SampleData.getExample1();

        Address address = createValidPerson();
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertNotNull(validatedBill.getCreditor());
        assertEquals("Zuppinger AG", validatedBill.getCreditor().getName());
        assertEquals("Industriestrasse", validatedBill.getCreditor().getStreet());
        assertEquals("34a", validatedBill.getCreditor().getHouseNo());
        assertEquals("9548", validatedBill.getCreditor().getPostalCode());
        assertEquals("Matzingen", validatedBill.getCreditor().getTown());
        assertEquals("CH", validatedBill.getCreditor().getCountryCode());

        bill.setCreditor(null);
        validate();
        assertMandatoryPersonMessages(Bill.FIELDROOT_CREDITOR);

        Address emptyAddress = new Address();
        bill.setCreditor(emptyAddress);
        validate();
        assertMandatoryPersonMessages(Bill.FIELDROOT_CREDITOR);

        emptyAddress.setName("  ");
        bill.setCreditor(emptyAddress);
        validate();
        assertMandatoryPersonMessages(Bill.FIELDROOT_CREDITOR);

        address = createValidPerson();
        address.setName("  ");
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_NAME, "field_is_mandatory");

        address = createValidPerson();
        address.setStreet(null);
        bill.setCreditor(address);
        validate();
        assertNoMessages();

        address = createValidPerson();
        address.setStreet(null);
        bill.setCreditor(address);
        validate();
        assertNoMessages();

        address = createValidPerson();
        address.setHouseNo(null);
        bill.setCreditor(address);
        validate();
        assertNoMessages();

        address = createValidPerson();
        address.setPostalCode("");
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_POSTAL_CODE, "field_is_mandatory");

        address = createValidPerson();
        address.setTown(null);
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_TOWN, "field_is_mandatory");

        address = createValidPerson();
        address.setCountryCode("  ");
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_COUNTRY_CODE, "field_is_mandatory");

        address = createValidPerson();
        address.setCountryCode("Schweiz");
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_COUNTRY_CODE, "valid_country_code");

        address = createValidPerson();
        address.setCountryCode("R!");
        bill.setCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_COUNTRY_CODE, "valid_country_code");
    }

    @Test
    public void clippedFieldTest() {
        bill = SampleData.getExample1();

        Address address = createValidPerson();
        address.setName("Name567890123456789012345678901234567890123456789012345678901234567890");
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals("Name567890123456789012345678901234567890123456789012345678901234567890", validatedBill.getCreditor().getName());

        address = createValidPerson();
        address.setName("Name5678901234567890123456789012345678901234567890123456789012345678901");
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_NAME, "field_clipped");
        assertEquals("Name567890123456789012345678901234567890123456789012345678901234567890", validatedBill.getCreditor().getName());

        address = createValidPerson();
        address.setStreet("Street7890123456789012345678901234567890123456789012345678901234567890");
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals("Street7890123456789012345678901234567890123456789012345678901234567890", validatedBill.getCreditor().getStreet());

        address = createValidPerson();
        address.setStreet("Street78901234567890123456789012345678901234567890123456789012345678901");
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_STREET, "field_clipped");
        assertEquals("Street7890123456789012345678901234567890123456789012345678901234567890", validatedBill.getCreditor().getStreet());

        address = createValidPerson();
        address.setHouseNo("HouseNo890123456");
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals("HouseNo890123456", validatedBill.getCreditor().getHouseNo());

        address = createValidPerson();
        address.setHouseNo("HouseNo8901234567");
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_HOUSE_NO, "field_clipped");
        assertEquals("HouseNo890123456", validatedBill.getCreditor().getHouseNo());

        address = createValidPerson();
        address.setPostalCode("Postal7890123456");
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals("Postal7890123456", validatedBill.getCreditor().getPostalCode());

        address = createValidPerson();
        address.setPostalCode("Postal78901234567");
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_POSTAL_CODE, "field_clipped");
        assertEquals("Postal7890123456", validatedBill.getCreditor().getPostalCode());

        address = createValidPerson();
        address.setTown("City5678901234567890123456789012345");
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals("City5678901234567890123456789012345", validatedBill.getCreditor().getTown());

        address = createValidPerson();
        address.setTown("City56789012345678901234567890123456");
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_TOWN, "field_clipped");
        assertEquals("City5678901234567890123456789012345", validatedBill.getCreditor().getTown());
    }

    @Test
    public void characterSetTest() {
        bill = SampleData.getExample1();

        Address address = createValidPerson();
        address.setName("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", validatedBill.getCreditor().getName());

        address = createValidPerson();
        address.setName("!\"#%&*;<>÷=@_$£[]{}\\`´");
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals("!\"#%&*;<>÷=@_$£[]{}\\`´", validatedBill.getCreditor().getName());

        final String TEXT_WITHOUT_COMBINING_ACCENTS = "àáâäçèéêëìíîïñòóôöùúûüýßÀÁÂÄÇÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÑ";
        final String TEXT_WITH_COMBINING_ACCENTS = "àáâäçèéêëìíîïñòóôöùúûüýßÀÁÂÄÇÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÑ";
        assertEquals(TEXT_WITHOUT_COMBINING_ACCENTS.length(), 46);
        assertEquals(TEXT_WITH_COMBINING_ACCENTS.length(), 59);

        address = createValidPerson();
        address.setName(TEXT_WITHOUT_COMBINING_ACCENTS);
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals(TEXT_WITHOUT_COMBINING_ACCENTS, validatedBill.getCreditor().getName());

        address = createValidPerson();
        address.setName(TEXT_WITH_COMBINING_ACCENTS);
        bill.setCreditor(address);
        validate();
        assertNoMessages(); // silently normalized
        assertEquals(TEXT_WITHOUT_COMBINING_ACCENTS, validatedBill.getCreditor().getName());

        address = createValidPerson();
        address.setName("abc\r\ndef");
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_NAME, "replaced_unsupported_characters");
        assertEquals("abc def", validatedBill.getCreditor().getName());

        address = createValidPerson();
        address.setStreet("abc€def©ghi^");
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_STREET, "replaced_unsupported_characters");
        assertEquals("abc.def.ghi.", validatedBill.getCreditor().getStreet());

        address = createValidPerson();
        address.setPostalCode("\uD83D\uDC80"); // surrogate pair (1 code point but 2 UTF-16 words)
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_POSTAL_CODE, "replaced_unsupported_characters");
        assertEquals(".", validatedBill.getCreditor().getPostalCode());

        address = createValidPerson();
        address.setTown("\uD83C\uDDE8\uD83C\uDDED"); // two surrogate pairs
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_TOWN, "replaced_unsupported_characters");
        assertEquals("..", validatedBill.getCreditor().getTown());

        address = createValidPerson();
        address.setTown("-- \uD83D\uDC68\uD83C\uDFFB --"); // two surrogate pairs
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_TOWN, "replaced_unsupported_characters");
        assertEquals("-- .. --", validatedBill.getCreditor().getTown());


    }

    @Test
    public void finalCreditorTest() {
        bill = SampleData.getExample1();

        Address address = createValidPerson();
        bill.setFinalCreditor(address);
        validate();
        assertNoMessages();
        assertNotNull(validatedBill.getFinalCreditor());
        assertEquals("Zuppinger AG", validatedBill.getFinalCreditor().getName());
        assertEquals("Industriestrasse", validatedBill.getFinalCreditor().getStreet());
        assertEquals("34a", validatedBill.getFinalCreditor().getHouseNo());
        assertEquals("9548", validatedBill.getFinalCreditor().getPostalCode());
        assertEquals("Matzingen", validatedBill.getFinalCreditor().getTown());
        assertEquals("CH", validatedBill.getFinalCreditor().getCountryCode());

        bill.setFinalCreditor(null);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getFinalCreditor());

        Address emptyAddress = new Address();
        bill.setFinalCreditor(emptyAddress);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getFinalCreditor());

        emptyAddress.setName("  ");
        bill.setFinalCreditor(emptyAddress);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getFinalCreditor());

        address = createValidPerson();
        address.setName("  ");
        bill.setFinalCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_FINAL_CREDITOR_NAME, "field_is_mandatory");

        address = createValidPerson();
        address.setTown(null);
        bill.setFinalCreditor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_FINAL_CREDITOR_TOWN, "field_is_mandatory");
    }

    @Test
    public void referenceNoTest() {
        bill = SampleData.getExample1();

        bill.setReferenceNo("210000000003139471430009017");
        validate();
        assertNoMessages();
        assertEquals("210000000003139471430009017", validatedBill.getReferenceNo());

        bill.setReferenceNo("21 00000 00003 13947 14300 09017");
        validate();
        assertNoMessages();
        assertEquals("210000000003139471430009017", validatedBill.getReferenceNo());

        bill.setReferenceNo("RF18539007547034");
        validate();
        assertNoMessages();
        assertEquals("RF18539007547034", validatedBill.getReferenceNo());

        bill.setAccount("CH3709000000304442225"); // non QR-IBAN
        bill.setReferenceNo(null);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getReferenceNo());

        bill.setReferenceNo("   ");
        validate();
        assertNoMessages();
        assertNull(validatedBill.getReferenceNo());

        bill.setAccount("CH4431999123000889012"); // QR-IBAN
        bill.setReferenceNo(null);
        validate();
        assertSingleErrorMessage(Bill.FIELD_REFERENCE_NO, "mandatory_for_qr_iban");

        bill.setReferenceNo("   ");
        validate();
        assertSingleErrorMessage(Bill.FIELD_REFERENCE_NO, "mandatory_for_qr_iban");

        bill.setReferenceNo("ABC");
        validate();
        assertSingleErrorMessage(Bill.FIELD_REFERENCE_NO, "valid_qr_ref_no");

        bill.setReferenceNo("1234567890");
        validate();
        assertSingleErrorMessage(Bill.FIELD_REFERENCE_NO, "valid_qr_ref_no");

        bill.setReferenceNo("123ABC7890");
        validate();
        assertSingleErrorMessage(Bill.FIELD_REFERENCE_NO, "valid_qr_ref_no");

        bill.setReferenceNo("RF38302!!3393");
        validate();
        assertSingleErrorMessage(Bill.FIELD_REFERENCE_NO, "valid_iso11649_creditor_ref");

        bill.setReferenceNo("RF00539007547034");
        validate();
        assertSingleErrorMessage(Bill.FIELD_REFERENCE_NO, "valid_iso11649_creditor_ref");
    }

    @Test
    public void additionalInfoTest() {
        bill = SampleData.getExample1();

        bill.setAdditionalInfo("Bill no 39133");
        validate();
        assertNoMessages();
        assertEquals("Bill no 39133", validatedBill.getAdditionalInfo());

        bill.setAdditionalInfo("   ");
        validate();
        assertNoMessages();
        assertNull(validatedBill.getAdditionalInfo());

        bill.setAdditionalInfo("  Bill no 39133 ");
        validate();
        assertNoMessages();
        assertEquals("Bill no 39133", validatedBill.getAdditionalInfo());
    }

    @Test
    public void openDebtorTest() {
        bill = SampleData.getExample1();

        bill.setDebtor(null);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getDebtor());
    }

    @Test
    public void debtorTest() {
        bill = SampleData.getExample1();

        Address address = createValidPerson();
        bill.setDebtor(address);
        validate();
        assertNoMessages();
        assertNotNull(validatedBill.getDebtor());
        assertEquals("Zuppinger AG", validatedBill.getDebtor().getName());
        assertEquals("Industriestrasse", validatedBill.getDebtor().getStreet());
        assertEquals("34a", validatedBill.getDebtor().getHouseNo());
        assertEquals("9548", validatedBill.getDebtor().getPostalCode());
        assertEquals("Matzingen", validatedBill.getDebtor().getTown());
        assertEquals("CH", validatedBill.getDebtor().getCountryCode());

        Address emptyAddress = new Address();
        bill.setDebtor(emptyAddress);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getDebtor());

        emptyAddress.setName("  ");
        bill.setDebtor(emptyAddress);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getDebtor());

        address = createValidPerson();
        address.setName("  ");
        bill.setDebtor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_DEBTOR_NAME, "field_is_mandatory");

        address = createValidPerson();
        address.setTown(null);
        bill.setDebtor(address);
        validate();
        assertSingleErrorMessage(Bill.FIELD_DEBTOR_TOWN, "field_is_mandatory");
    }

    @Test
    public void dueDateTest() {
        bill = SampleData.getExample1();

        bill.setDueDate(LocalDate.of(2018, 12, 10));
        validate();
        assertNoMessages();
        assertEquals(bill.getDueDate(), validatedBill.getDueDate());

        bill.setDueDate(null);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getDueDate());
    }

    private void validate() {
        result = new ValidationResult();
        Validator validator = new Validator(bill, result);
        validatedBill = validator.validate();
    }

    private void assertNoMessages() {
        assertEquals(false, result.hasErrors());
        assertEquals(false, result.hasWarnings());
        assertEquals(false, result.hasMessages());
        assertEquals(0, result.getValidationMessages().size());
    }

    private void assertSingleErrorMessage(String field, String messageKey) {
        assertEquals(true, result.hasErrors());
        assertEquals(false, result.hasWarnings());
        assertEquals(true, result.hasMessages());
        assertEquals(1, result.getValidationMessages().size());

        ValidationMessage msg = result.getValidationMessages().get(0);
        assertEquals(Type.Error, msg.getType());
        assertEquals(field, msg.getField());
        assertEquals(messageKey, msg.getMessageKey());
    }

    private void assertSingleWarningMessage(String field, String messageKey) {
        assertEquals(false, result.hasErrors());
        assertEquals(true, result.hasWarnings());
        assertEquals(true, result.hasMessages());
        assertEquals(1, result.getValidationMessages().size());

        ValidationMessage msg = result.getValidationMessages().get(0);
        assertEquals(Type.Warning, msg.getType());
        assertEquals(field, msg.getField());
        assertEquals(messageKey, msg.getMessageKey());
    }

    private void assertMandatoryPersonMessages(String fieldRoot) {
        assertEquals(true, result.hasErrors());
        assertEquals(false, result.hasWarnings());
        assertEquals(true, result.hasMessages());
        assertEquals(4, result.getValidationMessages().size());
        for (ValidationMessage msg : result.getValidationMessages()) {
            assertEquals(Type.Error, msg.getType());
            assertEquals("field_is_mandatory", msg.getMessageKey());
            assertTrue(msg.getField().startsWith(fieldRoot));
        }
    }

    private Address createValidPerson() {
        Address address = new Address();
        address.setName("Zuppinger AG");
        address.setStreet("Industriestrasse");
        address.setHouseNo("34a");
        address.setPostalCode("9548");
        address.setTown("Matzingen");
        address.setCountryCode("CH");
        return address;
    }
}
