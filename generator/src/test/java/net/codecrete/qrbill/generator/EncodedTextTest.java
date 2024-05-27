//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import net.codecrete.qrbill.testhelper.SampleData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for encoding the embedded QR code text
 */
@DisplayName("Encoding of embedded QR code text")
class EncodedTextTest {

    @ParameterizedTest
    @MethodSource("provideNewLineSampleCombinations")
    void createText(int sample, QrDataSeparator separator) {
        Bill bill = SampleQrCodeText.getBillData(sample);
        bill.getFormat().setQrDataSeparator(separator);
        assertEquals(
                SampleQrCodeText.getQrCodeText(sample, separator == QrDataSeparator.CR_LF ? "\r\n" : "\n"),
                QRBill.encodeQrCodeText(bill)
        );
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

    private static Stream<Arguments> provideNewLineSampleCombinations() {
        Stream.Builder<Arguments> builder = Stream.builder();
        for (int sample = 1; sample <= 5; sample++) {
            builder.add(Arguments.of(sample, QrDataSeparator.LF));
            builder.add(Arguments.of(sample, QrDataSeparator.CR_LF));
        }
        return builder.build();
    }
}
