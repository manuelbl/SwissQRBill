package net.codecrete.qrbill.generatortest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.Payments;

public class ISO11649Test {

    @Test
    public void modulo97() {
        assertEquals("should calculate from mixed case string", 79, Payments.calculateMod97("RF00AB2g59x56V543"));
    }

    @Test
    public void createAndSetISO11649() {
        Bill bill = new Bill();
        bill.createAndSetCreditorReference("AB2G5 9X56 V543");
        assertEquals("RF19AB2G59X56V543", bill.getReferenceNo());

        assertTrue(Payments.isValidISO11649ReferenceNo(bill.getReferenceNo()));

        // zero padding the checksum
        bill.createAndSetCreditorReference("7");
        assertEquals("RF097", bill.getReferenceNo());
        assertTrue(Payments.isValidISO11649ReferenceNo(bill.getReferenceNo()));
    }


    @Test
    public void createISO11649() {
        String reference = Payments.createISO11649Reference("AB2G5 9X56 V543");
        assertEquals("RF19AB2G59X56V543", reference);

        assertTrue(Payments.isValidISO11649ReferenceNo(reference));

        // zero padding the checksum
        reference = Payments.createISO11649Reference("7");
        assertEquals("RF097", reference);
        assertTrue(Payments.isValidISO11649ReferenceNo(reference));
    }


    @Test
    public void invalidChar() {
        assertFalse(Payments.isValidISO11649ReferenceNo("RFA9AB2G59X56V543"));
        assertFalse(Payments.isValidISO11649ReferenceNo("RF1CAB2G59X56V543"));
    }


    @Test(expected = IllegalArgumentException.class)
    public void invalidChar2() {
        Payments.createISO11649Reference("AB2G5-9X56V543");
    }

}
