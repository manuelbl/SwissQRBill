//
// Swiss QR Bill Generator
// Copyright (c) 2020 Christian Bernasconi
// Copyright (c) 2020 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generatortest;

import net.codecrete.qrbill.generator.SwicoBillInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Swico S1 Decoding")
public class SwicoS1DecodingTest {
    @Test
    public void example1_fullyDecoded() {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(SwicoExamples.EXAMPLE_1_TEXT);
        assertEquals(SwicoExamples.createExample1(), billInformation);
    }

    @Test
    public void example2_fullyDecoded() {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(SwicoExamples.EXAMPLE_2_TEXT);
        assertEquals(SwicoExamples.createExample2(), billInformation);
    }

    @Test
    public void example3_fullyDecoded() {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(SwicoExamples.EXAMPLE_3_TEXT);
        assertEquals(SwicoExamples.createExample3(), billInformation);
    }

    @Test
    public void example4_fullyDecoded() {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(SwicoExamples.EXAMPLE_4_TEXT);
        assertEquals(SwicoExamples.createExample4(), billInformation);
    }

    @Test
    public void nullValue_returnsNull() {
        assertNull(SwicoBillInformation.decodeText(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//S1/10//11//20//30/",
            "//S1/"})
    public void emptyValues_decoded(String rawBillInformation) {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(rawBillInformation);
        assertEquals(new SwicoBillInformation(), billInformation);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/S1/10/X.66711",
            "///S1/10/X.66711",
            "S1/10/X.66711",
            "10/X.66711"})
    public void invalidStart_returnsNull(String rawBillInformation) {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(rawBillInformation);
        assertNull(billInformation);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//S1/10/X.66711/XX/200",
            "//S1/10/X.66711/10.0/200"})
    public void invalidTag_isIgnored(String rawBillInformation) {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(rawBillInformation);
        SwicoBillInformation expected = new SwicoBillInformation();
        expected.setInvoiceNumber("X.66711");
        assertEquals(expected, billInformation);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//S1/11/190520/10/X.66711/30/123456789",
            "//S1/10/X.66711/30/123456789/11/190520",
            "//S1/11/201010/10/X.66711/11/190520/30/123456789"})
    public void invalidTagOrder_isIgnored(String rawBillInformation) {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(rawBillInformation);
        SwicoBillInformation expected = new SwicoBillInformation();
        expected.setInvoiceNumber("X.66711");
        expected.setInvoiceDate(LocalDate.of(2019, 5, 20));
        expected.setVatNumber("123456789");
        assertEquals(expected, billInformation);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//S1/10/X.66711/20/405\\/1/40/0:30---405/1",
            "//S1/10/X.66711/20/405\\\\1/40/0:30---405\\1",
            "//S1/10/X.66711/20/\\/405-1/40/0:30---/405-1",
            "//S1/10/X.66711/20/\\405-1/40/0:30---\\405-1",
            "//S1/10/X.66711/20/405\\\\/40/0:30---405\\"})
    public void escapedCharacters_unescaped(String testData) {
        String[] parameters = testData.split("---");
        String rawBillInformation = parameters[0];
        String customerReference = parameters[1];

        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(rawBillInformation);
        SwicoBillInformation expected = new SwicoBillInformation();
        expected.setInvoiceNumber("X.66711");
        expected.setCustomerReference(customerReference);
        expected.setPaymentConditions(Collections.singletonList(
                new SwicoBillInformation.PaymentCondition(BigDecimal.ZERO, 30)));
        assertEquals(expected, billInformation);
    }

    @Test
    public void billInformationTruncated1_isIgnored() {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText("//S1/10");
        assertEquals(new SwicoBillInformation(), billInformation);
    }

    @Test
    public void billInformationTruncated2_isIgnored() {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText("//S1/10/X.66711/11/190520/20");
        SwicoBillInformation expected = new SwicoBillInformation();
        expected.setInvoiceNumber("X.66711");
        expected.setInvoiceDate(LocalDate.of(2019, 5, 20));
        assertEquals(expected, billInformation);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//S1/10/X.66711/20/T.000-001/29/123",
            "//S1/10/X.66711/12/ABC/20/T.000-001"})
    public void unknownTag_isIgnored(String rawBillInformation) {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(rawBillInformation);
        SwicoBillInformation expected = new SwicoBillInformation();
        expected.setInvoiceNumber("X.66711");
        expected.setCustomerReference("T.000-001");
        assertEquals(expected, billInformation);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//S1/10/10201409/11/190570/20/405.789.Q",
            "//S1/10/10201409/11/19.05.20/20/405.789.Q",
            "//S1/10/10201409/11/1905213/20/405.789.Q",
            "//S1/10/10201409/11/200301 /20/405.789.Q"})
    public void invalidInvoiceDate_isIgnored(String rawBillInformation) {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(rawBillInformation);
        SwicoBillInformation expected = new SwicoBillInformation();
        expected.setInvoiceNumber("10201409");
        expected.setCustomerReference("405.789.Q");
        assertEquals(expected, billInformation);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//S1/10/10201409/31/190570",
            "//S1/10/10201409/31/1905213",
            "//S1/10/10201409/31/1905211905232",
            "//S1/10/10201409/31/19052119052 ",
            "//S1/10/10201409/31/190500190531",
            "//S1/10/10201409/31/190501190532"})
    public void invalidVatDates_isIgnored(String rawBillInformation) {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(rawBillInformation);
        SwicoBillInformation expected = new SwicoBillInformation();
        expected.setInvoiceNumber("10201409");
        assertEquals(expected, billInformation);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//S1/10/329348709/11/200629/32/AB/40/0:30",
            "//S1/10/329348709/11/200629/32/3.5./40/0:30"})
    public void invalidVatRate_isIgnored(String rawBillInformation) {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(rawBillInformation);
        SwicoBillInformation expected = new SwicoBillInformation();
        expected.setInvoiceNumber("329348709");
        expected.setInvoiceDate(LocalDate.of(2020, 6, 29));
        expected.setPaymentConditions(Collections.singletonList(
                new SwicoBillInformation.PaymentCondition(BigDecimal.ZERO, 30)));
        assertEquals(expected, billInformation);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//S1/10/10201409/11/200629/32/1;2/40/0:30",
            "//S1/10/10201409/11/200629/32/8:B/40/0:30",
            "//S1/10/10201409/11/200629/32/8:/40/0:30",
            "//S1/10/10201409/11/200629/32/:200;x:200/40/0:30"})
    public void invalidVatRateDetails_areIgnored_resultEmpty(String rawBillInformation) {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(rawBillInformation);
        SwicoBillInformation expected = new SwicoBillInformation();
        expected.setInvoiceNumber("10201409");
        expected.setInvoiceDate(LocalDate.of(2020, 6, 29));
        expected.setPaymentConditions(Collections.singletonList(
                new SwicoBillInformation.PaymentCondition(BigDecimal.ZERO, 30)));
        assertEquals(expected, billInformation);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//S1/10/10201409/11/200629/32/8:500;5/40/0:30",
            "//S1/10/10201409/11/200629/32/;8:500/40/0:30",
            "//S1/10/10201409/11/200629/32/8:500;/40/0:30",
            "//S1/10/10201409/11/200629/32/8:500;x:200/40/0:30"})
    public void invalidVatRateDetails_areIgnored_partialResult(String rawBillInformation) {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(rawBillInformation);
        SwicoBillInformation expected = new SwicoBillInformation();
        expected.setInvoiceNumber("10201409");
        expected.setInvoiceDate(LocalDate.of(2020, 6, 29));
        expected.setVatRateDetails(Collections.singletonList(
                new SwicoBillInformation.RateDetail(BigDecimal.valueOf(8), BigDecimal.valueOf(500))
        ));
        expected.setPaymentConditions(Collections.singletonList(
                new SwicoBillInformation.PaymentCondition(BigDecimal.ZERO, 30)));
        assertEquals(expected, billInformation);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//S1/10/10201409/11/200629/33/1;2/40/0:30",
            "//S1/10/10201409/11/200629/33/8:B/40/0:30",
            "//S1/10/10201409/11/200629/33/8:/40/0:30"})
    public void invalidVatImportTaxes_areIgnored_emptyResult(String rawBillInformation) {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(rawBillInformation);
        SwicoBillInformation expected = new SwicoBillInformation();
        expected.setInvoiceNumber("10201409");
        expected.setInvoiceDate(LocalDate.of(2020, 6, 29));
        expected.setPaymentConditions(Collections.singletonList(
                new SwicoBillInformation.PaymentCondition(BigDecimal.ZERO, 30)));
        assertEquals(expected, billInformation);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//S1/10/10201409/11/200629/33/8:24.5;5/40/0:30",
            "//S1/10/10201409/11/200629/33/;8:24.5/40/0:30",
            "//S1/10/10201409/11/200629/33/8:24.5;/40/0:30",
            "//S1/10/10201409/11/200629/33/8:24.5;x:200/40/0:30"})
    public void invalidVatImportTaxes_areIgnored_partialResult(String rawBillInformation) {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(rawBillInformation);
        SwicoBillInformation expected = new SwicoBillInformation();
        expected.setInvoiceNumber("10201409");
        expected.setInvoiceDate(LocalDate.of(2020, 6, 29));
        expected.setVatImportTaxes(Collections.singletonList(
                new SwicoBillInformation.RateDetail(BigDecimal.valueOf(8), BigDecimal.valueOf(24.50))
        ));
        expected.setPaymentConditions(Collections.singletonList(
                new SwicoBillInformation.PaymentCondition(BigDecimal.ZERO, 30)));
        assertEquals(expected, billInformation);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//S1/10/10201409/11/200629/40/1;60",
            "//S1/10/10201409/11/200629/40/1:5.0",
            "//S1/10/10201409/11/200629/40/3:B",
            "//S1/10/10201409/11/200629/40/ABC"})
    public void invalidPaymentConditions_areIgnored_emptyResult(String rawBillInformation) {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(rawBillInformation);
        SwicoBillInformation expected = new SwicoBillInformation();
        expected.setInvoiceNumber("10201409");
        expected.setInvoiceDate(LocalDate.of(2020, 6, 29));
        assertEquals(expected, billInformation);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "//S1/10/10201409/11/200629/40/0:30;5",
            "//S1/10/10201409/11/200629/40/;0:30",
            "//S1/10/10201409/11/200629/40/0:30;",
            "//S1/10/10201409/11/200629/40/x:1;2:x;0:30;x:200"})
    public void invalidPaymentConditions_areIgnored_partialResult(String rawBillInformation) {
        SwicoBillInformation billInformation = SwicoBillInformation.decodeText(rawBillInformation);
        SwicoBillInformation expected = new SwicoBillInformation();
        expected.setInvoiceNumber("10201409");
        expected.setInvoiceDate(LocalDate.of(2020, 6, 29));
        expected.setPaymentConditions(Collections.singletonList(
                new SwicoBillInformation.PaymentCondition(BigDecimal.ZERO, 30)));
        assertEquals(expected, billInformation);
    }

}
