package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.QRBillValidationError;
import net.codecrete.qrbill.generator.ValidationConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for decoding the embedded QR code text
 * with superfluous line feed at the end
 */
@DisplayName("Decoding of embedded QR code text")
public class SuperfluousLineFeedTest {

    @Test
    void withoutAlternativeSchemes() {
        Bill bill = SampleData.getExample2();
        DecodedTextTest.normalizeSourceBill(bill);
        String qrText = QRBill.encodeQrCodeText(bill) + "\r\n";
        Bill bill2 = QRBill.decodeQrCodeText(qrText);
        DecodedTextTest.normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void withAlternativeSchemes() {
        Bill bill = SampleData.getExample1();
        DecodedTextTest.normalizeSourceBill(bill);
        String qrText = QRBill.encodeQrCodeText(bill) + "\r\n";
        Bill bill2 = QRBill.decodeQrCodeText(qrText);
        DecodedTextTest.normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void invalidLineFeed() {
        Bill bill = SampleData.getExample1();
        DecodedTextTest.normalizeSourceBill(bill);
        String qrText = QRBill.encodeQrCodeText(bill) + "\r\n.";
        QRBillValidationError err = assertThrows(QRBillValidationError.class,
                () -> QRBill.decodeQrCodeText(qrText));
        DecodedTextTest.assertSingleError(err.getValidationResult(), ValidationConstants.KEY_VALID_DATA_STRUCTURE, ValidationConstants.FIELD_QR_TYPE);
    }
}
