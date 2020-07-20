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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

public class SwicoExamples {

    static final String EXAMPLE_1_TEXT =
            "//S1/10/10201409/11/190512/20/1400.000-53/30/106017086/31/180508/32/7.7/40/2:10;0:30";

    static SwicoBillInformation createExample1() {
        SwicoBillInformation info = new SwicoBillInformation();
        info.setInvoiceNumber("10201409");
        info.setInvoiceDate(LocalDate.of(2019, 5, 12));
        info.setCustomerReference("1400.000-53");
        info.setVatNumber("106017086");
        info.setVatDate(LocalDate.of(2018, 5, 8));
        info.setVatRate(BigDecimal.valueOf(7.7));
        info.setPaymentConditions(Arrays.asList(
                new PaymentCondition(BigDecimal.valueOf(2), 10),
                new PaymentCondition(BigDecimal.ZERO, 30)));
        return info;
    }

    static final String EXAMPLE_2_TEXT =
            "//S1/10/10104/11/180228/30/395856455/31/180226180227/32/3.7:400.19;7.7:553.39;0:14/40/0:30";

    static SwicoBillInformation createExample2() {
        SwicoBillInformation info = new SwicoBillInformation();
        info.setInvoiceNumber("10104");
        info.setInvoiceDate(LocalDate.of(2018, 2, 28));
        info.setVatNumber("395856455");
        info.setVatStartDate(LocalDate.of(2018, 2, 26));
        info.setVatEndDate(LocalDate.of(2018, 2, 27));
        info.setVatRateDetails(Arrays.asList(
                new RateDetail(BigDecimal.valueOf(3.7), BigDecimal.valueOf(400.19)),
                new RateDetail(BigDecimal.valueOf(7.7), BigDecimal.valueOf(553.39)),
                new RateDetail(BigDecimal.valueOf(0), BigDecimal.valueOf(14))
        ));
        info.setPaymentConditions(Collections.singletonList(
                new PaymentCondition(BigDecimal.ZERO, 30)));
        return info;
    }

    static final String EXAMPLE_3_TEXT =
            "//S1/10/4031202511/11/180107/20/61257233.4/30/105493567/32/8:49.82/33/2.5:14.85/40/0:30";

    static SwicoBillInformation createExample3() {
        SwicoBillInformation info = new SwicoBillInformation();
        info.setInvoiceNumber("4031202511");
        info.setInvoiceDate(LocalDate.of(2018, 1, 7));
        info.setCustomerReference("61257233.4");
        info.setVatNumber("105493567");
        info.setVatRateDetails(Collections.singletonList(
                new RateDetail(BigDecimal.valueOf(8), BigDecimal.valueOf(49.82))
        ));
        info.setVatImportTaxes(Collections.singletonList(
                new RateDetail(BigDecimal.valueOf(2.5), BigDecimal.valueOf(14.85))
        ));
        info.setPaymentConditions(Collections.singletonList(
                new PaymentCondition(BigDecimal.ZERO, 30)));
        return info;
    }

    static final String EXAMPLE_4_TEXT =
            "//S1/10/X.66711\\/8824/11/200712/20/MW-2020-04/30/107978798/32/2.5:117.22/40/3:5;1.5:20;1:40;0:60";

    static SwicoBillInformation createExample4() {
        SwicoBillInformation info = new SwicoBillInformation();
        info.setInvoiceNumber("X.66711/8824");
        info.setInvoiceDate(LocalDate.of(2020, 7, 12));
        info.setCustomerReference("MW-2020-04");
        info.setVatNumber("107978798");
        info.setVatRateDetails(Collections.singletonList(
                new RateDetail(BigDecimal.valueOf(2.5), BigDecimal.valueOf(117.22))
        ));
        info.setPaymentConditions(Arrays.asList(
                new PaymentCondition(BigDecimal.valueOf(3), 5),
                new PaymentCondition(BigDecimal.valueOf(1.5), 20),
                new PaymentCondition(BigDecimal.valueOf(1), 40),
                new PaymentCondition(BigDecimal.ZERO, 60))
        );
        return info;
    }

    static final String EXAMPLE_5_TEXT =
            "//S1/10/79269/11/200714210713/20/66359/30/109532551/32/7.7/40/0:30";

    static SwicoBillInformation createExample5() {
        SwicoBillInformation info = new SwicoBillInformation();
        info.setInvoiceNumber("79269");
        info.setInvoiceDate(LocalDate.of(2020, 7, 14));
        info.setCustomerReference("66359");
        info.setVatNumber("109532551");
        info.setVatRate(BigDecimal.valueOf(7.7));
        info.setPaymentConditions(Arrays.asList(
                new PaymentCondition(BigDecimal.ZERO, 30))
        );
        return info;
    }

    static final String EXAMPLE_6_TEXT =
            "//S1/10/802277/11/2007012107/20/55878/30/109532551/32/7.7/40/0:30";

    static SwicoBillInformation createExample6() {
        SwicoBillInformation info = new SwicoBillInformation();
        info.setInvoiceNumber("802277");
        info.setInvoiceDate(LocalDate.of(2020, 7, 1));
        info.setCustomerReference("55878");
        info.setVatNumber("109532551");
        info.setVatRate(BigDecimal.valueOf(7.7));
        info.setPaymentConditions(Arrays.asList(
                new PaymentCondition(BigDecimal.ZERO, 30))
        );
        return info;
    }
}
