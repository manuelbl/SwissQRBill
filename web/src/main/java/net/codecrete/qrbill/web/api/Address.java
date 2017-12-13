//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
package net.codecrete.qrbill.web.api;

import java.util.Objects;

/**
 * Address of a creditor or debtor
 */
public class Address {
    private String name;
    private String street;
    private String houseNo;
    private String postalCode;
    private String town;
    private String countryCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNo() {
        return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(name, street, houseNo, postalCode, town, countryCode);
    }

    public static Address from(net.codecrete.qrbill.generator.Address address) {
        if (address == null)
            return null;

        Address addr = new Address();
        addr.name = address.getName();
        addr.street = address.getStreet();
        addr.houseNo = address.getHouseNo();
        addr.postalCode = address.getPostalCode();
        addr.town = address.getTown();
        addr.countryCode = address.getCountryCode();
        return addr;
    }

    public net.codecrete.qrbill.generator.Address toGeneratorAddress() {
        net.codecrete.qrbill.generator.Address address = new net.codecrete.qrbill.generator.Address();
        address.setName(name);
        address.setStreet(street);
        address.setHouseNo(houseNo);
        address.setPostalCode(postalCode);
        address.setTown(town);
        address.setCountryCode(countryCode);
        return address;
    }
}
