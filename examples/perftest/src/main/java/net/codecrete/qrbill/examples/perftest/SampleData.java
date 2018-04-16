//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.examples.perftest;

import java.time.LocalDate;

import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Bill;

public class SampleData {

    public static Bill generateBill(int id) {
        Bill bill;
        switch (id) {
            case 0:
                bill = getBill1();
                break;
            case 1:
                bill = getBill2();
                break;
            case 2:
                bill = getBill3();
                break;
            default:
                bill = getBill4();
        }
        return bill;
    }

    public static Bill getBill1() {
        Bill bill = new Bill();
        bill.setLanguage(Bill.Language.EN);
        bill.setAccount("CH44 3199 9123 0008  89012");
        Address creditor = new Address();
        creditor.setName("Robert Schneider AG");
        creditor.setStreet("Rue du Lac");
        creditor.setHouseNo("1268/2/22");
        creditor.setPostalCode("2501");
        creditor.setTown("Biel");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        Address finalCreditor = new Address();
        finalCreditor.setName("Robert Schneider Services Switzerland AG");
        finalCreditor.setStreet("Rue du Lac ");
        finalCreditor.setHouseNo("1268/3/1");
        finalCreditor.setPostalCode("2501");
        finalCreditor.setTown("Biel");
        finalCreditor.setCountryCode("CH");
        bill.setFinalCreditor(finalCreditor);
        bill.setAmount(123949.75);
        bill.setCurrency("CHF");
        bill.setDueDate(LocalDate.of(2019, 10, 31));
        Address debtor = new Address();
        debtor.setName("Pia-Maria Rutschmann-Schnyder");
        debtor.setStreet("Grosse Marktgasse");
        debtor.setHouseNo("28");
        debtor.setPostalCode("9400");
        debtor.setTown(" Rorschach");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);
        bill.setReferenceNo("210000 000 00313 9471430009017");
        bill.setAdditionalInfo("Instruction of 15.09.2019##S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010");
        return bill;
    }

    public static Bill getBill2() {
        Bill bill = new Bill();
        bill.setLanguage(Bill.Language.DE);
        bill.setAccount("CH3709000000304442225");
        Address creditor = new Address();
        creditor.setName("Salvation Army Foundation Switzerland");
        creditor.setStreet(null);
        creditor.setHouseNo(null);
        creditor.setPostalCode("3000");
        creditor.setTown("Berne");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        bill.setFinalCreditor(null);
        bill.setAmount(null);
        bill.setCurrency("CHF");
        bill.setDueDate(null);
        bill.setDebtor(null);
        bill.setReferenceNo("");
        bill.setAdditionalInfo("Donation to the Winterfest Campaign");
        return bill;
    }

    public static Bill getBill3() {
        Bill bill = new Bill();
        bill.setLanguage(Bill.Language.FR);
        bill.setAccount("CH4431999123000889012");
        Address creditor = new Address();
        creditor.setName("Robert Schneider AG");
        creditor.setStreet("Rue du Lac");
        creditor.setHouseNo("1268/2/22");
        creditor.setPostalCode("2501");
        creditor.setTown("Biel");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        Address finalCreditor = new Address();
        finalCreditor.setName("Robert Schneider Services Switzerland AG");
        finalCreditor.setStreet("Rue du Lac");
        finalCreditor.setHouseNo("1268/3/1");
        finalCreditor.setPostalCode("2501");
        finalCreditor.setTown("Biel");
        finalCreditor.setCountryCode("CH");
        bill.setFinalCreditor(finalCreditor);
        bill.setAmount(199.95);
        bill.setCurrency("CHF");
        bill.setDueDate(LocalDate.of(2019, 10, 31));
        Address debtor = new Address();
        debtor.setName("Pia-Maria Rutschmann-Schnyder");
        debtor.setStreet("Grosse Marktgasse");
        debtor.setHouseNo("28");
        debtor.setPostalCode("9400");
        debtor.setTown("Rorschach");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);
        bill.setReferenceNo("RF18539007547034");
        bill.setAdditionalInfo(null);
        return bill;
    }

    public static Bill getBill4() {
        Bill bill = new Bill();
        bill.setLanguage(Bill.Language.IT);
        bill.setAccount("CH3709000000304442225");
        Address creditor = new Address();
        creditor.setName("ABC AG");
        creditor.setStreet(null);
        creditor.setHouseNo(null);
        creditor.setPostalCode("3000");
        creditor.setTown("Bern");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        bill.setFinalCreditor(null);
        bill.setAmount(null);
        bill.setCurrency("CHF");
        bill.setDueDate(null);
        bill.setDebtor(null);
        bill.setReferenceNo("");
        bill.setAdditionalInfo("");
        return bill;
    }
}
