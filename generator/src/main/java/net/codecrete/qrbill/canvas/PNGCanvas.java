//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.canvas;

import net.codecrete.qrbill.generator.QRBillGenerationException;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * Canvas for generating PNG files.
 * <p>
 * PNGs are not an optimal file format for QR bills. Vector formats such a SVG
 * or PDF are of better quality and use far less processing power to generate.
 * </p>
 */
public class PNGCanvas extends Graphics2DCanvas implements ByteArrayResult {

    private static final String METADATA_KEY_VALUE = "value";
    private static final String METADATA_KEY_KEYWORD = "keyword";

    private BufferedImage image;
    private Graphics2D graphics;
    private final int resolution;

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
        super(fontFamilyList);

        this.resolution = resolution;
        float scale = (float) (resolution / 25.4);

        // create image
        int w = (int) (width * scale + 0.5);
        int h = (int) (height * scale + 0.5);
        image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        // create graphics context
        graphics = image.createGraphics();

        // clear background
        graphics.setColor(new Color(0xffffff));
        graphics.fillRect(0, 0, w, h);

        setOffset(0, h);
        initGraphics(graphics, false, scale);
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
     * Saves image as PDF and stores metadata to indicate the resolution.
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

        IIOMetadataNode dimension = createDimensionNode(dpi);

        root = new IIOMetadataNode(PNG_STANDARD_METADATA_FORMAT);
        root.appendChild(dimension);
        metadata.mergeTree(PNG_STANDARD_METADATA_FORMAT, root);
    }

    private static IIOMetadataNode createDimensionNode(int dpi) {
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
        return dimension;
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
