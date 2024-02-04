//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.examples;

import net.codecrete.qrbill.generator.*;

import java.io.IOException;
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

    public static void main(String[] args) {

        double payable_amount = Double.parseDouble(args[0]);
        String reference_no = args[1];
        String additional_information = args[2];

        String debtor_name = args[3];
        String debtor_addr = args[4];
        String debtor_plz = args[5];

        // Setup bill
        Bill bill = new Bill();
        bill.setAccount("CH1234");
        bill.setAmountFromDouble(payable_amount);
        bill.setCurrency("CHF");

        // Set creditor
        Address creditor = new Address();
        creditor.setName("Basketballverein");
        creditor.setAddressLine1("Bahnhofstrasse");
        creditor.setAddressLine2("Zürich");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);

        // more bill data
        bill.setReference(reference_no);
        bill.setUnstructuredMessage(additional_information);

        // Set debtor
        Address debtor = new Address();
        debtor.setName(debtor_name);
        debtor.setAddressLine1(debtor_addr);
        debtor.setAddressLine2(debtor_plz);
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);

        // Set output format
        BillFormat format = new BillFormat();
        format.setGraphicsFormat(GraphicsFormat.PDF);
        format.setOutputSize(OutputSize.A4_PORTRAIT_SHEET);
        format.setLanguage(Language.EN);
        bill.setFormat(format);

        // Generate QR bill
        byte[] svg = QRBill.generate(bill);

        // Save QR bill
        Path path = Paths.get("qrbill.pdf");
        try {
            Files.write(path, svg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("QR bill saved at " + path.toAbsolutePath());

        System.out.println("Generated with version " + QRBill.getLibraryVersion());
    }
}
