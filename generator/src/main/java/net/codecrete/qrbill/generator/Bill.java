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

    // Itentionally use lowercase
    public enum Language {
        de,
        fr,
        it,
        en
    }

    public enum Version {
        V1_0
    }

    public static final String SUBFIELD_NAME = ".name";
    public static final String SUBFIELD_STREET = ".street";
    public static final String SUBFIELD_HOUSE_NO = ".houseNo";
    public static final String SUBFIELD_POSTAL_CODE = ".postalCode";
    public static final String SUBFIELD_TOWN = ".town";
    public static final String SUBFIELD_COUNTRY_CODE = ".countryCode";
    public static final String FIELD_LANGUAGE = ".language";
    public static final String FIELD_CURRENCY = ".currency";
    public static final String FIELD_AMOUNT = ".amount";
    public static final String FIELD_ACCOUNT = ".account";
    public static final String FIELDROOT_CREDITOR = ".creditor";
    public static final String FIELD_CREDITOR_NAME = ".creditor.name";
    public static final String FIELD_CREDITOR_STREET = ".creditor.street";
    public static final String FIELD_CREDITOR_HOUSE_NO = ".creditor.houseNo";
    public static final String FIELD_CREDITOR_POSTAL_CODE = ".creditor.postalCode";
    public static final String FIELD_CREDITOR_TOWN = ".creditor.town";
    public static final String FIELD_CREDITOR_COUNTRY_CODE = ".creditor.countryCode";
    public static final String FIELDROOT_FINAL_CREDITOR = ".finalCreditor";
    public static final String FIELD_FINAL_CREDITOR_NAME = ".finalCreditor.name";
    public static final String FIELD_FINAL_CREDITOR_STREET = ".finalCreditor.street";
    public static final String FIELD_FINAL_CREDITOR_HOUSE_NO = ".finalCreditor.houseNo";
    public static final String FIELD_FINAL_CREDITOR_POSTAL_CODE = ".finalCreditor.postalCode";
    public static final String FIELD_FINAL_CREDITOR_TOWN = ".finalCreditor.town";
    public static final String FIELD_FINAL_CREDITOR_COUNTRY_CODE = ".finalCreditor.countryCode";
    public static final String FIELD_REFERENCE_NO = ".referenceNo";
    public static final String FIELD_ADDITIONAL_INFO = ".additionalInfo";
    public static final String FIELDROOT_DEBTOR = ".debtor";
    public static final String FIELD_DEBTOR_NAME = ".debtor.name";
    public static final String FIELD_DEBTOR_STREET = ".debtor.street";
    public static final String FIELD_DEBTOR_HOUSE_NO = ".debtor.houseNo";
    public static final String FIELD_DEBTOR_POSTAL_CODE = ".debtor.postalCode";
    public static final String FIELD_DEBTOR_TOWN = ".debtor.town";
    public static final String FIELD_DEBTOR_COUNTRY_CODE = ".debtor.countryCode";
    public static final String FIELD_DUE_DATE = ".dueDate";

    private Language language = Language.en;
    private Version version = Version.V1_0;

    private Double amount = null;
    private String currency = "CHF";
    private String account = null;
    private Address creditor = new Address();
    private Address finalCreditor = null;
    private String referenceNo = null;
    private String additionalInfo = null;
    private Address debtor = null;
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

    public Address getCreditor() {
        return creditor;
    }

    public void setCreditor(Address creditor) {
        this.creditor = creditor;
    }

    public Address getFinalCreditor() {
        return finalCreditor;
    }

    public void setFinalCreditor(Address finalCreditor) {
        this.finalCreditor = finalCreditor;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Address getDebtor() {
        return debtor;
    }

    public void setDebtor(Address debtor) {
        this.debtor = debtor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return language == bill.language &&
                version == bill.version &&
                Objects.equals(amount, bill.amount) &&
                Objects.equals(currency, bill.currency) &&
                Objects.equals(account, bill.account) &&
                Objects.equals(creditor, bill.creditor) &&
                Objects.equals(finalCreditor, bill.finalCreditor) &&
                Objects.equals(referenceNo, bill.referenceNo) &&
                Objects.equals(additionalInfo, bill.additionalInfo) &&
                Objects.equals(debtor, bill.debtor) &&
                Objects.equals(dueDate, bill.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, version, amount, currency, account, creditor, finalCreditor,
                referenceNo, additionalInfo, debtor, dueDate);
    }
}
