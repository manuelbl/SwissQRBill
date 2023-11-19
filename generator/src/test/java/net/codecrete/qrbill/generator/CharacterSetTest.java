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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Various unit tests for invalid character replacement and warning in result
 */
@DisplayName("Character set replacement and warning")
class CharacterSetTest extends BillDataValidationBase {

    @Test
    void invalidCharacterReplacement() {
        bill = SampleData.getExample1();
        Address address = createValidPerson();
        address.setStreet("abc\uD83C\uDDE8\uD83C\uDDEDdef");
        bill.setCreditor(address);
        validate();
        assertEquals("abc.def", validatedBill.getCreditor().getStreet());
        assertSingleWarningMessage(ValidationConstants.FIELD_CREDITOR_STREET, ValidationConstants.KEY_REPLACED_UNSUPPORTED_CHARACTERS);
    }

    @Test
    void unstructuredMessageReplacement() {
        bill = SampleData.getExample1();
        bill.setUnstructuredMessage("Thanks 🙏 Lisa");
        validate();
        assertEquals("Thanks . Lisa", validatedBill.getUnstructuredMessage());
        assertSingleWarningMessage(ValidationConstants.FIELD_UNSTRUCTURED_MESSAGE, ValidationConstants.KEY_REPLACED_UNSUPPORTED_CHARACTERS);
    }

    @Test
    void billInfoReplacement() {
        bill = SampleData.getExample1();
        bill.setBillInformation("//abc \uD83D\uDE00 def");
        validate();
        assertEquals("//abc . def", validatedBill.getBillInformation());
        assertSingleWarningMessage(ValidationConstants.FIELD_BILL_INFORMATION, ValidationConstants.KEY_REPLACED_UNSUPPORTED_CHARACTERS);
    }
}
