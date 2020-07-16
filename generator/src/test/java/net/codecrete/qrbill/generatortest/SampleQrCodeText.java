//
// Swiss QR Bill Generator
// Copyright (c) 2018 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//

package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.AlternativeScheme;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.Language;

import java.math.BigDecimal;

class SampleQrCodeText {

    //@formatter:off
    private static final String QR_CODE_TEXT_1 = "SPC\n" +
            "0200\n" +
            "1\n" +
            "CH5800791123000889012\n" +
            "S\n" +
            "Robert Schneider AG\n" +
            "Rue du Lac\n" +
            "1268\n" +
            "2501\n" +
            "Biel\n" +
            "CH\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "3949.75\n" +
            "CHF\n" +
            "S\n" +
            "Pia Rutschmann\n" +
            "Marktgasse\n" +
            "28\n" +
            "9400\n" +
            "Rorschach\n" +
            "CH\n" +
            "NON\n" +
            "\n" +
            "Bill no. 3139 for gardening work and disposal of waste material\n" +
            "EPD\n";
    //@formatter:on

    static String getQrCodeText1(boolean withCRLF) {
        return handleLinefeed(QR_CODE_TEXT_1, withCRLF);
    }

    static Bill getBillData1() {
        Bill bill = new Bill();
        bill.getFormat().setLanguage(Language.EN);
        bill.setAccount("CH58 0079 1123 0008 8901 2");
        Address creditor = new Address();
        creditor.setName("Robert Schneider AG");
        creditor.setStreet("Rue du Lac");
        creditor.setHouseNo("1268");
        creditor.setPostalCode("2501");
        creditor.setTown("Biel");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        bill.setAmount(BigDecimal.valueOf(3949.75));
        bill.setCurrency("CHF");
        Address debtor = new Address();
        debtor.setName("Pia Rutschmann");
        debtor.setStreet("Marktgasse");
        debtor.setHouseNo("28");
        debtor.setPostalCode("9400");
        debtor.setTown(" Rorschach");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);
        bill.setUnstructuredMessage("Bill no. 3139 for gardening work and disposal of waste material");
        return bill;
    }

    //@formatter:off
    private static final String QR_CODE_TEXT_2 = "SPC\n" +
            "0200\n" +
            "1\n" +
            "CH4431999123000889012\n" +
            "S\n" +
            "Robert Schneider AG\n" +
            "Rue du Lac\n" +
            "1268\n" +
            "2501\n" +
            "Biel\n" +
            "CH\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "1949.75\n" +
            "CHF\n" +
            "S\n" +
            "Pia-Maria Rutschmann-Schnyder\n" +
            "Grosse Marktgasse\n" +
            "28\n" +
            "9400\n" +
            "Rorschach\n" +
            "CH\n" +
            "QRR\n" +
            "210000000003139471430009017\n" +
            "Order dated 18.06.2020\n" +
            "EPD\n" +
            "//S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010\n" +
            "UV;UltraPay005;12345\n" +
            "XY;XYService;54321";
    //@formatter:on

    static String getQrCodeText2(boolean withCRLF) {
        return handleLinefeed(QR_CODE_TEXT_2, withCRLF);
    }

    static Bill getBillData2() {
        Bill bill = new Bill();
        bill.getFormat().setLanguage(Language.EN);
        bill.setAccount("CH4431999123000889012");
        Address creditor = new Address();
        creditor.setName("Robert Schneider AG");
        creditor.setStreet("Rue du Lac");
        creditor.setHouseNo("1268");
        creditor.setPostalCode("2501");
        creditor.setTown("Biel");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        bill.setAmount(BigDecimal.valueOf(194975, 2));
        bill.setCurrency("CHF");
        Address debtor = new Address();
        debtor.setName("Pia-Maria Rutschmann-Schnyder");
        debtor.setStreet("Grosse Marktgasse");
        debtor.setHouseNo("28");
        debtor.setPostalCode("9400");
        debtor.setTown(" Rorschach");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);
        bill.setReference("210000000003139471430009017");
        bill.setUnstructuredMessage("Order dated 18.06.2020");
        bill.setBillInformation("//S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010");
        bill.setAlternativeSchemes(new AlternativeScheme[] {
                new AlternativeScheme("Ultraviolet", "UV;UltraPay005;12345"),
                new AlternativeScheme("Xing Yong", "XY;XYService;54321")
        });
        return bill;
    }

    //@formatter:off
    private static final String QR_CODE_TEXT_3 = "SPC\n" +
            "0200\n" +
            "1\n" +
            "CH3709000000304442225\n" +
            "S\n" +
            "Salvation Army Foundation Switzerland\n" +
            "\n" +
            "\n" +
            "3000\n" +
            "Bern\n" +
            "CH\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "CHF\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "NON\n" +
            "\n" +
            "Donnation to the Winterfest campaign\n" +
            "EPD\n";
    //@formatter:on

    static String getQrCodeText3(boolean withCRLF) {
        return handleLinefeed(QR_CODE_TEXT_3, withCRLF);
    }

    static Bill getBillData3() {
        Bill bill = new Bill();
        bill.getFormat().setLanguage(Language.EN);
        bill.setAccount("CH37 0900 0000 3044 4222 5");
        Address creditor = new Address();
        creditor.setName("Salvation Army Foundation Switzerland");
        creditor.setPostalCode("3000");
        creditor.setTown("Bern");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        bill.setCurrency("CHF");
        bill.setUnstructuredMessage("Donnation to the Winterfest campaign");
        return bill;
    }

    //@formatter:off
    private static final String QR_CODE_TEXT_4 = "SPC\n" +
            "0200\n" +
            "1\n" +
            "CH5800791123000889012\n" +
            "S\n" +
            "Robert Schneider AG\n" +
            "Rue du Lac\n" +
            "1268\n" +
            "2501\n" +
            "Biel\n" +
            "CH\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "199.95\n" +
            "CHF\n" +
            "K\n" +
            "Pia-Maria Rutschmann-Schnyder\n" +
            "Grosse Marktgasse 28\n" +
            "9400 Rorschach\n" +
            "\n" +
            "\n" +
            "CH\n" +
            "SCOR\n" +
            "RF18539007547034\n" +
            "\n" +
            "EPD\n";
    //@formatter:on

    static String getQrCodeText4(boolean withCRLF) {
        return handleLinefeed(QR_CODE_TEXT_4, withCRLF);
    }

    static Bill getBillData4() {
        Bill bill = new Bill();
        bill.getFormat().setLanguage(Language.EN);
        bill.setAccount("CH5800791123000889012");
        Address creditor = new Address();
        creditor.setName("Robert Schneider AG");
        creditor.setStreet("Rue du Lac");
        creditor.setHouseNo("1268");
        creditor.setPostalCode("2501");
        creditor.setTown("Biel");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        bill.setAmount(BigDecimal.valueOf(199.95));
        bill.setCurrency("CHF");
        Address debtor = new Address();
        debtor.setName("Pia-Maria Rutschmann-Schnyder");
        debtor.setAddressLine1("Grosse Marktgasse 28");
        debtor.setAddressLine2("9400 Rorschach");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);
        bill.setReference("RF18539007547034");
        return bill;
    }

    //@formatter:off
    private static final String QR_CODE_TEXT_5 = "SPC\n" +
            "0200\n" +
            "1\n" +
            "CH5800791123000889012\n" +
            "S\n" +
            "Robert Schneider AG\n" +
            "Rue du Lac\n" +
            "1268\n" +
            "2501\n" +
            "Biel\n" +
            "CH\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "0.50\n" +
            "CHF\n" +
            "K\n" +
            "Pia-Maria Rutschmann-Schnyder\n" +
            "Grosse Marktgasse 28\n" +
            "9400 Rorschach\n" +
            "\n" +
            "\n" +
            "CH\n" +
            "SCOR\n" +
            "RF18539007547034\n" +
            "\n" +
            "EPD\n";
    //@formatter:on

    static String getQrCodeText5(boolean withCRLF) {
        return handleLinefeed(QR_CODE_TEXT_5, withCRLF);
    }

    static Bill getBillData5() {
        Bill bill = new Bill();
        bill.getFormat().setLanguage(Language.EN);
        bill.setAccount("CH5800791123000889012");
        Address creditor = new Address();
        creditor.setName("Robert Schneider AG");
        creditor.setStreet("Rue du Lac");
        creditor.setHouseNo("1268");
        creditor.setPostalCode("2501");
        creditor.setTown("Biel");
        creditor.setCountryCode("CH");
        bill.setCreditor(creditor);
        bill.setAmount(BigDecimal.valueOf(50, 2));
        bill.setCurrency("CHF");
        Address debtor = new Address();
        debtor.setName("Pia-Maria Rutschmann-Schnyder");
        debtor.setAddressLine1("Grosse Marktgasse 28");
        debtor.setAddressLine2("9400 Rorschach");
        debtor.setCountryCode("CH");
        bill.setDebtor(debtor);
        bill.setReference("RF18539007547034");
        return bill;
    }

    private static String handleLinefeed(String text, boolean withCRLF) {
        if (withCRLF)
            text = text.replace("\n", "\r\n");
        return text;
    }
}
