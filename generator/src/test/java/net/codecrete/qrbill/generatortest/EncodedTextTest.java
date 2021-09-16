//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.QRBillValidationError;
import net.codecrete.qrbill.generator.QRCodeText;
import net.codecrete.qrbill.generator.ValidationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for encoding the embedded QR code text
 */
@DisplayName("Encoding of embedded QR code text")
class EncodedTextTest {

    @Test
    void createText1() {
        Bill bill = SampleQrCodeText.getBillData1();
        assertEquals(SampleQrCodeText.getQrCodeText1(false), QRBill.encodeQrCodeText(bill));
    }

    @Test
    void createText2() {
        Bill bill = SampleQrCodeText.getBillData2();
        assertEquals(SampleQrCodeText.getQrCodeText2(), QRBill.encodeQrCodeText(bill));
    }

    @Test
    void createText3() {
        Bill bill = SampleQrCodeText.getBillData3();
        assertEquals(SampleQrCodeText.getQrCodeText3(), QRBill.encodeQrCodeText(bill));
    }

    @Test
    void createText4() {
        Bill bill = SampleQrCodeText.getBillData4();
        assertEquals(SampleQrCodeText.getQrCodeText4(), QRBill.encodeQrCodeText(bill));
    }

    @Test
    void createText5() {
        Bill bill = SampleQrCodeText.getBillData5();
        assertEquals(SampleQrCodeText.getQrCodeText5(), QRBill.encodeQrCodeText(bill));
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
        assertEquals(SampleQrCodeText.getQrCodeText3(), QRCodeText.create(bill));
    }
}
