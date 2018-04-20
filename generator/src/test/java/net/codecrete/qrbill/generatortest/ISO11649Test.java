package net.codecrete.qrbill.generatortest;

import org.junit.Assert;
import org.junit.Test;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.Payments;

public class ISO11649Test {

    @Test
    public void modulo97() {
        Assert.assertEquals("should calculate from mixed case string", 79, Payments.calculateMod97("RF00AB2g59x56V543"));
    }

    @Test
    public void createAndSetISO11649() {
        Bill bill = new Bill();
        bill.createAndSetCreditorReference("AB2G5 9X56 V543");
        Assert.assertEquals("RF19AB2G59X56V543", bill.getReferenceNo());

        Assert.assertTrue(Payments.isValidISO11649ReferenceNo(bill.getReferenceNo()));

        // zero padding the checksum
        bill.createAndSetCreditorReference("7");
        Assert.assertEquals("RF097", bill.getReferenceNo());
        Assert.assertTrue(Payments.isValidISO11649ReferenceNo(bill.getReferenceNo()));
    }


    @Test
    public void createISO11649() {
        String reference = Payments.createISO11649Reference("AB2G5 9X56 V543");
        Assert.assertEquals("RF19AB2G59X56V543", reference);

        Assert.assertTrue(Payments.isValidISO11649ReferenceNo(reference));

        // zero padding the checksum
        reference = Payments.createISO11649Reference("7");
        Assert.assertEquals("RF097", reference);
        Assert.assertTrue(Payments.isValidISO11649ReferenceNo(reference));
    }

}
