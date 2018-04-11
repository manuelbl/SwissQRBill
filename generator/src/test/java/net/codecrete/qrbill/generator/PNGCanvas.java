//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

public class PNGCanvas  extends AbstractCanvas {

    private int resolution;
    private float coordinateScale;
    private float fontScale;
    private BufferedImage image;
    private Graphics2D graphics;
    private Path2D.Double currentPath;

    /**
     * Creates a new instance
     * @param resolution resolution of the result (in dpi)
     */
    public PNGCanvas(int resolution) {
        this.resolution = resolution;
        coordinateScale = (float)(resolution / 25.4);
        fontScale = (float)(resolution / 72);
    }

    @Override
    public void setupPage(double width, double height) {
        int w = (int)(width * coordinateScale + 0.5);
        int h = (int)(height * coordinateScale + 0.5);
        image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        graphics = image.createGraphics();
        graphics.setColor(new Color(0xffffff));
        graphics.fillRect(0, 0, w, h);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        setTransformation(0, 0, 1);
    }

    @Override
    public void setTransformation(double translateX, double translateY, double scale) {
        translateX *= coordinateScale;
        translateY *= coordinateScale;
        AffineTransform at
                = new AffineTransform(scale, 0, 0, scale, translateX, image.getHeight() - translateY);
        graphics.setTransform(at);
    }

    @Override
    public void putText(String text, double x, double y, int fontSize, boolean isBold) {
        x *= coordinateScale;
        y *= -coordinateScale;
        graphics.setColor(new Color(0));
        Font font = new Font("Helvetica", isBold ? Font.BOLD : Font.PLAIN, (int)(fontSize * fontScale + 0.5));
        graphics.setFont(font);
        graphics.drawString(text, (float)x, (float)y);
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
    public void fillPath(int color) {
        graphics.setColor(new Color(color));
        graphics.fill(currentPath);
    }

    @Override
    public void strokePath(double strokeWidth, int color) {
        graphics.setColor(new Color(color));
        graphics.setStroke(new BasicStroke((float)(strokeWidth * fontScale), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        graphics.draw(currentPath);
    }

    @Override
    public byte[] getResult() throws IOException {
        graphics.dispose();
        graphics = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        createPNG(image, os, resolution);
        //ImageIO.write(image, "png", os);
        return os.toByteArray();
    }

    @Override
    public void close() {
        if (graphics != null) {
            graphics.dispose();
            graphics = null;
        }
        image = null;
    }


    private static void createPNG(BufferedImage image, OutputStream os, int resolution) throws IOException {

        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName("png"); iw.hasNext();) {
            ImageWriter writer = iw.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(image.getType());
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported())
                continue;

            addDpiMetadata(metadata, resolution);

            try (ImageOutputStream stream = ImageIO.createImageOutputStream(os)) {
                writer.setOutput(stream);
                writer.write(metadata, new IIOImage(image, null, metadata), writeParam);
            }
            break;
        }
    }

    private static final String pngNativeMetadataFormat = "javax_imageio_png_1.0";
    private static final String pngStandardMetadataFormat = "javax_imageio_1.0";

    private static void addDpiMetadata(IIOMetadata metadata, int dpi) throws IIOInvalidTreeException {

        // native metadata format ("pHYs")
        double pixelsPerMeter = dpi / 25.4 * 1000;
        String pixelsPerMeterString = Integer.toString((int)(pixelsPerMeter + 0.5));

        IIOMetadataNode pHYs_node = new IIOMetadataNode("pHYs");
        pHYs_node.setAttribute("pixelsPerUnitXAxis", pixelsPerMeterString);
        pHYs_node.setAttribute("pixelsPerUnitYAxis", pixelsPerMeterString);
        pHYs_node.setAttribute("unitSpecifier", "meter");

        IIOMetadataNode root = new IIOMetadataNode(pngNativeMetadataFormat);
        root.appendChild(pHYs_node);
        metadata.mergeTree(pngNativeMetadataFormat, root);

        // standard metadata format
        double pixelsPerMM = dpi / 25.4;
        String pixelsPerMMString = Double.toString(pixelsPerMM);

        IIOMetadataNode horizontalPixelSize = new IIOMetadataNode("HorizontalPixelSize");
        horizontalPixelSize.setAttribute("value", pixelsPerMMString);

        IIOMetadataNode verticalPixelSize = new IIOMetadataNode("VerticalPixelSize");
        verticalPixelSize.setAttribute("value", pixelsPerMMString);

        IIOMetadataNode dimension = new IIOMetadataNode("Dimension");
        dimension.appendChild(horizontalPixelSize);
        dimension.appendChild(verticalPixelSize);

        root = new IIOMetadataNode(pngStandardMetadataFormat);
        root.appendChild(dimension);
        metadata.mergeTree(pngStandardMetadataFormat, root);
    }

}
