//
// Swiss QR Bill Generator
// Copyright (c) 2019 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.examples;

import net.codecrete.qrbill.canvas.PDFCanvas;
import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Console application for appending QR bill to PDF file.
 * <p>
 *     The application uses the <c>invoice.pdf</c> file from the <c>resources</c>
 *     folder and saves the modified invoice as <c>invoice-1.pdf</c> and <c>invoice-2.pdf</c> respectively.
 * </p>
 */
public class AppendToPDF {

    public static void main(String[] args) throws URISyntaxException, IOException {

        // Setup bill format
        Bill bill = new Bill();
        bill.getFormat().setLanguage(Language.DE);
        bill.getFormat().setOutputSize(OutputSize.QR_CODE_ONLY);

        // Set account and amount
        bill.setAccount("CH48 0900 0000 8575 7337 2");
        bill.setAmount(BigDecimal.valueOf(175605, 2));
        bill.setCurrency("CHF");

        // Set creditor
        Address creditor = new Address();
        creditor.setName("Omnia Trading AG");
        creditor.setAddressLine1("Allmendweg 30");
        creditor.setAddressLine2("4528 Zuchwil");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);

        // more bill data
        bill.createAndSetCreditorReference("2021007834");
        bill.setUnstructuredMessage("Auftrag 2830188 / Rechnung 2021007834");

        // Set debtor
        Address debtor = new Address();
        debtor.setName("Machina Futura AG");
        debtor.setAddressLine1("Alte Fabrik 3A");
        debtor.setAddressLine2("8400 Winterthur");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);

        Path invoiceWithoutQRBill = Paths.get(AppendToPDF.class.getResource("/invoice.pdf").toURI());

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
    }
}
