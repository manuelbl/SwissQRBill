package net.codecrete.qrbill.web.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import net.codecrete.qrbill.web.model.Address;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * QrBill
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2018-10-13T19:12:09.319311+02:00[Europe/Zurich]")

public class QrBill   {
  /**
   * Language of the generated QR bill
   */
  public enum LanguageEnum {
    DE("de"),
    
    FR("fr"),
    
    IT("it"),
    
    EN("en");

    private String value;

    LanguageEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static LanguageEnum fromValue(String text) {
      for (LanguageEnum b : LanguageEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("language")
  private LanguageEnum language = null;

  @JsonProperty("version")
  private String version = "V1_0";

  @JsonProperty("amount")
  private BigDecimal amount = null;

  @JsonProperty("currency")
  private String currency = "CHF";

  @JsonProperty("account")
  private String account = null;

  @JsonProperty("creditor")
  private Address creditor = null;

  @JsonProperty("finalCreditor")
  private Address finalCreditor = null;

  @JsonProperty("referenceNo")
  private String referenceNo = null;

  @JsonProperty("additionalInfo")
  private String additionalInfo = null;

  @JsonProperty("debtor")
  private Address debtor = null;

  @JsonProperty("dueDate")
  private LocalDate dueDate = null;

  public QrBill language(LanguageEnum language) {
    this.language = language;
    return this;
  }

  /**
   * Language of the generated QR bill
   * @return language
  **/
  @ApiModelProperty(value = "Language of the generated QR bill")


  public LanguageEnum getLanguage() {
    return language;
  }

  public void setLanguage(LanguageEnum language) {
    this.language = language;
  }

  public QrBill version(String version) {
    this.version = version;
    return this;
  }

  /**
   * QR bill specification version
   * @return version
  **/
  @ApiModelProperty(value = "QR bill specification version")


  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public QrBill amount(BigDecimal amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Bill amount
   * minimum: 0.01
   * maximum: 999999999.99
   * @return amount
  **/
  @ApiModelProperty(value = "Bill amount")

  @Valid
@DecimalMin("0.01") @DecimalMax("999999999.99") 
  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public QrBill currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Bill currency
   * @return currency
  **/
  @ApiModelProperty(value = "Bill currency")


  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public QrBill account(String account) {
    this.account = account;
    return this;
  }

  /**
   * Creditor's account
   * @return account
  **/
  @ApiModelProperty(value = "Creditor's account")


  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public QrBill creditor(Address creditor) {
    this.creditor = creditor;
    return this;
  }

  /**
   * Get creditor
   * @return creditor
  **/
  @ApiModelProperty(value = "")

  @Valid

  public Address getCreditor() {
    return creditor;
  }

  public void setCreditor(Address creditor) {
    this.creditor = creditor;
  }

  public QrBill finalCreditor(Address finalCreditor) {
    this.finalCreditor = finalCreditor;
    return this;
  }

  /**
   * Get finalCreditor
   * @return finalCreditor
  **/
  @ApiModelProperty(value = "")

  @Valid

  public Address getFinalCreditor() {
    return finalCreditor;
  }

  public void setFinalCreditor(Address finalCreditor) {
    this.finalCreditor = finalCreditor;
  }

  public QrBill referenceNo(String referenceNo) {
    this.referenceNo = referenceNo;
    return this;
  }

  /**
   * Payment reference number (QR/ISR reference number or ISO 11649 creditor reference)
   * @return referenceNo
  **/
  @ApiModelProperty(value = "Payment reference number (QR/ISR reference number or ISO 11649 creditor reference)")


  public String getReferenceNo() {
    return referenceNo;
  }

  public void setReferenceNo(String referenceNo) {
    this.referenceNo = referenceNo;
  }

  public QrBill additionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
    return this;
  }

  /**
   * Additional information for the bill recipient
   * @return additionalInfo
  **/
  @ApiModelProperty(value = "Additional information for the bill recipient")


  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  public QrBill debtor(Address debtor) {
    this.debtor = debtor;
    return this;
  }

  /**
   * Get debtor
   * @return debtor
  **/
  @ApiModelProperty(value = "")

  @Valid

  public Address getDebtor() {
    return debtor;
  }

  public void setDebtor(Address debtor) {
    this.debtor = debtor;
  }

  public QrBill dueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
    return this;
  }

  /**
   * Payment due date
   * @return dueDate
  **/
  @ApiModelProperty(value = "Payment due date")

  @Valid

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QrBill qrBill = (QrBill) o;
    return Objects.equals(this.language, qrBill.language) &&
        Objects.equals(this.version, qrBill.version) &&
        Objects.equals(this.amount, qrBill.amount) &&
        Objects.equals(this.currency, qrBill.currency) &&
        Objects.equals(this.account, qrBill.account) &&
        Objects.equals(this.creditor, qrBill.creditor) &&
        Objects.equals(this.finalCreditor, qrBill.finalCreditor) &&
        Objects.equals(this.referenceNo, qrBill.referenceNo) &&
        Objects.equals(this.additionalInfo, qrBill.additionalInfo) &&
        Objects.equals(this.debtor, qrBill.debtor) &&
        Objects.equals(this.dueDate, qrBill.dueDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(language, version, amount, currency, account, creditor, finalCreditor, referenceNo, additionalInfo, debtor, dueDate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QrBill {\n");
    
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    account: ").append(toIndentedString(account)).append("\n");
    sb.append("    creditor: ").append(toIndentedString(creditor)).append("\n");
    sb.append("    finalCreditor: ").append(toIndentedString(finalCreditor)).append("\n");
    sb.append("    referenceNo: ").append(toIndentedString(referenceNo)).append("\n");
    sb.append("    additionalInfo: ").append(toIndentedString(additionalInfo)).append("\n");
    sb.append("    debtor: ").append(toIndentedString(debtor)).append("\n");
    sb.append("    dueDate: ").append(toIndentedString(dueDate)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

