//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.generator;

import java.io.Serializable;
import java.util.Objects;

/**
 * Address of creditor, final creditor or debtor.
 */
public class Address implements Serializable {
    
    private static final long serialVersionUID = -8833174154173397772L;
    
	private String name;
    private String street;
    private String houseNo;
    private String postalCode;
    private String town;
    private String countryCode;

    /**
     * Gets the name, either the first and last name of a natural person or the company name of a legal person.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name, either the first and last name of a natural person or the company name of a legal person.
     * <p>The name is mandatory unless the entire address contains {@code null} or empty values.</p>
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the street.
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the street.
     * <p>This field must not contain the house or building number.</p>
     * <p>This field is optional.</p>
     * @param street the street
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Gets the house or building number.
     * @return the house number
     */
    public String getHouseNo() {
        return houseNo;
    }

    /**
     * Sets the house or building number.
     * <p>This field is optional.</p>
     * @param houseNo the house number
     */
    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    /**
     * Gets the postal code
     * @return the postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postal code
     * <p>The postal code is mandatory unless the entire address contains {@code null} or empty values.</p>
     * @param postalCode the postal code
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Gets the town.
     * @return the town
     */
    public String getTown() {
        return town;
    }

    /**
     * Sets the town
     * <p>The town is mandatory unless the entire address contains {@code null} or empty values.</p>
     * @param town the town
     */
    public void setTown(String town) {
        this.town = town;
    }

    /**
     * Gets the two-letter ISO country code.
     * @return the ISO country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the two-letter ISO country code
     * <p>The country code is mandatory unless the entire address contains {@code null} or empty values.</p>
     * @param countryCode the ISO country code
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(name, address.name) &&
                Objects.equals(street, address.street) &&
                Objects.equals(houseNo, address.houseNo) &&
                Objects.equals(postalCode, address.postalCode) &&
                Objects.equals(town, address.town) &&
                Objects.equals(countryCode, address.countryCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, street, houseNo, postalCode, town, countryCode);
    }
}
