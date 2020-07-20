package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.QRBillValidationError;
import net.codecrete.qrbill.generator.ValidationConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for decoding the embedded QR code text
 * with too many or too few lines
 * (illegal with strict interpretation, but found in the wild...)
 */
@DisplayName("Decoding of embedded QR code text")
class BarelyAcceptableQrCodeTest {

    @Test
    void withoutAlternativeSchemes() {
        Bill bill = SampleData.getExample2();
        DecodedTextTest.normalizeSourceBill(bill);
        String qrText = QRBill.encodeQrCodeText(bill) + "\n";
        Bill bill2 = QRBill.decodeQrCodeText(qrText);
        DecodedTextTest.normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void withAlternativeSchemes() {
        Bill bill = SampleData.getExample1();
        DecodedTextTest.normalizeSourceBill(bill);
        String qrText = QRBill.encodeQrCodeText(bill) + "\n";
        Bill bill2 = QRBill.decodeQrCodeText(qrText);
        DecodedTextTest.normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void invalidLineFeed() {
        Bill bill = SampleData.getExample1();
        DecodedTextTest.normalizeSourceBill(bill);
        String qrText = QRBill.encodeQrCodeText(bill) + "\n.";
        QRBillValidationError err = assertThrows(QRBillValidationError.class,
                () -> QRBill.decodeQrCodeText(qrText));
        DecodedTextTest.assertSingleError(err.getValidationResult(), ValidationConstants.KEY_VALID_DATA_STRUCTURE, ValidationConstants.FIELD_QR_TYPE);
    }

    @Test
    void tooManyLines() {
        Bill bill = SampleData.getExample1();
        DecodedTextTest.normalizeSourceBill(bill);
        String qrText = QRBill.encodeQrCodeText(bill) + "\n\n";
        QRBillValidationError err = assertThrows(QRBillValidationError.class,
                () -> QRBill.decodeQrCodeText(qrText));
        DecodedTextTest.assertSingleError(err.getValidationResult(), ValidationConstants.KEY_VALID_DATA_STRUCTURE, ValidationConstants.FIELD_QR_TYPE);
    }

    @Test
    void noNLAfterEPD() {
        Bill bill = SampleData.getExample2();
        DecodedTextTest.normalizeSourceBill(bill);
        String qrText = QRBill.encodeQrCodeText(bill);
        qrText = qrText.substring(0, qrText.length() - 1);
        assertTrue(qrText.endsWith("EPD"));

        Bill bill2 = QRBill.decodeQrCodeText(qrText);
        DecodedTextTest.normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void tooFewLines() {
        Bill bill = SampleData.getExample2();
        bill.setUnstructuredMessage(null);
        DecodedTextTest.normalizeSourceBill(bill);
        String qrText2 = QRBill.encodeQrCodeText(bill);
        String qrText = qrText2.substring(0, qrText2.length() - 5);

        QRBillValidationError err = assertThrows(QRBillValidationError.class,
                () -> QRBill.decodeQrCodeText(qrText));
        DecodedTextTest.assertSingleError(err.getValidationResult(), ValidationConstants.KEY_VALID_DATA_STRUCTURE, ValidationConstants.FIELD_QR_TYPE);
    }
}
