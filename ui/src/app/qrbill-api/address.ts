//
// Swiss QR Bill Generator
// Copyright (c) 2017 Manuel Bleichenbacher
// Licensed under MIT License
// https://opensource.org/licenses/MIT
//
export class Address {
  name?: string;
  street?: string | null;
  houseNo?: string | null;
  postalCode?: string;
  town?: string;
  countryCode?: string;

  static clone(address: Address): Address {
    if (!address) {
      return null;
    }

    return {
      name: address.name,
      street: address.street,
      houseNo: address.houseNo,
      postalCode: address.postalCode,
      town: address.town,
      countryCode: address.countryCode
    };
  }
}
