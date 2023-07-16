package net.codecrete.qrbill.canvas;

import net.codecrete.qrbill.generator.*;
import net.codecrete.qrbill.testhelper.FileComparison;
import net.codecrete.qrbill.testhelper.SampleData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for teting the different line styles (solid, dashed, dotted)
 * <p>
 * Resulting output is compared byte by byte.
 * </p>
 */
@DisplayName("Line styles (dashed and dotted))")
class LineStyleTest {

    @Test
    void svgWithDashedLines() {
        Bill bill = SampleData.getExample1();
        generateAndCompareBill(bill, GraphicsFormat.SVG,
                SeparatorType.DASHED_LINE, "linestyle_1.svg");
    }

    @Test
    void svgWithDottedLines() {
        Bill bill = SampleData.getExample1();
        generateAndCompareBill(bill, GraphicsFormat.SVG,
                SeparatorType.DOTTED_LINE_WITH_SCISSORS, "linestyle_2.svg");
    }

    @Test
    void pdfWithDashedLines() {
        Bill bill = SampleData.getExample1();
        generateAndCompareBill(bill, GraphicsFormat.PDF,
                SeparatorType.DASHED_LINE_WITH_SCISSORS, "linestyle_1.pdf");
    }

    @Test
    void pdfWithDottedLines() {
        Bill bill = SampleData.getExample1();
        generateAndCompareBill(bill, GraphicsFormat.PDF,
                SeparatorType.DOTTED_LINE, "linestyle_2.pdf");
    }

    @Test
    void pngWithDashedLines() {
        Bill bill = SampleData.getExample1();
        generateAndCompareBill(bill, GraphicsFormat.PNG,
                SeparatorType.DASHED_LINE, "linestyle_1.png");
    }

    @Test
    void pngWithDottedLines() {
        Bill bill = SampleData.getExample1();
        generateAndCompareBill(bill, GraphicsFormat.PNG,
                SeparatorType.DOTTED_LINE_WITH_SCISSORS, "linestyle_2.png");
    }

    private void generateAndCompareBill(Bill bill, GraphicsFormat graphicsFormat, SeparatorType separatorType,
                                        String expectedFileName) {
        bill.getFormat().setOutputSize(OutputSize.A4_PORTRAIT_SHEET);
        bill.getFormat().setSeparatorType(separatorType);
        bill.getFormat().setGraphicsFormat(graphicsFormat);
        byte[] imageData = QRBill.generate(bill);
        if (graphicsFormat == GraphicsFormat.PNG)
            FileComparison.assertGrayscaleImageContentsEqual(imageData, expectedFileName, 35000);
        else
            FileComparison.assertFileContentsEqual(imageData, expectedFileName);
    }
}
