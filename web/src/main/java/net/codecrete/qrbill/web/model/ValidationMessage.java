package net.codecrete.qrbill.web.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ValidationMessage
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2018-10-13T19:12:09.319311+02:00[Europe/Zurich]")

public class ValidationMessage   {
  /**
   * Message type (error or warning)
   */
  public enum TypeEnum {
    ERROR("Error"),
    
    WARNING("Warning");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TypeEnum fromValue(String text) {
      for (TypeEnum b : TypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("type")
  private TypeEnum type = null;

  @JsonProperty("messageKey")
  private String messageKey = null;

  @JsonProperty("messageParameters")
  @Valid
  private List<String> messageParameters = null;

  @JsonProperty("message")
  private String message = null;

  @JsonProperty("field")
  private String field = null;

  public ValidationMessage type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * Message type (error or warning)
   * @return type
  **/
  @ApiModelProperty(value = "Message type (error or warning)")


  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public ValidationMessage messageKey(String messageKey) {
    this.messageKey = messageKey;
    return this;
  }

  /**
   * Language independent message key
   * @return messageKey
  **/
  @ApiModelProperty(value = "Language independent message key")


  public String getMessageKey() {
    return messageKey;
  }

  public void setMessageKey(String messageKey) {
    this.messageKey = messageKey;
  }

  public ValidationMessage messageParameters(List<String> messageParameters) {
    this.messageParameters = messageParameters;
    return this;
  }

  public ValidationMessage addMessageParametersItem(String messageParametersItem) {
    if (this.messageParameters == null) {
      this.messageParameters = new ArrayList<>();
    }
    this.messageParameters.add(messageParametersItem);
    return this;
  }

  /**
   * Variable parts of the message (if any)
   * @return messageParameters
  **/
  @ApiModelProperty(value = "Variable parts of the message (if any)")


  public List<String> getMessageParameters() {
    return messageParameters;
  }

  public void setMessageParameters(List<String> messageParameters) {
    this.messageParameters = messageParameters;
  }

  public ValidationMessage message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Localized message (incl. variable parts). The Accept-Language header field is used to select a suitable language.
   * @return message
  **/
  @ApiModelProperty(value = "Localized message (incl. variable parts). The Accept-Language header field is used to select a suitable language.")


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ValidationMessage field(String field) {
    this.field = field;
    return this;
  }

  /**
   * Affected field name. Examples are Examples are: \"account\", \"creditor.street\"
   * @return field
  **/
  @ApiModelProperty(value = "Affected field name. Examples are Examples are: \"account\", \"creditor.street\"")


  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValidationMessage validationMessage = (ValidationMessage) o;
    return Objects.equals(this.type, validationMessage.type) &&
        Objects.equals(this.messageKey, validationMessage.messageKey) &&
        Objects.equals(this.messageParameters, validationMessage.messageParameters) &&
        Objects.equals(this.message, validationMessage.message) &&
        Objects.equals(this.field, validationMessage.field);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, messageKey, messageParameters, message, field);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValidationMessage {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    messageKey: ").append(toIndentedString(messageKey)).append("\n");
    sb.append("    messageParameters: ").append(toIndentedString(messageParameters)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    field: ").append(toIndentedString(field)).append("\n");
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

