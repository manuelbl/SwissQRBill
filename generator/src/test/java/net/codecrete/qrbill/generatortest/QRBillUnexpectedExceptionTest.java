//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.QRBillUnexpectedException;

/**
 * Tests for the {@link QRBillUnexpectedException} class
 */
@DisplayName("QRBillUnexpectedException exceptions")
class QRBillUnexpectedExceptionTest {

    @Test
    void messageOnly() {
        QRBillUnexpectedException e = assertThrows(QRBillUnexpectedException.class, () -> {
            throw new QRBillUnexpectedException("ABC");
        });
        assertEquals("ABC", e.getMessage());
        assertNull(e.getCause());
    }

    @Test
    void messageAndCause() {
        QRBillUnexpectedException e = assertThrows(QRBillUnexpectedException.class, () -> {
            try {
                ((String)null).length();
            } catch (Exception npe) {
                throw new QRBillUnexpectedException("QRS", npe);
            }
        });
        assertEquals("QRS", e.getMessage());
        assertSame(NullPointerException.class, e.getCause().getClass());
    }
}
