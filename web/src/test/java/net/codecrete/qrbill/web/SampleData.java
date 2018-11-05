//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import net.codecrete.qrbill.web.model.Address;
import net.codecrete.qrbill.web.model.QrBill;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Creates sample bill data
 */
class SampleData {

    static QrBill createBill1() {
        QrBill bill = new QrBill();
        bill.setLanguage(QrBill.LanguageEnum.DE);
        bill.setAmount(new BigDecimal(100.35, MathContext.DECIMAL32));
        bill.setCurrency("CHF");
        bill.setAccount("CH4431999123000889012");
        bill.setCreditor(new Address());
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
