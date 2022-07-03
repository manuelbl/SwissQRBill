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

    private static final String[] QR_CODE_TEXT_1 = new String[]{
            "SPC",
            "0200",
            "1",
            "CH5800791123000889012",
            "S",
            "Robert Schneider AG",
            "Rue du Lac",
            "1268",
            "2501",
            "Biel",
            "CH",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "3949.75",
            "CHF",
            "S",
            "Pia Rutschmann",
            "Marktgasse",
            "28",
            "9400",
            "Rorschach",
            "CH",
            "NON",
            "",
            "Bill no. 3139 for gardening work and disposal of waste material",
            "EPD"
    };

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

    private static final String[] QR_CODE_TEXT_2 = new String[]{
            "SPC",
            "0200",
            "1",
            "CH4431999123000889012",
            "S",
            "Robert Schneider AG",
            "Rue du Lac",
            "1268",
            "2501",
            "Biel",
            "CH",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "1949.75",
            "CHF",
            "S",
            "Pia-Maria Rutschmann-Schnyder",
            "Grosse Marktgasse",
            "28",
            "9400",
            "Rorschach",
            "CH",
            "QRR",
            "210000000003139471430009017",
            "Order dated 18.06.2020",
            "EPD",
            "//S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010",
            "UV;UltraPay005;12345",
            "XY;XYService;54321"
    };

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
        bill.setAlternativeSchemes(new AlternativeScheme[]{
                new AlternativeScheme("Ultraviolet", "UV;UltraPay005;12345"),
                new AlternativeScheme("Xing Yong", "XY;XYService;54321")
        });
        return bill;
    }

    private static final String[] QR_CODE_TEXT_3 = new String[]{
            "SPC",
            "0200",
            "1",
            "CH3709000000304442225",
            "S",
            "Salvation Army Foundation Switzerland",
            "",
            "",
            "3000",
            "Bern",
            "CH",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "CHF",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "NON",
            "",
            "Donnation to the Winterfest campaign",
            "EPD"
    };

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

    private static final String[] QR_CODE_TEXT_4 = new String[]{
            "SPC",
            "0200",
            "1",
            "CH5800791123000889012",
            "S",
            "Robert Schneider AG",
            "Rue du Lac",
            "1268",
            "2501",
            "Biel",
            "CH",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "199.95",
            "CHF",
            "K",
            "Pia-Maria Rutschmann-Schnyder",
            "Grosse Marktgasse 28",
            "9400 Rorschach",
            "",
            "",
            "CH",
            "SCOR",
            "RF18539007547034",
            "",
            "EPD"
    };

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

    private static final String[] QR_CODE_TEXT_5 = new String[]{
            "SPC",
            "0200",
            "1",
            "CH5800791123000889012",
            "S",
            "Robert Schneider AG",
            "Rue du Lac",
            "1268",
            "2501",
            "Biel",
            "CH",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "0.50",
            "CHF",
            "K",
            "Pia-Maria Rutschmann-Schnyder",
            "Grosse Marktgasse 28",
            "9400 Rorschach",
            "",
            "",
            "CH",
            "SCOR",
            "RF18539007547034",
            "",
            "EPD"
    };

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

    private static final String[][] QR_CODE_TEXTS = new String[][]{
            QR_CODE_TEXT_1,
            QR_CODE_TEXT_2,
            QR_CODE_TEXT_3,
            QR_CODE_TEXT_4,
            QR_CODE_TEXT_5
    };

    static String getQrCodeText(int sample) {
        return getQrCodeText(sample, "\n");
    }

    static String getQrCodeText(int sample, String newLine) {
        return String.join(newLine, QR_CODE_TEXTS[sample - 1]);
    }

    static Bill getBillData(int sample) {
        Bill bill = null;
        switch (sample) {
            case 1:
                bill = getBillData1();
                break;
            case 2:
                bill = getBillData2();
                break;
            case 3:
                bill = getBillData3();
                break;
            case 4:
                bill = getBillData4();
                break;
            case 5:
                bill = getBillData5();
                break;
        }
        return bill;
    }
}
