//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.time.LocalDate;
import java.util.Objects;

public class Bill {

    public enum Language {
        German,
        French,
        Italian,
        English
    }

    public enum Version {
        Version_1_0
    }

    public static final String SUBFIELD_NAME = "name";
    public static final String SUBFIELD_STREET = "street";
    public static final String SUBFIELD_HOUSE_NO = "house_no";
    public static final String SUBFIELD_POSTAL_CODE = "postal_code";
    public static final String SUBFIELD_CITY = "city";
    public static final String SUBFIELD_COUNTRY_CODE = "country_code";
    public static final String FIELD_LANGUGAGE = "language";
    public static final String FIELD_CURRENCY = "currency";
    public static final String FIELD_AMOUNT_IS_OPEN = "amount_is_open";
    public static final String FIELD_AMOUNT = "amount";
    public static final String FIELD_ACCOUNT = "account";
    public static final String FIELDROOT_CREDITOR = "creditor.";
    public static final String FIELD_CREDITOR_NAME = "creditor.name";
    public static final String FIELD_CREDITOR_STREET = "creditor.street";
    public static final String FIELD_CREDITOR_HOUSE_NO = "creditor.house_no";
    public static final String FIELD_CREDITOR_POSTAL_CODE = "creditor.postal_code";
    public static final String FIELD_CREDITOR_CITY = "creditor.city";
    public static final String FIELD_CREDITOR_COUNTRY_CODE = "creditor.country_code";
    public static final String FIELDROOT_FINAL_CREDITOR = "final_creditor.";
    public static final String FIELD_FINAL_CREDITOR_NAME = "final_creditor.name";
    public static final String FIELD_FINAL_CREDITOR_STREET = "final_creditor.street";
    public static final String FIELD_FINAL_CREDITOR_HOUSE_NO = "final_creditor.house_no";
    public static final String FIELD_FINAL_CREDITOR_POSTAL_CODE = "final_creditor.postal_code";
    public static final String FIELD_FINAL_CREDITOR_CITY = "final_creditor.city";
    public static final String FIELD_FINAL_CREDITOR_COUNTRY_CODE = "final_creditor.country_code";
    public static final String FIELD_REFERENCE_NO = "reference_no";
    public static final String FIELD_ADDITIONAL_INFO = "additional_info";
    public static final String FIELDROOT_DEBTOR = "debtor.";
    public static final String FIELD_DEBTOR_IS_OPEN = "debtor.is_open";
    public static final String FIELD_DEBTOR_NAME = "debtor.name";
    public static final String FIELD_DEBTOR_STREET = "debtor.street";
    public static final String FIELD_DEBTOR_HOUSE_NO = "debtor.house_no";
    public static final String FIELD_DEBTOR_POSTAL_CODE = "debtor.postal_code";
    public static final String FIELD_DEBTOR_CITY = "debtor.city";
    public static final String FIELD_DEBTOR_COUNTRY_CODE = "debtor.country_code";
    public static final String FIELD_DUE_DATE = "due_date";

    private Language language = Language.English;
    private Version version = Version.Version_1_0;

    private boolean isAmountOpen = true;
    private Double amount = null;
    private String currency = "CHF";
    private String account = null;
    private Person creditor = new Person();
    private Person finalCreditor = null;
    private String referenceNo = null;
    private String additionalInformation = null;
    private boolean isDebtorOpen = true;
    private Person debtor = null;
    private LocalDate dueDate = null;


    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public boolean isAmountOpen() {
        return isAmountOpen;
    }

    public void setAmountOpen(boolean amountOpen) {
        isAmountOpen = amountOpen;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Person getCreditor() {
        return creditor;
    }

    public void setCreditor(Person creditor) {
        this.creditor = creditor;
    }

    public Person getFinalCreditor() {
        return finalCreditor;
    }

    public void setFinalCreditor(Person finalCreditor) {
        this.finalCreditor = finalCreditor;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public boolean isDebtorOpen() {
        return isDebtorOpen;
    }

    public void setDebtorOpen(boolean debtorOpen) {
        isDebtorOpen = debtorOpen;
    }

    public Person getDebtor() {
        return debtor;
    }

    public void setDebtor(Person debtor) {
        this.debtor = debtor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return isAmountOpen == bill.isAmountOpen &&
                isDebtorOpen == bill.isDebtorOpen &&
                language == bill.language &&
                version == bill.version &&
                Objects.equals(amount, bill.amount) &&
                Objects.equals(currency, bill.currency) &&
                Objects.equals(account, bill.account) &&
                Objects.equals(creditor, bill.creditor) &&
                Objects.equals(finalCreditor, bill.finalCreditor) &&
                Objects.equals(referenceNo, bill.referenceNo) &&
                Objects.equals(additionalInformation, bill.additionalInformation) &&
                Objects.equals(debtor, bill.debtor) &&
                Objects.equals(dueDate, bill.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, version, isAmountOpen, amount, currency, account, creditor, finalCreditor,
                referenceNo, additionalInformation, isDebtorOpen, debtor, dueDate);
    }
}
