//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.canvas.AbstractCanvas;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.QRBillGenerationException;
import net.codecrete.qrbill.generator.QRBillValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for verifying the thrown exceptions when a QR bill is generated
 */
@DisplayName("QR bill generation exception")
class QRBillErrorsTest {

    @Test
    void throwsRuntimeException() {
        assertThrows(QRBillGenerationException.class, () -> {
            Bill bill = SampleData.getExample1();
            FailingCanvas canvas = new FailingCanvas();
            bill.getFormat().setOutputSize(OutputSize.QR_BILL_ONLY);
            QRBill.draw(bill, canvas);
        });
    }

    @Test
    void throwsValidationError1() {
        QRBillValidationError error = assertThrows(QRBillValidationError.class, () -> {
            Bill bill = SampleData.getExample1();
            bill.getCreditor().setName(" ");
            bill.getCreditor().setHouseNo("abcdefghijklmnopqrstuvwxyz");
            bill.getFormat().setOutputSize(OutputSize.QR_BILL_ONLY);
            bill.getFormat().setGraphicsFormat(GraphicsFormat.PDF);
            QRBill.generate(bill);
        });
        assertEquals("QR bill data is invalid: field \"creditor.name\" may not be empty (field_is_mandatory)", error.getMessage());
    }

    @Test
    void throwsValidationError2() {
        QRBillValidationError error = assertThrows(QRBillValidationError.class, () -> {
            Bill bill = SampleData.getExample1();
            bill.setUnstructuredMessage(null);
            bill.setBillInformation("//" + new String(new char[150]).replace('\0', 'X'));
            bill.getFormat().setOutputSize(OutputSize.QR_BILL_ONLY);
            bill.getFormat().setGraphicsFormat(GraphicsFormat.PDF);
            QRBill.generate(bill);
        });
        assertEquals("QR bill data is invalid: the value for field \"billInformation\" should not exceed a length of 140 characters (field_value_too_long)", error.getMessage());
    }

    @Test
    void throwsValidationError3() {
        QRBillValidationError error = assertThrows(QRBillValidationError.class, () -> {
            Bill bill = SampleData.getExample1();
            bill.setReference("RF1234");
            bill.setCurrency("XXX");
            bill.getFormat().setOutputSize(OutputSize.QR_BILL_ONLY);
            bill.getFormat().setGraphicsFormat(GraphicsFormat.PDF);
            QRBill.generate(bill);
        });
        assertEquals("QR bill data is invalid: currency should be \"CHF\" or \"EUR\" (currency_is_chf_or_eur); reference is invalid (numeric QR reference required) (valid_qr_ref_no)", error.getMessage());
    }

    static class FailingCanvas extends AbstractCanvas {

        public FailingCanvas() {
            setupFontMetrics("Arial");
        }

        @Override
        public void setTransformation(double translateX, double translateY, double rotate, double scaleX, double scaleY) throws IOException {
            throw new IOException("not implemented");
        }

        @Override
        public void putText(String text, double x, double y, int fontSize, boolean isBold) throws IOException {
            throw new IOException("not implemented");
        }

        @Override
        public void startPath() throws IOException {
            throw new IOException("not implemented");
        }

        @Override
        public void moveTo(double x, double y) throws IOException {
            throw new IOException("not implemented");
        }

        @Override
        public void lineTo(double x, double y) throws IOException {
            throw new IOException("not implemented");
        }

        @Override
        public void cubicCurveTo(double x1, double y1, double x2, double y2, double x, double y) throws IOException {
            throw new IOException("not implemented");
        }

        @Override
        public void addRectangle(double x, double y, double width, double height) throws IOException {
            throw new IOException("not implemented");
        }

        @Override
        public void closeSubpath() throws IOException {
            throw new IOException("not implemented");
        }

        @Override
        public void fillPath(int color) throws IOException {
            throw new IOException("not implemented");
        }

        @Override
        public void strokePath(double strokeWidth, int color) throws IOException {
            throw new IOException("not implemented");
        }

        @Override
        public void close() throws IOException {
            throw new IOException("not implemented");
        }

    }
}
