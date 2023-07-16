package net.codecrete.qrbill.generator;

import net.codecrete.qrbill.testhelper.SampleData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BillTextFormatterTest {

    private BillTextFormatter textFormatter;

    @BeforeEach
    void setUp() {
        textFormatter = new BillTextFormatter(SampleData.getExample1());
    }

    @Test
    void payableTo_isCorrect() {
        assertEquals(
                "CH44 3199 9123 0008 8901 2\nRobert Schneider AG\nRue du Lac 1268/2/22\n2501 Biel",
                textFormatter.getPayableTo());
    }

    @Test
    void payableToReduced_isCorrect() {
        assertEquals(
                "CH44 3199 9123 0008 8901 2\nRobert Schneider AG\n2501 Biel",
                textFormatter.getPayableToReduced());
    }

    @Test
    void account_isCorrect() {
        assertEquals("CH44 3199 9123 0008 8901 2", textFormatter.getAccount());
    }

    @Test
    void creditorAdddress_isCorrect() {
        assertEquals(
                "Robert Schneider AG\nRue du Lac 1268/2/22\n2501 Biel",
                textFormatter.getCreditorAddress());
    }

    @Test
    void creditorAddressReduced_isCorrect() {
        assertEquals(
                "Robert Schneider AG\n2501 Biel",
                textFormatter.getCreditorAddressReduced());
    }

    @Test
    void reference_isCorrect() {
        assertEquals("21 00000 00003 13947 14300 09017", textFormatter.getReference());
    }

    @Test
    void emptyReference_becomesNull() {
        textFormatter = new BillTextFormatter(SampleData.getExample2());
        assertNull(textFormatter.getReference());
    }

    @Test
    void amount_isCorrect() {
        assertEquals("123 949.75", textFormatter.getAmount());
    }

    @Test
    void missingAmount_becomesNull() {
        textFormatter = new BillTextFormatter(SampleData.getExample2());
        assertNull(textFormatter.getAmount());
    }

    @Test
    void payableBy_isCorrect() {
        assertEquals(
                "Pia-Maria Rutschmann-Schnyder\nGrosse Marktgasse 28\n9400 Rorschach",
                textFormatter.getPayableBy()
        );
    }

    @Test
    void emptyPayableBy_becomesNull() {
        textFormatter = new BillTextFormatter(SampleData.getExample2());
        assertNull(textFormatter.getPayableBy());
    }

    @Test
    void payableByReduced_isCorrect() {
        assertEquals("Pia-Maria Rutschmann-Schnyder\n9400 Rorschach", textFormatter.getPayableByReduced());
    }

    @Test
    void emptyPayableByReduced_becomesNull() {
        textFormatter = new BillTextFormatter(SampleData.getExample2());
        assertNull(textFormatter.getPayableByReduced());
    }

    @Test
    void additionalInformationWithTwoParts_isCorrect() {
        assertEquals(
                "Instruction of 15.09.2019\n//S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010",
                textFormatter.getAdditionalInformation()
        );
    }

    @Test
    void additionalInformationWithUnstructuredMessage_isCorrect() {
        textFormatter = new BillTextFormatter(SampleData.getExample2());
        assertEquals(
                "Donation to the Winterfest Campaign",
                textFormatter.getAdditionalInformation()
        );
    }

    @Test
    void additionalInformationWithBillInformation_isCorrect() {
        Bill bill = SampleData.getExample1();
        bill.setUnstructuredMessage("");
        textFormatter = new BillTextFormatter(bill);
        assertEquals(
                "//S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010",
                textFormatter.getAdditionalInformation()
        );
    }

    @Test
    void noAdditionalInformation_becomesNull() {
        textFormatter = new BillTextFormatter(SampleData.getExample3());
        assertNull(textFormatter.getAdditionalInformation());
    }

}
