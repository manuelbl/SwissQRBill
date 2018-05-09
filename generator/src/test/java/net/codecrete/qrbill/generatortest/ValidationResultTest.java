//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.ValidationMessage;
import net.codecrete.qrbill.generator.ValidationResult;
import net.codecrete.qrbill.generator.ValidationMessage.Type;

class ValidationResultTest {

    @Test
    void defaultConstructor() {
        ValidationResult result = new ValidationResult();
        assertTrue(result.isValid());
        assertFalse(result.hasMessages());
        assertFalse(result.hasWarnings());
        assertFalse(result.hasErrors());
        assertEquals(Collections.<ValidationMessage>emptyList(), result.getValidationMessages());
        assertNull(result.getCleanedBill());
    }

    @Test
    void singleWarning() {
        ValidationResult result = new ValidationResult();
        result.addMessage(Type.WARNING, "tfd", "dkw");
        assertTrue(result.isValid());
        assertTrue(result.hasMessages());
        assertTrue(result.hasWarnings());
        assertFalse(result.hasErrors());

        List<ValidationMessage> messages = result.getValidationMessages();
        assertNotNull(messages);
        assertEquals(1, messages.size());
        assertEquals(Type.WARNING, messages.get(0).getType());
        assertEquals("tfd", messages.get(0).getField());
        assertEquals("dkw", messages.get(0).getMessageKey());
        assertNull(messages.get(0).getMessageParameters());
    }

    @Test
    void singleError() {
        ValidationResult result = new ValidationResult();
        result.addMessage(Type.ERROR, "kdef.def", "qrdv.dwek-eke");
        assertFalse(result.isValid());
        assertTrue(result.hasMessages());
        assertFalse(result.hasWarnings());
        assertTrue(result.hasErrors());

        List<ValidationMessage> messages = result.getValidationMessages();
        assertNotNull(messages);
        assertEquals(1, messages.size());
        assertEquals(Type.ERROR, messages.get(0).getType());
        assertEquals("kdef.def", messages.get(0).getField());
        assertEquals("qrdv.dwek-eke", messages.get(0).getMessageKey());
        assertNull(messages.get(0).getMessageParameters());
    }

    @Test
    void multipleMessages() {
        ValidationResult result = new ValidationResult();
        result.addMessage(Type.ERROR, "abd-fds", "asdf.asdfe.werk");
        result.addMessage(Type.WARNING, "ieow.se3", "iwer.asdfwerk.asdf");
        assertFalse(result.isValid());
        assertTrue(result.hasMessages());
        assertTrue(result.hasWarnings());
        assertTrue(result.hasErrors());

        List<ValidationMessage> messages = result.getValidationMessages();
        assertNotNull(messages);
        assertEquals(2, messages.size());
        
        assertEquals(Type.ERROR, messages.get(0).getType());
        assertEquals("abd-fds", messages.get(0).getField());
        assertEquals("asdf.asdfe.werk", messages.get(0).getMessageKey());
        assertNull(messages.get(0).getMessageParameters());
        
        assertEquals(Type.WARNING, messages.get(1).getType());
        assertEquals("ieow.se3", messages.get(1).getField());
        assertEquals("iwer.asdfwerk.asdf", messages.get(1).getMessageKey());
        assertNull(messages.get(1).getMessageParameters());
    }

    @Test
    void messageWithMessageParameters() {
        ValidationResult result = new ValidationResult();
        result.addMessage(Type.WARNING, "jkr", "wcw.dw", new String[] { ")(*$" } );
        assertTrue(result.isValid());
        assertTrue(result.hasMessages());
        assertTrue(result.hasWarnings());
        assertFalse(result.hasErrors());

        List<ValidationMessage> messages = result.getValidationMessages();
        assertNotNull(messages);
        assertEquals(1, messages.size());
        assertEquals(Type.WARNING, messages.get(0).getType());
        assertEquals("jkr", messages.get(0).getField());
        assertEquals("wcw.dw", messages.get(0).getMessageKey());
        assertTrue(Arrays.equals(new String[] { ")(*$" }, messages.get(0).getMessageParameters()));
    }

    @Test
    void setCleanedBill() {
        ValidationResult result = new ValidationResult();
        result.setCleanedBill(SampleData.getExample2());

        assertEquals(SampleData.getExample2(), result.getCleanedBill());
    }
}
