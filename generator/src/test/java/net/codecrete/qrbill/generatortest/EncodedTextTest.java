//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for encoding the embedded QR code text
 */
@DisplayName("Encoding of embedded QR code text")
class EncodedTextTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void createText(int sample) {
        Bill bill = SampleQrCodeText.getBillData(sample);
        assertEquals(SampleQrCodeText.getQrCodeText(sample), QRBill.encodeQrCodeText(bill));
    }

    @Test
    void createTextError1() {
        Bill bill = SampleData.getExample4();
        bill.setAmount(BigDecimal.valueOf(-1, 2));
        assertThrows(QRBillValidationError.class, () -> QRBill.encodeQrCodeText(bill));
    }

    @Test
    void createTextEmptyReference() {
        Bill bill = SampleQrCodeText.getBillData3();
        ValidationResult result = QRBill.validate(bill);
        assertFalse(result.hasErrors());
        bill = result.getCleanedBill();
        bill.setReference("");
        assertEquals(SampleQrCodeText.getQrCodeText(3), QRCodeText.create(bill));
    }
}
