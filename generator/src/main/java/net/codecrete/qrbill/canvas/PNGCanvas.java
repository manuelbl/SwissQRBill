//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.canvas;

import net.codecrete.qrbill.generator.QRBillGenerationException;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Canvas for generating PNG files.
 * <p>
 * PNGs are not an optimal file format for QR bills. Vector formats such a SVG
 * or PDF are of better quality and use far less processing power to generate.
 * </p>
 */
public class PNGCanvas extends AbstractCanvas implements ByteArrayResult {

    private static final String METADATA_KEY_VALUE = "value";
    private static final String METADATA_KEY_KEYWORD = "keyword";

    private final int resolution;
    private final float coordinateScale;
    private final float fontScale;
    private BufferedImage image;
    private Graphics2D graphics;
    private Path2D.Double currentPath;

    /**
     * Creates a new instance with the specified image size, resolution and font family.
     * <p>
     * It is recommended to use at least 144 dpi for a readable result.
     * </p>
     * <p>
     * The first font family in the list is used.
     * </p>
     *
     * @param width          image width, in mm
     * @param height         image height, in mm
     * @param resolution     resolution of the result (in dpi)
     * @param fontFamilyList list of font families (comma separated, CSS syntax)
     */
    public PNGCanvas(double width, double height, int resolution, String fontFamilyList) {
        this.resolution = resolution;
        coordinateScale = (float) (resolution / 25.4);
        fontScale = (float) (resolution / 72.0);

        setupFontMetrics(findFontFamily(fontFamilyList));

        // create image
        int w = (int) (width * coordinateScale + 0.5);
        int h = (int) (height * coordinateScale + 0.5);
        image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        // create graphics context
        graphics = image.createGraphics();

        // clear background
        graphics.setColor(new Color(0xffffff));
        graphics.fillRect(0, 0, w, h);

        // enable high quality output
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        // initialize transformation
        setTransformation(0, 0, 0, 1, 1);
    }

    /**
     * Find the first family that's actually installed.
     * <p>
     * If none of the specified font families is installed, the input parameter is returned unchanged.
     * </p>
     *
     * @param fontFamilyList list of font families, separated by commas
     * @return the font family name of the first installed font
     */
    private String findFontFamily(String fontFamilyList) {
        for (String family : splitCommaSeparated(fontFamilyList)) {
            Font font = new Font(family, Font.PLAIN, 12);
            if (font.getFamily().toLowerCase(Locale.US).contains(family.toLowerCase(Locale.US)))
                return family;
        }
        return fontFamilyList;
    }

    private static final Pattern QUOTED_SPLITTER = Pattern.compile("(?:^|,)(\"(?:[^\"]+)*\"|[^,]*)");

    /**
     * Splits a comma separated list into its elements.
     * <p>
     * Elements can be quoted if they contain commas.
     * </p>
     *
     * @param input comma separated string
     * @return list of strings
     */
    private static List<String> splitCommaSeparated(String input) {
        List<String> result = new ArrayList<>();
        Matcher matcher = QUOTED_SPLITTER.matcher(input);
        while (matcher.find()) {
            String match = matcher.group(1).trim();
            if (match.charAt(0) == '"' && match.charAt(match.length() - 1) == '"')
                match = match.substring(1, match.length() - 1);
            result.add(match);
        }
        return result;
    }

    @Override
    public void setTransformation(double translateX, double translateY, double rotate, double scaleX, double scaleY) {
        // Our coordinate system extends from the bottom up. Java Graphics2D's system
        // extends from the top down. So Y coordinates need to be treated specially.
        translateX *= coordinateScale;
        translateY *= coordinateScale;
        AffineTransform at = new AffineTransform();
        at.translate(translateX, image.getHeight() - translateY);
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
        Font font = new Font(fontMetrics.getFirstFontFamily(), isBold ? Font.BOLD : Font.PLAIN, (int) (fontSize * fontScale + 0.5));
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
    public void fillPath(int color) {
        graphics.setColor(new Color(color));
        graphics.fill(currentPath);
    }

    @Override
    public void strokePath(double strokeWidth, int color, LineStyle lineStyle) {
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
        graphics.draw(currentPath);
    }

    @Override
    public byte[] toByteArray() throws IOException {
        graphics.dispose();
        graphics = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        // Instead of ImageIO.write(image, "png", os)
        createPNG(image, os, resolution);
        return os.toByteArray();
    }

    /**
     * Writes the resulting PNG image to the specified output stream.
     *
     * @param os the output stream
     * @throws IOException thrown if the image cannot be written
     */
    public void writeTo(OutputStream os) throws IOException {
        graphics.dispose();
        graphics = null;

        // Instead of ImageIO.write(image, "png", os)
        createPNG(image, os, resolution);
    }

    /**
     * Saves the resulting PNG image to the specified path.
     *
     * @param path the path to write to
     * @throws IOException thrown if the image cannot be written
     */
    public void saveAs(Path path) throws IOException {
        graphics.dispose();
        graphics = null;

        try (OutputStream os = Files.newOutputStream(path)) {
            // Instead of ImageIO.write(image, "png", os)
            createPNG(image, os, resolution);
        }
    }


    @Override
    public void close() {
        if (graphics != null) {
            graphics.dispose();
            graphics = null;
        }
        image = null;
    }

    /**
     * Saves image as PDF and stores meta data to indicate the resolution.
     */
    private static void createPNG(BufferedImage image, OutputStream os, int resolution) throws IOException {

        ImageWriter writer = null;
        ImageWriteParam writeParam = null;
        IIOMetadata metadata = null;

        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName("png"); iw.hasNext(); ) {
            writer = iw.next();
            writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(image.getType());
            metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (!metadata.isReadOnly() && metadata.isStandardMetadataFormatSupported())
                break;
        }

        if (writer == null || writeParam == null)
            throw new QRBillGenerationException("No valid PNG writer found");

        addDpiMetadata(metadata, resolution);
        addTextMetadata(metadata);

        try (ImageOutputStream stream = ImageIO.createImageOutputStream(os)) {
            writer.setOutput(stream);
            writer.write(metadata, new IIOImage(image, null, metadata), writeParam);
        }
    }

    private static final String PNG_STANDARD_METADATA_FORMAT = "javax_imageio_1.0";

    /**
     * Add meta data to specify the resolution
     */
    private static void addDpiMetadata(IIOMetadata metadata, int dpi) throws IIOInvalidTreeException {

        // native metadata format ("pHYs")
        double pixelsPerMeter = dpi / 25.4 * 1000;
        String pixelsPerMeterString = Integer.toString((int) (pixelsPerMeter + 0.5));

        IIOMetadataNode physNode = new IIOMetadataNode("pHYs");
        physNode.setAttribute("pixelsPerUnitXAxis", pixelsPerMeterString);
        physNode.setAttribute("pixelsPerUnitYAxis", pixelsPerMeterString);
        physNode.setAttribute("unitSpecifier", "meter");

        IIOMetadataNode root = new IIOMetadataNode(metadata.getNativeMetadataFormatName());
        root.appendChild(physNode);
        metadata.mergeTree(metadata.getNativeMetadataFormatName(), root);

        // standard metadata format
        double pixelsPerMM = dpi / 25.4;
        String pixelsPerMMString = Double.toString(pixelsPerMM);

        IIOMetadataNode horizontalPixelSize = new IIOMetadataNode("HorizontalPixelSize");
        horizontalPixelSize.setAttribute(METADATA_KEY_VALUE, pixelsPerMMString);

        IIOMetadataNode verticalPixelSize = new IIOMetadataNode("VerticalPixelSize");
        verticalPixelSize.setAttribute(METADATA_KEY_VALUE, pixelsPerMMString);

        IIOMetadataNode dimension = new IIOMetadataNode("Dimension");
        dimension.appendChild(horizontalPixelSize);
        dimension.appendChild(verticalPixelSize);

        root = new IIOMetadataNode(PNG_STANDARD_METADATA_FORMAT);
        root.appendChild(dimension);
        metadata.mergeTree(PNG_STANDARD_METADATA_FORMAT, root);
    }

    private static void addTextMetadata(IIOMetadata metadata) throws IIOInvalidTreeException {
        IIOMetadataNode textEntry = new IIOMetadataNode("tEXtEntry");
        textEntry.setAttribute(METADATA_KEY_KEYWORD, "Title");
        textEntry.setAttribute(METADATA_KEY_VALUE, "Swiss QR Bill");
        IIOMetadataNode text = new IIOMetadataNode("tEXt");
        text.appendChild(textEntry);
        IIOMetadataNode commentMetadata = new IIOMetadataNode(metadata.getNativeMetadataFormatName());
        commentMetadata.appendChild(text);
        metadata.mergeTree(metadata.getNativeMetadataFormatName(), commentMetadata);
    }

}
