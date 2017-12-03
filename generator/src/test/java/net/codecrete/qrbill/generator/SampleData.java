//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.time.LocalDate;

public class SampleData {

    public static void fillExample1(QRBill qrBill) {
        qrBill.setAccount("CH4431999123000889012");
        Person creditor = new Person();
        creditor.setName("Robert Schneider AG");
        creditor.setStreet("Rue du Lac");
        creditor.setHouseNumber("1268/2/22");
        creditor.setPostalCode("2501");
        creditor.setCity("Biel");
        creditor.setCountryCode("CH");
        qrBill.setCreditor(creditor);
        Person finalCreditor = new Person();
        finalCreditor.setName("Robert Schneider Services Switzerland AG");
        finalCreditor.setStreet("Rue du Lac");
        finalCreditor.setHouseNumber("1268/3/1");
        finalCreditor.setPostalCode("2501");
        finalCreditor.setCity("Biel");
        finalCreditor.setCountryCode("CH");
        qrBill.setFinalCreditor(finalCreditor);
        qrBill.setAmountOpen(false);
        qrBill.setAmount(123949.75);
        qrBill.setCurrency("CHF");
        qrBill.setDueDate(LocalDate.of(2019, 10, 31));
        qrBill.setDebtorOpen(false);
        Person debtor = new Person();
        debtor.setName("Pia-Maria Rutschmann-Schnyder");
        debtor.setStreet("Grosse Marktgasse");
        debtor.setHouseNumber("28");
        debtor.setPostalCode("9400");
        debtor.setCity("Rorschach");
        debtor.setCountryCode("CH");
        qrBill.setDebtor(debtor);
        qrBill.setReferenceNo("210000000003139471430009017");
        qrBill.setAdditionalInformation("Instruction of 15.09.2019##S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010");
    }

    public static void fillExample2(QRBill qrBill) {
        qrBill.setAccount("CH3709000000304442225");
        Person creditor = new Person();
        creditor.setName("Salvation Army Foundation Switzerland");
        creditor.setStreet(null);
        creditor.setHouseNumber(null);
        creditor.setPostalCode("3000");
        creditor.setCity("Berne");
        creditor.setCountryCode("CH");
        qrBill.setCreditor(creditor);
        qrBill.setFinalCreditor(null);
        qrBill.setAmountOpen(true);
        qrBill.setAmount(0);
        qrBill.setCurrency("CHF");
        qrBill.setDueDate(null);
        qrBill.setDebtorOpen(true);
        qrBill.setDebtor(null);
        qrBill.setReferenceNo("");
        qrBill.setAdditionalInformation("Donation to the Winterfest Campaign");
    }

    public static void fillExample3(QRBill qrBill) {
        qrBill.setAccount("CH4431999123000889012");
        Person creditor = new Person();
        creditor.setName("Robert Schneider AG");
        creditor.setStreet("Rue du Lac");
        creditor.setHouseNumber("1268/2/22");
        creditor.setPostalCode("2501");
        creditor.setCity("Biel");
        creditor.setCountryCode("CH");
        qrBill.setCreditor(creditor);
        Person finalCreditor = new Person();
        finalCreditor.setName("Robert Schneider Services Switzerland AG");
        finalCreditor.setStreet("Rue du Lac");
        finalCreditor.setHouseNumber("1268/3/1");
        finalCreditor.setPostalCode("2501");
        finalCreditor.setCity("Biel");
        finalCreditor.setCountryCode("CH");
        qrBill.setFinalCreditor(finalCreditor);
        qrBill.setAmountOpen(false);
        qrBill.setAmount(199.95);
        qrBill.setCurrency("CHF");
        qrBill.setDueDate(LocalDate.of(2019, 10, 31));
        qrBill.setDebtorOpen(false);
        Person debtor = new Person();
        debtor.setName("Pia-Maria Rutschmann-Schnyder");
        debtor.setStreet("Grosse Marktgasse");
        debtor.setHouseNumber("28");
        debtor.setPostalCode("9400");
        debtor.setCity("Rorschach");
        debtor.setCountryCode("CH");
        qrBill.setDebtor(debtor);
        qrBill.setReferenceNo("RF18539007547034");
        qrBill.setAdditionalInformation(null);
    }

    public static void fillExample4(QRBill qrBill) {
        qrBill.setAccount("CH3709000000304442225");
        Person creditor = new Person();
        creditor.setName("ABC AG");
        creditor.setStreet(null);
        creditor.setHouseNumber(null);
        creditor.setPostalCode("3000");
        creditor.setCity("Bern");
        creditor.setCountryCode("CH");
        qrBill.setCreditor(creditor);
        qrBill.setFinalCreditor(null);
        qrBill.setAmountOpen(true);
        qrBill.setAmount(0);
        qrBill.setCurrency("CHF");
        qrBill.setDueDate(null);
        qrBill.setDebtorOpen(true);
        qrBill.setDebtor(null);
        qrBill.setReferenceNo("");
        qrBill.setAdditionalInformation("");
    }
}
