//
// Swiss QR Bill Generator
// Copyright (c) 2020 Christian Bernasconi
// Copyright (c) 2020 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import net.codecrete.qrbill.generator.SwicoBillInformation.PaymentCondition;
import net.codecrete.qrbill.generator.SwicoBillInformation.RateDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SwicoBillInformation class")
class SwicoBillInformationTest {

    @Test
    void setInvoiceNumber() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setInvoiceNumber("ABC");
        assertEquals("ABC", billInformation.getInvoiceNumber());
    }

    @Test
    void setInvoiceDate() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setInvoiceDate(LocalDate.of(2020, 6, 30));
        assertEquals(LocalDate.of(2020, 6, 30), billInformation.getInvoiceDate());
    }

    @Test
    void setCustomerReference() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setCustomerReference("1234-ABC");
        assertEquals("1234-ABC", billInformation.getCustomerReference());
    }

    @Test
    void setVatNumber() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setVatNumber("109030864");
        assertEquals("109030864", billInformation.getVatNumber());
    }

    @Test
    void setVatDate() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setVatDate(LocalDate.of(2020, 3, 1));
        assertEquals(LocalDate.of(2020, 3, 1), billInformation.getVatDate());
    }

    @Test
    void setVatStartDate() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setVatStartDate(LocalDate.of(2019, 3, 1));
        assertEquals(LocalDate.of(2019, 3, 1), billInformation.getVatStartDate());
    }

    @Test
    void setVatEndDate() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setVatEndDate(LocalDate.of(2020, 2, 29));
        assertEquals(LocalDate.of(2020, 2, 29), billInformation.getVatEndDate());
    }

    @Test
    void setVatRate() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setVatRate(BigDecimal.valueOf(7.7));
        assertEquals(7.7, billInformation.getVatRate().doubleValue());
    }

    @Test
    void defaultRateDetails() {
        RateDetail detail = new RateDetail();
        assertNull(detail.getRate());
        assertNull(detail.getAmount());
    }

    @Test
    void rateDetailConstructor() {
        RateDetail detail = new RateDetail(
                BigDecimal.valueOf(7.7),
                BigDecimal.valueOf(430)
        );
        assertEquals(7.7, detail.getRate().doubleValue());
        assertEquals(430, detail.getAmount().doubleValue());
    }

    @Test
    void setRateDetailRate() {
        RateDetail detail = new RateDetail();
        detail.setRate(BigDecimal.valueOf(25, 1));
        assertEquals(2.5, detail.getRate().doubleValue());
    }

    @Test
    void setRateDetailAmount() {
        RateDetail detail = new RateDetail();
        detail.setAmount(BigDecimal.valueOf(430, 0));
        assertEquals(430, detail.getAmount().doubleValue());
    }

    @Test
    void setVatRateDetails() {
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
    void setVatImportTaxes() {
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
    void defaultPaymentCondition() {
        PaymentCondition condition = new PaymentCondition();
        assertNull(condition.getDiscount());
        assertEquals(0, condition.getDays());
    }

    @Test
    void paymentConditionConstructor() {
        PaymentCondition condition = new PaymentCondition(
                BigDecimal.valueOf(2.0),
                10
        );
        assertEquals(2.0, condition.getDiscount().doubleValue());
        assertEquals(10, condition.getDays());
    }

    @Test
    void setPaymentConditionDiscount() {
        PaymentCondition condition = new PaymentCondition();
        condition.setDiscount(BigDecimal.valueOf(25, 1));
        assertEquals(2.5, condition.getDiscount().doubleValue());
    }

    @Test
    void setPaymentConditionDays() {
        PaymentCondition condition = new PaymentCondition();
        condition.setDays(60);
        assertEquals(60, condition.getDays());
    }

    @Test
    void setPaymentConditions() {
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
    void dueDate_isValid() {
        SwicoBillInformation billInformation = new SwicoBillInformation();
        billInformation.setInvoiceDate(LocalDate.of(2020, 6, 30));
        billInformation.setPaymentConditions(Arrays.asList(
                new PaymentCondition(BigDecimal.valueOf(2.0), 10),
                new PaymentCondition(BigDecimal.ZERO, 30)
        ));
        assertEquals(LocalDate.of(2020, 7, 30), billInformation.getDueDate());
    }

    @Test
    void dueDate_isNull() {
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
    void testEqualsTrivial() {
        SwicoBillInformation info = new SwicoBillInformation();
        assertEquals(info, info);

        assertNotEquals(null, info);
        assertNotEquals("xxx", info);
    }

    @Test
    void testEquals() {
        SwicoBillInformation info1 = createBillInformation();
        SwicoBillInformation info2 = createBillInformation();
        assertEquals(info1, info2);
        assertEquals(info2, info1);

        info2.setCustomerReference("ABC");
        assertNotEquals(info1, info2);
    }

    @Test
    void testHashCode() {
        SwicoBillInformation info1 = createBillInformation();
        SwicoBillInformation info2 = createBillInformation();
        assertEquals(info1.hashCode(), info2.hashCode());
    }

    @Test
    void testToString() {
        SwicoBillInformation info = SwicoExamples.createExample3();
        assertEquals(
                "SwicoBillInformation{invoiceNumber='4031202511', invoiceDate=2018-01-07, customerReference='61257233.4', vatNumber='105493567', vatDate=null, vatStartDate=null, vatEndDate=null, vatRate=null, vatRateDetails=[RateDetail{rate=8, amount=49.82}], vatImportTaxes=[RateDetail{rate=2.5, amount=14.85}], paymentConditions=[PaymentCondition{discount=0, days=30}]}",
                info.toString());
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
