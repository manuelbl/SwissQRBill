//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.AlternativeScheme;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.BillFormat;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.QRBillValidationError;
import net.codecrete.qrbill.generator.ValidationConstants;
import net.codecrete.qrbill.generator.ValidationMessage;
import net.codecrete.qrbill.generator.ValidationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for decoding the embedded QR code text
 */
@DisplayName("Decoding of embedded QR code text")
class DecodedTextTest {

    @Test
    void decodeText1() {
        Bill bill = SampleData.getExample1();
        normalizeSourceBill(bill);
        Bill bill2 = QRBill.decodeQrCodeText(QRBill.encodeQrCodeText(bill));
        normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void decodeText2() {
        Bill bill = SampleData.getExample2();
        normalizeSourceBill(bill);
        Bill bill2 = QRBill.decodeQrCodeText(QRBill.encodeQrCodeText(bill));
        normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void decodeText3() {
        Bill bill = SampleData.getExample3();
        normalizeSourceBill(bill);
        Bill bill2 = QRBill.decodeQrCodeText(QRBill.encodeQrCodeText(bill));
        normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void decodeText4() {
        Bill bill = SampleData.getExample4();
        bill.setFormat(new BillFormat()); // set default values
        normalizeSourceBill(bill);
        Bill bill2 = QRBill.decodeQrCodeText(QRBill.encodeQrCodeText(bill));
        normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void decodeTextB1a() {
        Bill bill = SampleQrCodeText.getBillData1();
        normalizeSourceBill(bill);
        Bill bill2 = QRBill.decodeQrCodeText(SampleQrCodeText.getQrCodeText1(false));
        normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void decodeTextB1b() {
        Bill bill = SampleQrCodeText.getBillData1();
        normalizeSourceBill(bill);
        Bill bill2 = QRBill.decodeQrCodeText(SampleQrCodeText.getQrCodeText1(true));
        normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void decodeTextB2() {
        Bill bill = SampleQrCodeText.getBillData2();
        normalizeSourceBill(bill);
        Bill bill2 = QRBill.decodeQrCodeText(SampleQrCodeText.getQrCodeText2());
        normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void decodeTextB3() {
        Bill bill = SampleQrCodeText.getBillData3();
        normalizeSourceBill(bill);
        Bill bill2 = QRBill.decodeQrCodeText(SampleQrCodeText.getQrCodeText3());
        normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void decodeTextB4() {
        Bill bill = SampleQrCodeText.getBillData4();
        normalizeSourceBill(bill);
        Bill bill2 = QRBill.decodeQrCodeText(SampleQrCodeText.getQrCodeText4());
        normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void decodeTextB5() {
        Bill bill = SampleQrCodeText.getBillData5();
        normalizeSourceBill(bill);
        Bill bill2 = QRBill.decodeQrCodeText(SampleQrCodeText.getQrCodeText5());
        normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void decodeInvalidRefType() {
        Bill bill = SampleData.getExample3();
        String qrText = QRBill.encodeQrCodeText(bill);
        qrText = qrText.replace("SCOR", "XXXX");
        Bill bill2 = QRBill.decodeQrCodeText(qrText);
        assertEquals("XXXX", bill2.getReferenceType());
    }

    static void normalizeSourceBill(Bill bill) {
        bill.getFormat().setLanguage(Language.DE);
        bill.setAccount(bill.getAccount().replace(" ", ""));
        if (bill.getReference() != null)
            bill.setReference(bill.getReference().replace(" ", ""));
        if (bill.getCreditor() != null) {
            if (bill.getCreditor().getStreet() == null)
                bill.getCreditor().setStreet(""); // replace null with empty string
            if (bill.getCreditor().getHouseNo() == null)
                bill.getCreditor().setHouseNo(""); // replace null with empty string
        }
        if (bill.getDebtor() != null) {
            if (bill.getDebtor().getTown() != null)
                bill.getDebtor().setTown(bill.getDebtor().getTown().trim());
        }
        if (bill.getReference() == null)
            bill.setReference(""); // replace null with empty string
        if (bill.getUnstructuredMessage() == null)
            bill.setUnstructuredMessage(""); // replace null with empty string
        if (bill.getBillInformation() == null)
            bill.setBillInformation(""); // replace null with empty string
        if (bill.getAlternativeSchemes() != null) {
            for (AlternativeScheme scheme : bill.getAlternativeSchemes())
                scheme.setName(null);
        }
    }

    static void normalizeDecodedBill(Bill bill) {
        bill.getFormat().setLanguage(Language.DE); // fix language (not contained in text)
    }

    @Test
    void decodeInvalidFormat1() {
        QRBillValidationError err = assertThrows(QRBillValidationError.class, () -> QRBill.decodeQrCodeText("garbage"));
        assertSingleError(err.getValidationResult(), ValidationConstants.KEY_DATA_STRUCTURE_INVALID, ValidationConstants.FIELD_QR_TYPE);
    }

    @Test
    void decodeInvalidFormat2a() {
        QRBillValidationError err = assertThrows(QRBillValidationError.class,
                () -> QRBill.decodeQrCodeText("SPC\r\n0100\r\n\r\n\r\n"));
        assertSingleError(err.getValidationResult(), ValidationConstants.KEY_DATA_STRUCTURE_INVALID, ValidationConstants.FIELD_QR_TYPE);
    }

    @Test
    void decodeInvalidFormat2b() {
        QRBillValidationError err = assertThrows(QRBillValidationError.class, () -> QRBill.decodeQrCodeText(
                "SPC1\r\n0200\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n"));
        assertSingleError(err.getValidationResult(), ValidationConstants.KEY_DATA_STRUCTURE_INVALID, ValidationConstants.FIELD_QR_TYPE);
    }

    @Test
    void decodeInvalidFormat3() {
        QRBillValidationError err = assertThrows(QRBillValidationError.class, () -> QRBill.decodeQrCodeText(
                "SPC1\r\n0200\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n"));
        assertSingleError(err.getValidationResult(), ValidationConstants.KEY_DATA_STRUCTURE_INVALID, ValidationConstants.FIELD_QR_TYPE);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SPC\r\n0101\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n",
            "SPC\r\n020\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n",
            "SPC\r\n020f\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n"
    })
    void decodeInvalid1Version(String qrCodeText) {
        QRBillValidationError err = assertThrows(QRBillValidationError.class, () -> QRBill.decodeQrCodeText(qrCodeText));
        assertSingleError(err.getValidationResult(), ValidationConstants.KEY_VERSION_UNSUPPORTED, ValidationConstants.FIELD_VERSION);
    }

    @Test
    void decodeIgnoreMinorVersion() {
        Bill bill = SampleQrCodeText.getBillData1();
        normalizeSourceBill(bill);
        String qrCodeText = SampleQrCodeText.getQrCodeText1(false);
        qrCodeText = qrCodeText.replace("\n0200\n", "\n0201\n");
        Bill bill2 = QRBill.decodeQrCodeText(qrCodeText);
        normalizeDecodedBill(bill2);
        assertEquals(bill, bill2);
    }

    @Test
    void decodeInvalidCodingType() {
        QRBillValidationError err = assertThrows(QRBillValidationError.class, () -> QRBill.decodeQrCodeText(
                "SPC\r\n0200\r\n0\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n"));
        assertSingleError(err.getValidationResult(), ValidationConstants.KEY_CODING_TYPE_UNSUPPORTED, ValidationConstants.FIELD_CODING_TYPE);
    }

    @Test
    void decodeInvalidNumber() {
        String invalidText = SampleQrCodeText.getQrCodeText1(false).replace("3949.75", "1239d49.75");
        QRBillValidationError err = assertThrows(QRBillValidationError.class,
                () -> QRBill.decodeQrCodeText(invalidText));
        assertSingleError(err.getValidationResult(), ValidationConstants.KEY_NUMBER_INVALID, ValidationConstants.FIELD_AMOUNT);
    }

    @Test
    void decodeMissingEPD() {
        String invalidText = SampleQrCodeText.getQrCodeText1(false).replace("EPD", "E_P");
        QRBillValidationError err = assertThrows(QRBillValidationError.class,
                () -> QRBill.decodeQrCodeText(invalidText));
        assertSingleError(err.getValidationResult(), ValidationConstants.KEY_DATA_STRUCTURE_INVALID, ValidationConstants.FIELD_TRAILER);
    }

    static void assertSingleError(ValidationResult result, String messageKey, String field) {
        assertNotNull(result);
        List<ValidationMessage> messages = result.getValidationMessages();
        assertNotNull(messages);
        assertEquals(1, messages.size());
        assertEquals(ValidationMessage.Type.ERROR, messages.get(0).getType());
        assertEquals(messageKey, messages.get(0).getMessageKey());
        assertEquals(field, messages.get(0).getField());
    }
}
