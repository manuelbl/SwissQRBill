//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.time.LocalDate;

public class SampleData {

    public static Bill getExample1() {
        Bill bill = new Bill();
        bill.setLanguage(Bill.Language.English);
        bill.setAccount("CH4431999123000889012");
        Person creditor = new Person();
        creditor.setName("Robert Schneider AG");
        creditor.setStreet("Rue du Lac");
        creditor.setHouseNumber("1268/2/22");
        creditor.setPostalCode("2501");
        creditor.setCity("Biel");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        Person finalCreditor = new Person();
        finalCreditor.setName("Robert Schneider Services Switzerland AG");
        finalCreditor.setStreet("Rue du Lac");
        finalCreditor.setHouseNumber("1268/3/1");
        finalCreditor.setPostalCode("2501");
        finalCreditor.setCity("Biel");
        finalCreditor.setCountryCode("CH");
        bill.setFinalCreditor(finalCreditor);
        bill.setAmountOpen(false);
        bill.setAmount(123949.75);
        bill.setCurrency("CHF");
        bill.setDueDate(LocalDate.of(2019, 10, 31));
        bill.setDebtorOpen(false);
        Person debtor = new Person();
        debtor.setName("Pia-Maria Rutschmann-Schnyder");
        debtor.setStreet("Grosse Marktgasse");
        debtor.setHouseNumber("28");
        debtor.setPostalCode("9400");
        debtor.setCity("Rorschach");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);
        bill.setReferenceNo("210000000003139471430009017");
        bill.setAdditionalInformation("Instruction of 15.09.2019##S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010");
        return bill;
    }

    public static Bill getExample2() {
        Bill bill = new Bill();
        bill.setLanguage(Bill.Language.German);
        bill.setAccount("CH3709000000304442225");
        Person creditor = new Person();
        creditor.setName("Salvation Army Foundation Switzerland");
        creditor.setStreet(null);
        creditor.setHouseNumber(null);
        creditor.setPostalCode("3000");
        creditor.setCity("Berne");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        bill.setFinalCreditor(null);
        bill.setAmountOpen(true);
        bill.setAmount(null);
        bill.setCurrency("CHF");
        bill.setDueDate(null);
        bill.setDebtorOpen(true);
        bill.setDebtor(null);
        bill.setReferenceNo("");
        bill.setAdditionalInformation("Donation to the Winterfest Campaign");
        return bill;
    }

    public static Bill getExample3() {
        Bill bill = new Bill();
        bill.setLanguage(Bill.Language.French);
        bill.setAccount("CH4431999123000889012");
        Person creditor = new Person();
        creditor.setName("Robert Schneider AG");
        creditor.setStreet("Rue du Lac");
        creditor.setHouseNumber("1268/2/22");
        creditor.setPostalCode("2501");
        creditor.setCity("Biel");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        Person finalCreditor = new Person();
        finalCreditor.setName("Robert Schneider Services Switzerland AG");
        finalCreditor.setStreet("Rue du Lac");
        finalCreditor.setHouseNumber("1268/3/1");
        finalCreditor.setPostalCode("2501");
        finalCreditor.setCity("Biel");
        finalCreditor.setCountryCode("CH");
        bill.setFinalCreditor(finalCreditor);
        bill.setAmountOpen(false);
        bill.setAmount(199.95);
        bill.setCurrency("CHF");
        bill.setDueDate(LocalDate.of(2019, 10, 31));
        bill.setDebtorOpen(false);
        Person debtor = new Person();
        debtor.setName("Pia-Maria Rutschmann-Schnyder");
        debtor.setStreet("Grosse Marktgasse");
        debtor.setHouseNumber("28");
        debtor.setPostalCode("9400");
        debtor.setCity("Rorschach");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);
        bill.setReferenceNo("RF18539007547034");
        bill.setAdditionalInformation(null);
        return bill;
    }

    public static Bill getExample4() {
        Bill bill = new Bill();
        bill.setLanguage(Bill.Language.Italian);
        bill.setAccount("CH3709000000304442225");
        Person creditor = new Person();
        creditor.setName("ABC AG");
        creditor.setStreet(null);
        creditor.setHouseNumber(null);
        creditor.setPostalCode("3000");
        creditor.setCity("Bern");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        bill.setFinalCreditor(null);
        bill.setAmountOpen(true);
        bill.setAmount(null);
        bill.setCurrency("CHF");
        bill.setDueDate(null);
        bill.setDebtorOpen(true);
        bill.setDebtor(null);
        bill.setReferenceNo("");
        bill.setAdditionalInformation("");
        return bill;
    }
}
