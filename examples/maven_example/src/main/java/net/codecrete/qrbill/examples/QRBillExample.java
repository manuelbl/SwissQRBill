//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.examples;

import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

public class QRBillExample {

    public static void main(String[] args) {

        // Setup bill
        Bill bill = new Bill();
        bill.setLanguage(Bill.Language.FR);
        bill.setAccount("CH4431999123000889012");
        bill.setAmount(199.95);
        bill.setCurrency("CHF");

        // Set creditor
        Address creditor = new Address();
        creditor.setName("Robert Schneider AG");
        creditor.setStreet("Rue du Lac");
        creditor.setHouseNo("1268/2/22");
        creditor.setPostalCode("2501");
        creditor.setTown("Biel");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);

        // Set final creditor
        Address finalCreditor = new Address();
        finalCreditor.setName("Robert Schneider Services Switzerland AG");
        finalCreditor.setStreet("Rue du Lac");
        finalCreditor.setHouseNo("1268/3/1");
        finalCreditor.setPostalCode("2501");
        finalCreditor.setTown("Biel");
        finalCreditor.setCountryCode("CH");
        bill.setFinalCreditor(finalCreditor);

        // more bill data
        bill.setDueDate(LocalDate.of(2019, 10, 31));
        bill.setReference("RF18539007547034");
        bill.setAdditionalInfo(null);

        // Set debtor
        Address debtor = new Address();
        debtor.setName("Pia-Maria Rutschmann-Schnyder");
        debtor.setStreet("Grosse Marktgasse");
        debtor.setHouseNo("28");
        debtor.setPostalCode("9400");
        debtor.setTown("Rorschach");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);

        byte[] svg = QRBill.generate(bill, QRBill.BillFormat.A6_LANDSCAPE_SHEET, QRBill.GraphicsFormat.SVG);

        Path path = Paths.get("qrbill.svg");
        try {
            Files.write(path, svg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("QR bill saved at " + path.toAbsolutePath());
    }
}