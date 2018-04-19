package net.codecrete.qrbill.generator;

import org.junit.Assert;
import org.junit.Test;

public class ISO11649Test {

    @Test
    public void modulo97() {
        Assert.assertEquals("should calculate from mixed case string", 79, Strings.calculateMod97("RF00AB2g59x56V543"));
    }

    @Test
    public void createIso11649() {
        Bill bill = new Bill();
        bill.createSetCreditorReference("AB2G5 9X56 V543");
        Assert.assertEquals("RF19AB2G59X56V543", bill.getReferenceNo());

        Assert.assertTrue(PaymentValidation.isValidISO11649ReferenceNo(bill.getReferenceNo()));

        //zero padding the checksum
        bill.createSetCreditorReference("7");
        Assert.assertEquals("RF097", bill.getReferenceNo());
        Assert.assertTrue(PaymentValidation.isValidISO11649ReferenceNo(bill.getReferenceNo()));
    }

}
