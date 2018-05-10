//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import net.codecrete.qrbill.web.api.QrBill;

/**
 * Creates sample bill data
 */
class SampleData {

    static QrBill createBill1() {
        QrBill bill = new QrBill();
        bill.setLanguage(QrBill.Language.de);
        bill.setAmount(100.35);
        bill.setCurrency("CHF");
        bill.setAccount("CH4431999123000889012");
        bill.getCreditor().setName("Meierhans AG");
        bill.getCreditor().setStreet("Bahnhofstrasse");
        bill.getCreditor().setHouseNo("16");
        bill.getCreditor().setPostalCode("2100");
        bill.getCreditor().setTown("Irgendwo");
        bill.getCreditor().setCountryCode("CH");
        bill.setReferenceNo("RF18539007547034");
        return bill;
    }
}
