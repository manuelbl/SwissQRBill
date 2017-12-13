//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.api;

import net.codecrete.qrbill.generator.Bill;

import java.time.LocalDate;
import java.util.Objects;

/**
 * QR bill data
 */
public class QrBill {

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


    private Language language = Language.en;
    private Version version = Version.V1_0;

    private Double amount = null;
    private String currency = "CHF";
    private String account = null;
    private Address creditor = new Address();
    private Address finalCreditor = null;
    private String referenceNo = null;
    private String additionalInformation = null;
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

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
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
        QrBill bill = (QrBill) o;
        return language == bill.language &&
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
        return Objects.hash(language, version, amount, currency, account, creditor, finalCreditor,
                referenceNo, additionalInformation, debtor, dueDate);
    }


    public static QrBill from(Bill bill) {
        QrBill qrBill = new QrBill();
        qrBill.language = Language.valueOf(bill.getLanguage().name());
        qrBill.version = Version.valueOf(bill.getVersion().name());
        qrBill.amount = bill.getAmount();
        qrBill.currency = bill.getCurrency();
        qrBill.account = bill.getAccount();
        qrBill.creditor = Address.from(bill.getCreditor());
        qrBill.finalCreditor = Address.from(bill.getFinalCreditor());
        qrBill.referenceNo = bill.getReferenceNo();
        qrBill.additionalInformation = bill.getAdditionalInformation();
        qrBill.debtor = Address.from(bill.getDebtor());
        qrBill.dueDate = bill.getDueDate();
        return qrBill;
    }

    public static Bill toGeneratorBill(QrBill qrBill) {
        if (qrBill == null)
            return null;

        Bill bill = new Bill();
        bill.setLanguage(net.codecrete.qrbill.generator.Bill.Language.valueOf(qrBill.getLanguage().name()));
        bill.setVersion(net.codecrete.qrbill.generator.Bill.Version.valueOf(qrBill.getVersion().name()));
        bill.setAmount(qrBill.amount);
        bill.setCurrency(qrBill.currency);
        bill.setAccount(qrBill.account);
        bill.setCreditor(Address.toGeneratorAddress(qrBill.creditor));
        bill.setFinalCreditor(Address.toGeneratorAddress(qrBill.finalCreditor));
        bill.setReferenceNo(qrBill.referenceNo);
        bill.setAdditionalInformation(qrBill.additionalInformation);
        bill.setDebtor(Address.toGeneratorAddress(qrBill.debtor));
        bill.setDueDate(qrBill.dueDate);
        return bill;
    }
}
