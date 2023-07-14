//
// Swiss QR Bill Generator
// Copyright (c) 2023 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.examples;

import net.codecrete.qrbill.canvas.AbstractCanvas;
import net.codecrete.qrbill.canvas.Graphics2DCanvas;
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
public class QrBillRenderer implements Renderable, Graphics2DRenderable {
    private static final String FONT_FAMILY = "Liberation Sans";

    private final Bill bill;

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
        final var resolution = 72.0;
        var scale = (float) (resolution / 25.4);
        var canvas = new Graphics2DCanvas(grx, 0, (float) rectangle.getHeight(), scale, FONT_FAMILY);
        QRBill.draw(bill, canvas);
    }
}
