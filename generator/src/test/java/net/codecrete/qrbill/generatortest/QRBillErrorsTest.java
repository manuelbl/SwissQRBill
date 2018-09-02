//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.canvas.AbstractCanvas;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.QRBillUnexpectedException;
import net.codecrete.qrbill.generator.QRBillValidationError;

/**
 * Unit tests for verifying the thrown exceptions when a QR bill is generated
 */
@DisplayName("QR bill generatio exception")
class QRBillErrorsTest {

	@Test
	void throwsRuntimeException() {
		assertThrows(QRBillUnexpectedException.class, () -> {
			Bill bill = SampleData.getExample1();
			FailingCanvas canvas = new FailingCanvas();
			QRBill.generate(bill, QRBill.BillFormat.A6_LANDSCAPE_SHEET, canvas);
		});
	}

	@Test
	void throwsValidationError() {
		assertThrows(QRBillValidationError.class, () -> {
			Bill bill = SampleData.getExample1();
			bill.getCreditor().setName(" ");
			QRBill.generate(bill, QRBill.BillFormat.A6_LANDSCAPE_SHEET, QRBill.GraphicsFormat.PDF);
		});
	}

	static class FailingCanvas extends AbstractCanvas {

		@Override
		public void setupPage(double width, double height) throws IOException {
			throw new IOException("not implemented");
		}

		@Override
		public void setTransformation(double translateX, double translateY, double scaleX, double scaleY) throws IOException {
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
		public byte[] getResult() throws IOException {
			throw new IOException("not implemented");
		}

		@Override
		public void close() throws IOException {
			throw new IOException("not implemented");
		}

	}
}