//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.examples;

import net.codecrete.qrbill.canvas.PDFCanvas;
import net.codecrete.qrbill.generator.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Console application for generating a QR bill.
 * <p>
 *     The QR bill is saved as an SVG file in the working directory.
 *     The path of the working directory is printed to <c>stdout</c>.
 * </p>
 */
public class QRBillExample {

    public static void main(String[] args) throws IOException, URISyntaxException {

        // Setup bill
        Bill bill = new Bill();
        bill.setAccount("CH4431999123000889012");
        bill.setAmountFromDouble(199.95);
        bill.setCurrency("CHF");

        // Set creditor
        Address creditor = new Address();
        creditor.setName("Robert Schneider AG");
        creditor.setAddressLine1("Rue du Lac 1268/2/22");
        creditor.setAddressLine2("2501 Biel");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);

        // more bill data
        bill.setReference("210000000003139471430009017");
        bill.setUnstructuredMessage("Abonnement für 2020");

        // Set debtor
        Address debtor = new Address();
        debtor.setName("Pia-Maria Rutschmann-Schnyder");
        debtor.setAddressLine1("Grosse Marktgasse 28");
        debtor.setAddressLine2("9400 Rorschach");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);

        // Set output format
        BillFormat format = new BillFormat();
        format.setGraphicsFormat(GraphicsFormat.PDF);
        format.setOutputSize(OutputSize.A4_PORTRAIT_SHEET);
        format.setLanguage(Language.DE);
        bill.setFormat(format);

        // Generate new PDF with QR bill
        byte[] svg = QRBill.generate(bill);
        Path path0 = Paths.get("invoice-0.pdf");
        Files.write(path0, svg);
        System.out.println("QR bill saved at " + path0.toAbsolutePath());

        // Load existing PDF
        Path invoiceWithoutQRBill = Paths.get(QRBillExample.class.getResource("/invoice.pdf").toURI());

        // Add QR bill to last page
        try (PDFCanvas canvas = new PDFCanvas(invoiceWithoutQRBill, PDFCanvas.LAST_PAGE)) {
            QRBill.draw(bill, canvas);
            Path path = Paths.get("invoice-1.pdf");
            canvas.saveAs(path);
            System.out.println("Invoice 1 saved at " + path.toAbsolutePath());
        }

        // Append QR bill to a new page
        try (PDFCanvas canvas = new PDFCanvas(invoiceWithoutQRBill, PDFCanvas.NEW_PAGE_AT_END)) {
            QRBill.draw(bill, canvas);
            Path path = Paths.get("invoice-2.pdf");
            canvas.saveAs(path);
            System.out.println("Invoice 2 saved at " + path.toAbsolutePath());
        }

        System.out.println("Generated with version " + QRBill.getLibraryVersion());
    }
}