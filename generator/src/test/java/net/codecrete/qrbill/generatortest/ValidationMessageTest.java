//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.ValidationMessage;
import net.codecrete.qrbill.generator.ValidationMessage.Type;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationMessageTest {

    @Test
    void defaultConstructor() {
        ValidationMessage msg = new ValidationMessage();
        assertNull(msg.getType());
        assertNull(msg.getField());
        assertNull(msg.getMessageKey());
        assertNull(msg.getMessageParameters());
    }

    @Test
    void constructorWithThreeParameters() {
        ValidationMessage msg = new ValidationMessage(Type.ERROR, "fld", "msg3");
        assertEquals(Type.ERROR, msg.getType());
        assertEquals("fld", msg.getField());
        assertEquals("msg3", msg.getMessageKey());
        assertNull(msg.getMessageParameters());
    }

    @Test
    void constructorWithFourParameters() {
        ValidationMessage msg = new ValidationMessage(Type.WARNING, "addInfo", "clipped", new String[] { "xxx" });
        assertEquals(Type.WARNING, msg.getType());
        assertEquals("addInfo", msg.getField());
        assertEquals("clipped", msg.getMessageKey());
        assertNotNull(msg.getMessageParameters());
        assertEquals(1, msg.getMessageParameters().length);
        assertEquals("xxx", msg.getMessageParameters()[0]);
    }

    @Test
    void setType() {
        ValidationMessage msg = new ValidationMessage();
        msg.setType(Type.ERROR);
        assertEquals(Type.ERROR, msg.getType());
    }

    @Test
    void setField() {
        ValidationMessage msg = new ValidationMessage();
        msg.setField("tt3");
        assertEquals("tt3", msg.getField());
    }

    @Test
    void setMessageKey() {
        ValidationMessage msg = new ValidationMessage();
        msg.setMessageKey("msg.err.invalid");
        assertEquals("msg.err.invalid", msg.getMessageKey());
    }

    @Test
    void setMessageParameters() {
        ValidationMessage msg = new ValidationMessage();
        msg.setMessageParameters(new String[] { "abc", "def", "ghi" });
        assertArrayEquals(new String[]{"abc", "def", "ghi"}, msg.getMessageParameters());
    }
}
