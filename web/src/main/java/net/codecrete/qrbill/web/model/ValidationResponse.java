package net.codecrete.qrbill.web.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import net.codecrete.qrbill.web.model.QrBill;
import net.codecrete.qrbill.web.model.ValidationMessage;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ValidationResponse
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2018-10-13T19:12:09.319311+02:00[Europe/Zurich]")

public class ValidationResponse   {
  @JsonProperty("valid")
  private Boolean valid = null;

  @JsonProperty("validationMessages")
  @Valid
  private List<ValidationMessage> validationMessages = null;

  @JsonProperty("validatedBill")
  private QrBill validatedBill = null;

  @JsonProperty("billID")
  private String billID = null;

  @JsonProperty("qrCodeText")
  private String qrCodeText = null;

  public ValidationResponse valid(Boolean valid) {
    this.valid = valid;
    return this;
  }

  /**
   * Indicates if the bill data was valid or not
   * @return valid
  **/
  @ApiModelProperty(value = "Indicates if the bill data was valid or not")


  public Boolean getValid() {
    return valid;
  }

  public void setValid(Boolean valid) {
    this.valid = valid;
  }

  public ValidationResponse validationMessages(List<ValidationMessage> validationMessages) {
    this.validationMessages = validationMessages;
    return this;
  }

  public ValidationResponse addValidationMessagesItem(ValidationMessage validationMessagesItem) {
    if (this.validationMessages == null) {
      this.validationMessages = new ArrayList<>();
    }
    this.validationMessages.add(validationMessagesItem);
    return this;
  }

  /**
   * Get validationMessages
   * @return validationMessages
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<ValidationMessage> getValidationMessages() {
    return validationMessages;
  }

  public void setValidationMessages(List<ValidationMessage> validationMessages) {
    this.validationMessages = validationMessages;
  }

  public ValidationResponse validatedBill(QrBill validatedBill) {
    this.validatedBill = validatedBill;
    return this;
  }

  /**
   * Get validatedBill
   * @return validatedBill
  **/
  @ApiModelProperty(value = "")

  @Valid

  public QrBill getValidatedBill() {
    return validatedBill;
  }

  public void setValidatedBill(QrBill validatedBill) {
    this.validatedBill = validatedBill;
  }

  public ValidationResponse billID(String billID) {
    this.billID = billID;
    return this;
  }

  /**
   * Bill ID if the bill data was valid. Used to retrieve the QR bill as SVG or PDF.
   * @return billID
  **/
  @ApiModelProperty(value = "Bill ID if the bill data was valid. Used to retrieve the QR bill as SVG or PDF.")


  public String getBillID() {
    return billID;
  }

  public void setBillID(String billID) {
    this.billID = billID;
  }

  public ValidationResponse qrCodeText(String qrCodeText) {
    this.qrCodeText = qrCodeText;
    return this;
  }

  /**
   * Text embedded in QR code if the bill data was valid.
   * @return qrCodeText
  **/
  @ApiModelProperty(value = "Text embedded in QR code if the bill data was valid.")


  public String getQrCodeText() {
    return qrCodeText;
  }

  public void setQrCodeText(String qrCodeText) {
    this.qrCodeText = qrCodeText;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValidationResponse validationResponse = (ValidationResponse) o;
    return Objects.equals(this.valid, validationResponse.valid) &&
        Objects.equals(this.validationMessages, validationResponse.validationMessages) &&
        Objects.equals(this.validatedBill, validationResponse.validatedBill) &&
        Objects.equals(this.billID, validationResponse.billID) &&
        Objects.equals(this.qrCodeText, validationResponse.qrCodeText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(valid, validationMessages, validatedBill, billID, qrCodeText);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValidationResponse {\n");
    
    sb.append("    valid: ").append(toIndentedString(valid)).append("\n");
    sb.append("    validationMessages: ").append(toIndentedString(validationMessages)).append("\n");
    sb.append("    validatedBill: ").append(toIndentedString(validatedBill)).append("\n");
    sb.append("    billID: ").append(toIndentedString(billID)).append("\n");
    sb.append("    qrCodeText: ").append(toIndentedString(qrCodeText)).append("\n");
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

