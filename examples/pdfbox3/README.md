# Example using PDFBox 3.0

The Swiss QR Bill library is built with PDFBox version 2.0 (for PDF generation). PDFBox 3.0 is new
major version. It is not fully backward compatible. The Swiss QR Bill library will detect the
newer version and will still work.

In order to use PDFBox 3.0, you need to add an explicit dependency to PDFBox version 3.0 to your project
(`pom.xml` in case you are using Maven):

```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.0</version>
</dependency>
```
