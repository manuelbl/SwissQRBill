//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import org.junit.Test;

import java.time.LocalDate;

import static net.codecrete.qrbill.generator.ValidationMessage.*;
import static org.junit.Assert.*;

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

        bill.setAmountOpen(true);
        validate();
        assertNoMessages();
        assertEquals(true, validatedBill.isAmountOpen());
        assertEquals(null, validatedBill.getAmount());
    }

    @Test
    public void amountTest() {
        bill = SampleData.getExample1();

        bill.setAmountOpen(false);
        bill.setAmount(100.15);
        validate();
        assertNoMessages();
        assertEquals(Double.valueOf(100.15), validatedBill.getAmount());

        bill.setAmountOpen(false);
        bill.setAmount(null);
        validate();
        assertSingleErrorMessage(Bill.FIELD_AMOUNT, "amount_open_or_mandatory");

        bill.setAmountOpen(false);
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

        Person person = createValidPerson();
        bill.setCreditor(person);
        validate();
        assertNoMessages();
        assertNotNull(validatedBill.getCreditor());
        assertEquals("Zuppinger AG", validatedBill.getCreditor().getName());
        assertEquals("Industriestrasse", validatedBill.getCreditor().getStreet());
        assertEquals("34a", validatedBill.getCreditor().getHouseNumber());
        assertEquals("9548", validatedBill.getCreditor().getPostalCode());
        assertEquals("Matzingen", validatedBill.getCreditor().getCity());
        assertEquals("CH", validatedBill.getCreditor().getCountryCode());

        bill.setCreditor(null);
        validate();
        assertMandatoryPersonMessages(Bill.FIELDROOT_CREDITOR);

        Person emptyPerson = new Person();
        bill.setCreditor(emptyPerson);
        validate();
        assertMandatoryPersonMessages(Bill.FIELDROOT_CREDITOR);

        emptyPerson.setName("  ");
        bill.setCreditor(emptyPerson);
        validate();
        assertMandatoryPersonMessages(Bill.FIELDROOT_CREDITOR);

        person = createValidPerson();
        person.setName("  ");
        bill.setCreditor(person);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_NAME, "field_is_mandatory");

        person = createValidPerson();
        person.setStreet(null);
        bill.setCreditor(person);
        validate();
        assertNoMessages();

        person = createValidPerson();
        person.setStreet(null);
        bill.setCreditor(person);
        validate();
        assertNoMessages();

        person = createValidPerson();
        person.setHouseNumber(null);
        bill.setCreditor(person);
        validate();
        assertNoMessages();

        person = createValidPerson();
        person.setPostalCode("");
        bill.setCreditor(person);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_POSTAL_CODE, "field_is_mandatory");

        person = createValidPerson();
        person.setCity(null);
        bill.setCreditor(person);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_CITY, "field_is_mandatory");

        person = createValidPerson();
        person.setCountryCode("  ");
        bill.setCreditor(person);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_COUNTRY_CODE, "field_is_mandatory");

        person = createValidPerson();
        person.setCountryCode("Schweiz");
        bill.setCreditor(person);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_COUNTRY_CODE, "valid_country_code");

        person = createValidPerson();
        person.setCountryCode("R!");
        bill.setCreditor(person);
        validate();
        assertSingleErrorMessage(Bill.FIELD_CREDITOR_COUNTRY_CODE, "valid_country_code");
    }

    @Test
    public void clippedFieldTest() {
        bill = SampleData.getExample1();

        Person person = createValidPerson();
        person.setName("Name567890123456789012345678901234567890123456789012345678901234567890");
        bill.setCreditor(person);
        validate();
        assertNoMessages();
        assertEquals("Name567890123456789012345678901234567890123456789012345678901234567890", validatedBill.getCreditor().getName());

        person = createValidPerson();
        person.setName("Name5678901234567890123456789012345678901234567890123456789012345678901");
        bill.setCreditor(person);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_NAME, "field_clipped");
        assertEquals("Name567890123456789012345678901234567890123456789012345678901234567890", validatedBill.getCreditor().getName());

        person = createValidPerson();
        person.setStreet("Street7890123456789012345678901234567890123456789012345678901234567890");
        bill.setCreditor(person);
        validate();
        assertNoMessages();
        assertEquals("Street7890123456789012345678901234567890123456789012345678901234567890", validatedBill.getCreditor().getStreet());

        person = createValidPerson();
        person.setStreet("Street78901234567890123456789012345678901234567890123456789012345678901");
        bill.setCreditor(person);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_STREET, "field_clipped");
        assertEquals("Street7890123456789012345678901234567890123456789012345678901234567890", validatedBill.getCreditor().getStreet());

        person = createValidPerson();
        person.setHouseNumber("HouseNo890123456");
        bill.setCreditor(person);
        validate();
        assertNoMessages();
        assertEquals("HouseNo890123456", validatedBill.getCreditor().getHouseNumber());

        person = createValidPerson();
        person.setHouseNumber("HouseNo8901234567");
        bill.setCreditor(person);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_HOUSE_NO, "field_clipped");
        assertEquals("HouseNo890123456", validatedBill.getCreditor().getHouseNumber());

        person = createValidPerson();
        person.setPostalCode("Postal7890123456");
        bill.setCreditor(person);
        validate();
        assertNoMessages();
        assertEquals("Postal7890123456", validatedBill.getCreditor().getPostalCode());

        person = createValidPerson();
        person.setPostalCode("Postal78901234567");
        bill.setCreditor(person);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_POSTAL_CODE, "field_clipped");
        assertEquals("Postal7890123456", validatedBill.getCreditor().getPostalCode());

        person = createValidPerson();
        person.setCity("City5678901234567890123456789012345");
        bill.setCreditor(person);
        validate();
        assertNoMessages();
        assertEquals("City5678901234567890123456789012345", validatedBill.getCreditor().getCity());

        person = createValidPerson();
        person.setCity("City56789012345678901234567890123456");
        bill.setCreditor(person);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_CITY, "field_clipped");
        assertEquals("City5678901234567890123456789012345", validatedBill.getCreditor().getCity());
    }

    @Test
    public void characterSetTest() {
        bill = SampleData.getExample1();

        Person person = createValidPerson();
        person.setName("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        bill.setCreditor(person);
        validate();
        assertNoMessages();
        assertEquals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", validatedBill.getCreditor().getName());

        person = createValidPerson();
        person.setName("!\"#%&*;<>÷=@_$£[]{}\\`´");
        bill.setCreditor(person);
        validate();
        assertNoMessages();
        assertEquals("!\"#%&*;<>÷=@_$£[]{}\\`´", validatedBill.getCreditor().getName());

        final String TEXT_WITHOUT_COMBINING_ACCENTS = "àáâäçèéêëìíîïñòóôöùúûüýßÀÁÂÄÇÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÑ";
        final String TEXT_WITH_COMBINING_ACCENTS = "àáâäçèéêëìíîïñòóôöùúûüýßÀÁÂÄÇÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÑ";
        assertEquals(TEXT_WITHOUT_COMBINING_ACCENTS.length(), 46);
        assertEquals(TEXT_WITH_COMBINING_ACCENTS.length(), 59);

        person = createValidPerson();
        person.setName(TEXT_WITHOUT_COMBINING_ACCENTS);
        bill.setCreditor(person);
        validate();
        assertNoMessages();
        assertEquals(TEXT_WITHOUT_COMBINING_ACCENTS, validatedBill.getCreditor().getName());

        person = createValidPerson();
        person.setName(TEXT_WITH_COMBINING_ACCENTS);
        bill.setCreditor(person);
        validate();
        assertNoMessages(); // silently normalized
        assertEquals(TEXT_WITHOUT_COMBINING_ACCENTS, validatedBill.getCreditor().getName());

        person = createValidPerson();
        person.setName("abc\r\ndef");
        bill.setCreditor(person);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_NAME, "replaced_unsupported_characters");
        assertEquals("abc def", validatedBill.getCreditor().getName());

        person = createValidPerson();
        person.setStreet("abc€def©ghi^");
        bill.setCreditor(person);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_STREET, "replaced_unsupported_characters");
        assertEquals("abc.def.ghi.", validatedBill.getCreditor().getStreet());

        person = createValidPerson();
        person.setPostalCode("\uD83D\uDC80"); // surrogate pair (1 code point but 2 UTF-16 words)
        bill.setCreditor(person);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_POSTAL_CODE, "replaced_unsupported_characters");
        assertEquals(".", validatedBill.getCreditor().getPostalCode());

        person = createValidPerson();
        person.setCity("\uD83C\uDDE8\uD83C\uDDED"); // two surrogate pairs
        bill.setCreditor(person);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_CITY, "replaced_unsupported_characters");
        assertEquals("..", validatedBill.getCreditor().getCity());

        person = createValidPerson();
        person.setCity("-- \uD83D\uDC68\uD83C\uDFFB --"); // two surrogate pairs
        bill.setCreditor(person);
        validate();
        assertSingleWarningMessage(Bill.FIELD_CREDITOR_CITY, "replaced_unsupported_characters");
        assertEquals("-- .. --", validatedBill.getCreditor().getCity());


    }

    @Test
    public void finalCreditorTest() {
        bill = SampleData.getExample1();

        Person person = createValidPerson();
        bill.setFinalCreditor(person);
        validate();
        assertNoMessages();
        assertNotNull(validatedBill.getFinalCreditor());
        assertEquals("Zuppinger AG", validatedBill.getFinalCreditor().getName());
        assertEquals("Industriestrasse", validatedBill.getFinalCreditor().getStreet());
        assertEquals("34a", validatedBill.getFinalCreditor().getHouseNumber());
        assertEquals("9548", validatedBill.getFinalCreditor().getPostalCode());
        assertEquals("Matzingen", validatedBill.getFinalCreditor().getCity());
        assertEquals("CH", validatedBill.getFinalCreditor().getCountryCode());

        bill.setFinalCreditor(null);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getFinalCreditor());

        Person emptyPerson = new Person();
        bill.setFinalCreditor(emptyPerson);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getFinalCreditor());

        emptyPerson.setName("  ");
        bill.setFinalCreditor(emptyPerson);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getFinalCreditor());

        person = createValidPerson();
        person.setName("  ");
        bill.setFinalCreditor(person);
        validate();
        assertSingleErrorMessage(Bill.FIELD_FINAL_CREDITOR_NAME, "field_is_mandatory");

        person = createValidPerson();
        person.setCity(null);
        bill.setFinalCreditor(person);
        validate();
        assertSingleErrorMessage(Bill.FIELD_FINAL_CREDITOR_CITY, "field_is_mandatory");
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

        bill.setAdditionalInformation("Bill no 39133");
        validate();
        assertNoMessages();
        assertEquals("Bill no 39133", validatedBill.getAdditionalInformation());

        bill.setAdditionalInformation("   ");
        validate();
        assertNoMessages();
        assertNull(validatedBill.getAdditionalInformation());

        bill.setAdditionalInformation("  Bill no 39133 ");
        validate();
        assertNoMessages();
        assertEquals("Bill no 39133", validatedBill.getAdditionalInformation());
    }

    @Test
    public void openDebtorTest() {
        bill = SampleData.getExample1();

        bill.setDebtorOpen(true);
        validate();
        assertNoMessages();
        assertEquals(true, validatedBill.isDebtorOpen());
        assertNull(validatedBill.getDebtor());
    }

    @Test
    public void debtorTest() {
        bill = SampleData.getExample1();

        Person person = createValidPerson();
        bill.setDebtorOpen(false);
        bill.setDebtor(person);
        validate();
        assertNoMessages();
        assertFalse(validatedBill.isDebtorOpen());
        assertNotNull(validatedBill.getDebtor());
        assertEquals("Zuppinger AG", validatedBill.getDebtor().getName());
        assertEquals("Industriestrasse", validatedBill.getDebtor().getStreet());
        assertEquals("34a", validatedBill.getDebtor().getHouseNumber());
        assertEquals("9548", validatedBill.getDebtor().getPostalCode());
        assertEquals("Matzingen", validatedBill.getDebtor().getCity());
        assertEquals("CH", validatedBill.getDebtor().getCountryCode());

        Person emptyPerson = new Person();
        bill.setDebtor(emptyPerson);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getDebtor());

        emptyPerson.setName("  ");
        bill.setDebtor(emptyPerson);
        validate();
        assertNoMessages();
        assertNull(validatedBill.getDebtor());

        person = createValidPerson();
        person.setName("  ");
        bill.setDebtor(person);
        validate();
        assertSingleErrorMessage(Bill.FIELD_DEBTOR_NAME, "field_is_mandatory");

        person = createValidPerson();
        person.setCity(null);
        bill.setDebtor(person);
        validate();
        assertSingleErrorMessage(Bill.FIELD_DEBTOR_CITY, "field_is_mandatory");
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

    private Person createValidPerson() {
        Person person = new Person();
        person.setName("Zuppinger AG");
        person.setStreet("Industriestrasse");
        person.setHouseNumber("34a");
        person.setPostalCode("9548");
        person.setCity("Matzingen");
        person.setCountryCode("CH");
        return person;
    }
}
