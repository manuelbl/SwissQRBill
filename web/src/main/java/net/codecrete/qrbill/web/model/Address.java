package net.codecrete.qrbill.web.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Address
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2018-10-13T19:12:09.319311+02:00[Europe/Zurich]")

public class Address   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("street")
  private String street = null;

  @JsonProperty("houseNo")
  private String houseNo = null;

  @JsonProperty("town")
  private String town = null;

  @JsonProperty("postalCode")
  private String postalCode = null;

  @JsonProperty("countryCode")
  private String countryCode = null;

  public Address name(String name) {
    this.name = name;
    return this;
  }

  /**
   * First, middle and last name of a natural person or company or organization name of a legal person
   * @return name
  **/
  @ApiModelProperty(value = "First, middle and last name of a natural person or company or organization name of a legal person")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Address street(String street) {
    this.street = street;
    return this;
  }

  /**
   * Street name (without the house number)
   * @return street
  **/
  @ApiModelProperty(value = "Street name (without the house number)")


  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public Address houseNo(String houseNo) {
    this.houseNo = houseNo;
    return this;
  }

  /**
   * House or building number
   * @return houseNo
  **/
  @ApiModelProperty(value = "House or building number")


  public String getHouseNo() {
    return houseNo;
  }

  public void setHouseNo(String houseNo) {
    this.houseNo = houseNo;
  }

  public Address town(String town) {
    this.town = town;
    return this;
  }

  /**
   * Town or city name
   * @return town
  **/
  @ApiModelProperty(value = "Town or city name")


  public String getTown() {
    return town;
  }

  public void setTown(String town) {
    this.town = town;
  }

  public Address postalCode(String postalCode) {
    this.postalCode = postalCode;
    return this;
  }

  /**
   * Postal code
   * @return postalCode
  **/
  @ApiModelProperty(value = "Postal code")


  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public Address countryCode(String countryCode) {
    this.countryCode = countryCode;
    return this;
  }

  /**
   * Two letter ISO country code
   * @return countryCode
  **/
  @ApiModelProperty(value = "Two letter ISO country code")


  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Address address = (Address) o;
    return Objects.equals(this.name, address.name) &&
        Objects.equals(this.street, address.street) &&
        Objects.equals(this.houseNo, address.houseNo) &&
        Objects.equals(this.town, address.town) &&
        Objects.equals(this.postalCode, address.postalCode) &&
        Objects.equals(this.countryCode, address.countryCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, street, houseNo, town, postalCode, countryCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Address {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    street: ").append(toIndentedString(street)).append("\n");
    sb.append("    houseNo: ").append(toIndentedString(houseNo)).append("\n");
    sb.append("    town: ").append(toIndentedString(town)).append("\n");
    sb.append("    postalCode: ").append(toIndentedString(postalCode)).append("\n");
    sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
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

