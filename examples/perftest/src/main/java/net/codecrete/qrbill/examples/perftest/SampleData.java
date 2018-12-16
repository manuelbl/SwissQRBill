//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.examples.perftest;

import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.Language;

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
        bill.getFormat().setLanguage(Language.EN);
        bill.setAccount("CH44 3199 9123 0008  89012");
        Address creditor = new Address();
        creditor.setName("Robert Schneider AG");
        creditor.setStreet("Rue du Lac");
        creditor.setHouseNo("1268/2/22");
        creditor.setPostalCode("2501");
        creditor.setTown("Biel");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        bill.setAmountFromDouble(123949.75);
        bill.setCurrency("CHF");
        Address debtor = new Address();
        debtor.setName("Pia-Maria Rutschmann-Schnyder");
        debtor.setStreet("Grosse Marktgasse");
        debtor.setHouseNo("28");
        debtor.setPostalCode("9400");
        debtor.setTown(" Rorschach");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);
        bill.setReference("210000 000 00313 9471430009017");
        bill.setUnstructuredMessage("Instruction of 15.09.2019##S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010");
        return bill;
    }

    public static Bill getBill2() {
        Bill bill = new Bill();
        bill.getFormat().setLanguage(Language.DE);
        bill.setAccount("CH3709000000304442225");
        Address creditor = new Address();
        creditor.setName("Salvation Army Foundation Switzerland");
        creditor.setStreet(null);
        creditor.setHouseNo(null);
        creditor.setPostalCode("3000");
        creditor.setTown("Berne");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        bill.setAmount(null);
        bill.setCurrency("CHF");
        bill.setDebtor(null);
        bill.setReference("");
        bill.setUnstructuredMessage("Donation to the Winterfest Campaign");
        return bill;
    }

    public static Bill getBill3() {
        Bill bill = new Bill();
        bill.getFormat().setLanguage(Language.FR);
        bill.setAccount("CH93 0076 2011 6238 5295 7");
        Address creditor = new Address();
        creditor.setName("Robert Schneider AG");
        creditor.setStreet("Rue du Lac");
        creditor.setHouseNo("1268/2/22");
        creditor.setPostalCode("2501");
        creditor.setTown("Biel");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        bill.setAmountFromDouble(199.95);
        bill.setCurrency("CHF");
        Address debtor = new Address();
        debtor.setName("Pia-Maria Rutschmann-Schnyder");
        debtor.setStreet("Grosse Marktgasse");
        debtor.setHouseNo("28");
        debtor.setPostalCode("9400");
        debtor.setTown("Rorschach");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);
        bill.setReference("RF18539007547034");
        bill.setUnstructuredMessage(null);
        return bill;
    }

    public static Bill getBill4() {
        Bill bill = new Bill();
        bill.getFormat().setLanguage(Language.IT);
        bill.setAccount("CH3709000000304442225");
        Address creditor = new Address();
        creditor.setName("ABC AG");
        creditor.setStreet(null);
        creditor.setHouseNo(null);
        creditor.setPostalCode("3000");
        creditor.setTown("Bern");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        bill.setAmount(null);
        bill.setCurrency("CHF");
        bill.setDebtor(null);
        bill.setReference("");
        bill.setUnstructuredMessage("");
        return bill;
    }
}
