//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web;

import net.codecrete.qrbill.web.model.Address;
import net.codecrete.qrbill.web.model.AlternativeScheme;
import net.codecrete.qrbill.web.model.BillFormat;
import net.codecrete.qrbill.web.model.QrBill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    static QrBill createBill2() {
        QrBill bill = new QrBill();
        BillFormat format = new BillFormat();
        format.setLanguage(BillFormat.LanguageEnum.DE);
        format.setGraphicsFormat(BillFormat.GraphicsFormatEnum.SVG);
        format.setOutputSize(BillFormat.OutputSizeEnum.QR_BILL_ONLY);
        bill.setFormat(format);
        bill.setAmount(BigDecimal.valueOf(97830, 2));
        bill.setCurrency("EUR");
        bill.setAccount("CH9400790016934853060");
        Address creditor = new Address();
        creditor.setAddressType(Address.AddressTypeEnum.STRUCTURED);
        creditor.setName("Ruf & Kramer GmbH & Co. KG");
        creditor.setAddressLine1("Wilhelm-Maybach-Strasse 7");
        creditor.setAddressLine2("78250 Watterdingen");
        creditor.setCountryCode("DE");
        bill.setCreditor(creditor);
        bill.setReference("RF43RF2093450930345993");
        bill.setBillInformation("//S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010");
        List<AlternativeScheme> schemeList = new ArrayList<>();
        AlternativeScheme scheme = new AlternativeScheme();
        scheme.setName("TWINT");
        scheme.setInstruction("TW:CHF:40.80:+41798739292:+41763123572:Danke für den schönen Abend");
        schemeList.add(scheme);
        bill.setAlternativeSchemes(schemeList);
        return bill;
    }
}
