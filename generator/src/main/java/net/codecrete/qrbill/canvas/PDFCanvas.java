//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.canvas;

import net.codecrete.qrbill.generator.Bill;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.util.Matrix;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Canvas for generating PDF files.
 * <p>
 * The PDF generator currently only supports the Helvetica font.
 * </p>
 */
public class PDFCanvas extends AbstractCanvas implements ByteArrayResult {

    /**
     * Add the QR bill on the last page of the PDF document.
     */
    public static final int LAST_PAGE = -1;

    /**
     * Add the QR bill on a new page at the end of the PDF document.
     */
    public static final int NEW_PAGE_AT_END = -2;

    private PDDocument document;
    private PDFont regularFont;
    private PDFont boldFont;
    private PDPageContentStream contentStream;
    private final boolean isContentStreamOwned;
    private int lastStrokingColor = 0;
    private int lastNonStrokingColor = 0;
    private double lastLineWidth = 1;
    private LineStyle lastLineStyle = LineStyle.Solid;

    /**
     * Creates a new instance using the specified page size.
     * <p>
     * A new PDF file with a single page will be created.
     * It can later be retrieved as a byte array (see {@link #toByteArray()})
     * or written to an output stream (see {@link #writeTo(OutputStream)}).
     * </p>
     * <p>
     * Call {@link net.codecrete.qrbill.generator.QRBill#draw(Bill, Canvas)} to draw
     * the QR bill to this canvas. It will be drawn at the origin of the page,
     * i.e. the bottom left corner of the bill will be in the bottom left corner of the page.
     * </p>
     * <p>
     * For text, the PDF standard font Helvetica will be used. It does not need to be embedded into
     * the file and is available on all PDF viewers. But it is restricted to the WinANSI character set.
     * </p>
     *
     * @param width  page width, in mm
     * @param height page height, in mm
     * @throws IOException thrown if the creation fails
     */
    public PDFCanvas(double width, double height) throws IOException {
        this(width, height, PDFFontSettings.standardHelvetica());
    }

    /**
     * Creates a new instance using the specified page size and font.
     * <p>
     * A new PDF file with a single page will be created.
     * It can later be retrieved as a byte array (see {@link #toByteArray()})
     * or written to an output stream (see {@link #writeTo(OutputStream)}).
     * </p>
     * <p>
     * Call {@link net.codecrete.qrbill.generator.QRBill#draw(Bill, Canvas)} to draw
     * the QR bill to this canvas. It will be drawn at the origin of the page,
     * i.e. the bottom left corner of the bill will be in the bottom left corner of the page.
     * </p>
     * <p>
     * Font settings specify what font to use and whether to embed the font in the PDF file.
     * </p>
     *
     * @param width  page width, in mm
     * @param height page height, in mm
     * @param fontSettings font settings
     * @throws IOException thrown if the creation fails
     */
    public PDFCanvas(double width, double height, PDFFontSettings fontSettings) throws IOException {
        document = new PDDocument();
        document.getDocumentInformation().setTitle("Swiss QR Bill");
        configureFonts(document, fontSettings);
        PDPage page = new PDPage(new PDRectangle((float) (width * MM_TO_PT), (float) (height * MM_TO_PT)));
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true);
        isContentStreamOwned = true;
        initGraphicsState();
    }

    /**
     * Creates a new instance for adding the QR bill to an existing PDF document.
     * <p>
     * The QR bill can either be added to an existing page by specifying the page number
     * of an existing page (or {@link #LAST_PAGE}), or it can be added to a new page
     * at the end of the document (see {@link #NEW_PAGE_AT_END}). If a new page is added,
     * it will have A4 portrait format.
     * </p>
     * <p>
     * Call {@link net.codecrete.qrbill.generator.QRBill#draw(Bill, Canvas)} to draw
     * the QR bill to this canvas. It will be drawn at the origin of the page,
     * i.e. the bottom left corner of the bill will be in the bottom left corner of the page.
     * </p>
     * <p>
     * The new PDF file can later be retrieved as a byte array (see {@link #toByteArray()})
     * or written to an output stream (see {@link #writeTo(OutputStream)}).
     * </p>
     * <p>
     * For text, the PDF standard font Helvetica will be used. It does not need to be embedded into
     * the file and is available on all PDF viewers. But it is restricted to the WinANSI character set.
     * </p>
     *
     * @param path   path to existing PDF document
     * @param pageNo the zero-based number of the page the QR bill should be added to
     * @throws IOException thrown if the creation fails
     */
    public PDFCanvas(Path path, int pageNo) throws IOException {
        this(path, pageNo, PDFFontSettings.standardHelvetica());
    }

    /**
     * Creates a new instance for adding the QR bill to an existing PDF document.
     * <p>
     * The QR bill can either be added to an existing page by specifying the page number
     * of an existing page (or {@link #LAST_PAGE}), or it can be added to a new page
     * at the end of the document (see {@link #NEW_PAGE_AT_END}). If a new page is added,
     * it will have A4 portrait format.
     * </p>
     * <p>
     * Call {@link net.codecrete.qrbill.generator.QRBill#draw(Bill, Canvas)} to draw
     * the QR bill to this canvas. It will be drawn at the origin of the page,
     * i.e. the bottom left corner of the bill will be in the bottom left corner of the page.
     * </p>
     * <p>
     * The new PDF file can later be retrieved as a byte array (see {@link #toByteArray()})
     * or written to an output stream (see {@link #writeTo(OutputStream)}).
     * </p>
     * <p>
     * Font settings specify what font to use and whether to embed the font in the PDF file.
     * </p>
     *
     * @param path   path to existing PDF document
     * @param pageNo the zero-based number of the page the QR bill should be added to
     * @param fontSettings font settings
     * @throws IOException thrown if the creation fails
     */
    public PDFCanvas(Path path, int pageNo, PDFFontSettings fontSettings) throws IOException {
        document = Loader.loadPDF(path.toFile());
        configureFonts(document, fontSettings);
        preparePage(document, pageNo);
        isContentStreamOwned = true;
        initGraphicsState();
    }

    /**
     * Creates a new instance for adding the QR bill to an existing PDF document.
     * <p>
     * The QR bill can either be added to an existing page by specifying the page number
     * of an existing page (or {@link #LAST_PAGE}), or it can be added to a new page
     * at the end of the document (see {@link #NEW_PAGE_AT_END}). If a new page is added,
     * it will have A4 portrait format.
     * </p>
     * <p>
     * Call {@link net.codecrete.qrbill.generator.QRBill#draw(Bill, Canvas)} to draw
     * the QR bill to this canvas. It will be drawn at the origin of the page,
     * i.e. the bottom left corner of the bill will be in the bottom left corner of the page.
     * </p>
     * <p>
     * The new PDF file can later be retrieved as a byte array (see {@link #toByteArray()})
     * or written to an output stream (see {@link #writeTo(OutputStream)}).
     * </p>
     * <p>
     * For text, the PDF standard font Helvetica will be used. It does not need to be embedded into
     * the file and is available on all PDF viewers. But it is restricted to the WinANSI character set.
     * </p>
     *
     * @param pdfDocument binary array containing PDF document
     * @param pageNo      the zero-based number of the page the QR bill should be added to
     * @throws IOException thrown if the creation fails
     */
    public PDFCanvas(byte[] pdfDocument, int pageNo) throws IOException {
        this(pdfDocument, pageNo, PDFFontSettings.standardHelvetica());
    }

    /**
     * Creates a new instance for adding the QR bill to an existing PDF document.
     * <p>
     * The QR bill can either be added to an existing page by specifying the page number
     * of an existing page (or {@link #LAST_PAGE}), or it can be added to a new page
     * at the end of the document (see {@link #NEW_PAGE_AT_END}). If a new page is added,
     * it will have A4 portrait format.
     * </p>
     * <p>
     * Call {@link net.codecrete.qrbill.generator.QRBill#draw(Bill, Canvas)} to draw
     * the QR bill to this canvas. It will be drawn at the origin of the page,
     * i.e. the bottom left corner of the bill will be in the bottom left corner of the page.
     * </p>
     * <p>
     * The new PDF file can later be retrieved as a byte array (see {@link #toByteArray()})
     * or written to an output stream (see {@link #writeTo(OutputStream)}).
     * </p>
     * <p>
     * Font settings specify what font to use and whether to embed the font in the PDF file.
     * </p>
     *
     * @param pdfDocument binary array containing PDF document
     * @param pageNo      the zero-based number of the page the QR bill should be added to
     * @param fontSettings font settings
     * @throws IOException thrown if the creation fails
     */
    public PDFCanvas(byte[] pdfDocument, int pageNo, PDFFontSettings fontSettings) throws IOException {
        document = Loader.loadPDF(pdfDocument);
        configureFonts(document, fontSettings);
        preparePage(document, pageNo);
        isContentStreamOwned = true;
        initGraphicsState();
    }

    /**
     * Creates a new instance for adding the QR bill to the specified PDF document.
     * <p>
     * The QR bill can either be added to an existing page by specifying the page number
     * of an existing page (or {@link #LAST_PAGE}), or it can be added to a new page
     * at the end of the document (see {@link #NEW_PAGE_AT_END}). If a new page is added,
     * it will have A4 portrait format.
     * </p>
     * <p>
     * Call {@link net.codecrete.qrbill.generator.QRBill#draw(Bill, Canvas)} to draw
     * the QR bill to this canvas. It will be drawn at the origin of the page,
     * i.e. the bottom left corner of the bill will be in the bottom left corner of the page.
     * </p>
     * <p>
     * The PDF document must have been opened with the appropriate PDFBox method,
     * and it must be saved with a PDFBox method. Before saving it, this instance must
     * be closed (see {@link #close()}). The instance methods {@link #toByteArray()}
     * and {@link #writeTo(OutputStream)} may not be used and will throw an exception.
     * </p>
     * <p>
     * For text, the PDF standard font Helvetica will be used. It does not need to be embedded into
     * the file and is available on all PDF viewers. But it is restricted to the WinANSI character set.
     * </p>
     *
     * @param pdfDocument PDF document
     * @param pageNo      the zero-based number of the page the QR bill should be added to
     * @throws IOException thrown if the creation fails
     */
    public PDFCanvas(PDDocument pdfDocument, int pageNo) throws IOException {
        this(pdfDocument, pageNo, PDFFontSettings.standardHelvetica());
    }

    /**
     * Creates a new instance for adding the QR bill to the specified PDF document.
     * <p>
     * The QR bill can either be added to an existing page by specifying the page number
     * of an existing page (or {@link #LAST_PAGE}), or it can be added to a new page
     * at the end of the document (see {@link #NEW_PAGE_AT_END}). If a new page is added,
     * it will have A4 portrait format.
     * </p>
     * <p>
     * Call {@link net.codecrete.qrbill.generator.QRBill#draw(Bill, Canvas)} to draw
     * the QR bill to this canvas. It will be drawn at the origin of the page,
     * i.e. the bottom left corner of the bill will be in the bottom left corner of the page.
     * </p>
     * <p>
     * The PDF document must have been opened with the appropriate PDFBox method,
     * and it must be saved with a PDFBox method. Before saving it, this instance must
     * be closed (see {@link #close()}). The instance methods {@link #toByteArray()}
     * and {@link #writeTo(OutputStream)} may not be used and will throw an exception.
     * </p>
     * <p>
     * Font settings specify what font to use and whether to embed the font in the PDF file.
     * </p>
     *
     * @param pdfDocument PDF document
     * @param pageNo      the zero-based number of the page the QR bill should be added to
     * @param fontSettings font settings
     * @throws IOException thrown if the creation fails
     */
    public PDFCanvas(PDDocument pdfDocument, int pageNo, PDFFontSettings fontSettings) throws IOException {
        configureFonts(pdfDocument, fontSettings);
        preparePage(pdfDocument, pageNo);
        isContentStreamOwned = true;
        initGraphicsState();
    }

    /**
     * Creates a new instance for adding a QR bill to the specified PDF page content stream.
     * <p>
     * Call {@link net.codecrete.qrbill.generator.QRBill#draw(Bill, Canvas)} to draw
     * the QR bill to this canvas. It will be drawn at the current origin of the page,
     * i.e. the bottom left corner of the bill will be at the origin. The current
     * transformation matrix (CTM) is also applied.
     * </p>
     * <p>
     * The PDF document and PDF content stream must have been opened with the appropriate PDFBox method.
     * Do not access the PDF document or page content stream until this instance has been closed.
     * (see {@link #close()}). Closing it will also reset the graphics state to the state before
     * creating this instance.
     * </p>
     * <p>
     * For text, the PDF standard font Helvetica will be used. It does not need to be embedded into
     * the file and is available on all PDF viewers. But it is restricted to the WinANSI character set.
     * </p>
     *
     * @param contentStream PDF page content stream
     */
    public PDFCanvas(PDPageContentStream contentStream) {
        this.contentStream = contentStream;
        isContentStreamOwned = false;
        try {
            configureFonts(null, PDFFontSettings.standardHelvetica());
            initGraphicsState();
        } catch (IOException e) {
            // should add throws IOException to constructor in next major release
            throw new UncheckedIOException(e);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private void configureFonts(PDDocument doc, PDFFontSettings fontSettings) throws IOException {
        setupFontMetrics(fontSettings.getFontFamily());

        switch (fontSettings.getFontEmbedding()) {
            case STANDARD_HELVETICA:
                regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
                boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                break;
            case EMBEDDED_LIBERATION_SANS:
                regularFont = PDType0Font.load(doc, PDFCanvas.class.getResource("/fonts/LiberationSans-Regular.ttf").openStream());
                boldFont = PDType0Font.load(doc, PDFCanvas.class.getResource("/fonts/LiberationSans-Bold.ttf").openStream());
                break;
            case EMBEDDED_CUSTOM:
                regularFont = PDType0Font.load(doc, Files.newInputStream(fontSettings.getRegularFontPath()));
                boldFont = PDType0Font.load(doc, Files.newInputStream(fontSettings.getBoldFontPath()));
                break;
        }
    }

    private void preparePage(PDDocument doc, int pageNo) throws IOException {
        if (pageNo == NEW_PAGE_AT_END) {
            PDPage page = new PDPage(new PDRectangle((float) (210 * MM_TO_PT), (float) (297 * MM_TO_PT)));
            doc.addPage(page);
            contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.OVERWRITE, true, true);
        } else {
            if (pageNo == LAST_PAGE) pageNo = doc.getNumberOfPages() - 1;
            PDPage page = doc.getPage(pageNo);
            contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
        }
    }

    private void initGraphicsState() throws IOException {
        if (!isContentStreamOwned) {
            // save initial graphics state to restore it after drawing
            // (not needed if the content stream is owned as it will be closed)
            contentStream.saveGraphicsState();

            // set default graphics state
            contentStream.setStrokingColor(0.0f, 0.0f, 0.0f);
            contentStream.setNonStrokingColor(0.0f, 0.0f, 0.0f);
            contentStream.setLineWidth(1);
            contentStream.setLineJoinStyle(0);
            contentStream.setLineCapStyle(0);
            contentStream.setLineDashPattern(new float[0], 0);
            contentStream.setMiterLimit(10);
        }

        contentStream.saveGraphicsState();
    }

    @Override
    public void setTransformation(double translateX, double translateY, double rotate, double scaleX, double scaleY) throws IOException {
        translateX *= MM_TO_PT;
        translateY *= MM_TO_PT;

        contentStream.restoreGraphicsState();
        lastStrokingColor = 0;
        lastNonStrokingColor = 0;
        lastLineWidth = 1;

        contentStream.saveGraphicsState();
        Matrix matrix = new Matrix();
        matrix.translate((float) translateX, (float) translateY);
        if (rotate != 0) matrix.rotate(rotate);
        if (scaleX != 1 || scaleY != 1) matrix.scale((float) scaleX, (float) scaleY);
        contentStream.transform(matrix);
    }

    @Override
    public void putText(String text, double x, double y, int fontSize, boolean isBold) throws IOException {
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        contentStream.setFont(isBold ? boldFont : regularFont, fontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset((float) x, (float) y);
        contentStream.showText(text);
        contentStream.endText();
    }

    @Override
    public void putTextLines(String[] lines, double x, double y, int fontSize, double leading) throws IOException {
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        float lineHeight = (float) ((fontMetrics.getLineHeight(fontSize) + leading) * MM_TO_PT);
        contentStream.setFont(regularFont, fontSize);
        contentStream.beginText();
        contentStream.newLineAtOffset((float) x, (float) y);
        boolean isFirstLine = true;
        for (String line : lines) {
            if (isFirstLine) {
                isFirstLine = false;
            } else {
                contentStream.newLineAtOffset(0, -lineHeight);
            }
            contentStream.showText(line);
        }
        contentStream.endText();
    }

    @Override
    public void startPath() {
        // path is start implicitly
    }

    @Override
    public void moveTo(double x, double y) throws IOException {
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        contentStream.moveTo((float) x, (float) y);
    }

    @Override
    public void lineTo(double x, double y) throws IOException {
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        contentStream.lineTo((float) x, (float) y);
    }

    @Override
    public void cubicCurveTo(double x1, double y1, double x2, double y2, double x, double y) throws IOException {
        x1 *= MM_TO_PT;
        y1 *= MM_TO_PT;
        x2 *= MM_TO_PT;
        y2 *= MM_TO_PT;
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        contentStream.curveTo((float) x1, (float) y1, (float) x2, (float) y2, (float) x, (float) y);
    }

    @Override
    public void addRectangle(double x, double y, double width, double height) throws IOException {
        x *= MM_TO_PT;
        y *= MM_TO_PT;
        width *= MM_TO_PT;
        height *= MM_TO_PT;
        contentStream.addRect((float) x, (float) y, (float) width, (float) height);
    }

    @Override
    public void closeSubpath() throws IOException {
        contentStream.closePath();
    }

    @Override
    public void fillPath(int color, boolean smoothing) throws IOException {
        if (color != lastNonStrokingColor) {
            lastNonStrokingColor = color;
            int r = (color >> 16) & 0xff;
            int g = (color >> 8) & 0xff;
            int b = color & 0xff;
            contentStream.setNonStrokingColor(r / 255f, g / 255f, b / 255f);
        }
        contentStream.fill();
    }

    @Override
    public void strokePath(double strokeWidth, int color, LineStyle lineStyle, boolean smoothing) throws IOException {
        if (color != lastStrokingColor) {
            lastStrokingColor = color;
            int r = (color >> 16) & 0xff;
            int g = (color >> 8) & 0xff;
            int b = color & 0xff;
            contentStream.setStrokingColor(r / 255f, g / 255f, b / 255f);
        }
        if (lineStyle != lastLineStyle || (lineStyle != LineStyle.Solid && strokeWidth != lastLineWidth)) {
            lastLineStyle = lineStyle;
            float[] pattern;
            switch (lineStyle) {
                case Dashed:
                    pattern = new float[]{4 * (float) strokeWidth};
                    break;
                case Dotted:
                    pattern = new float[]{0, 3 * (float) strokeWidth};
                    break;
                default:
                    pattern = new float[]{};
            }
            contentStream.setLineCapStyle(lineStyle == LineStyle.Dotted ? 1 : 0);
            contentStream.setLineDashPattern(pattern, 0);
        }
        if (strokeWidth != lastLineWidth) {
            lastLineWidth = strokeWidth;
            contentStream.setLineWidth((float) (strokeWidth));
        }
        contentStream.stroke();
    }

    /**
     * Gets the resulting graphics as a byte array.
     * <p>
     * This method may only be called if the PDF document has been opened or created by this instance.
     * It may not be called if this instance was created by {@link #PDFCanvas(PDDocument, int)}
     * or {@link #PDFCanvas(PDPageContentStream)}.
     * </p>
     *
     * @return the byte array
     * @throws IOException thrown if the construction of the byte array fails
     */
    @Override
    public byte[] toByteArray() throws IOException {
        if (document == null)
            throw new IllegalStateException("toByteArray() may only be called if the PDF document has been opened/created by this instance. Use PDFBox methods instead to save the PDF document.");

        closeContentStream();
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            document.save(os);
            return os.toByteArray();
        }
    }

    /**
     * Writes the resulting PDF document to the specified output stream.
     * <p>
     * This method may only be called if the PDF document has been opened or created by this instance.
     * It may not be called if this instance was created by {@link #PDFCanvas(PDDocument, int)}
     * or {@link #PDFCanvas(PDPageContentStream)}.
     * </p>
     *
     * @param os the output stream
     * @throws IOException thrown if the image cannot be written
     */
    public void writeTo(OutputStream os) throws IOException {
        if (document == null)
            throw new IllegalStateException("writeTo() may only be called if the PDF document has been opened/created by this instance. Use PDFBox methods instead to save the PDF document.");

        closeContentStream();
        document.save(os);
    }

    /**
     * Saves the resulting PDF document to the specified path.
     * <p>
     * This method may only be called if the PDF document has been opened or created by this instance.
     * It may not be called if this instance was created by {@link #PDFCanvas(PDDocument, int)}
     * or {@link #PDFCanvas(PDPageContentStream)}.
     * </p>
     *
     * @param path the path to write to
     * @throws IOException thrown if the image cannot be written
     */
    public void saveAs(Path path) throws IOException {
        if (document == null)
            throw new IllegalStateException("saveAs() may only be called if the PDF document has been opened/created by this instance. Use PDFBox methods instead to save the PDF document.");

        closeContentStream();

        try (OutputStream os = Files.newOutputStream(path)) {
            document.save(os);
        }
    }

    @Override
    public void close() throws IOException {
        closeContentStream();

        if (document != null) {
            document.close();
            document = null;
        }
    }

    private void closeContentStream() throws IOException {
        if (contentStream != null) {
            contentStream.restoreGraphicsState();

            if (isContentStreamOwned) {
                contentStream.close();
            } else {
                // restore to default state
                contentStream.restoreGraphicsState();
                // restore to state before creating this instance
                contentStream.restoreGraphicsState();
            }
            contentStream = null;
        }
    }
}
