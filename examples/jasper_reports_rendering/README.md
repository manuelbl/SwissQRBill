# QR Bills in Jasper Reports

This example demonstrates an advanced approach for using the QR bill library in Jasper Reports.

A simple approach would be to first generated the QR bill payment slips as SVG files and then refer these files in the Jasper report. Unfortunately, Jasper Reports converts text in SVG into shapes (outlines), thereby considerably increasing the PDF file size. (Each payment slip will add about 100KB to the PDF file size.)

This approach implements the `QrBillRenderer` class, a custom rendering class producing the entire QR bill payment slip. It can be used in the `imageExpression` of a Jasper image element. Using this approach, each payment slip will add about 12KB to the PDF file size.


## Using the QR Bill Renderer

To create instances of `QrBillRenderer`, two options are available:

- Create it using the `QrBillRenderer(Bill)` constructor and integrate it into your data source.
- Use an instance of `QrBillImageDataSource`. It wraps an existing data source and adds the column `QrBillImage` with the renderer for the payment slip. It will retrieve the required data (creditor details, debtor details, amount etc.) from the wrapped data source. The field names are configurable.


## Relevant Configuration

In order to properly work and achieve the space-savings, two configuration options are important:

- The property `net.sf.jasperreports.export.pdf.force.svg.shapes` must be set to `false` (see `jasperreports.properties`). It is required to prevent th conversion of text into shapes.
- Control what fonts are used and if they are embedded. This example contains a configuration for the font *Liberation Sans*, an alternative to *Arial* and *Helvetica*, which is officially supported by the QR bill specification. For PDF output, the configuration instructs Jasper Reports to replace it with *Helvetica* and to not embed it. *Helvetica* is a standard PDF font supported by all PDF viewers. Without the font configuration, bold font faces likely won't work.


## Building and Running

This example requires Java 17 and Maven.

The Jasper report `invoices.jrxml` is compiled at build time into `invoices.jasper` using a Maven plug-in. (If you build and run it from IntelliJ IDEA, you will need to click *Generate Source and Update Folders For All Projects* in the *Maven* tool window.)

To build and run it, execute:

```text
$ mvn clean package exec:java
[INFO] Scanning for projects...
[INFO] 
[INFO] -----------< net.codecrete.qrbill:jasper-reports-rendering >------------
[INFO] Building Jasper Reports Rendering 3.1.1
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- clean:3.1.0:clean (default-clean) @ jasper-reports-rendering ---
[INFO] 
[INFO] --- jasperreports:3.5.6:jasper (default) @ jasper-reports-rendering ---
[INFO] Compiling 1 Jasper reports design files.
[INFO] Generated 1 jasper reports in 0.424 seconds
[INFO] 
[INFO] --- resources:3.0.2:resources (default-resources) @ jasper-reports-rendering ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 6 resources
[INFO] 
[INFO] --- compiler:3.8.0:compile (default-compile) @ jasper-reports-rendering ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 3 source files to /Users/me/Documents/SwissQRBill/examples/jasper_reports_rendering/target/classes
[INFO] 
[INFO] --- resources:3.0.2:testResources (default-testResources) @ jasper-reports-rendering ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /Users/me/Documents/SwissQRBill/examples/jasper_reports_rendering/src/test/resources
[INFO] 
[INFO] --- compiler:3.8.0:testCompile (default-testCompile) @ jasper-reports-rendering ---
[INFO] No sources to compile
[INFO] 
[INFO] --- surefire:2.22.1:test (default-test) @ jasper-reports-rendering ---
[INFO] No tests to run.
[INFO] 
[INFO] --- jar:3.0.2:jar (default-jar) @ jasper-reports-rendering ---
[INFO] Building jar: /Users/me/Documents/SwissQRBill/examples/jasper_reports_rendering/target/jasper-reports-rendering-3.1.1.jar
[INFO] 
[INFO] --- exec:3.0.0:java (default-cli) @ jasper-reports-rendering ---
119077 bytes written to invoices.pdf.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.179 s
[INFO] Finished at: 2023-07-09T22:17:59+02:00
[INFO] ------------------------------------------------------------------------
```


## Technical Background

In order to process SVG files and generate PDF, Jasper Reports uses the [Batik library](https://xmlgraphics.apache.org/) with a `Graphics2D` implementation from [OpenPDF](https://github.com/LibrePDF/OpenPDF). In this setup, Batik converts each letter (aka glyph) into a shape (aka path) and fills it. It likely does it to implement graphical effects that are not supported by Java 2D's `Graphics2D.drawString()`.

This behavior cannot be turned off. It will only be turned off in environments where the conversion to glyphs does not correctly work. This was the case for certain Java implementations before version 8. See [Batik's source code](
https://github.com/apache/xmlgraphics-batik/blob/trunk/batik-gvt/src/main/java/org/apache/batik/gvt/font/AWTGVTGlyphVector.java#L503).

Jasper Reports has a similar settings, which can and must be controlled:

```properties
net.sf.jasperreports.export.pdf.force.svg.shapes=false
```

However, it has no real effect if Batik is used in the graphics pipeline. The conversion into shapes will just happen at a later stage. So the QR Bill renderer is needed for a short graphics pipeline without Batik.

An additional benefit of using the QR bill renderer is that the QR bill library uses several techniques to reduce the size of QR codes in the resulting PDF. Even though some of the optimizations are lost in the graphics pipeline, the resulting QR codes are still considerably smaller than the ones generated by Batik (about half the size).

