//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EncodedTextTest {

    @Test
    public void createText1() {
        QRBill qrBill = new QRBill();
        SampleData.fillExample1(qrBill);
        assertEquals("SPC\r\n" +
                        "0100\r\n" +
                        "1\r\n" +
                        "CH4431999123000889012\r\n" +
                        "Robert Schneider AG\r\n" +
                        "Rue du Lac\r\n" +
                        "1268/2/22\r\n" +
                        "2501\r\n" +
                        "Biel\r\n" +
                        "CH\r\n" +
                        "Robert Schneider Services Switzerland AG\r\n" +
                        "Rue du Lac\r\n" +
                        "1268/3/1\r\n" +
                        "2501\r\n" +
                        "Biel\r\n" +
                        "CH\r\n" +
                        "123949.75\r\n" +
                        "CHF\r\n" +
                        "2019-10-31\r\n" +
                        "Pia-Maria Rutschmann-Schnyder\r\n" +
                        "Grosse Marktgasse\r\n" +
                        "28\r\n" +
                        "9400\r\n" +
                        "Rorschach\r\n" +
                        "CH\r\n" +
                        "QRR\r\n" +
                        "210000000003139471430009017\r\n" +
                        "Instruction of 15.09.2019##S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010",
                qrBill.createQRCodeText());
    }

    @Test
    public void createText2() {
        QRBill qrBill = new QRBill();
        SampleData.fillExample2(qrBill);
        assertEquals("SPC\r\n" +
                        "0100\r\n" +
                        "1\r\n" +
                        "CH3709000000304442225\r\n" +
                        "Salvation Army Foundation Switzerland\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "3000\r\n" +
                        "Berne\r\n" +
                        "CH\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "CHF\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "NON\r\n" +
                        "\r\n" +
                        "Donation to the Winterfest Campaign",
                qrBill.createQRCodeText());
    }

    @Test
    public void createText3() {
        QRBill qrBill = new QRBill();
        SampleData.fillExample3(qrBill);
        assertEquals("SPC\r\n" +
                        "0100\r\n" +
                        "1\r\n" +
                        "CH4431999123000889012\r\n" +
                        "Robert Schneider AG\r\n" +
                        "Rue du Lac\r\n" +
                        "1268/2/22\r\n" +
                        "2501\r\n" +
                        "Biel\r\n" +
                        "CH\r\n" +
                        "Robert Schneider Services Switzerland AG\r\n" +
                        "Rue du Lac\r\n" +
                        "1268/3/1\r\n" +
                        "2501\r\n" +
                        "Biel\r\n" +
                        "CH\r\n" +
                        "199.95\r\n" +
                        "CHF\r\n" +
                        "2019-10-31\r\n" +
                        "Pia-Maria Rutschmann-Schnyder\r\n" +
                        "Grosse Marktgasse\r\n" +
                        "28\r\n" +
                        "9400\r\n" +
                        "Rorschach\r\n" +
                        "CH\r\n" +
                        "SCOR\r\n" +
                        "RF18539007547034\r\n" +
                        "",
                qrBill.createQRCodeText());
    }

    @Test
    public void createText4() {
        QRBill qrBill = new QRBill();
        SampleData.fillExample4(qrBill);
        assertEquals("SPC\r\n" +
                        "0100\r\n" +
                        "1\r\n" +
                        "CH3709000000304442225\r\n" +
                        "ABC AG\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "3000\r\n" +
                        "Bern\r\n" +
                        "CH\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "CHF\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "NON\r\n" +
                        "\r\n" +
                        "",
                qrBill.createQRCodeText());
    }
}
