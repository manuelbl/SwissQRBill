//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.time.LocalDate;

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

    public static final String FIELD_AMOUNT = "amount";
    public static final String FIELD_CURRENCY = "currency";
    public static final String FIELD_ACCOUNT = "account";
    public static final String FIELD_CREDITOR = "creditor";
    public static final String FIELD_FINAL_CREDITOR = "final_creditor";
    public static final String FIELD_REFERENCE_NO = "reference_no";
    public static final String FIELD_ADDITIONAL_INFO = "additional_info";
    public static final String FIELD_DEBTOR = "debtor";

    private Language language = Language.English;
    private Version version = Version.Version_1_0;

    private boolean isAmountOpen = false;
    private double amount = 0;
    private String currency = "CHF";
    private String account = "CH";
    private Person creditor = new Person();
    private Person finalCreditor = null;
    private String referenceNo = "0";
    private String additionalInformation = null;
    private boolean isDebtorOpen = false;
    private Person debtor = new Person();
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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
}
