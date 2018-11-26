//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import net.codecrete.qrbill.web.model.Address;
import net.codecrete.qrbill.web.model.BillFormat;
import net.codecrete.qrbill.web.model.QrBill;

import java.math.BigDecimal;

/**
 * Creates sample bill data
 */
class SampleData {

    static QrBill createBill1() {
        QrBill bill = new QrBill();
        BillFormat format = new BillFormat();
        format.setLanguage(BillFormat.LanguageEnum.DE);
        format.setGraphicsFormat(BillFormat.GraphicsFormatEnum.SVG);
        format.setOutputSize(BillFormat.OutputSizeEnum.QR_BILL_ONLY);
        bill.setFormat(format);
        bill.setAmount(BigDecimal.valueOf(10035, 2));
        bill.setCurrency("CHF");
        bill.setAccount("CH7300774010291134700");
        Address creditor = new Address();
        creditor.setAddressType(Address.AddressTypeEnum.STRUCTURED);
        creditor.setName("Meierhans AG");
        creditor.setStreet("Bahnhofstrasse");
        creditor.setHouseNo("16");
        creditor.setPostalCode("2100");
        creditor.setTown("Irgendwo");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        bill.setReference("RF18539007547034");
        return bill;
    }
}
