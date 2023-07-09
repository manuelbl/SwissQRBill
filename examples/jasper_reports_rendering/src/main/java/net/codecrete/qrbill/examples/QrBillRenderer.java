//
// Swiss QR Bill Generator
// Copyright (c) 2023 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.examples;

import net.codecrete.qrbill.canvas.AbstractCanvas;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.renderers.Graphics2DRenderable;
import net.sf.jasperreports.renderers.Renderable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.UUID;

/**
 * QR bill canvas for JasperReports.
 * <p>
 * The canvas implements {@link Renderable} to be suitable as a field value for an image,
 * and {@link Graphics2DRenderable} to be suitable for being exported to PDF.
 * </p>
 * <p>
 * The QR bill is drawn using the Java 2D API as expected by {@link Graphics2DRenderable}.
 * </p>
 */
public class QrBillRenderer extends AbstractCanvas implements Renderable, Graphics2DRenderable {
    private static final String FONT_FAMILY = "Liberation Sans";

    private final Bill bill;
    private double height;
    private float coordinateScale;
    private float fontScale;
    private Graphics2D graphics;
    private Path2D.Double currentPath;

    /**
     * Creates a new instance for the given bill data.
     * @param bill bill data
     */
    public QrBillRenderer(Bill bill) {
        this.bill = bill;
    }


    @Override
    public String getId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void render(JasperReportsContext jasperReportsContext, Graphics2D grx, Rectangle2D rectangle) {

        var resolution = 72.0;
        coordinateScale = (float) (resolution / 25.4);
        fontScale = (float) (resolution / 72.0);
        height = rectangle.getHeight();

        try {

            graphics = grx;
            setupFontMetrics(FONT_FAMILY);

            // enable high quality output
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

            // initialize transformation
            setTransformation(0, 0, 0, 1, 1);

            QRBill.draw(bill, this);

        } finally {
            graphics = null;
        }
    }

    @Override
    public void setTransformation(double translateX, double translateY, double rotate, double scaleX, double scaleY) {
        // Our coordinate system extends from the bottom up. Java Graphics2D's system
        // extends from the top down. So Y coordinates need to be treated specially.
        translateX *= coordinateScale;
        translateY *= coordinateScale;
        AffineTransform at = new AffineTransform();
        at.translate(translateX, height - translateY);
        if (rotate != 0)
            at.rotate(-rotate);
        if (scaleX != 1 || scaleY != 1)
            at.scale(scaleX, scaleY);
        graphics.setTransform(at);
    }

    @Override
    public void putText(String text, double x, double y, int fontSize, boolean isBold) {
        x *= coordinateScale;
        y *= -coordinateScale;
        graphics.setColor(new Color(0));
        Font font = new Font(FONT_FAMILY, isBold ? Font.BOLD : Font.PLAIN, (int) (fontSize * fontScale + 0.5));
        graphics.setFont(font);
        graphics.drawString(text, (float) x, (float) y);
    }

    @Override
    public void startPath() {
        currentPath = new Path2D.Double(Path2D.WIND_NON_ZERO);
    }

    @Override
    public void moveTo(double x, double y) {
        x *= coordinateScale;
        y *= -coordinateScale;
        currentPath.moveTo(x, y);
    }

    @Override
    public void lineTo(double x, double y) {
        x *= coordinateScale;
        y *= -coordinateScale;
        currentPath.lineTo(x, y);
    }

    @Override
    public void cubicCurveTo(double x1, double y1, double x2, double y2, double x, double y) {
        x1 *= coordinateScale;
        y1 *= -coordinateScale;
        x2 *= coordinateScale;
        y2 *= -coordinateScale;
        x *= coordinateScale;
        y *= -coordinateScale;
        currentPath.curveTo(x1, y1, x2, y2, x, y);
    }

    @Override
    public void addRectangle(double x, double y, double width, double height) {
        x *= coordinateScale;
        y *= -coordinateScale;
        width *= coordinateScale;
        height *= -coordinateScale;
        currentPath.moveTo(x, y);
        currentPath.lineTo(x, y + height);
        currentPath.lineTo(x + width, y + height);
        currentPath.lineTo(x + width, y);
        currentPath.closePath();
    }

    @Override
    public void closeSubpath() {
        currentPath.closePath();
    }

    @Override
    public void fillPath(int color, boolean smoothing) {
        if (!smoothing) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        }
        graphics.setColor(new Color(color));
        graphics.fill(currentPath);
        if (!smoothing) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        }
    }

    @Override
    public void strokePath(double strokeWidth, int color, LineStyle lineStyle, boolean smoothing) {
        graphics.setColor(new Color(color));
        BasicStroke stroke;
        switch (lineStyle) {
            case Dashed:
                stroke = new BasicStroke((float) (strokeWidth * fontScale), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10, new float[] { 4 * (float) strokeWidth * fontScale }, 0);
                break;
            case Dotted:
                stroke = new BasicStroke((float) (strokeWidth * fontScale), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER,
                        10, new float[] { 0, 3 * (float) strokeWidth * fontScale }, 0);
                break;
            default:
                stroke = new BasicStroke((float) (strokeWidth * fontScale), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        }
        graphics.setStroke(stroke);
        if (!smoothing) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        }

        graphics.draw(currentPath);

        if (!smoothing) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        }
    }

    @Override
    public void close() {
        // nothing to do
    }
}
