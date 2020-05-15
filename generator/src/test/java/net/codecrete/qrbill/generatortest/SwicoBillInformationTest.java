//
// Swiss QR Bill Generator
// Copyright (c) 2020 Christian Bernasconi
// Copyright (c) 2020 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.SwicoBillInformation;
import net.codecrete.qrbill.generator.SwicoBillInformation.PaymentCondition;
import net.codecrete.qrbill.generator.SwicoBillInformation.RateDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("SwicoBillInformation class")
public class SwicoBillInformationTest {

    @Test
    public void setInvoiceNumber() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setInvoiceNumber("ABC");
        assertEquals("ABC", billInformation.getInvoiceNumber());
    }

    @Test
    public void setInvoiceDate() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setInvoiceDate(LocalDate.of(2020, 6, 30));
        assertEquals(LocalDate.of(2020, 6, 30), billInformation.getInvoiceDate());
    }

    @Test
    public void setCustomerReference() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setCustomerReference("1234-ABC");
        assertEquals("1234-ABC", billInformation.getCustomerReference());
    }

    @Test
    public void setVatNumber() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setVatNumber("109030864");
        assertEquals("109030864", billInformation.getVatNumber());
    }

    @Test
    public void setVatDate() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setVatDate(LocalDate.of(2020, 3, 1));
        assertEquals(LocalDate.of(2020, 3, 1), billInformation.getVatDate());
    }

    @Test
    public void setVatStartDate() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setVatStartDate(LocalDate.of(2019, 3, 1));
        assertEquals(LocalDate.of(2019, 3, 1), billInformation.getVatStartDate());
    }

    @Test
    public void setVatEndDate() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setVatEndDate(LocalDate.of(2020, 2, 29));
        assertEquals(LocalDate.of(2020, 2, 29), billInformation.getVatEndDate());
    }

    @Test
    public void setVatRate() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setVatRate(BigDecimal.valueOf(7.7));
        assertEquals(7.7, billInformation.getVatRate().doubleValue());
    }

    @Test
    public void defaultRateDetails() {
        RateDetail detail = new RateDetail();
        assertNull(detail.getRate());
        assertNull(detail.getAmount());
    }

    @Test
    public void rateDetailConstructor() {
        RateDetail detail = new RateDetail(
                BigDecimal.valueOf(7.7),
                BigDecimal.valueOf(430)
        );
        assertEquals(7.7, detail.getRate().doubleValue());
        assertEquals(430, detail.getAmount().doubleValue());
    }

    @Test
    public void setRateDetailRate() {
        RateDetail detail = new RateDetail();
        detail.setRate(BigDecimal.valueOf(25, 1));
        assertEquals(2.5, detail.getRate().doubleValue());
    }

    @Test
    public void setRateDetailAmount() {
        RateDetail detail = new RateDetail();
        detail.setAmount(BigDecimal.valueOf(430, 0));
        assertEquals(430, detail.getAmount().doubleValue());
    }

    @Test
    public void setVatRateDetails() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setVatRateDetails(Arrays.asList(
                new RateDetail(BigDecimal.valueOf(8, 0), BigDecimal.valueOf(1000, 0)),
                new RateDetail(BigDecimal.valueOf(25, 1), BigDecimal.valueOf(400, 0))
        ));
        assertEquals(2, billInformation.getVatRateDetails().size());
        assertEquals(8.0, billInformation.getVatRateDetails().get(0).getRate().doubleValue());
        assertEquals(400.0, billInformation.getVatRateDetails().get(1).getAmount().doubleValue());
    }

    @Test
    public void setVatImportTaxes() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setVatImportTaxes(Arrays.asList(
                new RateDetail(BigDecimal.valueOf(77, 1), BigDecimal.valueOf(4812, 2)),
                new RateDetail(BigDecimal.valueOf(25, 1), BigDecimal.valueOf(1723, 2))
        ));
        assertEquals(2, billInformation.getVatImportTaxes().size());
        assertEquals(7.7, billInformation.getVatImportTaxes().get(0).getRate().doubleValue());
        assertEquals(17.23, billInformation.getVatImportTaxes().get(1).getAmount().doubleValue());
    }

    @Test
    public void defaultPaymentCondition() {
        PaymentCondition condition = new PaymentCondition();
        assertNull(condition.getDiscount());
        assertEquals(0, condition.getDays());
    }

    @Test
    public void paymentConditionConstructor() {
        PaymentCondition condition = new PaymentCondition(
                BigDecimal.valueOf(2.0),
                10
        );
        assertEquals(2.0, condition.getDiscount().doubleValue());
        assertEquals(10, condition.getDays());
    }

    @Test
    public void setPaymentConditionDiscount() {
        PaymentCondition condition = new PaymentCondition();
        condition.setDiscount(BigDecimal.valueOf(25, 1));
        assertEquals(2.5, condition.getDiscount().doubleValue());
    }

    @Test
    public void setPaymentConditionDays() {
        PaymentCondition condition = new PaymentCondition();
        condition.setDays(60);
        assertEquals(60, condition.getDays());
    }

    @Test
    public void setPaymentConditions() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setPaymentConditions(Arrays.asList(
                new PaymentCondition(BigDecimal.valueOf(2.0), 10),
                new PaymentCondition(BigDecimal.ZERO, 30)
        ));
        assertEquals(2, billInformation.getPaymentConditions().size());
        assertEquals(2.0, billInformation.getPaymentConditions().get(0).getDiscount().doubleValue());
        assertEquals(30, billInformation.getPaymentConditions().get(1).getDays());
    }

    @Test
    public void dueDate_isValid() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setInvoiceDate(LocalDate.of(2020, 6, 30));
        billInformation.setPaymentConditions(Arrays.asList(
                new PaymentCondition(BigDecimal.valueOf(2.0), 10),
                new PaymentCondition(BigDecimal.ZERO, 30)
        ));
        assertEquals(LocalDate.of(2020, 7, 30), billInformation.getDueDate());
    }

    @Test
    public void dueDate_isNull() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        assertNull(billInformation.getDueDate());

        billInformation.setInvoiceDate(LocalDate.of(2020, 6, 30));
        assertNull(billInformation.getDueDate());

        billInformation.setPaymentConditions(new ArrayList<>());
        assertNull(billInformation.getDueDate());

        billInformation.setInvoiceDate(null);
        assertNull(billInformation.getDueDate());

        billInformation.setInvoiceDate(LocalDate.of(2020, 6, 30));
        assertNull(billInformation.getDueDate());

        billInformation.setPaymentConditions(Collections.singletonList(
                new PaymentCondition(BigDecimal.valueOf(2.0), 10)
        ));
        assertNull(billInformation.getDueDate());
    }

    @Test
    public void testEqualsTrivial() {
        SwicoBillInformation info = new SwicoBillInformation();
        assertEquals(info, info);

        assertNotEquals(null, info);
        assertNotEquals("xxx", info);
    }

    @Test
    public void testEquals() {
        SwicoBillInformation info1 = createBillInformation();
        SwicoBillInformation info2 = createBillInformation();
        assertEquals(info1, info2);
        assertEquals(info2, info1);

        info2.setCustomerReference("ABC");
        assertNotEquals(info1, info2);
    }

    @Test
    public void testHashCode() {
        SwicoBillInformation info1 = createBillInformation();
        SwicoBillInformation info2 = createBillInformation();
        assertEquals(info1.hashCode(), info2.hashCode());
    }

    private SwicoBillInformation createBillInformation() {
        SwicoBillInformation info = new SwicoBillInformation();
        info.setInvoiceNumber("R0000700312");
        info.setInvoiceDate(LocalDate.of(2020, 7, 10));
        info.setCustomerReference("Q.30007.100002");
        info.setVatNumber("105815317");
        info.setVatStartDate(LocalDate.of(2019, 11, 1));
        info.setVatEndDate(LocalDate.of(2020, 4, 30));
        info.setVatRate(BigDecimal.valueOf(8));
        info.setVatImportTaxes(Arrays.asList(
                new RateDetail(BigDecimal.valueOf(8.0), BigDecimal.valueOf(48.12)),
                new RateDetail(BigDecimal.valueOf(2.5), BigDecimal.valueOf(17.23))
        ));
        info.setPaymentConditions(Collections.singletonList(
                new PaymentCondition(BigDecimal.ZERO, 30)
        ));
        return info;
    }
}
