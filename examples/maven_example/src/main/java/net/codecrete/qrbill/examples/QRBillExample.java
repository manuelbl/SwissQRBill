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

public class QRBillExample {

    public static void main(String[] args) {

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

        BillFormat format = bill.getFormat();
        format.setGraphicsFormat(GraphicsFormat.SVG);
        format.setOutputSize(OutputSize.QR_BILL_ONLY);

        // Generate QR bill
        byte[] svg = QRBill.generate(bill);

        // Save QR bill
        Path path = Paths.get("qrbill.svg");
        try {
            Files.write(path, svg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("QR bill saved at " + path.toAbsolutePath());
    }
}