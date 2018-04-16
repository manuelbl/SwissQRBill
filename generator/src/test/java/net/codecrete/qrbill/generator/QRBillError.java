//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generator;

import java.io.IOException;

import org.junit.Test;

import net.codecrete.qrbill.canvas.AbstractCanvas;

public class QRBillError {

    @Test(expected = QRBillUnexpectedException.class)
    public void testQrBillRuntimeException() {
        Bill bill = SampleData.getExample1();
        FailingCanvas canvas = new FailingCanvas();
        QRBill.generate(bill, QRBill.BillFormat.A6_LANDSCAPE_SHEET, canvas);
    }

    @Test(expected = QRBillValidationError.class)
    public void testValidationError() {
        Bill bill = SampleData.getExample1();
        bill.getCreditor().setName(" ");
        QRBill.generate(bill, QRBill.BillFormat.A6_LANDSCAPE_SHEET, QRBill.GraphicsFormat.PDF);
    }


    static class FailingCanvas extends AbstractCanvas {

		@Override
		public void setupPage(double width, double height) throws IOException {
			throw new IOException("not implemented");
		}

		@Override
		public void setTransformation(double translateX, double translateY, double scale) throws IOException {
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
		public void addRectangle(double x, double y, double width, double height) throws IOException {
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
		public byte[] getResult() throws IOException {
			throw new IOException("not implemented");
		}

		@Override
		public void close() throws IOException {
			throw new IOException("not implemented");
		}

    }
}