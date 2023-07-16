//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link QRBillGenerationException} class
 */
@DisplayName("QRBillGenerationException exceptions")
class QRBillGenerationExceptionTest {

    @Test
    void messageOnly() {
        QRBillGenerationException e = assertThrows(QRBillGenerationException.class, () -> {
            throw new QRBillGenerationException("ABC");
        });
        assertEquals("ABC", e.getMessage());
        assertNull(e.getCause());
    }

    @Test
    void messageAndCause() {
        QRBillGenerationException e = assertThrows(QRBillGenerationException.class, () -> {
            try {
                ((String) null).length();
            } catch (Exception npe) {
                throw new QRBillGenerationException("QRS", npe);
            }
        });
        assertEquals("QRS", e.getMessage());
        assertSame(NullPointerException.class, e.getCause().getClass());
    }
}
