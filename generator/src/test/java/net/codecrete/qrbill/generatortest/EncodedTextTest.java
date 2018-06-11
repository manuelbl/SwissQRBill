//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.QRBillValidationError;

/**
 * Unit tests for encoding the embedded QR code text
 */
@DisplayName("Encoding of embedded QR code text")
class EncodedTextTest {

    @Test
    void createText1() {
        Bill bill = SampleData.getExample1();
        //@formatter:off
        assertEquals(
                "SPC\r\n" +
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
            QRBill.encodeQrCodeText(bill)
        );
        //@formatter:on
    }

    @Test
    void createText2() {
        Bill bill = SampleData.getExample2();
        //@formatter:off
        assertEquals(
                "SPC\r\n" +
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
            QRBill.encodeQrCodeText(bill)
        );
        //@formatter:on
    }

    @Test
    void createText3() {
        Bill bill = SampleData.getExample3();
        //@formatter:off
        assertEquals(
                "SPC\r\n" +
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
            QRBill.encodeQrCodeText(bill)
        );
        //@formatter:on
    }

    @Test
    void createText4() {
        Bill bill = SampleData.getExample4();
        //@formatter:off
        assertEquals(
                "SPC\r\n" +
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
            QRBill.encodeQrCodeText(bill)
        );
        //@formatter:on
    }

    @Test
    void createText5() {
        assertThrows(QRBillValidationError.class, () -> {
            Bill bill = SampleData.getExample4();
            bill.setAmount(0.0);
            QRBill.encodeQrCodeText(bill);
        });
    }

}
