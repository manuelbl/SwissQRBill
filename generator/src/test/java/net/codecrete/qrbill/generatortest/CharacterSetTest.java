//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.ValidationConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Various unit tests for character set validations and replacements
 */
@DisplayName("Character set validation and replacement")
class CharacterSetTest extends BillDataValidationBase {
    private static final String TEXT_WITHOUT_COMBINING_ACCENTS = "àáâäçèéêëìíîïñòóôöùúûüýßÀÁÂÄÇÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÑ";
    private static final String TEXT_WITH_COMBINING_ACCENTS = "àáâäçèéêëìíîïñòóôöùúûüýßÀÁÂÄÇÈÉÊËÌÍÎÏÒÓÔÖÙÚÛÜÑ";

    @Test
    void verifyTestData() {
        assertEquals(46, TEXT_WITHOUT_COMBINING_ACCENTS.length());
        assertEquals(59, TEXT_WITH_COMBINING_ACCENTS.length());
    }

    @Test
    void validLetters() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setName("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", validatedBill.getCreditor().getName());
    }

    @Test
    void validSpecialChars() {
        bill = SampleData.getExample1();

        Address address = createValidPerson();
        address.setName("!\"#%&*;<>÷=@_$£[]{}\\`´");
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals("!\"#%&*;<>÷=@_$£[]{}\\`´", validatedBill.getCreditor().getName());
    }

    @Test
    void validAccents() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setName(TEXT_WITHOUT_COMBINING_ACCENTS);
        bill.setCreditor(address);
        validate();
        assertNoMessages();
        assertEquals(TEXT_WITHOUT_COMBINING_ACCENTS, validatedBill.getCreditor().getName());
    }

    @Test
    void validCombiningAccents() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setName(TEXT_WITH_COMBINING_ACCENTS);
        bill.setCreditor(address);
        validate();
        assertNoMessages(); // silently normalized
        assertEquals(TEXT_WITHOUT_COMBINING_ACCENTS, validatedBill.getCreditor().getName());
    }

    @Test
    void newlineReplacement() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setName("abc\r\ndef");
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(ValidationConstants.FIELD_CREDITOR_NAME, ValidationConstants.KEY_REPLACED_UNSUPPORTED_CHARACTERS);
        assertEquals("abc def", validatedBill.getCreditor().getName());
    }

    @Test
    void invalidCharacterReplacement() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setStreet("abc€def©ghi^");
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(ValidationConstants.FIELD_CREDITOR_STREET, ValidationConstants.KEY_REPLACED_UNSUPPORTED_CHARACTERS);
        assertEquals("abc.def.ghi.", validatedBill.getCreditor().getStreet());
    }

    @Test
    void unstructuredMessageReplacement() {
        bill = SampleData.getExample1();
        bill.setUnstructuredMessage("Thanks 🙏 Lisa");
        validate();
        assertSingleWarningMessage(ValidationConstants.FIELD_UNSTRUCTURED_MESSAGE, ValidationConstants.KEY_REPLACED_UNSUPPORTED_CHARACTERS);
        assertEquals("Thanks . Lisa", validatedBill.getUnstructuredMessage());
    }

    @Test
    void billInfoReplacement() {
        bill = SampleData.getExample1();
        bill.setBillInformation("//AZ/400€/123");
        validate();
        assertSingleWarningMessage(ValidationConstants.FIELD_BILL_INFORMATION, ValidationConstants.KEY_REPLACED_UNSUPPORTED_CHARACTERS);
        assertEquals("//AZ/400./123", validatedBill.getBillInformation());
    }

    @Test
    void replacedSurrogatePair() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setPostalCode("\uD83D\uDC80"); // surrogate pair (1 code point but 2 UTF-16 words)
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(ValidationConstants.FIELD_CREDITOR_POSTAL_CODE, ValidationConstants.KEY_REPLACED_UNSUPPORTED_CHARACTERS);
        assertEquals(".", validatedBill.getCreditor().getPostalCode());
    }

    @Test
    void twoReplacedConsecutiveSurrogatePairs() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setTown("\uD83C\uDDE8\uD83C\uDDED"); // two surrogate pairs
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(ValidationConstants.FIELD_CREDITOR_TOWN, ValidationConstants.KEY_REPLACED_UNSUPPORTED_CHARACTERS);
        assertEquals("..", validatedBill.getCreditor().getTown());
    }

    @Test
    void twoReplacedSurrogatePairsWithWhitespace() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setTown("-- \uD83D\uDC68\uD83C\uDFFB --"); // two surrogate pairs
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(ValidationConstants.FIELD_CREDITOR_TOWN, ValidationConstants.KEY_REPLACED_UNSUPPORTED_CHARACTERS);
        assertEquals("-- .. --", validatedBill.getCreditor().getTown());
    }

    @ParameterizedTest
    @ValueSource(strings = { "^", "\u007f", "\u0080", "\u00a0", "\u00A0", "¡", "¤", "©", "±", "µ", "¼", "Å", "Æ", "Ð",
            "×", "Ø", "Ý", "Þ", "å", "æ", "ø", "€", "¿", "Ý", "Ą", "Ď", "ð", "õ", "ã", "Ã" })
    void invalidChars(String invalidChar) {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setStreet("ABC" + invalidChar + "QRS");
        bill.setCreditor(address);
        validate();
        assertSingleWarningMessage(ValidationConstants.FIELD_CREDITOR_STREET, ValidationConstants.KEY_REPLACED_UNSUPPORTED_CHARACTERS);
        assertEquals("ABC.QRS", validatedBill.getCreditor().getStreet());
    }
}
